/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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
package com.adobe.cq.wcm.core.components.commons.link;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builds a link and sets link properties.
 */
public interface LinkBuilder {

    /**
     * Sets the property name to read the link URL from.
     *
     * @param name The property name
     * @return {@link LinkBuilder}
     */
    @NotNull
    LinkBuilder setLinkURLPropertyName(@Nullable String name);

    /**
     * Sets the link target.
     *
     * @param target The link target
     * @return {@link LinkBuilder}
     */
    @NotNull
    LinkBuilder setLinkTarget(@Nullable String target);

    /**
     * Sets the link attributes.
     *
     * @param attributes The link attributes
     * @return {@link LinkBuilder}
     */
    @NotNull
    LinkBuilder setLinkAttributes(@NotNull Map<String, String> attributes);

    /**
     * Returns the resolved link.
     *
     * @return {@link Link}
     */
    Link build();
}
