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
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractImageDelegatingModel;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.sandbox.models.Teaser;
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

    public final static String RESOURCE_TYPE = "core/wcm/sandbox/components/teaser/v1/teaser";

    private String title;
    private String description;
    private String linkURL;
    private String titleType;
    private boolean withCTA = false;
    private boolean hideTitle = false;
    private boolean hideDescription = false;
    private boolean hideImageLink = false;
    private boolean hideTitleLink = false;
    private boolean titleValueFromPage = false;
    private boolean descriptionValueFromPage = false;
    private List<ListItem> ctas = new ArrayList<>();
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
        withCTA = properties.get(Teaser.PN_WITH_CTA, withCTA);

        populateStyleProperties();

        titleValueFromPage = properties.get(Teaser.PN_TITLE_VALUE_FROM_PAGE, titleValueFromPage);
        descriptionValueFromPage = properties.get(Teaser.PN_DESCRIPTION_VALUE_FROM_PAGE, descriptionValueFromPage);
        linkURL = properties.get(ImageResource.PN_LINK_URL, String.class);

        if (withCTA) {
            hiddenImageResourceProperties.add(ImageResource.PN_LINK_URL);
            populateCTAs();
            if (ctas.size() > 0) {
                ListItem firstCTA = ctas.get(0);
                linkURL = firstCTA.getPath();
                if (linkURL.startsWith("/")) {
                    linkURL = firstCTA.getPath().substring(0, linkURL.lastIndexOf('.'));
                }
            }
        }

        targetPage = pageManager.getPage(linkURL);

        if (hideTitle) {
            title = null;
        } else {
            title = properties.get(JcrConstants.JCR_TITLE, String.class);
            if (titleValueFromPage) {
                if (targetPage != null) {
                    title = StringUtils.defaultIfEmpty(targetPage.getPageTitle(), targetPage.getTitle());
                } else {
                    title = null;
                }
            }
        }
        if (hideDescription) {
            description = null;
        } else {
            description = properties.get(JcrConstants.JCR_DESCRIPTION, String.class);
            if (descriptionValueFromPage) {
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
        if (hasImage) {
            if (targetPage != null) {
                linkURL = Utils.getURL(request, targetPage);
            }
            setImageResource(component, request.getResource(), hiddenImageResourceProperties);
        }
    }

    private void populateStyleProperties() {
        if (currentStyle != null) {
            hideTitle = currentStyle.get(Teaser.PN_HIDE_TITLE, hideTitle);
            hideDescription = currentStyle.get(Teaser.PN_HIDE_DESCRIPTION, hideDescription);
            titleType = currentStyle.get(Teaser.PN_TITLE_TYPE, titleType);
            hideImageLink = currentStyle.get(Teaser.PN_HIDE_IMAGE_LINK, hideImageLink);
            hideTitleLink = currentStyle.get(Teaser.PN_HIDE_TITLE_LINK, hideTitleLink);
            if (hideImageLink) {
                hiddenImageResourceProperties.add(ImageResource.PN_LINK_URL);
            }
            if (currentStyle.get(Teaser.PN_DISABLE_CTA, false)) {
                withCTA = false;
            }
        }
    }

    private void populateCTAs() {
        Resource ctasNode = resource.getChild(Teaser.NN_CTAS);
        if (ctasNode != null) {
            for(Resource cta : ctasNode.getChildren()) {
                ctas.add(new ListItem() {

                    private ValueMap properties = cta.getValueMap();
                    private String title = properties.get(PN_CTA_TEXT, String.class);
                    private String url = properties.get(PN_CTA_LINK, String.class);
                    {
                        if (url != null && url.startsWith("/")) {
                            Page page = pageManager.getPage(url);
                            if (page != null) {
                                url = Utils.getURL(request, page);
                            }
                        }
                    }

                    @Nullable
                    @Override
                    public String getTitle() {
                        return title;
                    }

                    @Nullable
                    @Override
                    public String getPath() {
                        return url;
                    }
                });
            }
        }
    }

    @Override
    public boolean isWithCTA() {
        return withCTA;
    }

    @Override
    public List<ListItem> getCTAs() {
        return ctas;
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
    public boolean isHideImageLink() {
        return hideImageLink;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public boolean isHideTitleLink() {
        return hideTitleLink;
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
