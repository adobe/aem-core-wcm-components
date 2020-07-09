/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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

import com.adobe.cq.wcm.core.components.models.ImageArea;

/**
 * Image area implementation.
 */
public final class ImageAreaImpl implements ImageArea {

    /**
     * The shape of the area.
     */
    private final String shape;

    /**
     * The coordinates of the area.
     */
    private final String coordinates;

    /**
     * a relative unit representation of the {@code coords}.
     */
    private final String relativeCoordinates;

    /**
     * The image area anchor {@code href} value.
     */
    private final String href;

    /**
     * The image area anchor {@code target} value.
     */
    private final String target;

    /**
     * The image area anchor {@code alt} value
     */
    private final String alt;

    /**
     * Construct an Image Area.
     *
     * @param shape The shape of the area.
     * @param coordinates The coordinates of the area.
     * @param relativeCoordinates The relative unit representation of the {@code coords}.
     * @param href The image area anchor href.
     * @param target The image area anchor target.
     * @param alt The image area anchor alt text.
     */
    public ImageAreaImpl(final String shape,
                         final String coordinates,
                         final String relativeCoordinates,
                         final String href,
                         final String target,
                         final String alt) {
        this.shape = shape;
        this.coordinates = coordinates;
        this.relativeCoordinates = relativeCoordinates;
        this.href = href;
        this.target = target;
        this.alt = alt;
    }

    @Override
    public String getShape() {
        return shape;
    }

    @Override
    public String getCoordinates() {
        return coordinates;
    }

    @Override
    public String getRelativeCoordinates() {
        return relativeCoordinates;
    }

    @Override
    public String getHref() {
        return href;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public String getAlt() {
        return alt;
    }
}
