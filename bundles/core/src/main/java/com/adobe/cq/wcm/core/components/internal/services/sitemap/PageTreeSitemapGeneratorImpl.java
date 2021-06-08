/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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
package com.adobe.cq.wcm.core.components.internal.services.sitemap;

import java.util.*;
import java.util.stream.Stream;

import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;
import org.apache.sling.sitemap.SitemapException;
import org.apache.sling.sitemap.builder.Sitemap;
import org.apache.sling.sitemap.builder.Url;
import org.apache.sling.sitemap.builder.extensions.AlternateLanguageExtension;
import org.apache.sling.sitemap.common.SitemapLinkExternalizer;
import org.apache.sling.sitemap.generator.ResourceTreeSitemapGenerator;
import org.apache.sling.sitemap.generator.SitemapGenerator;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.internal.models.v2.PageImpl;
import com.adobe.cq.wcm.core.components.services.sitemap.PageTreeSitemapGenerator;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.Page;
import com.day.text.Text;

/**
 * The main {@link SitemapGenerator} for AEM pages.
 */
@Component(service = { SitemapGenerator.class, PageTreeSitemapGenerator.class })
public class PageTreeSitemapGeneratorImpl extends ResourceTreeSitemapGenerator implements PageTreeSitemapGenerator {

    private final Logger LOG = LoggerFactory.getLogger(PageTreeSitemapGeneratorImpl.class);

    @Reference(policyOption = ReferencePolicyOption.GREEDY)
    private SitemapLinkExternalizer externalizer;
    @Reference
    private SlingSettingsService slingSettingsService;
    @Reference
    private LanguageManager languageManager;

    @Override
    @NotNull
    public Set<String> getNames(@NotNull Resource sitemapRoot) {
        return Collections.singleton(SitemapGenerator.DEFAULT_SITEMAP);
    }

    @Override
    protected void addResource(@NotNull String name, @NotNull Sitemap sitemap, @NotNull Resource resource)
        throws SitemapException {
        Page page = resource.adaptTo(Page.class);

        if (page == null) {
            LOG.debug("Skipping resource at {}: not a page", resource.getPath());
            return;
        }

        String location = externalizer.externalize(resource);

        if (location == null) {
            LOG.debug("Skipping resource at {}: externalised location is null", resource.getPath());
            return;
        }

        Url url = sitemap.addUrl(location + ".html");
        // last modified
        ReplicationStatus replicationStatus = page.getContentResource().adaptTo(ReplicationStatus.class);
        if (replicationStatus != null) {
            url.setLastModified(replicationStatus.getLastPublished().toInstant());
        }
        // alternate languages
        Map<Locale, String> languageAlternatives = getLanguageAlternatives(page);
        if (languageAlternatives.size() > 0) {
            AlternateLanguageExtension alternateLanguages = url.addExtension(AlternateLanguageExtension.class);
            if (alternateLanguages != null) {
                for (Map.Entry<Locale, String> entry : languageAlternatives.entrySet()) {
                    alternateLanguages.setLocale(entry.getKey());
                    alternateLanguages.setHref(entry.getValue());
                }
            }
        }
    }

    @Override
    protected final boolean shouldFollow(@NotNull Resource resource) {
        return super.shouldFollow(resource) && !isProtected(resource);
    }

    @Override
    protected final boolean shouldInclude(@NotNull Resource resource) {
        return super.shouldInclude(resource) && Optional.ofNullable(resource.adaptTo(Page.class))
            .map(this::shouldInclude).orElse(Boolean.FALSE);
    }

    /**
     * Include only pages that are published, exclude pages that have noindex set, are CUG protected or are redirects.
     * <p>
     * Redirects are filtered explicitly because they either point to another page in the same site, which if it makes the same filter will
     * anyway be in the sitemap, or they will point to a url outside of the current site.
     *
     * @param page
     * @return
     */
    private boolean shouldInclude(@NotNull Page page) {
        return isPublished(page) && !isNoIndex(page) && !isRedirect(page) && !isProtected(page);
    }

    @Override
    public Map<Locale, String> getLanguageAlternatives(Page page) {
        ResourceResolver resolver = page.getContentResource().getResourceResolver();
        Collection<Resource> languageRoots = languageManager.getLanguageRootResources(resolver, page.getPath(), true);

        Resource pageLanguageRoot = languageRoots.stream()
            .filter(languageRoot -> Text.isDescendantOrEqual(languageRoot.getPath(), page.getPath()))
            .findFirst()
            .orElse(null);

        if (pageLanguageRoot == null) {
            return Collections.emptyMap();
        }

        Stream<Resource> alternativesCandidates = languageRoots.stream()
            .filter(resource -> !Text.isDescendantOrEqual(resource.getPath(), page.getPath()));

        if (Text.isDescendant(pageLanguageRoot.getPath(), page.getPath())) {
            String childPath = page.getPath().substring(pageLanguageRoot.getPath().length() + 1);
            alternativesCandidates = alternativesCandidates.map(resource -> resource.getChild(childPath));
        }

        Map<Locale, String> alternatives = new HashMap<>(languageRoots.size());
        for (Resource resource : (Iterable<Resource>) alternativesCandidates.filter(Objects::nonNull)::iterator) {
            Locale key = languageManager.getLanguage(resource, true);
            String location = externalizer.externalize(resource);
            if (key != null && location != null && shouldInclude(resource)) {
                alternatives.putIfAbsent(key, location);
            }
        }
        return Collections.unmodifiableMap(alternatives);
    }

    @Override
    public boolean isPublished(@NotNull Page page) {
        if (slingSettingsService.getRunModes().contains("publish")) {
            // for publishers, pages are always published
            return true;
        }

        ReplicationStatus replicationStatus = page.getContentResource().adaptTo(ReplicationStatus.class);
        return replicationStatus != null && replicationStatus.getLastReplicationAction() == ReplicationActionType.ACTIVATE;
    }

    @Override
    public boolean isNoIndex(@NotNull Page page) {
        return false;
    }

    @Override
    public boolean isRedirect(@NotNull Page page) {
        return page.getContentResource().getValueMap().get(PageImpl.PN_REDIRECT_TARGET, String.class) != null;
    }

    @Override
    public boolean isProtected(@NotNull Page page) {
        return Optional.ofNullable(page.adaptTo(Resource.class)).map(this::isProtected).orElse(Boolean.FALSE);
    }

    @Override
    public boolean isProtected(@NotNull Resource resource) {
        return Optional.of(resource)
            .map(Resource::getValueMap)
            .map(properties -> properties.get(JcrConstants.JCR_MIXINTYPES, String[].class))
            .map(Arrays::asList)
            .map(mixins -> mixins.contains("granite:AuthenticationRequired"))
            .orElse(Boolean.FALSE);
    }
}
