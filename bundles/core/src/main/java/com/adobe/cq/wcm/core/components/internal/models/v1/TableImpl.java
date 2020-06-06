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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import com.adobe.cq.wcm.core.components.models.Table;
import com.adobe.cq.wcm.core.components.services.table.ResourceReader;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, adapters = Table.class)
public class TableImpl implements Table {

    @ValueMapValue(name = "source", injectionStrategy = InjectionStrategy.OPTIONAL)
    private String source;

    @ValueMapValue(name = "propertyNames", injectionStrategy = InjectionStrategy.OPTIONAL)
    private String[] propertyNames;

    @SlingObject
    private ResourceResolver resourceResolver;

    @OSGiService
    private ResourceReader resourceReader;

    private List<String> formattedPropertyNames;
    private List<List<String>> rows;

    @PostConstruct
    public void init() throws IOException {
        formatPropertyNames();
        rows = new ArrayList<>();
        Resource resource = resourceResolver.getResource(source);
        resourceReader.readData(resource,propertyNames);
    }

    /**
     * This method formats the property name to friendly names by removing jcr: prefix. Formatted
     * property names are used to display the headers in table.
     */
    private void formatPropertyNames() {
        formattedPropertyNames = new ArrayList<>();
        for (String propertyName : propertyNames)
            if (propertyName.contains("jcr:")) {
                formattedPropertyNames.add(propertyName.substring(propertyName.indexOf("jcr:") + 4));
            } else formattedPropertyNames.add(propertyName);
    }

    @Override
    public List<String> getFormattedPropertyNames() {
        return formattedPropertyNames;
    }

    @Override
    public List<List<String>> getRows() {
        return rows;
    }
}
