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
package com.adobe.cq.wcm.core.components.sandbox.models;

import org.apache.sling.api.resource.Resource;

/**
 * Defines the {@code Teaser} Sling Model for the {@code /apps/core/wcm/components/teaser} component.
 *
 * @since com.adobe.cq.wcm.core.components.sandbox.models 2.3.0
 */
public interface Teaser {

    /**
     * Name of the resource property that will store the link text for a {@code Teaser}.
     *
     * @since com.adobe.cq.wcm.core.components.sandbox.models 2.3.0
     */

    String PN_LINK_TEXT = "linkText";

    /**
     * Returns this teaser's title, if one was defined.
     *
     * @return the teaser's title or {@code null}
     * @since com.adobe.cq.wcm.core.components.sandbox.models 2.3.0
     */
    default String getTitle() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns this teaser's description, if one was defined.
     *
     * @return the teaser's description or {@code null}
     * @since com.adobe.cq.wcm.core.components.sandbox.models 2.3.0
     */
    default String getDescription() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the URL to which this teaser links, if one was defined.
     *
     * @return the URL to which teaser links or {@code null}
     * @since com.adobe.cq.wcm.core.components.sandbox.models 2.3.0
     */
    default String getLinkURL() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the text of the URL to which this teaser links, if one was defined.
     *
     * @return the text of the URL to which teaser links or {@code null}
     * @see #getLinkURL()
     * @since com.adobe.cq.wcm.core.components.sandbox.models 2.3.0
     */
    default String getLinkText() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the image resource for this teaser.
     * @return the image resource for this teaser or {@code null}
     * @since com.adobe.cq.wcm.core.components.sandbox.models 2.3.0
     */
    default Resource getImageResource() {
        throw new UnsupportedOperationException();
    }

}
