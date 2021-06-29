/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.mime.MimeTypeService;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.internal.link.LinkHandler;
import com.adobe.cq.wcm.core.components.internal.servlets.AdaptiveImageServlet;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.datalayer.ImageData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.AssetDataBuilder;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import com.day.cq.commons.DownloadResource;
import com.day.cq.commons.ImageResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonIgnore;

import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT;
import static com.day.cq.commons.jcr.JcrConstants.JCR_MIMETYPE;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Image.class, ComponentExporter.class}, resourceType = ImageImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ImageImpl extends AbstractComponentImpl implements Image {

    public static final String RESOURCE_TYPE = "core/wcm/components/image/v1/image";
    private static final String DEFAULT_EXTENSION = "jpeg";

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageImpl.class);
    protected static final String DOT = ".";
    protected static final String MIME_TYPE_IMAGE_JPEG = "image/jpeg";
    protected static final String MIME_TYPE_IMAGE_SVG = "image/svg+xml";
    private static final String MIME_TYPE_IMAGE_PREFIX = "image/";

    @ScriptVariable
    protected PageManager pageManager;

    @ScriptVariable
    protected Page currentPage;

    @ScriptVariable
    protected Style currentStyle;

    @ScriptVariable
    protected ValueMap properties;

    @Inject
    @Source("osgi-services")
    protected MimeTypeService mimeTypeService;

    @ValueMapValue(name = DownloadResource.PN_REFERENCE, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    protected String fileReference;

    @ValueMapValue(name = ImageResource.PN_ALT, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    protected String alt;

    @ValueMapValue(name = JcrConstants.JCR_TITLE, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    protected String title;

    @Self
    protected LinkHandler linkHandler;
    protected Optional<Link> link;

    protected String src;
    protected String[] smartImages = new String[]{};
    protected int[] smartSizes = new int[0];
    protected String json;
    protected boolean displayPopupTitle;
    protected boolean isDecorative;

    protected boolean hasContent;
    protected String mimeType;
    protected String selector;
    protected String extension;
    protected long lastModifiedDate = 0;
    protected boolean inTemplate;
    protected String baseResourcePath;
    protected String templateRelativePath;
    protected boolean disableLazyLoading;
    protected int jpegQuality;
    protected String imageName;
    private Resource fileResource;

    public ImageImpl() {
        selector = AdaptiveImageServlet.DEFAULT_SELECTOR;
    }

    /**
     * needs to be protected so that implementations that extend this one can optionally call super.initModel; Sling Models doesn't
     * correctly handle this scenario, although the documentation says something else: see
     * https://github.com/apache/sling-org-apache-sling-models-impl/commit/45570dab4818dc9f626f89f8aa6dbca6557dcc42#diff-8b70000e82308890fe104a598cd2bec2R731
     */
    @PostConstruct
    protected void initModel() {
        initFeaturedImageBasedProperties();
        mimeType = MIME_TYPE_IMAGE_JPEG;
        displayPopupTitle = properties.get(PN_DISPLAY_POPUP_TITLE, currentStyle.get(PN_DISPLAY_POPUP_TITLE, false));
        isDecorative = properties.get(PN_IS_DECORATIVE, currentStyle.get(PN_IS_DECORATIVE, false));
        Asset asset = null;
        if (StringUtils.isNotEmpty(fileReference)) {
            // the image is coming from DAM
            final Resource assetResource = request.getResourceResolver().getResource(fileReference);
            if (assetResource != null) {
                asset = assetResource.adaptTo(Asset.class);
                if (asset != null) {
                    mimeType = PropertiesUtil.toString(asset.getMimeType(), MIME_TYPE_IMAGE_JPEG);
                    imageName = getImageNameFromDam();
                    hasContent = true;
                } else {
                    LOGGER.error("Unable to adapt resource '{}' used by image '{}' to an asset.", fileReference, resource.getPath());
                }
            } else {
                LOGGER.error("Unable to find resource '{}' used by image '{}'.", fileReference, resource.getPath());
            }
        } else {
            if (fileResource != null) {
                mimeType = PropertiesUtil.toString(fileResource.getResourceMetadata().get(ResourceMetadata.CONTENT_TYPE), null);
                if (StringUtils.isEmpty(mimeType)) {
                    Resource fileResourceContent = fileResource.getChild(JCR_CONTENT);
                    if (fileResourceContent != null) {
                        ValueMap fileProperties = fileResourceContent.getValueMap();
                        mimeType = fileProperties.get(JCR_MIMETYPE, MIME_TYPE_IMAGE_JPEG);
                    }
                }
                String fileName = properties.get(ImageResource.PN_FILE_NAME, String.class);
                imageName = StringUtils.isNotEmpty(fileName) ? getSeoFriendlyName(FilenameUtils.getBaseName(fileName)) : "";
                hasContent = true;
            }
        }
        if (hasContent) {
            // validate if correct mime type (i.e. rasterized image)
            if (!mimeType.startsWith(MIME_TYPE_IMAGE_PREFIX)) {
                LOGGER.error("Image at {} uses a binary with a non-image mime type ({})", resource.getPath(), mimeType);
                hasContent = false;
                return;
            }
            // The jcr:mimeType property may contain a charset suffix (image/jpeg;charset=UTF-8).
            // For example if a file was written with JcrUtils#putFile and an optional charset was provided.
            // Check for the suffix and remove as necessary.
            mimeType = mimeType.split(";")[0];
            extension = mimeTypeService.getExtension(mimeType);
            ValueMap properties = resource.getValueMap();
            Calendar lastModified = properties.get(JcrConstants.JCR_LASTMODIFIED, Calendar.class);
            if (lastModified == null) {
                lastModified = properties.get(NameConstants.PN_PAGE_LAST_MOD, Calendar.class);
            }
            if (lastModified != null) {
                lastModifiedDate = lastModified.getTimeInMillis();
            }
            if (asset != null) {
                long assetLastModifiedDate = asset.getLastModified();
                if (assetLastModifiedDate > lastModifiedDate) {
                    lastModifiedDate = assetLastModifiedDate;
                }
            }
            if (extension == null || extension.equalsIgnoreCase("tif") || extension.equalsIgnoreCase("tiff")) {
                extension = DEFAULT_EXTENSION;
            }
            disableLazyLoading = currentStyle.get(PN_DESIGN_LAZY_LOADING_ENABLED, false);
            jpegQuality = currentStyle.get(PN_DESIGN_JPEG_QUALITY, AdaptiveImageServlet.DEFAULT_JPEG_QUALITY);
            int index = 0;
            Template template = currentPage.getTemplate();
            if (template != null && resource.getPath().startsWith(template.getPath())) {
                inTemplate = true;
                baseResourcePath = currentPage.getPath();
                templateRelativePath = resource.getPath().substring(template.getPath().length());
            } else {
                baseResourcePath = resource.getPath();
            }
            baseResourcePath = resource.getResourceResolver().map(request, baseResourcePath);
            if (smartSizesSupported()) {
                Set<Integer> supportedRenditionWidths = getSupportedRenditionWidths();
                smartImages = new String[supportedRenditionWidths.size()];
                smartSizes = new int[supportedRenditionWidths.size()];
                for (Integer width : supportedRenditionWidths) {
                    smartImages[index] = baseResourcePath + DOT +
                            selector + DOT + jpegQuality + DOT + width + DOT + extension +
                            (inTemplate ? Text.escapePath(templateRelativePath) : "") +
                            (lastModifiedDate > 0 ? ("/" + lastModifiedDate + (StringUtils.isNotBlank(imageName) ? ("/" + imageName) : "")) : "") +
                            (inTemplate || lastModifiedDate > 0 ? DOT + extension : "");
                    smartSizes[index] = width;
                    index++;
                }
            } else {
                smartImages = new String[0];
                smartSizes = new int[0];
            }
            src = baseResourcePath + DOT + selector + DOT;
            if (smartSizes.length == 1) {
                src += jpegQuality + DOT + smartSizes[0] + DOT + extension;
            } else {
                src += extension;
            }
            src += (inTemplate ? Text.escapePath(templateRelativePath) : "") +
                    (lastModifiedDate > 0 ? ("/" + lastModifiedDate + (StringUtils.isNotBlank(imageName) ? ("/" + imageName): "")) : "") +
                    (inTemplate || lastModifiedDate > 0 ? DOT + extension : "");
            if (!isDecorative) {
                link = linkHandler.getLink(resource);
            } else {
                link = Optional.empty();
                alt = null;
            }
            buildJson();
        }
    }

    /**
     * Extracts the image name from the DAM resource
     *
     * @return image name from DAM
     */
    protected String getImageNameFromDam() {
        return Optional.ofNullable(this.fileReference)
            .map(reference -> request.getResourceResolver().getResource(reference))
            .map(damResource -> damResource.adaptTo(Asset.class))
            .map(Asset::getName)
            .map(StringUtils::trimToNull)
            .map(FilenameUtils::getBaseName)
            .map(this::getSeoFriendlyName)
            .orElse(StringUtils.EMPTY);
    }

    /**
     * Content editors can store DAM assets with white spaces in the name, this
     * method makes the asset name SEO friendly, Translates the string into
     * {@code application/x-www-form-urlencoded} format using {@code utf-8} encoding
     * scheme.
     *
     * @param imageName The image name
     * @return the SEO friendly image name
     */
    protected String getSeoFriendlyName(String imageName) {

        // Google recommends using hyphens (-) instead of underscores (_) for SEO. See
        // https://support.google.com/webmasters/answer/76329?hl=en
        String seoFriendlyName = imageName.replaceAll("[\\ _]", "-").toLowerCase();
        try {
            seoFriendlyName = URLEncoder.encode(seoFriendlyName, CharEncoding.UTF_8);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("The Character Encoding is not supported.");
        }
        return seoFriendlyName;
    }


    @Override
    public String getSrc() {
        return src;
    }

    @Override
    public boolean displayPopupTitle() {
        return displayPopupTitle;
    }

    @Override
    public String getAlt() {
        return alt;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getLink() {
        return link == null ? null : link.map(Link::getURL).orElse(null);
    }

    @Override
    @JsonIgnore
    public String getFileReference() {
        return fileReference;
    }

    @Override
    @JsonIgnore
    @Deprecated
    public String getJson() {
        return json;
    }

    @NotNull
    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }

    @Override
    @JsonIgnore
    public boolean isDecorative() {
        return this.isDecorative;
    }

    @SuppressWarnings("squid:CallToDeprecatedMethod")
    protected void buildJson() {
        JsonArrayBuilder smartSizesJsonBuilder = Json.createArrayBuilder();
        for (int size : smartSizes) {
            smartSizesJsonBuilder.add(size);
        }
        JsonArrayBuilder smartImagesJsonBuilder = Json.createArrayBuilder();
        for (String image : smartImages) {
            smartImagesJsonBuilder.add(image);
        }
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add(JSON_SMART_IMAGES, smartImagesJsonBuilder);
        jsonObjectBuilder.add(JSON_SMART_SIZES, smartSizesJsonBuilder);
        jsonObjectBuilder.add(JSON_LAZY_ENABLED, !disableLazyLoading);
        json = jsonObjectBuilder.build().toString();
    }

    private Set<Integer> getSupportedRenditionWidths() {
        Set<Integer> allowedRenditionWidths = new TreeSet<>();
        String[] supportedWidthsConfig = currentStyle.get(PN_DESIGN_ALLOWED_RENDITION_WIDTHS, new String[0]);
        for (String width : supportedWidthsConfig) {
            try {
                allowedRenditionWidths.add(Integer.parseInt(width));
            } catch (NumberFormatException e) {
                LOGGER.error(String.format("Invalid width detected (%s) for content policy configuration.", width), e);
            }
        }
        return allowedRenditionWidths;
    }

    private boolean smartSizesSupported() {
        // "smart sizes" is supported for all images except SVG
        return !StringUtils.equals(mimeType, MIME_TYPE_IMAGE_SVG);
    }

    /*
     * DataLayer specific methods
     */

    @Override
    @JsonIgnore
    @NotNull
    public ImageData getComponentData() {
        return DataLayerBuilder.extending(super.getComponentData()).asImageComponent()
            .withTitle(this::getTitle)
            .withLinkUrl(() -> link.map(Link::getMappedURL).orElse(null))
            .withAssetData(() ->
                Optional.ofNullable(this.fileReference)
                    .map(reference -> this.request.getResourceResolver().getResource(reference))
                    .map(assetResource -> assetResource.adaptTo(Asset.class))
                    .map(DataLayerBuilder::forAsset)
                    .map(AssetDataBuilder::build)
                    .orElse(null))
            .build();
    }

    private void initFeaturedImageBasedProperties() {
        fileResource = resource.getChild(DownloadResource.NN_FILE);
        if (StringUtils.isEmpty(fileReference) && fileResource == null) {
            Resource featuredImage = ComponentUtils.getFeaturedImage(currentPage);
            if (featuredImage != null) {
                fileResource = featuredImage.getChild(DownloadResource.NN_FILE);
                ValueMap featuredImageProps = featuredImage.getValueMap();
                fileReference = featuredImageProps.get(DownloadResource.PN_REFERENCE, String.class);
            }
        }
    }

}
