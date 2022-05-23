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
package com.adobe.cq.wcm.core.components.services.link;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.commons.link.Link;

/**
 * Interface for resolving links.
 */
public interface LinkHandler {

    /**
     * Returns a link based on the configuration defined in the link builder.
     *
     * @param builder {@link LinkBuilder} holding the configuration for the link.
     * @return {@link Optional} of  {@link Link}
     */
    Optional<Link> getLink(@NotNull LinkBuilder builder);

}
