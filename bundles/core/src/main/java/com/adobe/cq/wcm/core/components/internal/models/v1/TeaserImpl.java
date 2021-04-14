/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.factory.ModelFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.internal.Heading;
import com.adobe.cq.wcm.core.components.internal.link.LinkHandler;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Teaser;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import com.day.cq.commons.DownloadResource;
import com.day.cq.commons.ImageResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.adobe.cq.wcm.core.components.util.ComponentUtils.ID_SEPARATOR;

/**
 * Teaser model implementation.
 */
@Model(adaptables = SlingHttpServletRequest.class, adapters = {Teaser.class, ComponentExporter.class}, resourceType = TeaserImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME , extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class TeaserImpl extends AbstractImageDelegatingModel implements Teaser {

    /**
     * The resource type.
     */
    public final static String RESOURCE_TYPE = "core/wcm/components/teaser/v1/teaser";

    /**
     * The pre-title text.
     */
    private String pretitle;

    /**
     * The title.
     */
    private String title;

    /**
     * The description.
     */
    private String description;

    /**
     * The title heading level.
     */
    private String titleType;

    /**
     * The target page.
     */
    private Page targetPage;

    /**
     * The image src.
     */
    private String imageSrc;

    /**
     * Flag indicating if CTA actions are enabled.
     */
    private boolean actionsEnabled = false;

    /**
     * Flag indicating if the title should be hidden.
     */
    private boolean titleHidden = false;

    /**
     * Flag indicating if the title type should be hidden.
     */
    private boolean showTitleType = false;

    /**
     * Flag indicating if the description should be hidden.
     */
    private boolean descriptionHidden = false;

    /**
     * Flag indicating if the image should not be linked.
     */
    private boolean imageLinkHidden = false;

    /**
     * Flag indicating if the pre-title should be hidden.
     */
    private boolean pretitleHidden = false;

    /**
     * Flag indicating if the title should not be linked.
     */
    private boolean titleLinkHidden = false;

    /**
     * Flag indicating if the title should be inherited from the target page.
     */
    private boolean titleFromPage = false;

    /**
     * Flag indicating if the description should be inherited from the target page.
     */
    private boolean descriptionFromPage = false;

    /**
     * List of CTA actions.
     */
    private List<Action> actions;

    /**
     * List of properties that should be suppressed on image delegation.
     */
    private final List<String> hiddenImageResourceProperties = new ArrayList<String>() {{
        add(JcrConstants.JCR_TITLE);
        add(JcrConstants.JCR_DESCRIPTION);
    }};

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
     * The page manager.
     */
    @ScriptVariable
    private PageManager pageManager;

    /**
     * The current style.
     */
    @ScriptVariable
    protected Style currentStyle;

    /**
     * The current request.
     */
    @Self
    private SlingHttpServletRequest request;

    /**
     * The model factory service.
     */
    @OSGiService
    private ModelFactory modelFactory;

    @Self
    private LinkHandler linkHandler;
    protected Optional<Link> link;

    /**
     * Initialize the model.
     */
    @PostConstruct
    private void initModel() {
        ValueMap properties = resource.getValueMap();

        pretitleHidden = currentStyle.get(Teaser.PN_PRETITLE_HIDDEN, pretitleHidden);
        titleHidden = currentStyle.get(Teaser.PN_TITLE_HIDDEN, titleHidden);
        descriptionHidden = currentStyle.get(Teaser.PN_DESCRIPTION_HIDDEN, descriptionHidden);
        titleType = currentStyle.get(Teaser.PN_TITLE_TYPE, titleType);
        showTitleType = currentStyle.get(Teaser.PN_SHOW_TITLE_TYPE, showTitleType);
        imageLinkHidden = currentStyle.get(Teaser.PN_IMAGE_LINK_HIDDEN, imageLinkHidden);
        titleLinkHidden = currentStyle.get(Teaser.PN_TITLE_LINK_HIDDEN, titleLinkHidden);
        if (imageLinkHidden) {
            hiddenImageResourceProperties.add(Link.PN_LINK_URL);
        }
        actionsEnabled = !currentStyle.get(Teaser.PN_ACTIONS_DISABLED, !properties.get(Teaser.PN_ACTIONS_ENABLED, actionsEnabled));

        titleFromPage = properties.get(Teaser.PN_TITLE_FROM_PAGE, titleFromPage);
        descriptionFromPage = properties.get(Teaser.PN_DESCRIPTION_FROM_PAGE, descriptionFromPage);

        if (this.hasImage()) {
            this.setImageResource(component, request.getResource(), hiddenImageResourceProperties);
        }
        // use the target page as the link if it exists
        link = this.getTargetPage()
                .map(page -> Optional.of(linkHandler.getLink(page).orElse(null)))
                .orElseGet(() -> {
                    // target page doesn't exist
                    if (this.isActionsEnabled()) {
                        return this.getActions().stream().findFirst()
                                .map(action -> Optional.ofNullable(linkHandler.getLink(action.getURL(), null).orElse(null)))
                                .orElse(Optional.empty());
                    } else {
                        // use the property value if actions are not enabled
                        return Optional.ofNullable(linkHandler.getLink(resource, Link.PN_LINK_URL).orElse(null));
                    }
                });
    }

    /**
     * Check if the teaser has an image.
     *
     * The teaser has an image if the `{@value DownloadResource#PN_REFERENCE}` property is set and the value
     * resolves to a resource; or if the `{@value DownloadResource#NN_FILE} child resource exists.
     *
     * @return True if the teaser has an image, false if it does not.
     */
    private boolean hasImage() {
        return Optional.ofNullable(this.resource.getValueMap().get(DownloadResource.PN_REFERENCE, String.class))
            .map(request.getResourceResolver()::getResource)
            .orElseGet(() -> request.getResource().getChild(DownloadResource.NN_FILE)) != null;
    }

    protected ListItem newAction(Resource actionRes, Component component) {
        return new Action(actionRes, getId(), component);
    }

    @Override
    public boolean isActionsEnabled() {
        return actionsEnabled;
    }

    /**
     * Get the target page.
     *
     * If actions are enabled then the target page is the first action's page.
     * If actions are disabled then the target page is the page located at `{@value ImageResource#PN_LINK_URL}`.
     *
     * @return The target page if it exists, or empty if not.
     */
    @NotNull
    private Optional<Page> getTargetPage() {
        if (this.targetPage == null) {
            if (this.isActionsEnabled()) {
                this.targetPage = this.getTeaserActions().stream().findFirst().flatMap(Action::getCtaPage).orElse(null);
            } else {
                this.targetPage = Optional.ofNullable(this.resource.getValueMap().get(ImageResource.PN_LINK_URL, String.class))
                    .map(this.pageManager::getPage)
                    .orElse(null);
            }
        }
        return Optional.ofNullable(this.targetPage);
    }

    /**
     * Get the list of teaser actions.
     *
     * @return List of teaser actions.
     */
    @NotNull
    private List<Action> getTeaserActions() {
        if (this.actions == null) {
            this.actions = Optional.ofNullable(this.isActionsEnabled() ? this.resource.getChild(Teaser.NN_ACTIONS) : null)
                .map(Resource::getChildren)
                .map(Iterable::spliterator)
                .map(s -> StreamSupport.stream(s, false))
                .orElseGet(Stream::empty)
                .map(action -> new Action(action, this.getId(), component))
                .collect(Collectors.toList());
        }
        return this.actions;
    }

    @Override
    public List<ListItem> getActions() {
        return Collections.unmodifiableList(this.getTeaserActions());
    }

    @Override
    public String getLinkURL() {
        return link.map(Link::getURL).orElse(null);
    }

    /**
     * Get the image path.
     *
     * Note: This method exists only for JSON model.
     *
     * @return The image src path if it exists, null if it does not.
     */
    @JsonProperty(value = "imagePath")
    @Nullable
    public String getImagePath() {
        if (imageSrc == null) {
            this.imageSrc = Optional.ofNullable(this.getImageResource())
                .map(imageResource -> this.modelFactory.getModelFromWrappedRequest(this.request, imageResource, Image.class))
                .map(Image::getSrc)
                .orElse(null);
        }
        return this.imageSrc;
    }

    @Override
    public boolean isImageLinkHidden() {
        return imageLinkHidden;
    }

    @Override
    public String getTitle() {
        if (this.title == null && !this.titleHidden) {
            if (this.titleFromPage) {
                this.title = this.getTargetPage()
                    .map(tp -> StringUtils.defaultIfEmpty(tp.getPageTitle(), tp.getTitle()))
                    .orElseGet(() -> this.getTeaserActions().stream().findFirst()
                        .map(Action::getTitle)
                        .orElse(null));
            } else {
                this.title = this.resource.getValueMap().get(JcrConstants.JCR_TITLE, String.class);
            }
        }
        return title;
    }

    @Override
    public String getPretitle() {
        if (this.pretitle == null && !pretitleHidden) {
            this.pretitle = this.resource.getValueMap().get("pretitle", String.class);
        }
        return pretitle;
    }

    @Override
    public boolean isTitleLinkHidden() {
        return titleLinkHidden;
    }

    @Override
    public String getDescription() {
        if (this.description == null && !this.descriptionHidden) {
            if (this.descriptionFromPage) {
                this.description = this.getTargetPage().map(Page::getDescription).orElse(null);
            } else {
                this.description = this.resource.getValueMap().get(JcrConstants.JCR_DESCRIPTION, String.class);
            }
        }
        return this.description;
    }

    @Override
    public String getTitleType() {
        if (showTitleType) {
            titleType = resource.getValueMap().get(Teaser.PN_TITLE_TYPE, titleType);
        }

        Heading heading = Heading.getHeading(titleType);
        if (heading != null) {
            return heading.getElement();
        }
        return null;
    }

    @NotNull
    @Override
    public String getExportedType() {
        return request.getResource().getResourceType();
    }

    @NotNull
    @Override
    protected ComponentData getComponentData() {
        return DataLayerBuilder.extending(super.getComponentData()).asComponent()
            .withTitle(this::getTitle)
            .withLinkUrl(this::getLinkURL)
            .withDescription(this::getDescription)
            .build();
    }


    /**
     * Teaser CTA.
     */
    @JsonIgnoreProperties({"path", "description", "lastModified", "name"})
    public class Action extends AbstractListItemImpl implements ListItem {

        /**
         * ID prefix.
         */
        private static final String CTA_ID_PREFIX = "cta";


        /**
         * The resource for this CTA.
         */
        @NotNull
        private final Resource ctaResource;


        protected final String ctaTitle;
        /**
         * The CTA link.
         */
        protected final Optional<Link<Page>> ctaLink;

        /**
         * The ID of the teaser that contains this action.
         */
        private final String ctaParentId;

        /**
         * The ID of this action.
         */
        private String ctaId;

        /**
         * Create a CTA.
         *
         * @param actionRes The action resource.
         * @param parentId The ID of the containing Teaser.
         */
        private Action(@NotNull final Resource actionRes, final String parentId, Component component) {
            super(parentId, actionRes, component);
            ctaParentId = parentId;
            ctaResource = actionRes;
            ValueMap ctaProperties = actionRes.getValueMap();
            ctaTitle = ctaProperties.get(PN_ACTION_TEXT, String.class);
            ctaLink = Optional.ofNullable(linkHandler.getLink(actionRes, PN_ACTION_LINK).orElse(null));
            if (component != null) {
                this.dataLayerType = component.getResourceType() + "/" + CTA_ID_PREFIX;
            }
        }

        @Override
        @JsonIgnore
        public @NotNull Link getLink() {
            return ctaLink.orElse(null);
        }

        /**
         * Get the referenced page.
         *
         * @return The referenced page if this CTA references a page, empty if not.
         */
        @NotNull
        protected Optional<Page> getCtaPage() {
            return Optional.ofNullable(ctaLink.map(Link::getReference).orElse(null));
        }

        @Nullable
        @Override
        public String getTitle() {
            return ctaTitle;
        }

        @Nullable
        @Override
        public String getPath() {
            Page page = ctaLink.map(Link::getReference).orElse(null);
            if (page != null) {
                return page.getPath();
            }
            else {
                // probably would make more sense to return null when not page is target, but we keep this for backward compatibility
                return ctaLink.map(Link::getURL).orElse(null);
            }
        }

        @Nullable
        @Override
        public String getURL() {
            return ctaLink.map(Link::getURL).orElse(null);
        }

        @Override
        public String getId() {
            if (ctaId == null) {
                ctaId = Optional.ofNullable(ctaResource.getValueMap().get(com.adobe.cq.wcm.core.components.models.Component.PN_ID, String.class))
                    .filter(StringUtils::isNotEmpty)
                    .map(id -> StringUtils.replace(StringUtils.normalizeSpace(StringUtils.trim(id)), " ", ID_SEPARATOR))
                    .orElseGet(() ->
                        ComponentUtils.generateId(StringUtils.join(ctaParentId, ID_SEPARATOR, CTA_ID_PREFIX), this.ctaResource.getPath())
                    );
            }
            return ctaId;
        }
    }
}
