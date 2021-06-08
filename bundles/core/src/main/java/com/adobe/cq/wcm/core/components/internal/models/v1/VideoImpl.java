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
import com.adobe.cq.wcm.core.components.models.datalayer.AssetData;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.AssetDataBuilder;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.day.cq.dam.api.Asset;
import com.day.cq.rewriter.linkchecker.LinkChecker;
import com.day.cq.rewriter.linkchecker.LinkCheckerSettings;
import com.day.cq.rewriter.linkchecker.LinkValidity;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Reference;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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

    @ValueMapValue(name = "videoFileReference", injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String fileReference;

    @ValueMapValue(name = "posterImageReference", injectionStrategy = InjectionStrategy.OPTIONAL)
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
        final BundleContext bundleContext = FrameworkUtil.getBundle(VideoImpl.class).getBundleContext();
        final ServiceReference<?> factoryReference = bundleContext.getServiceReference(LinkChecker.class.getName());
        final LinkChecker checker = (LinkChecker) bundleContext.getService(factoryReference);
        final LinkCheckerSettings linkCheckerSettings = checker.createSettings(this.request);
        final LinkValidity validity = checker.getLink(fileReference, linkCheckerSettings).getValidity();
        if (validity.equals(LinkValidity.VALID)) {
            return fileReference;
        } else {
            return null;
        }
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
        return DataLayerBuilder.extending(super.getComponentData())
            .asVideoComponent()
            .withAssetData(dataSupplier)
            .build();
    }

    @Override
    public @NotNull String getExportedType() {
        return resource.getResourceType();
    }

    private final Supplier<AssetData> dataSupplier = () ->
        Optional.ofNullable(this.fileReference)
            .map(reference -> this.request.getResourceResolver().getResource(reference))
            .map(assetResource -> assetResource.adaptTo(Asset.class))
            .map(DataLayerBuilder::forAsset)
            .map(AssetDataBuilder::build)
            .orElse(null);

    private final Supplier<LinkChecker> linkCheckerServiceSupplier = () -> {
        final BundleContext bundleContext = FrameworkUtil.getBundle(VideoImpl.class).getBundleContext();
        final ServiceReference<?> factoryReference = bundleContext.getServiceReference(LinkChecker.class.getName());
        return (LinkChecker) bundleContext.getService(factoryReference);
    };

    private final Predicate<String> checkResource = value ->
        Optional.ofNullable(this.request.getResourceResolver().getResource(value)).isPresent();
}
