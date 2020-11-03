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
package com.adobe.cq.wcm.core.components.internal.models.v1.datalayer;

import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerSupplier;
import com.adobe.cq.wcm.core.components.models.datalayer.AssetData;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;

/**
 * {@link DataLayerSupplier} backed asset data model implementation.
 *
 * @see AssetData
 */
public final class AssetDataImpl implements AssetData {

    /**
     * The current data layer supplier.
     */
    @NotNull
    private final DataLayerSupplier dataLayerSupplier;

    /**
     * The ID field value.
     */
    private String id;

    /**
     * The last modified date field value.
     */
    private Date lastModifiedDate;

    /**
     * The format field value.
     */
    private String format;

    /**
     * The URL field value.
     */
    private String url;

    /**
     * The tags field value.
     */
    private String[] tags;

    /**
     * The smart tags field value.
     */
    private Map<String, Object> smartTags;

    /**
     * Construct an AssetData model.
     *
     * @param supplier The data layer supplier.
     */
    public AssetDataImpl(@NotNull final DataLayerSupplier supplier) {
        this.dataLayerSupplier = supplier;
    }

    @Override
    @NotNull
    public String getId() {
        if (this.id == null) {
            this.id = this.dataLayerSupplier.getId().get();
        }
        return this.id;
    }

    @Override
    public Date getLastModifiedDate() {
        if (this.lastModifiedDate == null) {
            this.lastModifiedDate = this.dataLayerSupplier
                .getLastModifiedDate()
                .map(Supplier::get)
                .orElse(null);
        }
        if (this.lastModifiedDate != null) {
            return new Date(this.lastModifiedDate.getTime());
        }
        return null;
    }

    @Override
    public String getFormat() {
        if (this.format == null) {
            this.format = this.dataLayerSupplier
                .getFormat()
                .map(Supplier::get)
                .orElse(null);
        }
        return this.format;
    }

    @Override
    public String getUrl() {
        if (this.url == null) {
            this.url = this.dataLayerSupplier
                .getUrl()
                .map(Supplier::get)
                .orElse(null);
        }
        return this.url;
    }

    @Override
    public String[] getTags() {
        if (this.tags == null) {
            this.tags = this.dataLayerSupplier
                .getTags()
                .map(Supplier::get)
                .orElse(null);
        }
        if (this.tags != null) {
            return Arrays.copyOf(this.tags, this.tags.length);
        }
        return null;
    }

    @Override
    public Map<String, Object> getSmartTags() {
        if (this.smartTags == null) {
            this.smartTags = this.dataLayerSupplier
                .getSmartTags()
                .map(Supplier::get)
                .orElse(null);
        }
        return this.smartTags;
    }
}
