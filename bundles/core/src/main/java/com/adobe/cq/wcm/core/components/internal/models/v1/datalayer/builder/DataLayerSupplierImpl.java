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
package com.adobe.cq.wcm.core.components.internal.models.v1.datalayer.builder;

import com.adobe.cq.wcm.core.components.models.datalayer.AssetData;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.ContainerData;
import com.adobe.cq.wcm.core.components.models.datalayer.ImageData;
import com.adobe.cq.wcm.core.components.models.datalayer.PageData;
import com.adobe.cq.wcm.core.components.models.datalayer.ContentFragmentData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerSupplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Wrapper for {@link DataLayerSupplier}.
 */
public final class DataLayerSupplierImpl implements DataLayerSupplier {

    /**
     * The wrapped data layer supplier.
     */
    @NotNull
    private final DataLayerSupplier wrappedSupplier;

    /**
     * The ID value supplier.
     */
    @Nullable
    private Supplier<@NotNull String> idSupplier;

    /**
     * The Type value supplier.
     */
    @Nullable
    private Supplier<String> typeSupplier;

    /**
     * The Type value supplier.
     */
    @Nullable
    private Supplier<Date> lastModifiedDateSupplier;

    /**
     * The parent ID value supplier.
     */
    @Nullable
    private Supplier<String> parentIdSupplier;

    /**
     * The Title value supplier.
     */
    @Nullable
    private Supplier<String> titleSupplier;

    /**
     * The Description value supplier.
     */
    @Nullable
    private Supplier<String> descriptionSupplier;

    /**
     * The Text value supplier.
     */
    @Nullable
    private Supplier<String> textSupplier;

    /**
     * The link URL value supplier.
     */
    @Nullable
    private Supplier<String> linkUrlSupplier;

    /**
     * The shown items value supplier.
     */
    @Nullable
    private Supplier<String[]> shownItemsSupplier;

    /**
     * The asset data value supplier.
     */
    @Nullable
    private Supplier<AssetData> assetDataSupplier;

    /**
     * The URL value supplier.
     */
    @Nullable
    private Supplier<String> urlSupplier;

    /**
     * The format value supplier.
     */
    @Nullable
    private Supplier<String> formatSupplier;

    /**
     * The tags value supplier.
     */
    @Nullable
    private Supplier<String[]> tagsSupplier;

    /**
     * The smart tags value supplier.
     */
    @Nullable
    private Supplier<Map<String, Object>> smartTagsSupplier;

    /**
     * The template path value supplier.
     */
    @Nullable
    private Supplier<String> templatePathSupplier;

    /**
     * The language value supplier.
     */
    @Nullable
    private Supplier<String> languageSupplier;

    /**
     * The language value supplier.
     */
    @Nullable
    private Supplier<ContentFragmentData.ElementData[]> contentFragmentElementsSupplier;

    /**
     * The embeddable value supplier;
     */
    @Nullable
    private Supplier<Map<String, Object>> embeddableSupplier;

    /**
     * Construct a wrapper for a {@link DataLayerSupplier}.
     *
     * @param dataLayerSupplier The data layer supply to wrap.
     */
    public DataLayerSupplierImpl(@NotNull final DataLayerSupplier dataLayerSupplier) {
        this.wrappedSupplier = dataLayerSupplier;
    }

    /**
     * Construct a DataLayerSupplier from existing ComponentData.
     *
     * @param data The existing component data.
     * @return A new DataLayerSupplier that uses the field values from the provided component data.
     */
    public static DataLayerSupplier extend(@NotNull final ComponentData data) {
        // set component field value suppliers
        final DataLayerSupplierImpl supplier = new DataLayerSupplierImpl(DataLayerSupplier.EMPTY_SUPPLIER)
            .setId(data::getId)
            .setType(data::getType)
            .setLastModifiedDate(data::getLastModifiedDate)
            .setParentId(data::getParentId)
            .setTitle(data::getTitle)
            .setDescription(data::getDescription)
            .setText(data::getText)
            .setLinkUrl(data::getLinkUrl);

        // set page field value suppliers
        if (data instanceof PageData) {
            PageData pageData = (PageData) data;
            supplier.setUrl(pageData::getUrl)
                .setTags(pageData::getTags)
                .setTemplatePath(pageData::getTemplatePath)
                .setLanguage(pageData::getLanguage);
        }

        // set container field value suppliers
        if (data instanceof ContainerData) {
            supplier.setShownItems(((ContainerData) data)::getShownItems);
        }

        // set image component field value suppliers
        if (data instanceof ImageData) {
            supplier.setAssetData(((ImageData) data)::getAssetData);
        }

        return supplier;
    }

    /**
     * Construct a DataLayerSupplier from existing AssetData.
     *
     * @param data The existing asset data.
     * @return A new DataLayerSupplier that uses the field values from the provided asset data.
     */
    public static DataLayerSupplier extend(@NotNull final AssetData data) {
        return new DataLayerSupplierImpl(DataLayerSupplier.EMPTY_SUPPLIER)
            .setId(data::getId)
            .setUrl(data::getUrl)
            .setFormat(data::getFormat)
            .setTags(data::getTags)
            .setLastModifiedDate(data::getLastModifiedDate);
    }

