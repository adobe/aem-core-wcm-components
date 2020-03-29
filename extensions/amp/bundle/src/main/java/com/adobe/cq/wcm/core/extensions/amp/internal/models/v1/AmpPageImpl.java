/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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
package com.adobe.cq.wcm.core.extensions.amp.internal.models.v1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.extensions.amp.internal.AmpUtil;
import com.adobe.cq.wcm.core.extensions.amp.models.AmpPage;
import com.adobe.cq.wcm.core.extensions.amp.services.ClientLibraryAggregatorService;
import com.day.cq.wcm.api.Page;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.adobe.cq.wcm.core.extensions.amp.internal.AmpUtil.AMP_SELECTOR;
import static com.adobe.cq.wcm.core.extensions.amp.internal.AmpUtil.DOT;
import static com.day.cq.wcm.foundation.List.URL_EXTENSION;

@Model(adaptables = SlingHttpServletRequest.class,
    adapters = {AmpPage.class})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class AmpPageImpl implements AmpPage {

    private static final Logger LOG = LoggerFactory.getLogger(AmpPageImpl.class);

    @SlingObject
    private SlingHttpServletRequest request;

    @ScriptVariable
    private Page currentPage;

    @OSGiService
    private ClientLibraryAggregatorService aggregatorService;

    private Map<String, String> pageLinkAttrs;
    private Set<String> headlibIncludes;
    private String ampMode;

    @PostConstruct
    protected void init() {
        ampMode = AmpUtil.getAmpMode(request);
        boolean isAmpSelector = Arrays.asList(request.getRequestPathInfo().getSelectors()).contains(AMP_SELECTOR);
        pageLinkAttrs = new HashMap<>();
        headlibIncludes = new HashSet<>();
        String relValue;
        String hrefValue;
        if (!isAmpSelector && ampMode.equals(AmpUtil.PAIRED_AMP)) {
            relValue = "amphtml";
            hrefValue = currentPage.getPath() + DOT + AMP_SELECTOR + URL_EXTENSION;
        } else {
            relValue = "canonical";
            hrefValue = currentPage.getPath() + URL_EXTENSION;
        }

        pageLinkAttrs.put("rel", relValue);
        pageLinkAttrs.put("href", hrefValue);
        if (isAmpSelector) {
            Set<String> resourceTypes = AmpUtil.getResourceTypes(currentPage.getContentResource(),
                aggregatorService.getResourceTypeRegex(), new HashSet<>());

            try (ResourceResolver resolver = aggregatorService.getClientlibResourceResolver()) {
                AmpUtil.getTemplateResourceTypes(currentPage, aggregatorService.getResourceTypeRegex(), resolver,
                    resourceTypes);
                // Last part of any headlib path.
                String headLibRelPath = "/" + aggregatorService.getHeadlibName();

                // Iterate through each resource type and read its AMP headlib.
                for (String resourceType : resourceTypes) {

                    // Resolve the resource type's AMP headlib.
                    Resource headLibResource;
                    String headLibPath = resourceType + headLibRelPath;
                    headLibResource = AmpUtil.resolveResource(resolver, headLibPath);
                    if (headLibResource == null) {
                        LOG.trace("No custom headlib for resource type {}.", resourceType);

                        // Get headLibResource from resource superType.
                        headLibResource = getHeadlibResourceSuperType(resolver, resourceType, headLibRelPath);
                        if (headLibResource == null) {
                            LOG.trace("No custom headlib for resource superType from resource type {}.", resourceType);
                            continue;
                        }
                    }
                    headlibIncludes.add(headLibResource.getPath());
                }
            } catch (LoginException e) {
                LOG.error("Unable to get the service resource resolver.", e);
            }
        }
    }

    private Resource getHeadlibResourceSuperType(ResourceResolver resolver, String resourceType, String headLibRelPath) {
        Resource resource = AmpUtil.resolveResource(resolver, resourceType);
        if (resource == null) {
            LOG.debug("Can't access resource from resource type {}.", resourceType);
            return null;
        }
        // Get resource superType path from the resource type.
        String superTypePath = resource.getResourceSuperType();
        if (superTypePath == null) {
            LOG.trace("No resource superType from resource type {}.", resourceType);
            return null;
        }
        // Get headLibResource from resource superType.
        Resource headLibResource = AmpUtil.resolveResource(resolver, superTypePath + headLibRelPath);
        // Return next superType or headLibResource.
        return headLibResource == null ?
            getHeadlibResourceSuperType(resolver, superTypePath, headLibRelPath) :
            headLibResource;
    }

    @Override
    public Map<String, String> getPageLinkAttrs() {
        return pageLinkAttrs;
    }

    @Override
    public List<String> getHeadlibIncludes() {
        return new ArrayList<>(headlibIncludes);
    }

    @Override
    public boolean isAmpEnabled() {
        return ampMode.equals(AmpUtil.PAIRED_AMP) || ampMode.equals(AmpUtil.AMP_ONLY);
    }
}
