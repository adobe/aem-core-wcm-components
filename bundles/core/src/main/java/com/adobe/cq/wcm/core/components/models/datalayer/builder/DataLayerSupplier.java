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

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.models.datalayer.AssetData;
import com.adobe.cq.wcm.core.components.models.datalayer.ContentFragmentData;
import com.adobe.cq.wcm.core.components.models.datalayer.EmbeddableData;

/**
 * Data layer field value supplier.
 * A supplier for every possible Data Layer field is provided via this interface.
 */
public interface DataLayerSupplier {

    /**
     * An empty DataLayerSupplier which has null values for all field value suppliers.
     */
    DataLayerSupplier EMPTY_SUPPLIER = new DataLayerSupplier() {};

    /**
     * Get the ID field value supplier.
     *
     * @return The ID field value supplier, or empty if not set.
    */
    @NotNull
    default Supplier<@NotNull String> getId() {
        return () -> null;
    }

    /**
     * Get the type field value supplier.
     *
     * @return The type field value supplier, or empty if not set.
    */
    @NotNull
    default Optional<Supplier<String>> getType() {
        return Optional.empty();
    }

    /**
     * Get the last modified date field value supplier.
     *
     * @return The last modified data field value supplier, or empty if not set.
    */
    @NotNull
    default Optional<Supplier<Date>> getLastModifiedDate() {
        return Optional.empty();
    }

    /**
     * Get the parent ID field value supplier.
     *
     * @return The parent ID field value supplier, or empty if not set.
    */
    @NotNull
    default Optional<Supplier<String>> getParentId() {
        return Optional.empty();
    }

    /**
     * Get the title field value supplier.
     *
     * @return The title field value supplier, or empty if not set.
    */
    @NotNull
    default Optional<Supplier<String>> getTitle() {
        return Optional.empty();
    }

    /**
     * Get the description field value supplier.
     *
     * @return The description field value supplier, or empty if not set.
    */
    @NotNull
    default Optional<Supplier<String>> getDescription() {
        return Optional.empty();
    }

    /**
     * Get the text field value supplier.
     *
     * @return The text field value supplier, or empty if not set.
    */
    @NotNull
    default Optional<Supplier<String>> getText() {
        return Optional.empty();
    }

    /**
     * Get the link URL field value supplier.
     *
     * @return The link URL field value supplier, or empty if not set.
    */
    @NotNull
    default Optional<Supplier<String>> getLinkUrl() {
        return Optional.empty();
    }

    /**
     * Get the shown items field value supplier.
     *
     * @return The shown items field value supplier, or empty if not set.
    */
    @NotNull
    default Optional<Supplier<String[]>> getShownItems() {
        return Optional.empty();
    }

    /**
     * Get the URL field value supplier.
     *
     * @return The URL field value supplier, or empty if not set.
    */
    @NotNull
    default Optional<Supplier<String>> getUrl() {
        return Optional.empty();
    }

    /**
     * Get the format field value supplier.
     *
     * @return The format field value supplier, or empty if not set.
    */
    @NotNull
    default Optional<Supplier<String>> getFormat() {
        return Optional.empty();
    }

    /**
     * Get the tags field value supplier.
     *
     * @return The tags field value supplier, or empty if not set.
    */
    @NotNull
    default Optional<Supplier<String[]>> getTags() {
        return Optional.empty();
    }

    /**
     * Get the smart tags field value supplier.
     *
     * @return The smart tags field value supplier, or empty if not set.
     */
    @NotNull
    default Optional<Supplier<Map<String, Object>>> getSmartTags() {
        return Optional.empty();
    }

    /**
     * Get the embeddable details value supplier.
     *
     * @return The embeddable details value supplier, or empty if not set.
     */
    @NotNull
    default Optional<Supplier<Map<String, Object>>> getEmbeddableDetails() {
        return Optional.empty();
    }

    /**
     * Get the asset data field value supplier.
     *
     * @return The asset data field value supplier, or empty if not set.
    */
    @NotNull
    default Optional<Supplier<AssetData>> getAssetData()  {
        return Optional.empty();
    }


    /**
     * Get the template path field value supplier.
     *
     * @return The template path field value supplier, or empty if not set.
     */
    @NotNull
    default Optional<Supplier<String>> getTemplatePath() {
        return Optional.empty();
    }

    /**
     * Get the language field value supplier.
     *
     * @return The language field value supplier, or empty if not set.
     */
    @NotNull
    default Optional<Supplier<String>> getLanguage() {
        return Optional.empty();
    }

    /**
     * Get the content fragment elements field value supplier.
     *
     * @return The content fragment elements field value supplier, or empty if not set.
     */
    @NotNull
    default Optional<Supplier<ContentFragmentData.ElementData[]>> getContentFragmentElements() {
        return Optional.empty();
    }
}
