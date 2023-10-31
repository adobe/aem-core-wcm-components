/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2023 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.wcm.core.components.internal.services.clientLibraries;

import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.services.clientLibraries.ClientLibraryLookupService;
import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ClientLibraryLookupService}.
 */
@Component(service = {ClientLibraryLookupService.class, EventHandler.class},
    immediate = true,
    property = {
        EventConstants.EVENT_TOPIC + "=com/adobe/granite/ui/librarymanager/INVALIDATED"
    }
)
public final class ClientLibraryLookupServiceImpl implements ClientLibraryLookupService, EventHandler {

    /**
     * Default logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ClientLibraryLookupServiceImpl.class);

    /**
     * Name of the sub-service used to authenticate as in order to be able to read details about components and
     * client libraries.
     */
    private static final String COMPONENTS_SERVICE = "components-service";

    /**
     * Placeholder set used to indicate a cache miss.
     */
    private static final LinkedHashSet<ClientLibrary> CACHE_MISS_SET = new LinkedHashSet<>();

    /**
     * Resource resolver factory service.
     */
    private final ResourceResolverFactory resolverFactory;

    /**
     * Html Library Manager service.
     */
    private final HtmlLibraryManager libraryManager;

    /**
     * Cache used to store previous resource type -> client library mappings.
     */
    private final ConcurrentHashMap<String, Set<ClientLibrary>> cache = new ConcurrentHashMap<>();

    /**
     * Activate the service.
     *
     * @param resourceResolverFactory The resource resolver factory service.
     * @param htmlLibraryManager      The Html Library Manager service.
     */
    @Activate
    public ClientLibraryLookupServiceImpl(@NotNull @Reference final ResourceResolverFactory resourceResolverFactory,
                                          @NotNull @Reference final HtmlLibraryManager htmlLibraryManager) {
        this.resolverFactory = resourceResolverFactory;
        this.libraryManager = htmlLibraryManager;
    }

    @Override
    @NotNull
    public Set<ClientLibrary> getAllClientLibraries(@NotNull final Set<String> resourceTypes, boolean inherited, @NotNull final ResourceResolver resourceResolver) {
        Set<String> allResourceTypes = getAllResourceTypes(resourceTypes, inherited, resourceResolver);
        LinkedHashMap<String, Set<ClientLibrary>> results = new LinkedHashMap<>();
        boolean hasMisses = false;

        // loop through each resource type getting the client libraries from cache or
        // adding a placeholder if not in cache already.
        for (String resourceType : allResourceTypes) {
            Set<ClientLibrary> libraries = this.cache.get(resourceType);
            if (libraries == null) {
                LOG.debug("Cache miss {}", resourceType);
                results.put(resourceType, CACHE_MISS_SET);
                hasMisses = true;
            } else {
                LOG.debug("Cache hit {}", resourceType);
                results.put(resourceType, libraries);
            }
        }

        // handle all cache misses. Cache misses are handled as a block so that there is only
        // one call to expensive methods (such as session logins, or getting every library).
        if (hasMisses) {
            Map<String, ClientLibrary> allLibraries = this.libraryManager.getLibraries();
            try (ResourceResolver serviceResourceResolver = resolverFactory.getServiceResourceResolver(Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, COMPONENTS_SERVICE))) {
                LOG.debug("Service resource resolver login");
                // resolve all the cache misses
                LinkedHashMap<String, Set<ClientLibrary>> resolvedLibraries = results.entrySet().stream()
                    .filter(entry -> entry.getValue() == CACHE_MISS_SET)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toMap(
                        Function.identity(),
                        resourceType -> Optional.ofNullable(serviceResourceResolver.getResource(resourceType))
                            .map(resource -> getClientLibraries(resource, allLibraries))
                            .orElseGet(LinkedHashSet::new),
                        (a, b) -> a,
                        LinkedHashMap::new
                    ));

                // add all newly resolved libraries to the cache
                this.cache.putAll(resolvedLibraries);

                // add all newly resolved libraries to the results
                results.putAll(resolvedLibraries);
            } catch (LoginException e) {
                LOG.error("Cannot login as a service user", e);
            }
        }

        // merge and return all the results
        return results.values().stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Gets a list of client libraries, starting from the given resource
     * and diving into its descendants.
     *
     * @param resource     - the given resource, which will be checked to see if it's a client library
     * @param allLibraries - Map of all client libraries.
     * @return Set of client libraries for the given resource.
     */
    @NotNull
    private static LinkedHashSet<ClientLibrary> getClientLibraries(@Nullable final Resource resource, @NotNull final Map<String, ClientLibrary> allLibraries) {
        return Optional.ofNullable(resource)
            .map(Resource::getPath)
            .map(path -> allLibraries.entrySet().stream()
                .filter(entry -> entry.getKey().equals(path) || entry.getKey().startsWith(path + "/"))
                .map(Map.Entry::getValue)
                .collect(Collectors.toCollection(LinkedHashSet::new)))
            .orElseGet(LinkedHashSet::new);
    }

    /**
     * Gets all resource types.
     *
     * @param resourceTypes    Set of initial resource types.
     * @param inherited        Flag indicating if super resource types should be included.
     * @param resourceResolver A resource resolver - does not have to be a special resource resolver.
     * @return Set of all resource types under which to search for client libraries.
     */
    @NotNull
    private Set<String> getAllResourceTypes(@NotNull final Set<String> resourceTypes, boolean inherited, @NotNull final ResourceResolver resourceResolver) {
        Set<String> allResourceTypes = new LinkedHashSet<>(resourceTypes);
        if (inherited) {
            for (String resourceType : resourceTypes) {
                allResourceTypes.addAll(Utils.getSuperTypes(resourceType, resourceResolver));
            }
        }
        return allResourceTypes;
    }

    @Override
    public void handleEvent(@NotNull final Event event) {
        this.cache.clear();
    }
}
