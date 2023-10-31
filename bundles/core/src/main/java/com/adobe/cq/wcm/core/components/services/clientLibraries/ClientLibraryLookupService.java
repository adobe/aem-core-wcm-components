/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2023 Adobe
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
package com.adobe.cq.wcm.core.components.services.clientLibraries;

import com.adobe.granite.ui.clientlibs.ClientLibrary;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Service for looking up client libraries belonging to resource types.
 *
 * @since com.adobe.cq.wcm.core.components.services.clientLibraries 1.0.0
 */
public interface ClientLibraryLookupService {

    /**
     * Gets all the client libraries for a given set of resource types.
     *
     * @param inherited Flag indicating if super resource types should be included.
     * @param resourceResolver A resource resolver - does not have to be a special resource resolver.
     * @return Set of all client libraries.
     */
    @NotNull
    Set<ClientLibrary> getAllClientLibraries(@NotNull Set<String> resourceTypes, boolean inherited, @NotNull ResourceResolver resourceResolver);
}
