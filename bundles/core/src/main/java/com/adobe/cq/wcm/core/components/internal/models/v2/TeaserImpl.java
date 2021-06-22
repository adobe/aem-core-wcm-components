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

import java.util.Collections;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.internal.Heading;
import com.adobe.cq.wcm.core.components.internal.resource.CoreResourceWrapper;
import com.adobe.cq.wcm.core.components.models.Teaser;
import com.adobe.cq.wcm.core.components.models.Title;
import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import com.day.cq.commons.ImageResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableMap;

import static com.adobe.cq.wcm.core.components.models.List.PN_TEASER_DELEGATE;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Teaser.class, ComponentExporter.class}, resourceType = TeaserImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME , extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class TeaserImpl extends com.adobe.cq.wcm.core.components.internal.models.v1.TeaserImpl {

    public final static String RESOURCE_TYPE = "core/wcm/components/teaser/v2/teaser";
    private static final Logger LOG = LoggerFactory.getLogger(TeaserImpl.class);

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
     * List of properties that should be inherited when delegating to the featured image of the page.
     */
    private Map<String, String> overriddenImageProperties = new HashMap<>();
    private Resource titleResource;

    /**
     * Initialize the model.
     */
    @PostConstruct
    protected void initModel() {
        super.initModel();
        ValueMap properties = resource.getValueMap();
        if (!this.hasImage() && this.getTargetPage().isPresent()) {
            Page targetPage = this.getTargetPage().get();
            Resource featuredImageResource = ComponentUtils.getFeaturedImage(targetPage);
            if (featuredImageResource != null) {
                String linkURL = properties.get(ImageResource.PN_LINK_URL, String.class);
                if (StringUtils.isNotEmpty(linkURL)) {
                    // make the featured image inherit following properties from the teaser node
                    overriddenImageProperties.put(ImageResource.PN_LINK_URL, linkURL);
                }
                this.setImageResource(component, featuredImageResource, hiddenImageResourceProperties, overriddenImageProperties);
            }
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

    @Override
    @JsonIgnore
    public @Nullable Resource getTitleResource() {
        if (component != null && titleResource == null) {
            String delegateResourceType = component.getProperties().get(PN_TITLE_DELEGATE, String.class);
            if (StringUtils.isEmpty(delegateResourceType)) {
                LOG.error("no " + PN_TEASER_DELEGATE + " property set on component " + component.getPath());
            } else {
                titleResource = new CoreResourceWrapper(resource, delegateResourceType, Collections.emptyList(),
                        ImmutableMap.of(JcrConstants.JCR_TITLE, this.getTitle(), Title.PN_DESIGN_DEFAULT_TYPE,
                                StringUtils.defaultIfEmpty(this.getTitleType(), Heading.H2.getElement())));
            }
        }
        return titleResource;
    }

    protected Action newAction(Resource actionRes, Component component) {
        return new Action(actionRes, getId(), component);
    }


    public class Action extends com.adobe.cq.wcm.core.components.internal.models.v1.TeaserImpl.Action {

        private Resource buttonResource;

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

        @Override
        @JsonIgnore
        public @Nullable Resource getButtonResource() {
            if (component != null && buttonResource == null) {
                String delegateResourceType = component.getProperties().get(PN_BUTTON_DELEGATE, String.class);
                if (StringUtils.isEmpty(delegateResourceType)) {
                    LOG.error("no " + PN_BUTTON_DELEGATE + " property set on component " + component.getPath());
                } else {
                    buttonResource = new CoreResourceWrapper(resource, delegateResourceType, Collections.emptyList(),
                            ImmutableMap.of(JcrConstants.JCR_TITLE, this.ctaTitle));
                }
            }
            return buttonResource;
        }
    }


}