    @Override
    @NotNull
    public Supplier<@NotNull String> getId() {
        return Optional.ofNullable(this.idSupplier).orElseGet(this.wrappedSupplier::getId);
    }

    /**
     * Set the ID field value supplier.
     *
     * @param supplier The ID field value supplier.
     * @return This.
     */
    public DataLayerSupplierImpl setId(@NotNull final Supplier<@NotNull String> supplier) {
        this.idSupplier = supplier;
        return this;
    }

    @Override
    @NotNull
    public Optional<Supplier<String>> getType() {
        if (this.typeSupplier != null) {
            return Optional.of(this.typeSupplier);
        }
        return this.wrappedSupplier.getType();
    }

    /**
     * Set the type field value supplier.
     *
     * @param supplier The type field value supplier.
     * @return This.
     */
    public DataLayerSupplierImpl setType(@NotNull final Supplier<String> supplier) {
        this.typeSupplier = supplier;
        return this;
    }

    @Override
    @NotNull
    public Optional<Supplier<Date>> getLastModifiedDate() {
        if (this.lastModifiedDateSupplier != null) {
            return Optional.of(this.lastModifiedDateSupplier);
        }
        return this.wrappedSupplier.getLastModifiedDate();
    }

    /**
     * Set the last modified date field value supplier.
     *
     * @param supplier The last modified date field value supplier.
     * @return This.
     */
    public DataLayerSupplierImpl setLastModifiedDate(@NotNull final Supplier<Date> supplier) {
        this.lastModifiedDateSupplier = supplier;
        return this;
    }

    @Override
    @NotNull
    public Optional<Supplier<String>> getParentId() {
        if (this.parentIdSupplier != null) {
            return Optional.of(this.parentIdSupplier);
        }
        return this.wrappedSupplier.getParentId();
    }

    /**
     * Set the parent ID field value supplier.
     *
     * @param supplier The parent ID field value supplier.
     * @return This.
     */
    public DataLayerSupplierImpl setParentId(@NotNull final Supplier<String> supplier) {
        this.parentIdSupplier = supplier;
        return this;
    }

    @Override
    @NotNull
    public Optional<Supplier<String>> getTitle() {
        if (this.titleSupplier != null) {
            return Optional.of(this.titleSupplier);
        }
        return this.wrappedSupplier.getTitle();
    }

    /**
     * Set the title field value supplier.
     *
     * @param supplier The title field value supplier.
     * @return This.
     */
    public DataLayerSupplierImpl setTitle(@NotNull final Supplier<String> supplier) {
        this.titleSupplier = supplier;
        return this;
    }

    @Override
    @NotNull
    public Optional<Supplier<String>> getDescription() {
        if (this.descriptionSupplier != null) {
            return Optional.of(this.descriptionSupplier);
        }
        return this.wrappedSupplier.getDescription();
    }

    /**
     * Set the description field value supplier.
     *
     * @param supplier The description field value supplier.
     * @return This.
     */
    public DataLayerSupplierImpl setDescription(@NotNull final Supplier<String> supplier) {
        this.descriptionSupplier = supplier;
        return this;
    }

    @Override
    @NotNull
    public Optional<Supplier<String>> getText() {
        if (this.textSupplier != null) {
            return Optional.of(this.textSupplier);
        }
        return this.wrappedSupplier.getText();
    }

    /**
     * Set the text field value supplier.
     *
     * @param supplier The text field value supplier.
     * @return This.
     */
    public DataLayerSupplierImpl setText(@NotNull final Supplier<String> supplier) {
        this.textSupplier = supplier;
        return this;
    }

    @Override
    @NotNull
    public Optional<Supplier<String>> getLinkUrl() {
        if (this.linkUrlSupplier != null) {
            return Optional.of(this.linkUrlSupplier);
        }
        return this.wrappedSupplier.getLinkUrl();
    }

    /**
     * Set the link URL field value supplier.
     *
     * @param supplier The link URL field value supplier.
     * @return This.
     */
    public DataLayerSupplierImpl setLinkUrl(@NotNull final Supplier<String> supplier) {
        this.linkUrlSupplier = supplier;
        return this;
    }

    @Override
    @NotNull
    public Optional<Supplier<String[]>> getShownItems() {
        if (this.shownItemsSupplier != null) {
            return Optional.of(this.shownItemsSupplier);
        }
        return this.wrappedSupplier.getShownItems();
    }

    /**
     * Set the shown items field value supplier.
     *
     * @param supplier The shown items field value supplier.
     * @return This.
     */
    public DataLayerSupplierImpl setShownItems(@NotNull final Supplier<String[]> supplier) {
        this.shownItemsSupplier = supplier;
        return this;
    }

