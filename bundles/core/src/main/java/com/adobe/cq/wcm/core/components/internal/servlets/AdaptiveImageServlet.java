/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.adobe.cq.wcm.core.components.internal.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.mime.MimeTypeService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.models.impl.v1.ImageImpl;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.day.cq.wcm.commons.AbstractImageServlet;
import com.day.cq.wcm.foundation.Image;
import com.day.image.Layer;

@Component(
        service = Servlet.class,
        configurationPid = "com.adobe.cq.wcm.core.components.internal.servlets.AdaptiveImageServlet",
        property = {
                "sling.servlet.selectors=" + AdaptiveImageServlet.DEFAULT_SELECTOR,
                "sling.servlet.resourceTypes=core/wcm/components/image",
                "sling.servlet.resourceTypes=" + ImageImpl.RESOURCE_TYPE,
                "sling.servlet.extensions=jpg",
                "sling.servlet.extensions=jpeg",
                "sling.servlet.extensions=png",
                "sling.servlet.extensions=gif"
        }
)
@Designate(
        ocd = AdaptiveImageServlet.Configuration.class
)
public class AdaptiveImageServlet extends AbstractImageServlet {

    public static final String DEFAULT_SELECTOR = "img";

    private static final Logger LOGGER = LoggerFactory.getLogger(AdaptiveImageServlet.class);
    private static final String DEFAULT_MIME = "image/jpeg";
    private ThreadLocal<Integer> threadLocalWidthSelector = new ThreadLocal<>();
    private int defaultResizeWidth;

    @Reference
    private MimeTypeService mimeTypeService;

    @ObjectClassDefinition(
        name = "AEM Core WCM Components Adaptive Image Servlet Configuration",
        description = "Adaptive Image Servlet configuration options"
    )
    @interface Configuration {

        @AttributeDefinition(description = "In case the requested image contains no width information in the request and the image also " +
                "doesn't have a content policy that defines the allowed rendition widths, then the image processed by this server will be" +
                " resized to this configured width.")
        int defaultResizeWidth() default 1280;

    }

    @Activate
    protected void activate(Configuration configuration) {
        defaultResizeWidth = configuration.defaultResizeWidth();
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        try {
            String[] selectors = request.getRequestPathInfo().getSelectors();
            if (selectors.length != 1 && selectors.length != 2) {
                LOGGER.error("Expected 1 or 2 selectors, instead got: {}.", Arrays.toString(selectors));
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            Resource imageResource = request.getResource();
            Image image = new Image(imageResource);
            if (!image.hasContent()) {
                LOGGER.error("The image from {} does not have a valid file reference.", imageResource.getPath());
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            if ("image/gif".equals(getImageType(request.getRequestPathInfo().getExtension()))) {
                if (checkModifiedSince(request, response)) {
                    return;
                } else {
                    response.setContentType("image/gif");
                    try {
                        String fileReference = image.getFileReference();
                        if (StringUtils.isNotEmpty(fileReference)) {
                            String damOriginalRendition = fileReference + "/jcr:content/renditions/original";
                            response.getOutputStream().write(IOUtils.toByteArray(request.getResourceResolver().getResource
                                    (damOriginalRendition).adaptTo(InputStream.class)));
                        } else {
                            response.getOutputStream().write(IOUtils.toByteArray(image.getData().getBinary().getStream()));
                        }

                        return;
                    } catch (Exception e) {
                        LOGGER.error("Cannot write GIF image stream.", e);
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }
                }
            }
            String widthSelector = selectors[selectors.length - 1];
            if (!DEFAULT_SELECTOR.equals(widthSelector)) {
                try {
                    Integer width = Integer.parseInt(widthSelector);
                    boolean isRequestedWidthAllowed = false;
                    for (Integer allowedWidth : getAllowedRenditionWidths(request)) {
                        if (width.equals(allowedWidth)) {
                            isRequestedWidthAllowed = true;
                            break;
                        }
                    }
                    if (isRequestedWidthAllowed) {
                        LOGGER.debug("The image was requested with a {}px width. Resizing.", width);
                        threadLocalWidthSelector.set(width);
                    } else {
                        LOGGER.error("The requested width ({}) is not allowed by the content policy.", width);
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }
                } catch (NumberFormatException e) {
                    LOGGER.error("The requested width ({}) is not a valid Integer.", widthSelector);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            } else {
                List<Integer> allowedRenditionWidths = getAllowedRenditionWidths(request);
                if (!allowedRenditionWidths.isEmpty()) {
                    // resize to the first value of the allowedRenditionWidths
                    int size = allowedRenditionWidths.get(0);
                    LOGGER.debug("The image request contains no width information, but the image's content policy defines at least one " +
                            "allowed width. Will resize the image to the first allowed width - {}px.", size);
                    threadLocalWidthSelector.set(size);
                } else {
                    // resize to the default value
                    LOGGER.debug("The image request contains no width information and there's no information about the allowed widths in " +
                            "the image's content policy. Will resize the image to {}px.", defaultResizeWidth);
                    threadLocalWidthSelector.set(defaultResizeWidth);
                }
            }
            super.doGet(request, response);
        } finally {
            threadLocalWidthSelector.remove();
        }
    }

    @Override
    protected String getImageType(String ext) {
        if (ext == null) {
            return DEFAULT_MIME;
        }
        if ("tiff".equalsIgnoreCase(ext) || "tif".equalsIgnoreCase(ext)) {
            return DEFAULT_MIME;
        }
        return mimeTypeService.getMimeType(ext);
    }

    @Override
    protected Layer createLayer(ImageContext imageContext) throws RepositoryException, IOException {
        Image image = new Image(imageContext.resource);
        Integer width = threadLocalWidthSelector.get();
        if (width == 0) {
            // return the original size image
            return image.getLayer(true, false, true);
        } else {
            Layer layer = image.getLayer(false, false, false);
            image.crop(layer);
            image.rotate(layer);
            int currentWidth = layer.getWidth();
            int currentHeight = layer.getHeight();

            double widthRatio = (double) width / currentWidth;
            int newHeight = (int) (currentHeight * widthRatio);

            layer.resize(width, newHeight);
            return layer;
        }
    }

    /**
     * Returns the list of allowed renditions sizes from this component's content policy. If the component doesn't have a content policy,
     * then the list will be empty. Rendition widths that are not valid {@link Integer} numbers will be ignored.
     *
     * @param request the request identifying the component
     * @return the list of the allowed widths; the list will be <i>empty</i> if the component doesn't have a content policy
     */
    private List<Integer> getAllowedRenditionWidths(@Nonnull SlingHttpServletRequest request) {
        List<Integer> list = new ArrayList<>();
        ResourceResolver resourceResolver = request.getResourceResolver();
        ContentPolicyManager policyManager = resourceResolver.adaptTo(ContentPolicyManager.class);
        ContentPolicy contentPolicy = policyManager.getPolicy(request.getResource());
        if (contentPolicy != null) {
            String[] allowedRenditionWidths = contentPolicy.getProperties()
                    .get(com.adobe.cq.wcm.core.components.models.Image.PN_DESIGN_ALLOWED_RENDITION_WIDTHS, new String[0]);
            for (String width : allowedRenditionWidths) {
                try {
                   list.add(Integer.parseInt(width));
                } catch (NumberFormatException e) {
                    LOGGER.warn("One of the configured widths ({}) from the {} content policy is not a valid Integer.", width,
                            contentPolicy.getPath());
                    return list;
                }
            }
        }
        return list;
    }
}
