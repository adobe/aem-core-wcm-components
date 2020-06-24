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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.SlingHttpServletRequestWrapper;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.factory.ModelFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Teaser;
import com.day.cq.commons.DownloadResource;
import com.day.cq.commons.ImageResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import static com.adobe.cq.wcm.core.components.internal.Utils.ID_SEPARATOR;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Teaser.class, ComponentExporter.class}, resourceType = TeaserImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME , extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class TeaserImpl extends AbstractImageDelegatingModel implements Teaser {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeaserImpl.class);

    public final static String RESOURCE_TYPE = "core/wcm/components/teaser/v1/teaser";

    private String pretitle;
    private String title;
    private String description;
    private String linkURL;
    private String titleType;
    private boolean actionsEnabled = false;
    private boolean titleHidden = false;
    private boolean descriptionHidden = false;
    private boolean imageLinkHidden = false;
    private boolean pretitleHidden = false;
    private boolean titleLinkHidden = false;
    private boolean titleFromPage = false;
    private boolean descriptionFromPage = false;
    private List<ListItem> actions = new ArrayList<>();
    private final List<String> hiddenImageResourceProperties = new ArrayList<String>() {{
        add(JcrConstants.JCR_TITLE);
        add(JcrConstants.JCR_DESCRIPTION);
    }};

    @ScriptVariable
    private Component component;

    @Inject
    private Resource resource;

    @ScriptVariable
    private PageManager pageManager;

    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @JsonIgnore
    @Nullable
    protected Style currentStyle;

    @Self
    private SlingHttpServletRequest request;

    @OSGiService
    private ModelFactory modelFactory;

    private Page targetPage;
    private Image image;

    @PostConstruct
    private void initModel() {
        ValueMap properties = resource.getValueMap();
        actionsEnabled = properties.get(Teaser.PN_ACTIONS_ENABLED, actionsEnabled);

        populateStyleProperties();

        titleFromPage = properties.get(Teaser.PN_TITLE_FROM_PAGE, titleFromPage);
        descriptionFromPage = properties.get(Teaser.PN_DESCRIPTION_FROM_PAGE, descriptionFromPage);
        linkURL = properties.get(ImageResource.PN_LINK_URL, String.class);

        if (actionsEnabled) {
            hiddenImageResourceProperties.add(ImageResource.PN_LINK_URL);
            linkURL = null;
            populateActions();
            if (!actions.isEmpty()) {
                ListItem firstAction = actions.get(0);
                if (firstAction != null) {
                    targetPage = pageManager.getPage(firstAction.getPath());
                }
            }
        } else {
            targetPage = pageManager.getPage(linkURL);
        }

        if (pretitleHidden) {
            pretitle = null;
        } else {
            pretitle = properties.get("pretitle", String.class);
        }
        if (titleHidden) {
            title = null;
        } else {
            title = properties.get(JcrConstants.JCR_TITLE, String.class);
            if (titleFromPage) {
                if (targetPage != null) {
                    title = StringUtils.defaultIfEmpty(targetPage.getPageTitle(), targetPage.getTitle());
                } else if (actionsEnabled && !actions.isEmpty()) {
                    title = actions.get(0).getTitle();
                    linkURL = actions.get(0).getURL();
                } else {
                    title = null;
                }
            }
        }
        if (descriptionHidden) {
            description = null;
        } else {
            description = properties.get(JcrConstants.JCR_DESCRIPTION, String.class);
            if (descriptionFromPage) {
                if (targetPage != null) {
                    description = targetPage.getDescription();
                } else {
                    description = null;
                }
            }
        }
        String fileReference = properties.get(DownloadResource.PN_REFERENCE, String.class);
        boolean hasImage = true;
        if (StringUtils.isEmpty(linkURL)) {
            LOGGER.debug("Teaser component from " + request.getResource().getPath() + " does not define a link.");
        }
        if (StringUtils.isEmpty(fileReference)) {
            if (request.getResource().getChild(DownloadResource.NN_FILE) == null) {
                LOGGER.debug("Teaser component from " + request.getResource().getPath() + " does not have an asset or an image file " +
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
        if (hasImage) {
            setImageResource(component, request.getResource(), hiddenImageResourceProperties);
        }
        if (targetPage != null) {
            linkURL = Utils.getURL(request, targetPage);
        }
    }

    private void populateStyleProperties() {
        if (currentStyle != null) {
            pretitleHidden = currentStyle.get(Teaser.PN_PRETITLE_HIDDEN, pretitleHidden);
            titleHidden = currentStyle.get(Teaser.PN_TITLE_HIDDEN, titleHidden);
            descriptionHidden = currentStyle.get(Teaser.PN_DESCRIPTION_HIDDEN, descriptionHidden);
            titleType = currentStyle.get(Teaser.PN_TITLE_TYPE, titleType);
            imageLinkHidden = currentStyle.get(Teaser.PN_IMAGE_LINK_HIDDEN, imageLinkHidden);
            titleLinkHidden = currentStyle.get(Teaser.PN_TITLE_LINK_HIDDEN, titleLinkHidden);
            if (imageLinkHidden) {
                hiddenImageResourceProperties.add(ImageResource.PN_LINK_URL);
            }
            if (currentStyle.get(Teaser.PN_ACTIONS_DISABLED, false)) {
                actionsEnabled = false;
            }
        }
    }

    private void populateActions() {
        Resource actionsNode = resource.getChild(Teaser.NN_ACTIONS);
        if (actionsNode != null) {
            for (Resource actionRes : actionsNode.getChildren()) {
                actions.add(new Action(actionRes, this.getId()));
            }
        }
    }

    @Override
    public boolean isActionsEnabled() {
        return actionsEnabled;
    }

    @Override
    public List<ListItem> getActions() {
        return Collections.unmodifiableList(actions);
    }

    @Override
    public String getLinkURL() {
        return linkURL;
    }

    public String getImagePath() {
        if (image != null) {
            return image.getSrc();
        }
        Resource imageResource = getImageResource();
        if (imageResource != null) {
            SlingHttpServletRequestWrapper wrappedRequest = new SlingHttpServletRequestWrapper(request) {
                @NotNull
                @Override
                public Resource getResource() {
                    return imageResource;
                }
            };
            image = (Image) modelFactory.getModelFromRequest(wrappedRequest);
            if (image != null) {
                return image.getSrc();
            }
        }
        return null;
    }

    @Override
    public boolean isImageLinkHidden() {
        return imageLinkHidden;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getPretitle() {
        return pretitle;
    }

    @Override
    public boolean isTitleLinkHidden() {
        return titleLinkHidden;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getTitleType() {
        Utils.Heading heading = Utils.Heading.getHeading(titleType);
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

    /*
     * DataLayerProvider implementation of field getters
     */

    @Override
    public String getDataLayerTitle() {
        return getTitle();
    }

    @Override
    public String getDataLayerLinkUrl() {
        return getLinkURL();
    }

    @Override
    public String getDataLayerDescription() {
        return getDescription();
    }


    @JsonIgnoreProperties({"path", "description", "lastModified", "name"})
    public class Action extends AbstractListItemImpl implements ListItem {

        private static final String CTA_ID_PREFIX = "cta";

        private Resource ctaResource;
        private String ctaTitle;
        private String ctaUrl;
        private String ctaPath;
        private Page ctaPage;
        private String ctaParentId;
        private String ctaId;

        private Action(Resource actionRes, String parentId) {
            super(parentId, actionRes);
            ctaParentId = parentId;
            ctaResource = actionRes;
            ValueMap ctaProperties = actionRes.getValueMap();
            ctaTitle = ctaProperties.get(PN_ACTION_TEXT, String.class);
            ctaUrl = ctaProperties.get(PN_ACTION_LINK, String.class);
            ctaPath = actionRes.getPath();
            if (ctaUrl != null && ctaUrl.startsWith("/")) {
                ctaPage = pageManager.getPage(ctaUrl);
            }
        }

        @Nullable
        @Override
        public String getTitle() {
            return ctaTitle;
        }

        @Nullable
        @Override
        public String getPath() {
            return ctaUrl;
        }

        @Nullable
        @Override
        public String getURL() {
            if (ctaPage != null) {
                return Utils.getURL(request, ctaPage);
            } else {
                return ctaUrl;
            }
        }

        @Nullable
        @Override
        public String getId() {
            if (ctaId == null) {
                if (ctaResource != null) {
                    ValueMap properties = ctaResource.getValueMap();
                    ctaId = properties.get(com.adobe.cq.wcm.core.components.models.Component.PN_ID, String.class);
                }
                if (StringUtils.isEmpty(ctaId)) {
                    String prefix = StringUtils.join(ctaParentId, ID_SEPARATOR, CTA_ID_PREFIX);
                    ctaId = Utils.generateId(prefix, ctaPath);
                } else {
                    ctaId = StringUtils.replace(StringUtils.normalizeSpace(StringUtils.trim(ctaId)), " ", ID_SEPARATOR);
                }
            }
            return ctaId;
        }

        /*
         * DataLayerProvider implementation of field getters
         */

        @Override
        public String getDataLayerLinkUrl() {
            return getURL();
        }

        @Override
        public String getDataLayerTitle() {
            return getTitle();
        }
    }
}
