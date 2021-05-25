/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Video;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.day.cq.commons.DownloadResource;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.PostConstruct;

@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = {Video.class, ComponentExporter.class},
    resourceType = VideoImpl.RESOURCE_TYPE
)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
public class VideoImpl extends AbstractComponentImpl implements Video {

    public static final String RESOURCE_TYPE = "core/wcm/components/video/v1/video";

    @ValueMapValue(name = DownloadResource.PN_REFERENCE, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String fileReference;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String posterImageReference;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(booleanValues = false)
    private boolean hideControl;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(booleanValues = false)
    private boolean loopEnabled;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(booleanValues = false)
    private boolean autoplayEnabled;

    /**
     * The current resource properties.
     */
    @ScriptVariable
    protected ValueMap properties;

    /**
     * Initialize the model.
     */
    @PostConstruct
    private void initModel() {
        posterImageReference = properties.get(Video.PN_POSTER_REFERENCE, posterImageReference);
        hideControl = properties.get(Video.PN_HIDE_CONTROL, hideControl);
        loopEnabled = properties.get(Video.PN_LOOP_ENABLED, loopEnabled);
        autoplayEnabled = properties.get(Video.PN_AUTOPLAY_ENABLED, autoplayEnabled);
    }

    @Override
    @Nullable
    public String getFileReference() {
        return fileReference;
    }

    @Override
    @Nullable
    public String getPosterImageReference() {
        return posterImageReference;
    }

    @Override
    public boolean isHideControl() {
        return hideControl;
    }

    @Override
    public boolean isLoopEnabled() {
        return loopEnabled;
    }

    @Override
    public boolean isAutoplayEnabled() {
        return autoplayEnabled;
    }

    @Override
    @NotNull
    protected ComponentData getComponentData() {
        return DataLayerBuilder.extending(super.getComponentData()).asComponent()
            .build();
    }

    @Override
    public @NotNull String getExportedType() {
        return resource.getResourceType();
    }
}
