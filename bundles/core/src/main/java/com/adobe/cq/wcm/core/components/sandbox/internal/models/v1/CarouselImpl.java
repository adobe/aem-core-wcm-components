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
package com.adobe.cq.wcm.core.components.sandbox.internal.models.v1;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.sandbox.models.Carousel;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Carousel.class, ComponentExporter.class}, resourceType = CarouselImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME , extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class CarouselImpl implements Carousel {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarouselImpl.class);

    public final static String RESOURCE_TYPE = "core/wcm/sandbox/components/carousel/v1/carousel";

    @Inject
    private Resource resource;

    @Self
    private SlingHttpServletRequest request;

    public boolean isExpanded() {
        String[] selectors = request.getRequestPathInfo().getSelectors();
        for (String selector : selectors) {
            if (StringUtils.equals("expanded", selector)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasItems() {
        Iterable<Resource> iterable = getItems();
        if (iterable != null) {
            return iterable.iterator().hasNext();
        }
        return false;
    }

    public Iterable<Resource> getItems() {
        Resource container = getContainer();
        if (container != null) {
            return container.getChildren();
        }
        return null;
    }

    public Resource getContainer() {
        return resource.getChild("container");
    }

    @Nonnull
    @Override
    public String getExportedType() {
        return request.getResource().getResourceType();
    }

}
