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
package com.adobe.cq.wcm.core.components.commons.editor.dialog;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.factory.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

/**
 * Defines a Sling Model used by the {@code core/wcm/components/commons/editor/dialog/pageimagethumbnail/v1/pageimagethumbnail} dialog component.
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

    @PostConstruct
    protected void initModel() {
        String path = request.getRequestPathInfo().getSuffix();
        if (StringUtils.isBlank(path)) {
            RequestParameter itemParam = request.getRequestParameter("item");
            if (itemParam == null) {
                log.error("Suffix and 'item' param are blank");
                return;
            }
            path = itemParam.getString();
        }

        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (pageManager == null) {
            log.error("pagemanager is null");
            return;
        }

        Resource component = resourceResolver.getResource(path);
        Page containingPage = pageManager.getContainingPage(component);
        if (containingPage == null) {
            log.error("page is null");
            return;
        }

        Resource featuredImage = ComponentUtils.getFeaturedImage(containingPage);
        if (featuredImage == null) {
            log.error("the featured image is null");
            return;
        }

        Image imageModel = modelFactory.getModelFromWrappedRequest(request, featuredImage, Image.class);
        if (imageModel == null) {
            log.error("the image model is null");
            return;
        }

        alt = imageModel.getAlt();
        src = imageModel.getSrc();
    }


    /**
     * Returns the alternative text of the featured image of the page, which the component belongs to.
     *
     * @return the alternative text of the page image
     */
    public String getAlt() {
        return alt;
    }

    /**
     * Returns the src attribute of the featured image of the page, which the component belongs to.
     *
     * @return the alternative text of the page image
     */
    public String getSrc() {
        return src;
    }

}
