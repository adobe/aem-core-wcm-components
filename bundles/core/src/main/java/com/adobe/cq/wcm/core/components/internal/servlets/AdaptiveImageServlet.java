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

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Nonnull;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
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
import com.day.cq.commons.DownloadResource;
import com.day.cq.commons.ImageResource;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.api.handler.AssetHandler;
import com.day.cq.dam.api.handler.store.AssetStore;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
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
public class AdaptiveImageServlet extends SlingSafeMethodsServlet {

    public static final String DEFAULT_SELECTOR = "img";
    private static final Logger LOGGER = LoggerFactory.getLogger(AdaptiveImageServlet.class);
    private static final String DEFAULT_MIME = "image/jpeg";
    private int defaultResizeWidth;

    @Reference
    private MimeTypeService mimeTypeService;

    @Reference
    private AssetStore assetStore;

    @ObjectClassDefinition(
            name = "AEM Core WCM Components Adaptive Image Servlet Configuration",
            description = "Adaptive Image Servlet configuration options"
    )
    @interface Configuration {

        @AttributeDefinition(
                description = "In case the requested image contains no width information in the request and the image also " +
                        "doesn't have a content policy that defines the allowed rendition widths, then the image processed by this server will be" +
                        " resized to this configured width, for images whose width is larger than this value."
        )
        int defaultResizeWidth() default 1280;

    }

    @Activate
    protected void activate(Configuration configuration) {
        defaultResizeWidth = configuration.defaultResizeWidth();
    }

