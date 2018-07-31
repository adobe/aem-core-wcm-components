/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.Utils;
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

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Teaser.class, ComponentExporter.class}, resourceType = TeaserImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME , extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class TeaserImpl extends AbstractImageDelegatingModel implements Teaser {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeaserImpl.class);

    public final static String RESOURCE_TYPE = "core/wcm/components/teaser/v1/teaser";

    private String title;
    private String description;
    private String linkURL;
    private String titleType;
    private boolean actionsEnabled = false;
    private boolean titleHidden = false;
    private boolean descriptionHidden = false;
    private boolean imageLinkHidden = false;
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

    @ScriptVariable
    private ValueMap properties;

    @Inject
    private Resource resource;

    @ScriptVariable
    private PageManager pageManager;

    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @JsonIgnore
    protected Style currentStyle;

    @Self
    private SlingHttpServletRequest request;

    private Page targetPage;

    @PostConstruct
    private void initModel() {
        actionsEnabled = properties.get(Teaser.PN_ACTIONS_ENABLED, actionsEnabled);

        populateStyleProperties();

        titleFromPage = properties.get(Teaser.PN_TITLE_FROM_PAGE, titleFromPage);
        descriptionFromPage = properties.get(Teaser.PN_DESCRIPTION_FROM_PAGE, descriptionFromPage);
        linkURL = properties.get(ImageResource.PN_LINK_URL, String.class);

        if (actionsEnabled) {
            hiddenImageResourceProperties.add(ImageResource.PN_LINK_URL);
            linkURL = null;
            populateActions();
            if (actions.size() > 0) {
                ListItem firstAction = actions.get(0);
                if (firstAction != null) {
                    targetPage = pageManager.getPage(firstAction.getPath());
                }
            }
        } else {
            targetPage = pageManager.getPage(linkURL);
        }

        if (titleHidden) {
            title = null;
        } else {
            title = properties.get(JcrConstants.JCR_TITLE, String.class);
            if (titleFromPage) {
                if (targetPage != null) {
                    title = StringUtils.defaultIfEmpty(targetPage.getPageTitle(), targetPage.getTitle());
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
            if (targetPage != null) {
                linkURL = Utils.getURL(request, targetPage);
            }
            setImageResource(component, request.getResource(), hiddenImageResourceProperties);
        }
    }

    private void populateStyleProperties() {
        if (currentStyle != null) {
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
            for(Resource action : actionsNode.getChildren()) {
                actions.add(new ListItem() {

                    private ValueMap properties = action.getValueMap();
                    private String title = properties.get(PN_ACTION_TEXT, String.class);
                    private String url = properties.get(PN_ACTION_LINK, String.class);
                    private Page page = null;
                    {
                        if (url != null && url.startsWith("/")) {
                            page = pageManager.getPage(url);
                        }
                    }

                    @Nullable
                    @Override
                    public String getTitle() {
                        return title;
                    }

                    @Nullable
                    @Override
                    @JsonIgnore
                    public String getPath() {
                        return url;
                    }

                    @Nullable
                    @Override
                    public String getURL() {
                        if (page != null) {
                            return Utils.getURL(request, page);
                        } else {
                            return url;
                        }
                    }
                });
            }
        }
    }

    @Override
    public boolean isActionsEnabled() {
        return actionsEnabled;
    }

    @Override
    public List<ListItem> getActions() {
        return actions;
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

    @Override
    public boolean isImageLinkHidden() {
        return imageLinkHidden;
    }

    @Override
    public String getTitle() {
        return title;
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

    @Nonnull
    @Override
    public String getExportedType() {
        return request.getResource().getResourceType();
    }
}
