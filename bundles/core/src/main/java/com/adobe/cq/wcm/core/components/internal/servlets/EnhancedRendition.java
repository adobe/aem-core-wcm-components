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
import java.util.Arrays;

import javax.jcr.Binary;

import org.apache.commons.imaging.Imaging;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceWrapper;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.api.Rendition;

/**
 * A {@link Rendition} wrapper that facilitates finding out the rendition's dimensions.
 */
public class EnhancedRendition extends ResourceWrapper implements Rendition {

    private static final Logger LOG = LoggerFactory.getLogger(EnhancedRendition.class);

    private static final String[] IGNORED_RENDITIONS = new String[]{"fpo"};

    private Rendition rendition;
    private Dimension dimension;
    private boolean dimensionProcessed = false;

    public EnhancedRendition(@NotNull Rendition rendition) {
        super(rendition);
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
                    int width = Integer.parseInt(asset.getMetadataValue(DamConstants.TIFF_IMAGEWIDTH));
                    int height = Integer.parseInt(asset.getMetadataValue(DamConstants.TIFF_IMAGELENGTH));
                    dimension = new Dimension(width, height);
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

    /**
     * Checks to see if this rendition should be one that is ignored. Initially this is just going
     * to ignore the FPO rendition that is used for the OOTB Creative Cloud integration which is the same
     * dimensions as the original, but usually significantly lower quality.
     * @return
     */
    public boolean isValid() {
        return Arrays.stream(IGNORED_RENDITIONS).noneMatch(selector -> this.rendition.getName().contains("." + selector + "."));
    }

    /**
     * See {@link Rendition#getMimeType()}
     */
    @Override
    public String getMimeType() {
        return rendition.getMimeType();
    }

    /**
     * See {@link Rendition#getProperties()}
     */
    @Override
    public ValueMap getProperties() {
        return rendition.getProperties();
    }

    /**
     * See {@link Rendition#getSize()}
     */
    @Override
    public long getSize() {
        return rendition.getSize();
    }

    /**
     * See {@link Rendition#getStream()}
     */
    @Override
    public InputStream getStream() {
        return rendition.getStream();
    }

    /**
     * See {@link Rendition#getAsset()}
     */
    @Override
    public Asset getAsset() {
        return rendition.getAsset();
    }
}
