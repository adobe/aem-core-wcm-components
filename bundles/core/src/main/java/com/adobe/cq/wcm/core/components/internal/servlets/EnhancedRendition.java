/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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
import java.io.InputStream;

import org.apache.commons.imaging.Imaging;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.api.Rendition;

/**
 * A {@link Rendition} delegate that facilitates finding out the rendition's dimensions.
 */
public class EnhancedRendition {

    private static final Logger LOG = LoggerFactory.getLogger(EnhancedRendition.class);

    private Rendition rendition;
    private Dimension dimension;
    private boolean dimensionProcessed = false;

    public EnhancedRendition(@NotNull Rendition rendition) {
        this.rendition = rendition;
    }

    /**
     * Getter for the rendition's dimension.
     *
     * @return rendition's dimension if possible to determine, {@code null} otherwise
     */
    @Nullable
    public Dimension getDimension() {
        if (!dimensionProcessed) {
            if (DamConstants.ORIGINAL_FILE.equals(getName())) {
                // Original asset
                try {
                    Asset asset = getAsset();
                    String widthString = asset.getMetadataValue(DamConstants.TIFF_IMAGEWIDTH);
                    String heightString = asset.getMetadataValue(DamConstants.TIFF_IMAGELENGTH);
                    if (StringUtils.isNotEmpty(widthString) && StringUtils.isNotEmpty(heightString)) {
                        int width = Integer.parseInt(widthString);
                        int height = Integer.parseInt(heightString);
                        dimension = new Dimension(width, height);
                    }
                } catch (NumberFormatException nfex) {
                    LOG.error("Cannot parse original asset dimensions", nfex);
                }
            } else if (getProperties().containsKey(DamConstants.TIFF_IMAGEWIDTH) && getProperties().containsKey(DamConstants.TIFF_IMAGELENGTH)) {
                // Use dimensions from rendition metadata
                try {
                    int width = Integer.parseInt(getProperties().get(DamConstants.TIFF_IMAGEWIDTH, String.class));
                    int height = Integer.parseInt(getProperties().get(DamConstants.TIFF_IMAGELENGTH, String.class));
                    dimension = new Dimension(width, height);
                } catch (NumberFormatException nfex) {
                    LOG.error("Cannot parse rendition dimensions from metadata", nfex);
                }
            } else {
                if (StringUtils.startsWith(rendition.getMimeType(), "image/")
                    && rendition.getSize() < Math.pow(AdaptiveImageServlet.DEFAULT_MAX_SIZE, 2)) {
                    // Try to load image to determine dimensions, if not too large\
                    try (InputStream stream = getStream()) {
                        dimension = Imaging.getImageSize(stream, getName());
                    } catch (Exception e) {
                        LOG.error("Cannot get rendition {} dimension from stream", getName(), e);
                    }
                }
            }
            dimensionProcessed = true;
        }
        return dimension;
    }

    String getMimeType() {
        return rendition.getMimeType();
    }

    ValueMap getProperties() {
        return rendition.getProperties();
    }

    long getSize() {
        return rendition.getSize();
    }

    InputStream getStream() {
        return rendition.getStream();
    }

    Asset getAsset() {
        return rendition.getAsset();
    }

    String getPath() {
        return rendition.getPath();
    }

    String getName() {
        return rendition.getName();
    }

    Rendition getRendition() {
        return rendition;
    }
}
