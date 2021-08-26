/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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

import javax.annotation.PostConstruct;

import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.ProgressBar;

@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = {ProgressBar.class, ComponentExporter.class},
    resourceType = ProgressBarImpl.RESOURCE_TYPE
)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
public class ProgressBarImpl extends AbstractComponentImpl implements ProgressBar {

    protected static final String RESOURCE_TYPE = "core/wcm/components/progressbar/v1/progressbar";

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL, name = ProgressBar.PN_COMPLETED)
    @Default(intValues = 0)
    private float completed;

    @PostConstruct
    private void initModel() {
        if (completed < 0) {
            completed = 0;
        }
        if (completed > 100) {
            completed = 100;
        }
    }

    @Override
    public float getCompleted() {
        return completed;
    }

    @Override
    public float getRemaining() {
        return 100 - completed;
    }
}
