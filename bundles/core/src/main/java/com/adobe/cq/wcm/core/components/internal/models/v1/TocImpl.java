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

import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.models.Toc;
import com.adobe.cq.wcm.core.components.models.TocItem;
import com.day.cq.commons.jcr.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;

import java.util.ArrayList;
import java.util.List;

@Model(adaptables = SlingHttpServletRequest.class,
    adapters = {Toc.class, ComponentExporter.class},
    resourceType = {TocImpl.RESOURCE_TYPE}
    )
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
public class TocImpl implements Toc {


    public static final String RESOURCE_TYPE = "core/wcm/components/toc/v1/toc";
    private static final String PROP_DEFAULT_TITLE = "Table of Content";

    @Self
    private SlingHttpServletRequest slingRequest;
    private ResourceResolver resourceResolver;

    private List<TocItem> items;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL, name = JcrConstants.JCR_TITLE)
    @Default(values = PROP_DEFAULT_TITLE)
    private String title;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String listType;


    @Override
    public String getTitle() { return title; }

    @Override
    public String getType() {
        if (listType != null) {
            return listType;
        }
        return null;
    }

    @Override
    public List<TocItem> getItems() {
        if (items == null) {
            items = new ArrayList<>();
            Resource parent = slingRequest.getResource().getParent();
            resourceResolver = slingRequest.getResourceResolver();
            if (parent != null) {
            Iterable<Resource> children = parent.getChildren();
                if(children!=null){
                    for(Resource resource : children) {
                        String resourceType = resource.getResourceType();
                        if (resourceResolver.isResourceType(resource, resourceType)) {
                            TocItem item = getItemLevel(resource);
                            if(item != null) {
                                items.add(item);
                            }
                        }
                    }
                }

            }
        }
        return items;
    }

    private TocItem getItemLevel(Resource resource) {
        String type = null;
        String title = null;
        int level = 1;
        ValueMap properties = resource.adaptTo(ValueMap.class);
        if(properties!= null) {
            type = properties.get("type", String.class);
            title = properties.get("jcr:title", String.class);
        }
        if(title == null) {
            return null;
        } else
        if(type != null) {
            switch (Utils.Heading.getHeading(type)) {
                case H1:
                    level = 1;
                    break;
                case H2:
                    level = 2;
                    break;
                case H3:
                    level = 3;
                    break;
                case H4:
                    level = 4;
                    break;
                case H5:
                    level = 5;
                    break;
                case H6:
                    level = 6;
                    break;
                default:
                    break;
            }
        }
        return new TocItemImpl(level, title);
    }

    @Nonnull
    @Override
    public String getExportedType() {
        return slingRequest.getResource().getResourceType();
    }

}