    @Override
    @NotNull
    public Optional<Supplier<AssetData>> getAssetData() {
        if (this.assetDataSupplier != null) {
            return Optional.of(this.assetDataSupplier);
        }
        return this.wrappedSupplier.getAssetData();
    }

    /**
     * Set the asset data field value supplier.
     *
     * @param supplier The asset data field value supplier.
     * @return This.
     */
    public DataLayerSupplierImpl setAssetData(@NotNull final Supplier<AssetData> supplier) {
        this.assetDataSupplier = supplier;
        return this;
    }

    @Override
    @NotNull
    public Optional<Supplier<String>> getUrl() {
        if (this.urlSupplier != null) {
            return Optional.of(this.urlSupplier);
        }
        return this.wrappedSupplier.getUrl();
    }

    /**
     * Set the URL field value supplier.
     *
     * @param supplier The URL field value supplier.
     * @return This.
     */
    public DataLayerSupplierImpl setUrl(@NotNull final Supplier<String> supplier) {
        this.urlSupplier = supplier;
        return this;
    }

    @Override
    @NotNull
    public Optional<Supplier<String>> getFormat() {
        if (this.formatSupplier != null) {
            return Optional.of(this.formatSupplier);
        }
        return this.wrappedSupplier.getFormat();
    }

    /**
     * Set the format field value supplier.
     *
     * @param supplier The format field value supplier.
     * @return This.
     */
    public DataLayerSupplierImpl setFormat(@NotNull final Supplier<String> supplier) {
        this.formatSupplier = supplier;
        return this;
    }

    @Override
    @NotNull
    public Optional<Supplier<String[]>> getTags() {
        if (this.tagsSupplier != null) {
            return Optional.of(this.tagsSupplier);
        }
        return this.wrappedSupplier.getTags();
    }

    /**
     * Set the tags field value supplier.
     *
     * @param supplier The tags field value supplier.
     * @return This.
     */
    public DataLayerSupplierImpl setTags(@NotNull final Supplier<String[]> supplier) {
        this.tagsSupplier = supplier;
        return this;
    }

    @Override
    @NotNull
    public Optional<Supplier<Map<String, Object>>> getSmartTags() {
        if (this.smartTagsSupplier != null) {
            return Optional.of(this.smartTagsSupplier);
        }
        return this.wrappedSupplier.getSmartTags();
    }

    /**
     * Set the smart tags field value supplier.
     *
     * @param supplier The smart tags field value supplier.
     * @return This.
     */
    public DataLayerSupplierImpl setSmartTags(@NotNull final Supplier<Map<String, Object>> supplier) {
        this.smartTagsSupplier = supplier;
        return this;
    }

    @Override
    @NotNull
    public Optional<Supplier<Map<String, Object>>> getEmbeddableDetails() {
        if (this.embeddableSupplier != null) {
            return Optional.of(this.embeddableSupplier);
        }
        return this.wrappedSupplier.getEmbeddableDetails();
    }

    /**
     * Sets the embeddable details value supplier.
     *
     * @param supplier The embeddable details value supplier.
     * @return This.
     */
    public DataLayerSupplierImpl setEmbeddableDetails(@NotNull final Supplier<Map<String, Object>> supplier) {
        this.embeddableSupplier = supplier;
        return this;
    }

    @Override
    @NotNull
    public Optional<Supplier<String>> getTemplatePath() {
        if (this.templatePathSupplier != null) {
            return Optional.of(this.templatePathSupplier);
        }
        return this.wrappedSupplier.getTemplatePath();
    }

    /**
     * Sets the template path field value supplier.
     *
     * @param supplier The template path field value supplier.
     * @return This.
     */
    public DataLayerSupplierImpl setTemplatePath(@NotNull final Supplier<String> supplier) {
        this.templatePathSupplier = supplier;
        return this;
    }

    @Override
    @NotNull
    public Optional<Supplier<String>> getLanguage() {
        if (this.languageSupplier != null) {
            return Optional.of(this.languageSupplier);
        }
        return this.wrappedSupplier.getLanguage();
    }

    /**
     * Set the language field value supplier.
     *
     * @param supplier The language field value supplier.
     * @return This.
     */
    public DataLayerSupplierImpl setLanguage(@NotNull final Supplier<String> supplier) {
        this.languageSupplier = supplier;
        return this;
    }

    @Override
    @NotNull
    public Optional<Supplier<ContentFragmentData.ElementData[]>> getContentFragmentElements() {
        if (this.contentFragmentElementsSupplier != null) {
            return Optional.of(this.contentFragmentElementsSupplier);
        }
        return this.wrappedSupplier.getContentFragmentElements();
    }

    /**
     * Set the content fragment elements field value supplier.
     *
     * @param supplier The content fragment elements field value supplier.
     * @return This.
     */
    public DataLayerSupplierImpl setContentFragmentElements(@NotNull final Supplier<ContentFragmentData.ElementData[]> supplier) {
        this.contentFragmentElementsSupplier = supplier;
        return this;
    }
}
