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
package com.adobe.cq.wcm.core.components.models.datalayer.builder;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.internal.models.v1.datalayer.builder.DataLayerSupplierImpl;
import com.adobe.cq.wcm.core.components.models.datalayer.AssetData;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.ContainerData;
import com.adobe.cq.wcm.core.components.models.datalayer.ImageData;
import com.adobe.cq.wcm.core.components.models.datalayer.PageData;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.tagging.TagConstants;

import static com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerSupplier.EMPTY_SUPPLIER;

/**
 * Data layer builder utility.
 *
 * This utility is designed to help build valid data models for integration with
 * <a href="https://github.com/adobe/adobe-client-data-layer">Adobe Client Data Layer</a>.
 */
public final class DataLayerBuilder {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private DataLayerBuilder() {
        // NOOP
    }

    /**
     * Get a ComponentDataBuilder for a component.
     *
     * @return An empty ComponentDataBuilder.
     */
    public static IdRequiredDataBuilder<ComponentDataBuilder, ComponentData> forComponent() {
        return new IdRequiredDataBuilder<>(new ComponentDataBuilder(EMPTY_SUPPLIER));
    }

    /**
     * Get a ComponentDataBuilder for a container component
     *
     * @return An empty ContainerDataBuilder.
     */
    public static IdRequiredDataBuilder<ContainerDataBuilder, ContainerData> forContainer() {
        return new IdRequiredDataBuilder<>(new ContainerDataBuilder(EMPTY_SUPPLIER));
    }

    /**
     * Get a ImageComponentDataBuilder for an image component.
     *
     * @return An empty ImageComponentDataBuilder.
     */
    public static IdRequiredDataBuilder<ImageComponentDataBuilder, ImageData> forImageComponent() {
        return new IdRequiredDataBuilder<>(new ImageComponentDataBuilder(EMPTY_SUPPLIER));
    }

    /**
     * Get a PageDataBuilder for a page.
     *
     * @return An empty PageDataBuilder.
     */
    public static IdRequiredDataBuilder<PageDataBuilder, PageData> forPage() {
        return new IdRequiredDataBuilder<>(new PageDataBuilder(EMPTY_SUPPLIER));
    }

    /**
     * Get a AssetDataBuilder for an asset.
     *
     * @return An empty AssetDataBuilder.
     */
    public static IdRequiredDataBuilder<AssetDataBuilder, AssetData> forAsset() {
        return new IdRequiredDataBuilder<>(new AssetDataBuilder(EMPTY_SUPPLIER));
    }

    /**
     * Get an AssetDataBuilder with standard asset data.
     * This builder is suitable for most DAM Assets and pre-populates all required fields from the asset metadata.
     *
     * @param asset The asset used to initialize the AssetDataBuilder.
     * @return A new AssetDataBuilder pre-initialized using the DAM asset metadata.
     */
    public static AssetDataBuilder forAsset(@NotNull final Asset asset) {
        return DataLayerBuilder.forAsset()
            .withId(asset::getID)
            .withFormat(asset::getMimeType)
            .withUrl(asset::getPath)
            .withLastModifiedDate(() -> new Date(
                Optional.of(asset.getLastModified())
                    .filter(lastMod -> lastMod > 0)
                    .orElseGet(() -> Optional.ofNullable(asset.adaptTo(ValueMap.class))
                        .map(vm -> vm.get(JcrConstants.JCR_CREATED, Calendar.class))
                        .map(Calendar::getTimeInMillis)
                        .orElse(0L))))
            .withTags(() ->
                Optional.ofNullable(asset.getMetadataValueFromJcr(TagConstants.PN_TAGS))
                    .filter(StringUtils::isNotEmpty)
                    .map(tagsValue -> tagsValue.split(","))
                    .map(Arrays::stream)
                    .orElseGet(Stream::empty)
                    .filter(StringUtils::isNotEmpty)
                    .toArray(String[]::new))
            .withSmartTags(() -> {
                Map<String, Object> smartTags = new HashMap<>();
                Optional.ofNullable(asset.adaptTo(Resource.class))
                    .map(assetResource -> assetResource.getChild(DamConstants.PREDICTED_TAGS))
                    .map(predictedTagsResource -> {
                        for (Resource smartTagResource : predictedTagsResource.getChildren()) {
                            Optional.ofNullable(smartTagResource.adaptTo(ValueMap.class))
                                .map(props -> Optional.ofNullable(props.get(AssetDataBuilder.SMARTTAG_NAME_PROP))
                                    .map(tagName -> Optional.ofNullable(smartTags.put((String)tagName, props.get(AssetDataBuilder.SMARTTAG_CONFIDENCE_PROP)))
                                    ));
                        }
                        return Optional.empty();
                    });
                return smartTags;
            });
    }

    /**
     * Get a AssetDataBuilder that extends existing asset data.
     *
     * @param assetData The asset data to extend.
     * @return A new AssetDataBuilder pre-initialized with the existing asset data.
     */
    public static AssetDataBuilder extending(@NotNull final AssetData assetData) {
        return new AssetDataBuilder(DataLayerSupplierImpl.extend(assetData));
    }

    /**
     * Extend an existing component data layer model.
     *
     * @param componentData The component data to extend.
     * @return The component data layer extender.
     */
    public static ComponentDataLayerExtender extending(@NotNull final ComponentData componentData) {
        return new ComponentDataLayerExtender(componentData);
    }
}
