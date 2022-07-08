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

import java.util.Calendar;

import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.wcm.core.components.commons.link.Link;

/**
 * Interface for a generic list item, used by the {@link List} and {@link Search} models.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.2.0
 */
@ConsumerType
public interface ListItem extends Component {

    /**
     * Returns the link of this {@code ListItem}.
     *
     * @return the link of this list item.
     * @since com.adobe.cq.wcm.core.components.models 12.20.0
     */
    @Nullable
    default Link getLink() {
        return null;
    }

    /**
     * Returns the URL of this {@code ListItem}.
     *
     * @return the URL of this list item or {@code null}
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     * @deprecated Please use {@link #getLink()}
     */
    @Deprecated
    @Nullable
    default String getURL() {
        return null;
    }

    /**
     * Returns the title of this {@code ListItem}.
     *
     * @return the title of this list item or {@code null}
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    @Nullable
    default String getTitle() {
        return null;
    }

    /**
     * Returns the description of this {@code ListItem}.
     *
     * @return the description of this list item or {@code null}
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    @Nullable
    default String getDescription() {
        return null;
    }

    /**
     * Returns the date when this {@code ListItem} was last modified.
     *
     * @return the last modified date of this list item or {@code null}
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    @Nullable
    default Calendar getLastModified() {
        return null;
    }

    /**
     * Returns the path of this {@code ListItem}.
     *
     * @return the list item path or {@code null}
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    @Nullable
    default String getPath() {
        return null;
    }

    /**
     * Returns the name of this {@code ListItem}.
     *
     * @return the list item name or {@code null}
     * @since com.adobe.cq.wcm.core.components.models 12.6.0
     */
    @Nullable
    default String getName() {
        return null;
    }

    /**
     * Returns a wrapped resource of the item which is used to render the item as a Teaser component.
     *
     * The wrapped resource is either:
     * - the featured image of the item page, if it exists
     * - the content node of the item page, if it exists
     * - null otherwise
     *
     * @return wrapped resource of the item which can be rendered as a Teaser component
     * @since com.adobe.cq.wcm.core.components.models 12.21.0
     */
    @Nullable
    default Resource getTeaserResource() { return null;}

}
