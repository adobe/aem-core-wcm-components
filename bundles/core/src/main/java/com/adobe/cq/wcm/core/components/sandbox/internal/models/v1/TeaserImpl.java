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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractImageDelegatingModel;
import com.adobe.cq.wcm.core.components.sandbox.models.Teaser;
import com.day.cq.commons.DownloadResource;
import com.day.cq.commons.ImageResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.components.Component;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Teaser.class, ComponentExporter.class}, resourceType = TeaserImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME , extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class TeaserImpl extends AbstractImageDelegatingModel implements Teaser {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeaserImpl.class);

    public final static String RESOURCE_TYPE = "core/wcm/sandbox/components/teaser/v1/teaser";

    private String title;
    private String description;
    private String linkURL;
    private static final List<String> hiddenImageResourceProperties = new ArrayList<String>() {{
        add(JcrConstants.JCR_TITLE);
        add(JcrConstants.JCR_DESCRIPTION);
        add(ImageResource.PN_LINK_URL);
    }};

    @ScriptVariable
    private Component component;

    @ScriptVariable
    private ValueMap properties;

    @ScriptVariable
    private PageManager pageManager;

    @Self
    private SlingHttpServletRequest request;

    private Page targetPage;

    @PostConstruct
    private void initModel() {
        title = properties.get(JcrConstants.JCR_TITLE, String.class);
        linkURL = properties.get(ImageResource.PN_LINK_URL, String.class);
        targetPage = pageManager.getPage(linkURL);
        if (StringUtils.isEmpty(title) && targetPage != null) {
            title = StringUtils.defaultIfEmpty(targetPage.getPageTitle(), targetPage.getTitle());
        }
        description = properties.get(JcrConstants.JCR_DESCRIPTION, String.class);
        if (StringUtils.isEmpty(description) && targetPage != null) {
            description = targetPage.getDescription();
        }
        String fileReference = properties.get(DownloadResource.PN_REFERENCE, String.class);
        boolean hasImage = true;
        if (StringUtils.isEmpty(linkURL)) {
            LOGGER.debug("Teaser component from " + request.getResource().getPath() + " requires a link.");
        }
        if (StringUtils.isEmpty(fileReference)) {
            if (request.getResource().getChild(DownloadResource.NN_FILE) == null) {
                LOGGER.debug("Teaser component from " + request.getResource().getPath() + " requires an asset or an image file " +
                        "configured.");
                hasImage = false;
            }
        } else {
            if (request.getResourceResolver().getResource(fileReference) == null) {
                LOGGER.error("Asset " + fileReference + " configured for the teaser component from " + request.getResource().getPath() +
                        " doesn't exist.");
                hasImage = false;
            }
        }
        if (StringUtils.isNotEmpty(linkURL) && hasImage) {
            if (targetPage != null) {
                linkURL = Utils.getURL(request, targetPage);
            }
            setImageResource(component, request.getResource(), hiddenImageResourceProperties);
        }
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

    public String getImagePath() {
        Resource image = getImageResource();
        if (image == null) {
            return null;
        }
        return image.getPath();
    }

    @Nonnull
    @Override
    public String getExportedType() {
        return request.getResource().getResourceType();
    }
}
