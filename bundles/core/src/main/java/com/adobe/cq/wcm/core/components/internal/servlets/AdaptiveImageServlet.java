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
package com.adobe.cq.wcm.core.components.internal.servlets;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
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

import com.adobe.cq.wcm.core.components.internal.models.v1.ImageImpl;
import com.day.cq.commons.DownloadResource;
import com.day.cq.commons.ImageResource;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.api.handler.AssetHandler;
import com.day.cq.dam.api.handler.store.AssetStore;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.components.ComponentManager;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.day.image.Layer;
import com.google.common.base.Joiner;

@Component(
        service = Servlet.class,
        configurationPid = "com.adobe.cq.wcm.core.components.internal.servlets.AdaptiveImageServlet",
        property = {
                "sling.servlet.selectors=" + AdaptiveImageServlet.DEFAULT_SELECTOR,
                "sling.servlet.resourceTypes=core/wcm/components/image/",
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
    private static final int DEFAULT_RESIZE_WIDTH = 1280;
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
        int defaultResizeWidth() default DEFAULT_RESIZE_WIDTH;

    }

    @Activate
    protected void activate(Configuration configuration) {
        defaultResizeWidth = configuration.defaultResizeWidth() > 0 ? configuration.defaultResizeWidth() : DEFAULT_RESIZE_WIDTH;
    }

    @Override
    protected void doGet(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response) throws IOException {
        String[] selectors = request.getRequestPathInfo().getSelectors();
        if (selectors.length != 1 && selectors.length != 2) {
            LOGGER.error("Expected 1 or 2 selectors, instead got: {}.", Arrays.toString(selectors));
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Resource component = request.getResource();
        Resource image = getImage(component);
        if (image == null) {
            LOGGER.error("The image from {} does not have a valid file reference.", component.getPath());
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        ValueMap componentProperties = component.getValueMap();
        long lastModifiedEpoch = 0;
        Calendar lastModifiedDate = componentProperties.get(JcrConstants.JCR_LASTMODIFIED, Calendar.class);
        if (lastModifiedDate == null) {
            lastModifiedDate = componentProperties.get(NameConstants.PN_PAGE_LAST_MOD, Calendar.class);
        }
        if (lastModifiedDate != null) {
            lastModifiedEpoch = lastModifiedDate.getTimeInMillis();
        }
        Source source = getImageSource(component, image);
        Asset asset = null;
        if (source == Source.ASSET) {
            asset = image.adaptTo(Asset.class);
            if (asset == null) {
                LOGGER.error("Unable to adapt resource {} used by image {} to an asset.", image.getPath(), component.getPath());
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            long assetLastModifiedEpoch = asset.getLastModified();
            if (assetLastModifiedEpoch > lastModifiedEpoch) {
                lastModifiedEpoch = assetLastModifiedEpoch;
            }
        }
        long requestLastModifiedSuffix = getRequestLastModifiedSuffix(request);
        if (requestLastModifiedSuffix >= 0 && requestLastModifiedSuffix != lastModifiedEpoch) {
            String redirectLocation = getRedirectLocation(request, lastModifiedEpoch);
            LOGGER.info("The last modified information present in the request ({}) is different than expected. Redirect request to " +
                    "correct suffix ({})", requestLastModifiedSuffix, redirectLocation);
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            response.setHeader("Location", redirectLocation);
            return;
        }
        if (!handleIfModifiedSinceHeader(request, response, lastModifiedEpoch)) {
            int resizeWidth = defaultResizeWidth;
            String widthSelector = selectors[selectors.length - 1];
            List<Integer> allowedRenditionWidths = getAllowedRenditionWidths(request);
            if (!DEFAULT_SELECTOR.equals(widthSelector)) {
                try {
                    Integer width = Integer.parseInt(widthSelector);
                    boolean isRequestedWidthAllowed = false;
                    if (!allowedRenditionWidths.isEmpty()) {
                        for (Integer allowedWidth : allowedRenditionWidths) {
                            if (width.equals(allowedWidth)) {
                                isRequestedWidthAllowed = true;
                                resizeWidth = width;
                                break;
                            }
                        }
                        if (resizeWidth < 0) {
                            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                            return;
                        }
                        if (isRequestedWidthAllowed) {
                            String imageType = getImageType(request.getRequestPathInfo().getExtension());
                            if (source == Source.FILE) {
                                resizeAndStreamFile(response, componentProperties, resizeWidth, image, imageType);
                            } else if (source == Source.ASSET) {
                                resizeAndStreamAsset(response, componentProperties, resizeWidth, asset, imageType);
                            }
                        } else {
                            LOGGER.error("The requested width ({}) is not allowed by the content policy.", width);
                            response.sendError(HttpServletResponse.SC_NOT_FOUND);
                        }
                    } else {
                        LOGGER.error("There's no content policy defined and the request provides a width selector ({}).", width);
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    }
                } catch (NumberFormatException e) {
                    LOGGER.error("The requested width ({}) is not a valid Integer.", widthSelector);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            } else {
                LOGGER.debug("The image request contains no width information. Will resize the image to {}px.", defaultResizeWidth);
        String imageType = getImageType(request.getRequestPathInfo().getExtension());
                if (source == Source.FILE) {
                    resizeAndStreamFile(response, componentProperties, defaultResizeWidth, image, imageType);
                } else if (source == Source.ASSET) {
                    resizeAndStreamAsset(response, componentProperties, defaultResizeWidth, asset, imageType);
                }
            }
        }


    }

    private String getRedirectLocation(SlingHttpServletRequest request, long lastModifiedEpoch) {
        RequestPathInfo requestPathInfo = request.getRequestPathInfo();
        return Joiner.on(".").join(request.getContextPath() + requestPathInfo.getResourcePath(), requestPathInfo.getSelectorString(),
                requestPathInfo.getExtension() + "/" + lastModifiedEpoch, requestPathInfo.getExtension());
    }

    private void resizeAndStreamAsset(SlingHttpServletResponse response, ValueMap componentProperties, int resizeWidth, Asset asset, String
            imageType) throws IOException {
        String extension = mimeTypeService.getExtension(imageType);
            if ("gif".equalsIgnoreCase(extension)) {
                LOGGER.debug("GIF asset detected; will render the original rendition.");
                stream(response, asset.getOriginal().getStream(), imageType);
                return;
            }
        int rotationAngle = getRotation(componentProperties);
        Rectangle rectangle = getCropRect(componentProperties);
            if (rotationAngle != 0 || rectangle != null || resizeWidth > 0) {
                int originalWidth = getDimension(asset.getMetadataValue(DamConstants.TIFF_IMAGEWIDTH));
                int originalHeight = getDimension(asset.getMetadataValue(DamConstants.TIFF_IMAGELENGTH));
                AssetHandler assetHandler = assetStore.getAssetHandler(imageType);
                Layer layer = null;
                boolean appliedRotationOrCropping = false;
                if (rectangle != null) {
                    double scaling;
                    Rendition webRendition = getAWebRendition(asset);
                    double renditionWidth;
                    if (webRendition != null) {
                        try (InputStream renditionStream = webRendition.getStream()) {
                            Layer rendition = new Layer(renditionStream);
                            renditionWidth = rendition.getWidth();
                            LOGGER.debug("Found rendition {} with width {}px; assuming the cropping rectangle was calculated using this " +
                                    "rendition.", webRendition.getPath(), renditionWidth);
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
    }

    private void resizeAndStreamFile(SlingHttpServletResponse response, ValueMap componentProperties,  int
            resizeWidth, Resource imageFile, String imageType) throws
            IOException {
            InputStream is = null;
            try {
                is = imageFile.adaptTo(InputStream.class);
            if ("gif".equalsIgnoreCase(mimeTypeService.getExtension(imageType))) {
                LOGGER.debug("GIF file detected; will render the original file.");
                if (is != null) {
                    stream(response, is, imageType);
                }
                return;
            }
            int rotationAngle = getRotation(componentProperties);
            Rectangle rectangle = getCropRect(componentProperties);
                if (is !=null) {
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
                }
            } finally {
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
        try {
            IOUtils.copy(inputStream, response.getOutputStream());
        }  finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Retrieves the cropping rectangle, if one is defined for the image.
     *
     * @param properties the image component's properties
     * @return the cropping rectangle, if one is found, {@code null} otherwise
     */
    private Rectangle getCropRect(@Nonnull ValueMap properties) {
        String csv = properties.get(ImageResource.PN_IMAGE_CROP, String.class);
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
                LOGGER.warn(String.format("Invalid cropping rectangle %s.", csv), e);
            }
        }
        return null;
    }

    /**
     * Retrieves the rotation angle for the image, if one is present. Typically this should be a value between 0 and 360.
     *
     * @param properties the image component's properties
     * @return the rotation angle
     */
    private int getRotation(@Nonnull ValueMap properties) {
        String rotationString = properties.get(ImageResource.PN_IMAGE_ROTATE, String.class);
        if (rotationString != null) {
            try {
                return Integer.parseInt(rotationString);
            } catch (NumberFormatException e) {
                LOGGER.warn(String.format("Invalid rotation value %s.", rotationString), e);
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
     * <p>
     * Checks if the {@code request} contains the {@code If-Modified-Since} header and compares this value to the passed {@code
     * lastModified} parameter.
     * </p>
     *
     * <p>If the value of {@code lastModified} is greater than 0 but less than or equal to the value of the {@code
     * If-Modified-Since} header, then {@link HttpServletResponse#SC_NOT_MODIFIED} will be set as the {@code response} status code.</p>
     *
     * <p>If the value of {@code lastModified} is greater than the value of the {@code If-Modified-Since} header, then this method will
     * set the {@link HttpConstants#HEADER_LAST_MODIFIED} {@code response} header with the value of {@code lastModified}.</p>
     *
     * <p>If the value of {@code lastModified} is less than or equal to 0 this method doesn't have any effect on the {@code response}.</p>
     *
     * @param request    the request
     * @param response   the response
     * @param lastModified the underlying resource's last modified date in milliseconds, expressed as UTC milliseconds from the Unix epoch
     *                     (00:00:00 UTC Thursday 1, January 1970)
     * @return {@code true} if the {@code response}'s status code was set (to {@link HttpServletResponse#SC_NOT_MODIFIED}, {@code false} otherwise
     */
    private boolean handleIfModifiedSinceHeader(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response,
                                                long lastModified) {
        if (lastModified > 0) {
            long ifModifiedSince = request.getDateHeader(HttpConstants.HEADER_IF_MODIFIED_SINCE) / 1000;
            if (lastModified / 1000 <= ifModifiedSince) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                LOGGER.debug("If-Modified-Since header was present in the request. The resource was not changed, therefore replying with " +
                        "a 304 status code.");
                return true;
            }
            response.setDateHeader(HttpConstants.HEADER_LAST_MODIFIED, lastModified);
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
        if (policyManager != null) {
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
        }
        return list;
    }

    private long getRequestLastModifiedSuffix(SlingHttpServletRequest request) {
        long requestLastModified = 0;
        String suffix = request.getRequestPathInfo().getSuffix();
        if (StringUtils.isNotEmpty(suffix) && suffix.contains(".")) {
            String lastMod = suffix.substring(1, suffix.lastIndexOf("."));
            try {
                requestLastModified = Long.parseLong(lastMod);
            } catch (NumberFormatException e) {
                // do nothing
            }
        }
        return requestLastModified;
    }

    /**
     * Checks if the the passed {@code component} has the correct content for an image and returns it. This means that:
     * <ol>
     * <li>the {@code component} has a child {@code file} node, of type {@code nt:file}, that has a {@code jcr:data}
     * {@link javax.jcr.Binary} property, or
     * </li>
     * <li>the component has a {@code fileReference} property, pointing to a DAM asset.</li>
     * </ol>
     *
     * @param component the component that represents the image component to be rendered
     * @return the {@link Resource} identifying the actual image to render, whether this is a file or an {@link Asset}; if the image
     * component identified by {@code component} has not been yet configured this method will return {@code null}
     */
    @Nullable
    private Resource getImage(@Nonnull Resource component) {
        Resource childFileNode = component.getChild(DownloadResource.NN_FILE);
        if (childFileNode != null) {
            if (JcrConstants.NT_FILE.equals(childFileNode.getResourceType())) {
                Resource jcrContent = childFileNode.getChild(JcrConstants.JCR_CONTENT);
                if (jcrContent != null) {
                    if (jcrContent.getValueMap().containsKey(JcrConstants.JCR_DATA)) {
                        return childFileNode;
                    }
                }
            }
        } else {
            String fileReference = component.getValueMap().get(DownloadResource.PN_REFERENCE, String.class);
            if (StringUtils.isNotEmpty(fileReference)) {
                return component.getResourceResolver().getResource(fileReference);
            }
        }
        return null;
    }

    private enum Source {
        ASSET,
        FILE
    }

    @Nonnull
    private Source getImageSource(@Nonnull Resource imageComponent, @Nonnull Resource image) {
        String parentResourcePath = ResourceUtil.getParent(image.getPath());
        if (StringUtils.equals(parentResourcePath, imageComponent.getPath())) {
            return Source.FILE;
        }
        return Source.ASSET;
    }
}