    @Override
    protected void doGet(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response) throws ServletException,
            IOException {
        String[] selectors = request.getRequestPathInfo().getSelectors();
        if (selectors.length != 1 && selectors.length != 2) {
            LOGGER.error("Expected 1 or 2 selectors, instead got: {}.", Arrays.toString(selectors));
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Resource image = request.getResource();
        ValueMap imageProperties = image.getValueMap();
        if (!handleIfModifiedSinceHeader(request, response, imageProperties)) {
            if (!hasContent(image)) {
                LOGGER.error("The image from {} does not have a valid file reference.", image.getPath());
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            int resizeWidth = defaultResizeWidth;
            String widthSelector = selectors[selectors.length - 1];
            if (!DEFAULT_SELECTOR.equals(widthSelector)) {
                try {
                    Integer width = Integer.parseInt(widthSelector);
                    boolean isRequestedWidthAllowed = false;
                    for (Integer allowedWidth : getAllowedRenditionWidths(request)) {
                        if (width.equals(allowedWidth)) {
                            isRequestedWidthAllowed = true;
                            resizeWidth = width;
                            break;
                        }
                    }
                    if (isRequestedWidthAllowed) {
                        resizeAndStream(request, response, image, imageProperties, resizeWidth);
                    } else {
                        LOGGER.error("The requested width ({}) is not allowed by the content policy.", width);
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    }
                } catch (NumberFormatException e) {
                    LOGGER.error("The requested width ({}) is not a valid Integer.", widthSelector);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            } else {
                List<Integer> allowedRenditionWidths = getAllowedRenditionWidths(request);
                if (!allowedRenditionWidths.isEmpty()) {
                    // resize to the first value of the allowedRenditionWidths
                    int size = allowedRenditionWidths.get(0);
                    LOGGER.debug(
                            "The image request contains no width information, but the image's content policy defines at least one " +
                                    "allowed width. Will resize the image to the first allowed width - {}px.", size);
                    resizeAndStream(request, response, image, imageProperties, size);

                } else {
                    // resize to the default value
                    LOGGER.debug(
                            "The image request contains no width information and there's no information about the allowed widths in " +
                                    "the image's content policy. Will resize the image to {}px.", defaultResizeWidth);
                    resizeAndStream(request, response, image, imageProperties, defaultResizeWidth);
                }
            }
        }
    }

    /**
     * Calling this method will copy the image's bytes into the response's output stream, after performing all the needed transformations
     * on the requested image.
     *
     * @param request         the request
     * @param response        the response
     * @param image           the image component resource
     * @param imageProperties the image properties
     * @param resizeWidth     the width to which the image has to be resized
     */
    private void resizeAndStream(SlingHttpServletRequest request, SlingHttpServletResponse response, Resource image, ValueMap
            imageProperties, int resizeWidth) throws IOException {
        if (resizeWidth < 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String fileReference = imageProperties.get(DownloadResource.PN_REFERENCE, String.class);
        String imageType = getImageType(request.getRequestPathInfo().getExtension());
        String extension = mimeTypeService.getExtension(imageType);
        Asset asset = null;
        Resource imageFile = null;
        if (StringUtils.isNotEmpty(fileReference)) {
            // the image is coming from DAM
            final Resource assetResource = request.getResourceResolver().getResource(fileReference);
            if (assetResource == null) {
                LOGGER.error(String.format("Unable to find resource %s used by image %s.", fileReference, image.getPath()));
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            asset = assetResource.adaptTo(Asset.class);
            if (asset == null) {
                LOGGER.error(String.format("Unable to adapt resource %s used by image %s to an asset.", fileReference, image.getPath()));
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            if ("gif".equalsIgnoreCase(extension)) {
                LOGGER.debug("GIF asset detected; will render the original rendition.");
                stream(response, asset.getOriginal().getStream(), imageType);
                return;
            }
        } else {
            imageFile = image.getChild(DownloadResource.NN_FILE);
            if ("gif".equalsIgnoreCase(extension)) {
                LOGGER.debug("GIF file detected; will render the original file.");
                stream(response, imageFile.adaptTo(InputStream.class), imageType);
                return;
            }
        }
        if (asset != null) {
            int rotationAngle = getRotation(image, imageProperties);
            Rectangle rectangle = getCropRect(image, imageProperties);
            if (rotationAngle != 0 || rectangle != null || resizeWidth > 0) {
                int originalWidth = getDimension(asset.getMetadataValue(DamConstants.TIFF_IMAGEWIDTH));
                int originalHeight = getDimension(asset.getMetadataValue(DamConstants.TIFF_IMAGELENGTH));
                AssetHandler assetHandler = assetStore.getAssetHandler(imageType);
                Layer layer = null;
                boolean appliedRotationOrCropping = false;
                if (rectangle != null) {
                    double scaling;
                    Rendition webRendition = getAWebRendition(asset);
                    double renditionWidth = 1280D;
                    if (webRendition != null) {
                        try {
                            renditionWidth = Double.parseDouble(webRendition.getName().split("\\.")[2]);
                            LOGGER.debug("Found rendition {} with width {}px; assuming the cropping rectangle was calculated using this " +
                                    "rendition.", webRendition.getPath(), renditionWidth);
                        } catch (NumberFormatException e) {
                            LOGGER.warn("Cannot determine rendition width for {}. Will fallback to 1280px.", webRendition.getPath());
                        }
                    } else {
                        renditionWidth = originalWidth;
                    }
                    if (originalWidth > renditionWidth) {
                        scaling = (double) originalWidth / renditionWidth;
                    } else {
                        scaling = renditionWidth / originalWidth;
                    }
                    layer = new Layer(assetHandler.getImage(asset.getOriginal()));
                    if (Math.abs(scaling - 1.0D) != 0) {
                        Rectangle scaledRectangle = new Rectangle(
                                (int) (rectangle.x * scaling),
                                (int) (rectangle.y * scaling),
                                (int) (rectangle.getWidth() * scaling),
                                (int) (rectangle.getHeight() * scaling)
                        );
                        layer.crop(scaledRectangle);
                    } else {
                        layer.crop(rectangle);
                    }
                    appliedRotationOrCropping = true;
                }
                if (rotationAngle != 0) {
                    if (layer == null) {
                        layer = new Layer(assetHandler.getImage(asset.getOriginal()));
                    }
                    layer.rotate(rotationAngle);
                    LOGGER.debug("Applied rotation transformation ({} degrees).", rotationAngle);
                    appliedRotationOrCropping = true;
                }
                if (!appliedRotationOrCropping) {
                    Rendition rendition = asset.getRendition(String.format(DamConstants.PREFIX_ASSET_WEB + ".%d.%d.%s", resizeWidth,
                            resizeWidth, extension));
                    if (rendition != null) {
                        LOGGER.debug("Found rendition {} with a width equal to the resize width ({}px); rendering.", rendition.getPath(),
                                resizeWidth);
                        stream(response, rendition.getStream(), imageType);
                    } else {
                        int resizeHeight = calculateResizeHeight(originalWidth, originalHeight, resizeWidth);
                        if (resizeHeight > 0 && resizeHeight != originalHeight) {
                            layer = new Layer(assetHandler.getImage(asset.getOriginal()));
                            layer.resize(resizeWidth, resizeHeight);
                            response.setContentType(imageType);
                            LOGGER.debug("Resizing asset {} to requested width of {}px; rendering.", asset.getPath(), resizeWidth);
                            layer.write(imageType, 1.0, response.getOutputStream());
                        } else {
                            LOGGER.debug("Rendering the original asset {} since its width ({}px) is either smaller than the requested " +
                                    "width ({}px) or since no resize is needed.", asset.getPath(), originalWidth, resizeWidth);
                            stream(response, asset.getOriginal().getStream(), imageType);
                        }
                    }
                } else {
                    resizeAndStreamLayer(response, layer, imageType, resizeWidth);
                }
            } else {
                LOGGER.debug("No need to perform any processing on asset {}; rendering.", asset.getPath());
                stream(response, asset.getOriginal().getStream(), imageType);
            }
        } else if (imageFile != null) {
            InputStream is = imageFile.adaptTo(InputStream.class);
            int rotationAngle = getRotation(image, imageProperties);
            Rectangle rectangle = getCropRect(image, imageProperties);
            if (rotationAngle != 0 || rectangle != null || resizeWidth > 0) {
                Layer layer = null;
                if (rectangle != null) {
                    layer = new Layer(is);
                    layer.crop(rectangle);
                    LOGGER.debug("Applied cropping transformation.");
                }
                if (rotationAngle != 0) {
                    if (layer == null) {
                        layer = new Layer(is);
                    }
                    layer.rotate(rotationAngle);
                    LOGGER.debug("Applied rotation transformation ({} degrees).", rotationAngle);
                }
                if (layer == null) {
                    layer = new Layer(is);
                }
                resizeAndStreamLayer(response, layer, imageType, resizeWidth);
            } else {
                LOGGER.debug("No need to perform any processing on file {}; rendering.", imageFile.getPath());
                stream(response, is, imageType);
            }
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * Given a {@link Layer}, this method will attempt to resize it proportionally given the supplied {@code resizeWidth}. If the resize
     * operation would result in up-scaling, then the layer is rendered without any resize operation applied.
     *
     * @param response    the response
     * @param layer       the layer
     * @param imageType   the mime type of the image represented by the {@code layer}
     * @param resizeWidth the resize width
     * @throws IOException if the streaming of the {@link Layer} into the response's output stream cannot be performed
     */
    private void resizeAndStreamLayer(SlingHttpServletResponse response, Layer layer, String imageType, int resizeWidth)
            throws IOException {
        int width = layer.getWidth();
        int height = layer.getHeight();
        int resizeHeight = calculateResizeHeight(width, height, resizeWidth);
        if (resizeHeight > 0) {
            layer.resize(resizeWidth, resizeHeight);
            response.setContentType(imageType);
            LOGGER.debug("Resizing processed (cropped and/or rotated) layer from its current width of {}px to {}px.", width, resizeWidth);
            layer.write(imageType, 1.0, response.getOutputStream());
        } else {
            response.setContentType(imageType);
            LOGGER.debug("No need to resize processed (cropped and/or rotated) layer since it would lead to upscaling; rendering.");
            layer.write(imageType, 1.0, response.getOutputStream());
        }
    }

    /**
     * Given an {@link Asset}, this method will return the first web {@link Rendition} it finds in the asset's renditions list.
     *
     * @param asset the asset for which to retrieve the web rendition
     * @return the rendition, if found, {@code null} otherwise
     */
    private Rendition getAWebRendition(Asset asset) {
        List<Rendition> renditions = asset.getRenditions();
        for (Rendition rendition : renditions) {
            if (rendition.getName().startsWith(DamConstants.PREFIX_ASSET_WEB)) {
                return rendition;
            }
        }
        return null;
    }

    private void stream(@Nonnull SlingHttpServletResponse response, @Nonnull InputStream inputStream, @Nonnull String contentType)
            throws IOException {
        response.setContentType(contentType);
        IOUtils.copy(inputStream, response.getOutputStream());
        IOUtils.closeQuietly(inputStream);
    }

    /**
     * Retrieves the cropping rectangle, if one is defined for the image.
     *
     * @param image           the image resource
     * @param imageProperties the image's properties
     * @return the cropping rectangle, if one is found, {@code null} otherwise
     */
    private Rectangle getCropRect(Resource image, ValueMap imageProperties) {
        String csv = imageProperties.get(ImageResource.PN_IMAGE_CROP, String.class);
        if (StringUtils.isNotEmpty(csv)) {
            try {
                int ratio = csv.indexOf("/");
                if (ratio >= 0) {
                    // skip ratio
                    csv = csv.substring(0, ratio);
                }
                String[] coords = csv.split(",");
                int x1 = Integer.parseInt(coords[0]);
                int y1 = Integer.parseInt(coords[1]);
                int x2 = Integer.parseInt(coords[2]);
                int y2 = Integer.parseInt(coords[3]);
                return new Rectangle(x1, y1, x2 - x1, y2 - y1);
            } catch (Exception e) {
                LOGGER.warn(String.format("Invalid cropping rectangle %s for image %s", csv, image.getPath()), e);
            }
        }
        return null;
    }

    /**
     * Retrieves the rotation angle for the image, if one is present. Typically this should be a value between 0 and 360.
     *
     * @param image           the image resource
     * @param imageProperties the image's properties
     * @return the rotation angle
     */
    private int getRotation(Resource image, ValueMap imageProperties) {
        String rotationString = imageProperties.get(ImageResource.PN_IMAGE_ROTATE, String.class);
        if (rotationString != null) {
            try {
                return Integer.parseInt(rotationString);
            } catch (NumberFormatException e) {
                LOGGER.warn(String.format("Invalid rotation value %s for image %s. Will return 0.", rotationString, image.getPath()), e);
            }
        }
        return 0;
    }

    /**
     * Given a {@code String} value, this method will try to convert it to an {@code int}.
     *
     * @param stringValue the string value to convert
     * @return the {@code int} representation of the provided string, or 0 if the string cannot be parsed
     */
    private int getDimension(String stringValue) {
        try {
            return Integer.parseInt(stringValue);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Given an asset's width and height, together with a desired resize width, this method will calculate the resize height of the asset.
     *
     * @param assetWidth  the asset's width, in pixels
     * @param assetHeight the asset's height, in pixels
     * @param resizeWidth the resize width, in pixels
     * @return the resize height, in pixels; returns 0 if the resize width is greater than the asset's width
     */
    private int calculateResizeHeight(int assetWidth, int assetHeight, int resizeWidth) {
        if (assetWidth > 0 && assetHeight > 0 && resizeWidth < assetWidth) {
            // we only scale down, otherwise we return the original
            double scaleFactor = (double) resizeWidth / (double) assetWidth;
            return (int) (scaleFactor * assetHeight);
        }
        if (assetWidth > 0 && assetHeight > 0 && resizeWidth == assetWidth) {
            return assetHeight;
        }
        return 0;
    }

    /**
     * Checks if the request contains the {@code If-Modified-Since} header and if the the request's underlying resource has a
     * {@link JcrConstants#JCR_LASTMODIFIED} or a {@link NameConstants#PN_PAGE_LAST_MOD} property. If the properties were modified
     * before the header a 304 is sent, otherwise the response's {@code Last-Modified} header is set.
     *
     * @param request    the request
     * @param response   the response
     * @param properties the underlying resource's properties
     * @return {@code true} if the response was set, {@code false} otherwise
     */
    private boolean handleIfModifiedSinceHeader(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response,
                                                @Nonnull ValueMap
                                                        properties) {
        Calendar lastModified = properties.get(JcrConstants.JCR_LASTMODIFIED, Calendar.class);
        if (lastModified == null) {
            lastModified = properties.get(NameConstants.PN_PAGE_LAST_MOD, Calendar.class);
        }
        if (lastModified != null) {
            long ifModifiedSince = request.getDateHeader(HttpConstants.HEADER_IF_MODIFIED_SINCE) / 1000;
            long lastModifiedMillis = lastModified.getTimeInMillis();
            if (lastModifiedMillis / 1000 <= ifModifiedSince) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                LOGGER.debug("If-Modified-Since header was present in the request. The resource was not changed, therefore replying with " +
                        "a 304 status code.");
                return true;
            }
            response.setDateHeader(HttpConstants.HEADER_LAST_MODIFIED, lastModifiedMillis);
        }
        return false;
    }

    /**
     * Checks if the requested {@link Resource} has the correct content for an image. This means that:
     * <ol>
     * <li>the resource has a child {@code file} node, of type {@code nt:file}, that has a {@code jcr:data} {@link javax.jcr.Binary}
     * property, or
     * </li>
     * <li>the resource has a {@code fileReference} property, pointing to a DAM asset.</li>
     * </ol>
     *
     * @param resource the resource that represents the image to be rendered
     * @return {@code true}, if the requested resource has the correct content for an image, {@code false} otherwise
     */
    private boolean hasContent(Resource resource) {
        Resource childFileNode = resource.getChild(DownloadResource.NN_FILE);
        if (childFileNode != null) {
            if (JcrConstants.NT_FILE.equals(childFileNode.getResourceType())) {
                Resource jcrContent = childFileNode.getChild(JcrConstants.JCR_CONTENT);
                if (jcrContent != null) {
                    return jcrContent.getValueMap().containsKey(JcrConstants.JCR_DATA);
                }
            }
        } else {
            ValueMap properties = resource.getValueMap();
            return StringUtils.isNotEmpty(properties.get(DownloadResource.PN_REFERENCE, String.class));
        }
        return false;
    }

    private String getImageType(String ext) {
        if (ext == null) {
            return DEFAULT_MIME;
        }
        if ("tiff".equalsIgnoreCase(ext) || "tif".equalsIgnoreCase(ext)) {
            return DEFAULT_MIME;
        }
        return mimeTypeService.getMimeType(ext);
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
