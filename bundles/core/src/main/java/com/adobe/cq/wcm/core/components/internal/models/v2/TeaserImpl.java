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

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.models.Teaser;
import com.day.cq.commons.DownloadResource;
import com.day.cq.commons.ImageResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.foundation.Image;
import com.day.text.Text;
import com.fasterxml.jackson.annotation.JsonIgnore;

import static com.adobe.cq.wcm.core.components.util.ComponentUtils.ID_SEPARATOR;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Teaser.class, ComponentExporter.class}, resourceType = TeaserImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME , extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class TeaserImpl extends com.adobe.cq.wcm.core.components.internal.models.v1.TeaserImpl {

    public final static String RESOURCE_TYPE = "core/wcm/components/teaser/v2/teaser";

    /**
     * Image ID prefix.
     */
    private static final String IMAGE_ID_PREFIX = "image";

    /**
     * The title.
     */
    private String title;

    /**
     * The description.
     */
    private String description;

    @ScriptVariable
    protected Page currentPage;

    @Override
    protected void initProperties() {
        titleFromPage = true;
        descriptionFromPage = true;
        actionsEnabled = true;
        super.initProperties();
    }

    @Override
    protected void initImage() {
        overriddenImageResourceProperties.put(Image.PN_LINK_URL, getTargetPage().map(Page::getPath).orElse(null));
        overriddenImageResourceProperties.put(Teaser.PN_ACTIONS_ENABLED, Boolean.valueOf(actionsEnabled).toString());
        overriddenImageResourceProperties.put(PN_ID, String.join(ID_SEPARATOR, this.getId(), IMAGE_ID_PREFIX));

        if (StringUtils.isNotEmpty(getTitle()) || getTeaserActions().size() > 0) {
            overriddenImageResourceProperties.put(Teaser.PN_IMAGE_LINK_HIDDEN, Boolean.TRUE.toString());
        }
        super.initImage();
    }

    @Override
    protected void initLink() {
        // use the target page as the link if it exists
        link = linkManager.get(resource).withLinkUrlPropertyName(Link.PN_LINK_URL).build();
    }

    @Override
    @Nullable
    public Link getLink() {
        return link.isValid() ? link : null;
    }

    @Override
    @JsonIgnore
    @Deprecated
    public String getLinkURL() {
        return super.getLinkURL();
    }

    @Override
    @NotNull
    protected Optional<Page> getTargetPage() {
        if (this.targetPage == null) {
            String linkURL = resource.getValueMap().get(ImageResource.PN_LINK_URL, String.class);
            if (StringUtils.isNotEmpty(linkURL)) {
                this.targetPage = Optional.ofNullable(this.resource.getValueMap().get(ImageResource.PN_LINK_URL, String.class))
                        .map(this.pageManager::getPage).orElse(null);
            } else if (actionsEnabled && getActions().size() > 0) {
                this.targetPage = getTeaserActions().stream().findFirst()
                        .flatMap(com.adobe.cq.wcm.core.components.internal.models.v1.TeaserImpl.Action::getCtaPage)
                        .orElse(null);
            } else {
                targetPage = currentPage;
            }
        }
        return Optional.ofNullable(this.targetPage);
    }

    @Override
    public String getTitle() {
        if (this.title == null && !this.titleHidden) {
            if (titleFromPage) {
                this.title = this.getTargetPage()
                        .map(tp -> StringUtils.defaultIfEmpty(tp.getPageTitle(), tp.getTitle()))
                        .orElseGet(() -> this.getTeaserActions().stream().findFirst()
                                .map(com.adobe.cq.wcm.core.components.internal.models.v1.TeaserImpl.Action::getTitle)
                                .orElseGet(() -> Optional.ofNullable(getCurrentPage())
                                        .map(cp -> StringUtils.defaultIfEmpty(cp.getPageTitle(), cp.getTitle()))
                                        .orElse(null)));
            } else {
                this.title = this.resource.getValueMap().get(JcrConstants.JCR_TITLE, String.class);
            }
        }
        return title;
    }

    @Override
    public String getDescription() {
        if (this.description == null && !this.descriptionHidden) {
            if (descriptionFromPage) {
                this.description = this.getTargetPage().map(Optional::of).orElseGet(() -> Optional.ofNullable(getCurrentPage()))
                        .map(Page::getDescription)
                        // page properties uses a plain text field - which may contain special chars that need to be escaped in HTML
                        // because the resulting description from the teaser is expected to be HTML produced by the RTE editor
                        .map(Text::escapeXml)
                        .orElse(null);
            } else {
                this.description = this.resource.getValueMap().get(JcrConstants.JCR_DESCRIPTION, String.class);
            }
        }
        return this.description;
    }

    protected boolean hasImage() {
        // As Teaser v2 supports inheritance from the featured image of the page, the current resource is wrapped and
        // augmented with the inherited properties and child resources of the featured image.
        Resource wrappedResource = Utils.getWrappedImageResourceWithInheritance(resource, linkManager, currentStyle, currentPage);
        return Optional.ofNullable(wrappedResource.getValueMap().get(DownloadResource.PN_REFERENCE, String.class))
                .map(request.getResourceResolver()::getResource)
                .orElseGet(() -> wrappedResource.getChild(DownloadResource.NN_FILE)) != null;
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
