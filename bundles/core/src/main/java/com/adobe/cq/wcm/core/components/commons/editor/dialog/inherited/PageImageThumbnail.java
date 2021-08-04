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
package com.adobe.cq.wcm.core.components.commons.editor.dialog.inherited;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.factory.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

/**
 * Defines a Sling Model used by the {@code core/wcm/components/commons/editor/dialog/pageimagethumbnail/v1/pageimagethumbnail} dialog widget
 * that displays a thumbnail of the featured image of either the linked page if a linkURL is available or of the page
 * that contains the component.
 *
 * @since com.adobe.cq.wcm.core.components.commons.editor.dialog 1.0.0
 */
@Model(
        adaptables = SlingHttpServletRequest.class
)
public class PageImageThumbnail {

    private final static Logger log = LoggerFactory.getLogger(PageImageThumbnail.class);

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private ResourceResolver resourceResolver;

    @Inject
    private ModelFactory modelFactory;

    private String alt;
    private String src;
    private String componentPath;
    private String currentPagePath;
    private String configPath;

    @PostConstruct
    protected void initModel() {
        configPath = request.getRequestPathInfo().getResourcePath();
        componentPath = request.getRequestPathInfo().getSuffix();
        if (StringUtils.isBlank(componentPath)) {
            RequestParameter itemParam = request.getRequestParameter("item");
            if (itemParam == null) {
                log.error("Suffix and 'item' param are blank");
                return;
            }
            componentPath = itemParam.getString();
        }

        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (pageManager == null) {
            log.error("pagemanager is null");
            return;
        }

        Page targetPage = null;
        Resource component = resourceResolver.getResource(componentPath);
        if (component == null) {
            log.error("the component at {} does not exist", componentPath);
            return;
        }
        Page currentPage = pageManager.getContainingPage(component);
        if (currentPage != null) {
            currentPagePath = currentPage.getPath();
        }
        RequestParameter linkURLParam = request.getRequestParameter(Link.PN_LINK_URL);
        String linkURL = null;
        if (linkURLParam != null) {
            linkURL = linkURLParam.getString();
        } else {
            // get the linkURL property defined in the repository for the component
            ValueMap properties = component.getValueMap();
            linkURL = properties.get(Link.PN_LINK_URL, String.class);
        }

        if (StringUtils.isNotEmpty(linkURL)) {
            targetPage = pageManager.getPage(linkURL);
        } else {
            targetPage = currentPage;
        }

        if (targetPage == null) {
            log.error("page is null");
            return;
        }

        Resource featuredImage = ComponentUtils.getFeaturedImage(targetPage);
        if (featuredImage == null) {
            log.error("the featured image is null");
            return;
        }

        Image imageModel = modelFactory.getModelFromWrappedRequest(request, featuredImage, Image.class);
        if (imageModel == null) {
            log.error("the image model is null");
            return;
        }

        this.alt = imageModel.getAlt();
        this.src = imageModel.getSrc();
    }


    /**
     * Returns the alternative text of the featured image of either the linked page if a linkURL is available or of
     * the page that contains the component.
     *
     * @return the alternative text of the page image
     */
    public String getAlt() {
        return alt;
    }

    /**
     * Returns the src text of the featured image of either the linked page if a linkURL is available or of
     * the page that contains the component.
     *
     * @return the alternative text of the page image
     */
    public String getSrc() {
        return src;
    }

    /**
     * Returns the component path.
     *
     * @return the component path
     */
    public String getComponentPath() {
        return componentPath;
    }

    /**
     * Returns the configuration path of the widget.
     *
     * @return the configuration path
     */
    public String getConfigPath() {
        return configPath;
    }

    /**
     * Returns the path of the page containing the component.
     *
     * @return the path of the page containing the component
     */
    public String getCurrentPagePath() {
        return currentPagePath;
    }

}
