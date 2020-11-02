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
import com.adobe.cq.wcm.core.components.models.datalayer.PageData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * {@link DataLayerSupplier} backed page component data implementation.
 */
public final class PageDataImpl extends ComponentDataImpl implements PageData {

    /**
     * The template path field value.
     */
    private String templatePath;

    /**
     * The language field value.
     */
    private String language;

    /**
     * The tags field value.
     */
    private String[] tags;

    /**
     * The URL field value.
     */
    private String url;

    /**
     * Construct the data layer model.
     *
     * @param supplier The data layer supplier.
     */
    public PageDataImpl(@NotNull final DataLayerSupplier supplier) {
        super(supplier);
    }

    @Override
    @Nullable
    public String getTemplatePath() {
        if (this.templatePath == null) {
            this.templatePath = this.getDataLayerSupplier()
                .getTemplatePath()
                .map(Supplier::get)
                .orElse(null);
        }
        return this.templatePath;
    }

    @Override
    @Nullable
    public String getLanguage() {
        if (this.language == null) {
            this.language = this.getDataLayerSupplier()
                .getLanguage()
                .map(Supplier::get)
                .orElse(null);
        }
        return this.language;
    }

    @Override
    @Nullable
    public String[] getTags() {
        if (this.tags == null) {
            this.tags = this.getDataLayerSupplier()
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
    @Nullable
    public String getUrl() {
        if (this.url == null) {
            this.url = this.getDataLayerSupplier()
                .getUrl()
                .map(Supplier::get)
                .orElse(null);
        }
        return this.url;
    }
}
