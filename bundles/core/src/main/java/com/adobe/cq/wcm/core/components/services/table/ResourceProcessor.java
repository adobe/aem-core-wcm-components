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
package com.adobe.cq.wcm.core.components.services.table;

import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

/**
 * A service which process data based on mime type
 *
 * @since com.adobe.cq.wcm.core.components.services.table.ResourceProcess 12.10.0
 */
public interface ResourceProcessor {
    List<List<String>> processData(@NotNull Resource resource, String[] propertyNames) throws IOException;

    boolean canProcess(String mimeType);
}
