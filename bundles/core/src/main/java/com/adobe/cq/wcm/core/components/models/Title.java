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

package com.adobe.cq.wcm.core.components.models;

import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.wcm.core.components.commons.link.Link;

/**
 * Defines the {@code Title} Sling Model used for the {@code /apps/core/wcm/components/title} component.
 *
 * @since com.adobe.cq.wcm.core.components.models 11.0.0
 */
@ConsumerType
public interface Title extends Component {

    /**
     * Name of the configuration policy property that will store the default value for this title's HTML element type.
     *
     * @see #getType()
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    String PN_DESIGN_DEFAULT_TYPE = "type";

    /**
     * Name of the policy property that defines whether or not the title link is disabled.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.4.0
     */
    String PN_TITLE_LINK_DISABLED = "linkDisabled";

    /**
     * Returns the text to be displayed as title.
     *
     * @return the title's text
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default String getText() {
        return null;
    }

    /**
     * Returns the HTML element type (h1-h6) used for the markup.
     *
     * @return the element type
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default String getType() {
        return null;
    }

    /**
     * Returns the Title's link.
     *
     * @return the title's link
     * @since com.adobe.cq.wcm.core.components.models 12.20.0
     */
    @Nullable
    default Link getLink() {
        return null;
    }

    /**
     * Returns the Title's link URL, if one was set.
     *
     * @return the title's link URL, if one was set, or {@code null}
     * @since com.adobe.cq.wcm.core.components.models 12.4.0
     * @deprecated Please use {@link #getLink()}
     */
    @Deprecated
    default String getLinkURL() {
        return null;
    }

    /**
     * Checks if link is disabled on the title.
     *
     * @return {@code true} if link is disabled on the title, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.4.0
     */
    default boolean isLinkDisabled() {
        return false;
    }

}
