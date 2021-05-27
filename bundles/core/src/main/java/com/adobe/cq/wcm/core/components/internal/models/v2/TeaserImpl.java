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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.Teaser;
import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import com.day.cq.commons.ImageResource;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Teaser.class, ComponentExporter.class}, resourceType = TeaserImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME , extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class TeaserImpl extends com.adobe.cq.wcm.core.components.internal.models.v1.TeaserImpl {

    public final static String RESOURCE_TYPE = "core/wcm/components/teaser/v2/teaser";

    /**
     * The current component.
     */
    @ScriptVariable
    private Component component;

    /**
     * The current resource.
     */
    @Inject
    private Resource resource;

    /**
     * Flag indicating if the image should be inherited from the target page.
     */
    private boolean imageFromPage = false;

    /**
     * List of properties that should be inherited when delegating to the featured image of the page.
     */
    private Map<String, String> inheritedProperties = new HashMap<>();

    /**
     * Initialize the model.
     */
    @PostConstruct
    protected void initModel() {
        super.initModel();
        ValueMap properties = resource.getValueMap();
        imageFromPage = properties.get(Teaser.PN_IMAGE_FROM_PAGE, imageFromPage);
        if (!this.hasImage() && imageFromPage && this.getTargetPage().isPresent()) {
            Page targetPage = this.getTargetPage().get();
            Resource featuredImageResource = ComponentUtils.getFeaturedImage(targetPage);
            String linkURL = properties.get(ImageResource.PN_LINK_URL, String.class);
            if (StringUtils.isNotEmpty(linkURL)) {
                // make the featured image inherit following properties from the teaser node
                inheritedProperties.put(ImageResource.PN_LINK_URL, linkURL);
            }
            this.setImageResource(component, featuredImageResource, hiddenImageResourceProperties, inheritedProperties);
        }
    }

    @Override
    @Nullable
    public Link getLink() {
        return link.orElse(null);
    }

    @Override
    @JsonIgnore
    @Deprecated
    public String getLinkURL() {
        return super.getLinkURL();
    }

    protected Action newAction(Resource actionRes, Component component) {
        return new Action(actionRes, getId(), component);
    }

    public class Action extends com.adobe.cq.wcm.core.components.internal.models.v1.TeaserImpl.Action {

        public Action(@NotNull final Resource actionRes, final String parentId, Component component) {
            super(actionRes, parentId, component);
        }

        @Override
        @JsonIgnore(false)
        @Nullable
        public Link getLink() {
            return super.getLink();
        }

        @Nullable
        @Override
        @JsonIgnore
        @Deprecated
        public String getURL() {
            return super.getURL();
        }

    }

}
