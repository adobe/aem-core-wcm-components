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
package com.adobe.cq.wcm.core.components.internal.models;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Carousel;
import com.day.cq.wcm.api.designer.Style;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Carousel.class, ComponentExporter.class}, resourceType = CarouselImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class CarouselImpl extends AbstractContainerImpl implements Carousel {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarouselImpl.class);

    public static final String RESOURCE_TYPE = "core/wcm/components/carousel/v1/carousel";
    protected static final Long DEFAULT_DELAY = 5000L; // milliseconds

    @ScriptVariable
    protected Style currentStyle;

    @ScriptVariable
    protected ValueMap properties;

    protected boolean autoplay;
    protected Long delay;
    protected boolean autopauseDisabled;

    @PostConstruct
    protected void initModel() {
        autoplay = properties.get(PN_AUTOPLAY, currentStyle.get(PN_AUTOPLAY, false));
        delay = properties.get(PN_DELAY, currentStyle.get(PN_DELAY, DEFAULT_DELAY));
        autopauseDisabled = properties.get(PN_AUTOPAUSE_DISABLED, currentStyle.get(PN_AUTOPAUSE_DISABLED, false));
    }

    @Override
    public boolean getAutoplay() {
        return autoplay;
    }

    @Override
    public Long getDelay() {
        return delay;
    }

    @Override
    public boolean getAutopauseDisabled() {
        return autopauseDisabled;
    }

}
