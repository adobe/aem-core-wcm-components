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
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
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
import com.adobe.cq.wcm.core.components.internal.resource.CoreResourceWrapper;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.ImageArea;
import com.adobe.cq.wcm.core.components.models.datalayer.ImageData;
import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Image.class, ComponentExporter.class}, resourceType = ImageImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ImageImpl extends com.adobe.cq.wcm.core.components.internal.models.v2.ImageImpl implements Image {

    public static final String RESOURCE_TYPE = "core/wcm/components/image/v3/image";
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageImpl.class);

    @ValueMapValue(name = "width", injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    protected String width;

    @ValueMapValue(name="height", injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    protected String height;

    @Inject
    private ModelFactory modelFactory;

    protected Image featuredImage;
    protected boolean useFeaturedImage = false;

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
    protected ImageArea newImageArea(String shape, String coordinates, String relativeCoordinates, @NotNull Link link, String alt ) {
        return new ImageAreaImpl(shape, coordinates, relativeCoordinates, link, alt);
    }

    /**
     * Initialize the model.
     */
    @PostConstruct
    protected void initModel() {
        super.initModel();
        if (!hasContent) {
            Resource featuredImageResource = ComponentUtils.getFeaturedImage(currentPage);
            if (featuredImageResource != null && !StringUtils.equals(resource.getPath(), featuredImageResource.getPath())) {
                // Use the resource type of the caller image to render the featured image
                Resource wrappedFeaturedImageResource = new CoreResourceWrapper(featuredImageResource, resource.getResourceType());
                featuredImage = modelFactory.getModelFromWrappedRequest(this.request, wrappedFeaturedImageResource, Image.class);
                if (featuredImage != null) {
                    // use the properties of the featured image node, except for the resource type and the ID
                    src = featuredImage.getSrc();
                    alt = featuredImage.getAlt();
                    link = Optional.ofNullable(featuredImage.getImageLink());
                    fileReference = featuredImage.getFileReference();
                    isDecorative = featuredImage.isDecorative();

                    srcUriTemplate = featuredImage.getSrcUriTemplate();
                    disableLazyLoading = !featuredImage.isLazyEnabled();
                    dmImage = featuredImage.isDmImage();
                    smartCropRendition = featuredImage.getSmartCropRendition();
                    lazyThreshold = featuredImage.getLazyThreshold();
                    areas = featuredImage.getAreas();
                    uuid = featuredImage.getUuid();
                }
            }

        }
    }

    @Override
    @JsonIgnore
    @NotNull
    public ImageData getComponentData() {
        if (useFeaturedImage) {
            return featuredImage.getComponentData();
        }
        return super.getComponentData();
    }

    @Override
    public String getSrcset() {
        String [] srcsetArray = new String[super.getWidths().length];
        if (super.getWidths().length > 0 && super.getSrcUriTemplate() != null) {
            String srcUriTemplateDecoded = "";
            try {
                srcUriTemplateDecoded  = URLDecoder.decode(super.getSrcUriTemplate(), StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("Character Decoding failed.");
            }
            if (srcUriTemplateDecoded.contains("{.width}")) {
                for (int i = 0; i < super.getWidths().length; i++) {
                    if (srcUriTemplateDecoded.contains("={.width}")) {
                        srcsetArray[i] = srcUriTemplateDecoded.replace("{.width}", String.format("%s", super.getWidths()[i])) + " " + super.getWidths()[i] + "w";
                    } else {
                        srcsetArray[i] = srcUriTemplateDecoded.replace("{.width}", String.format(".%s", super.getWidths()[i])) + " " + super.getWidths()[i] + "w";
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

}



