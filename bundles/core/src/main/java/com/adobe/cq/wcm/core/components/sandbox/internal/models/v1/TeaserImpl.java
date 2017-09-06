/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.sandbox.internal.models.v1;

import java.util.HashMap;
import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceWrapper;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.internal.Constants;
import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.sandbox.models.Teaser;
import com.day.cq.commons.DownloadResource;
import com.day.cq.commons.ImageResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.PageManager;

@Model(adaptables = SlingHttpServletRequest.class, adapters = Teaser.class, resourceType = TeaserImpl.RESOURCE_TYPE)
@Exporter(name = Constants.EXPORTER_NAME, extensions = Constants.EXPORTER_EXTENSION)
public class TeaserImpl implements Teaser {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeaserImpl.class);

    public final static String RESOURCE_TYPE = "core/wcm/sandbox/components/teaser/v1/teaser";

    private String title;
    private String description;
    private String linkURL;
    private String linkText;
    private Resource imageResource;
    private Resource wrappedImageResource;

    @ScriptVariable
    private ValueMap properties;

    @ScriptVariable
    private PageManager pageManager;

    @Self
    private SlingHttpServletRequest request;

    @PostConstruct
    private void initModel() {
        title = properties.get(JcrConstants.JCR_TITLE, String.class);
        description = properties.get(JcrConstants.JCR_DESCRIPTION, String.class);
        linkURL = properties.get(ImageResource.PN_LINK_URL, String.class);
        linkText = properties.get(Teaser.PN_LINK_TEXT, String.class);
        String fileReference = properties.get(DownloadResource.PN_REFERENCE, String.class);
        if (StringUtils.isEmpty(linkURL)) {
            LOGGER.warn("Please provide a link for the teaser component from " + request.getResource().getPath() + ".");
        }
        if (StringUtils.isEmpty(fileReference)) {
            LOGGER.warn("Please provide an asset path for the teaser component from " + request.getResource().getPath() + ".");
        } else {
            imageResource = request.getResourceResolver().getResource(fileReference);
            if (imageResource == null) {
                LOGGER.error("Asset " + fileReference + " configured for the teaser component from " + request.getResource().getPath() +
                        " doesn't exist.");
            }
        }
        linkURL = Utils.getURL(request, pageManager, linkURL);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getLinkURL() {
        return linkURL;
    }

    @Override
    public String getLinkText() {
        return linkText;
    }

    @Override
    public Resource getImageResource() {
        if (wrappedImageResource == null && imageResource != null) {
            wrappedImageResource = new TeaserImageResource(request.getResource());
        }
        return wrappedImageResource;
    }

    private class TeaserImageResource extends ResourceWrapper {

        private ValueMap valueMap;

        TeaserImageResource(@Nonnull Resource resource) {
            super(resource);
            valueMap = new ValueMapDecorator(new HashMap<>(properties));
            valueMap.remove(JcrConstants.JCR_TITLE);
            valueMap.remove(JcrConstants.JCR_DESCRIPTION);
            valueMap.remove(ImageResource.PN_LINK_URL);
        }

        @Override
        public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
            if (type == ValueMap.class) {
                return (AdapterType) valueMap;
            }
            return super.adaptTo(type);
        }

        @Override
        @Nonnull
        public ValueMap getValueMap() {
            return valueMap;
        }
    }
}
