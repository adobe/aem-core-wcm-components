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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import javax.annotation.Nonnull;

import com.adobe.cq.wcm.core.components.models.TableOfContent;
import com.adobe.cq.wcm.core.components.models.TableOfContentItem;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;

import java.util.ArrayList;
import java.util.List;

@Model(adaptables = SlingHttpServletRequest.class,
    adapters = {TableOfContent.class, ComponentExporter.class},
    resourceType = {TableOfContentImpl.RESOURCE_TYPE}
    )
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
public class TableOfContentImpl implements  TableOfContent {


    public static final String RESOURCE_TYPE = "core/wcm/components/tableOfContent/v1/tableOfContent";
    private static final String PROP_DEFAULT_TITLE = "Table of Content";
    private static final String TITLE_V1 = "core/wcm/components/title/v1/title";
    private static final String TITLE_V2 = "core/wcm/components/title/v2/title";

    @Self
    private SlingHttpServletRequest slingRequest;

    private List<TableOfContentItem> items;

    @ValueMapValue
    @Default(values = PROP_DEFAULT_TITLE)
    private String title;


    @Override
    public String getTitle() { return title; }

    @Override
    public List<TableOfContentItem> getItems() {
        if (items == null) {
            items = new ArrayList<>();
            Resource parent = slingRequest.getResource().getParent();
            if (parent != null) {
            Iterable<Resource> children = parent.getChildren();

                for(Resource resource : children) {
                    String resourceType = resource.getResourceType();
                    if (resourceType.equals(TITLE_V1) || resourceType.equals(TITLE_V2)) {
                        TableOfContentItem item = getItemLevel(resource);
                        items.add(item);
                    }
                }
            }
        }
        return items;
    }

    private TableOfContentItem getItemLevel(Resource resource) {
        String type = null;
        String title = null;
        int level = -1;
        ValueMap properties = resource.adaptTo(ValueMap.class);
        if(properties!= null) {
            type = properties.get("type", String.class);
            title = properties.get("jcr:title", String.class);
        }
        if(type != null) {
            switch (type) {
                case "h1":
                    level = 1;
                    break;
                case "h2":
                    level = 2;
                    break;
                case "h3":
                    level = 3;
                    break;
                case "h4":
                    level = 4;
                    break;
                case "h5":
                    level = 5;
                    break;
                case "h6":
                    level = 6;
                    break;
                default:
                    level = 0;
                    break;
            }
        }
        return new TableOfContentItemImpl(level, title);
    }

    @Nonnull
    @Override
    public String getExportedType() {
        return slingRequest.getResource().getResourceType();
    }

}
