
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
package com.adobe.cq.wcm.core.components.internal.services.table;


import com.adobe.cq.wcm.core.components.services.table.ResourceProcessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Component(service = ResourceProcessor.class, immediate = true)
public class DefaultResourceProcessor implements ResourceProcessor {

    @Override
    public List<List<String>> processData(@NotNull Resource resource, String[] headerNames) {
        if (!resource.hasChildren()) {
            return new ArrayList<>();
        }

        return StreamSupport.stream(resource.getChildren().spliterator(), false)
                            .map(Resource::getValueMap)
                            .map(props -> getTableRowData(headerNames, props))
                            .collect(toList());
    }

    private List<String> getTableRowData(String[] headerNames, ValueMap props) {
        return Arrays.stream(headerNames)
                     .map(headerName -> props.get(headerName, StringUtils.EMPTY))
                     .collect(toList());
    }

    @Override
    public boolean canProcess(String mimeType) {
        return StringUtils.isBlank(mimeType);
    }
}
