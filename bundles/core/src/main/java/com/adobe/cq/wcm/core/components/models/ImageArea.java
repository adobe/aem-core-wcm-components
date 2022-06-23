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
package com.adobe.cq.wcm.core.components.models;

import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.wcm.core.components.commons.link.Link;

/**
 * Interface for an image map area, used by the {@link Image} model.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.4.0
 */
@ConsumerType
public interface ImageArea {

    /**
     * Returns the value for the {@code shape} attribute of the image map area.
     *
     * @return the image map area shape
     * @since com.adobe.cq.wcm.core.components.models 12.4.0
     */
    default String getShape() {
        return null;
    }

    /**
     * Returns the value for the {@code coords} attribute of the image map area.
     *
     * @return the image map area coordinates
     * @since com.adobe.cq.wcm.core.components.models 12.4.0
     */
    default String getCoordinates() {
        return null;
    }

    /**
     * Returns the value for a relative unit representation of the {@code coords} attribute of the image map area.
     *
     * @return the image map area coordinates expressed in relative units
     * @since com.adobe.cq.wcm.core.components.models 12.4.0
     */
    default String getRelativeCoordinates() {
        return null;
    }

    /**
     * Returns the link of the image area.
     *
     * @see Link of the image area.
     * @since com.adobe.cq.wcm.core.components.models 12.20.0
     * @return a {@link com.adobe.cq.wcm.core.components.commons.link.Link} object
     */
    default Link getLink() {
        return null;
    }

    /**
     * Returns the value for the {@code href} attribute of the image map area.
     *
     * @return the image map area link href
     * @since com.adobe.cq.wcm.core.components.models 12.4.0
     */
    default String getHref() {
        return null;
    }

    /**
     * Returns the value for the {@code target} attribute of the image map area.
     *
     * @return the image map area link target
     * @since com.adobe.cq.wcm.core.components.models 12.4.0
     */
    default String getTarget() {
        return null;
    }

    /**
     * Returns the value for the {@code alt} attribute of the image map area.
     *
     * @return the image map area's alternative text
     * @since com.adobe.cq.wcm.core.components.models 12.4.0
     */
    default String getAlt() {
        return null;
    }
}
