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
package com.adobe.cq.wcm.core.components.internal.models.v3;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.factory.ModelFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.internal.models.v2.ImageAreaImpl;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.ImageArea;
import com.adobe.cq.wcm.core.components.models.datalayer.ImageData;
import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import com.day.cq.commons.DownloadResource;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Image.class, ComponentExporter.class}, resourceType = ImageImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ImageImpl extends com.adobe.cq.wcm.core.components.internal.models.v2.ImageImpl implements Image {

    public static final String RESOURCE_TYPE = "core/wcm/components/image/v3/image";

    private static final String PN_IMAGE_FROM_PAGE_IMAGE = "imageFromPageImage";
    private static final String PN_ALT_VALUE_FROM_PAGE_IMAGE = "altValueFromPageImage";

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageImpl.class);

    @ValueMapValue(name = "width", injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    protected String width;

    @ValueMapValue(name = "height", injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    protected String height;

    /**
     * The model factory.
     */
    @OSGiService
    protected ModelFactory modelFactory;

    private boolean altValueFromPageImage;
    private boolean imageFromPageImage;
    private Image pageImageModel;

    @PostConstruct
    protected void initModel() {
        super.initModel();
        altValueFromPageImage = properties.get(PN_ALT_VALUE_FROM_PAGE_IMAGE, true);
        if (imageFromPageImage) {
            Resource featuredImage = ComponentUtils.getFeaturedImage(currentPage);
            if (featuredImage != null) {
                if (!StringUtils.equals(resource.getPath(), featuredImage.getPath())) {
                    pageImageModel = modelFactory.getModelFromWrappedRequest(this.request, featuredImage, Image.class);
                }
            }
        }
    }

    @Override
    public String getAlt() {
        if (imageFromPageImage && pageImageModel != null && altValueFromPageImage && !isDecorative) {
            return pageImageModel.getAlt();
        }
        return alt;
    }

    @Override
    public String getUuid() {
        if (imageFromPageImage && pageImageModel != null) {
            return pageImageModel.getUuid();
        }
        return uuid;
    }

    @Override
    @JsonIgnore
    public String getFileReference() {
        if (imageFromPageImage && pageImageModel != null) {
            return pageImageModel.getFileReference();
        }
        return fileReference;
    }

    @Override
    @Nullable
    public Link getImageLink() {
        return link.orElse(null);
    }

    @Override
    @JsonIgnore
    @Deprecated
    public String getLink() {
        return super.getLink();
    }

    @Override
    protected ImageArea newImageArea(String shape, String coordinates, String relativeCoordinates, @NotNull Link link, String alt) {
        return new ImageAreaImpl(shape, coordinates, relativeCoordinates, link, alt);
    }


    @Override
    public String getSrcset() {
        int[] widthsArray = super.getWidths();
        String srcUritemplate = super.getSrcUriTemplate();
        String[] srcsetArray = new String[widthsArray.length];
        if (widthsArray.length > 0 && srcUritemplate != null) {
            String srcUriTemplateDecoded = "";
            try {
                srcUriTemplateDecoded = URLDecoder.decode(srcUritemplate, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("Character Decoding failed for " + resource.getPath());
            }
            if (srcUriTemplateDecoded.contains("{.width}")) {
                for (int i = 0; i < widthsArray.length; i++) {
                    if (srcUriTemplateDecoded.contains("={.width}")) {
                        srcsetArray[i] = srcUriTemplateDecoded.replace("{.width}", String.format("%s", widthsArray[i])) + " " + widthsArray[i] + "w";
                    } else {
                        srcsetArray[i] = srcUriTemplateDecoded.replace("{.width}", String.format(".%s", widthsArray[i])) + " " + widthsArray[i] + "w";
                    }
                }
                return StringUtils.join(srcsetArray, ',');
            }
        }
        return null;
    }

    @Nullable
    @Override
    public String getWidth() {
        return width;
    }

    @Nullable
    @Override
    public String getHeight() {
        return height;
    }

    @Override
    @JsonIgnore
    @NotNull
    public ImageData getComponentData() {
        String inheritedFileReference = null;
        if (inheritedResource != null) {
            inheritedFileReference = inheritedResource.getValueMap().get(DownloadResource.PN_REFERENCE, String.class);
        }
        return getComponentData(inheritedFileReference);
    }

    @Override
    protected void initInheritedResource() {
        imageFromPageImage = properties.get(PN_IMAGE_FROM_PAGE_IMAGE, StringUtils.isEmpty(fileReference) && fileResource == null);
        if (imageFromPageImage) {
            Resource featuredImage = ComponentUtils.getFeaturedImage(currentPage);
            if (featuredImage != null) {
                inheritedResource = featuredImage;
            }
        } else {
            inheritedResource = resource;
        }
    }
}
