/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.sandbox.internal.models;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.sandbox.models.Carousel;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentManager;

// TODO: remove the Tabs resource type once the Tabs has its onw Sling model
@Model(adaptables = SlingHttpServletRequest.class, adapters = {Carousel.class, ComponentExporter.class}, resourceType = {CarouselImpl.RESOURCE_TYPE, "core/wcm/sandbox/components/tabs/v1/tabs"})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME , extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class CarouselImpl implements Carousel {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarouselImpl.class);

    public final static String RESOURCE_TYPE = "core/wcm/sandbox/components/carousel/v1/carousel";

    @SlingObject
    private Resource resource;

    @Self
    private SlingHttpServletRequest request;

    private List<ListItem> items;

    @PostConstruct
    private void initModel() {
        readItems();
    }

    private void readItems() {
        items = new ArrayList<>();
        if (resource != null) {
            ComponentManager componentManager = request.getResourceResolver().adaptTo(ComponentManager.class);
            for (Resource res : resource.getChildren()) {
                Component component = componentManager.getComponentOfResource(res);
                if (component != null) {
                    items.add(new ResourceListItemImpl(request, res));
                }
            }
        }
    }

    @Override
    public List<ListItem> getItems() {
        return items;
    }
}
