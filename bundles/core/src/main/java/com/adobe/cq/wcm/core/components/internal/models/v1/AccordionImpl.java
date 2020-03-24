/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.models.Accordion;
import com.day.cq.wcm.api.designer.Style;

@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = { Accordion.class, ComponentExporter.class, ContainerExporter.class },
    resourceType = AccordionImpl.RESOURCE_TYPE
)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
public class AccordionImpl extends PanelContainerImpl implements Accordion {

    public final static String RESOURCE_TYPE = "core/wcm/components/accordion/v1/accordion";

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private boolean singleExpansion;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String[] expandedItems;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String headingElement;

    @ScriptVariable
    private Style currentStyle;

    /**
     * The cached node names of the expanded items for which there is a valid matching child resource.
     */
    private String[] expandedItemNames;
    private String[] expandedItemIds;

    /**
     * The {@link com.adobe.cq.wcm.core.components.internal.Utils.Heading} object for the HTML element
     * to use for accordion headers.
     */
    private Utils.Heading heading;

    @Override
    public boolean isSingleExpansion() {
        return singleExpansion;
    }

    @Override
    public String[] getExpandedItems() {
        if (expandedItemNames == null) {
            List<String> expanded = new ArrayList<>();
            if (expandedItems != null) {
                for (String expandedItemName : expandedItems) {
                    Resource child = resource.getChild(expandedItemName);
                    if (child != null) {
                        expanded.add(expandedItemName);
                    }
                }
            }
            if (!expanded.isEmpty()) {
                expandedItemNames = expanded.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
            } else {
                expandedItemNames = ArrayUtils.EMPTY_STRING_ARRAY;
            }
        }
        return Arrays.copyOf(expandedItemNames, expandedItemNames.length);
    }

    @Override
    public String getHeadingElement() {
        if (heading == null) {
            heading = Utils.Heading.getHeading(headingElement);
            if (heading == null) {
                heading = Utils.Heading.getHeading(currentStyle.get(PN_DESIGN_HEADING_ELEMENT, String.class));
            }
        }
        if (heading != null) {
            return heading.getElement();
        }
        return null;
    }

    /*
     * DataLayerProvider implementation of field getters
     */

    @Override
    public String getDataLayerName() {
        return resource.getName();
    }

    @Override
    public int getDataLayerItemsCount() {
        return getItems().size();
    }

    @Override
    public String[] getDataLayerExpandedItems() {
        if (expandedItems == null) {
            return new String[0];
        }

        if (expandedItemIds == null) {
            List<String> expandedItemsName = Arrays.asList(expandedItems);
            List<String> expandedItemsIds = this.getItems().stream()
                .filter(item -> expandedItemsName.contains(item.getName()))
                .map(item -> item.getDataLayerId())
                .collect(Collectors.toList());

            String[] expandedItems = new String[expandedItemsIds.size()];

            for (int i =0; i < expandedItemsIds.size(); i++)
                expandedItems[i] = expandedItemsIds.get(i);

            expandedItemIds = expandedItems;
        }

        return Arrays.copyOf(expandedItemIds, expandedItemIds.length);
    }
}
