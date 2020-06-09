
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
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Objects.isNull;

@Component(service = ResourceProcessor.class, immediate = true)
public class DefaultResourceProcessor implements ResourceProcessor {

    @Reference
    private ResourceResolver resourceResolver;

    @Override
    public List<List<String>> processData(Resource resource, String[] headerNames) throws IOException {
        List<List<String>> rows = new ArrayList<>();
        Iterator<Resource> children = resourceResolver.listChildren(resource);
        if(isNull(children)){
            return rows;
        }
        while (children.hasNext()) {
            Resource child = children.next();
            ValueMap props = child.adaptTo(ValueMap.class);
            List<String> row = new ArrayList<>();
            for (String propertyName : headerNames) {
                String propValue = props != null ? props.get(propertyName, StringUtils.EMPTY) : StringUtils.EMPTY;
                row.add(propValue);
            }
            rows.add(row);
        }
        return rows;
    }

    @Override
    public Boolean canProcess(String mimeType) {
        return mimeType == null || StringUtils.EMPTY.equals(mimeType);
    }
}
