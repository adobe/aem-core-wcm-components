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

import com.adobe.cq.wcm.core.components.internal.models.v1.datalayer.PageDataImpl;
import com.adobe.cq.wcm.core.components.internal.models.v1.datalayer.builder.DataLayerSupplierImpl;
import com.adobe.cq.wcm.core.components.models.datalayer.PageData;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Data layer builder for pages.
 * This builder will produce a valid {@link PageData} object.
 */
public final class PageDataBuilder extends GenericComponentDataBuilder<PageDataBuilder, PageData> {

    /**
     * Construct a data layer builder for a page.
     *
     * @param supplier The data layer supplier.
     */
    PageDataBuilder(@NotNull final DataLayerSupplier supplier) {
        super(supplier);
    }

    /**
     * Set the supplier that supplies the page's tags data.
     *
     * @param supplier The tags data value supplier.
     * @return A new {@link PageDataBuilder}.
     * @see PageData#getTags()
     */
    @NotNull
    public PageDataBuilder withTags(@NotNull final Supplier<String[]> supplier) {
        return this.createInstance(new DataLayerSupplierImpl(this.getDataLayerSupplier()).setTags(supplier));
    }

    /**
     * Set the supplier that supplies the page's URL data.
     *
     * @param supplier The URL data value supplier.
     * @return A new {@link PageDataBuilder}.
     * @see PageData#getUrl()
     */
    @NotNull
    public PageDataBuilder withUrl(@NotNull final Supplier<String> supplier) {
        return this.createInstance(new DataLayerSupplierImpl(this.getDataLayerSupplier()).setUrl(supplier));
    }

    /**
     * Set the supplier that supplies the page's template path data.
     *
     * @param supplier The template path data value supplier.
     * @return A new {@link PageDataBuilder}.
     * @see PageData#getTemplatePath()
     */
    @NotNull
    public PageDataBuilder withTemplatePath(@NotNull final Supplier<String> supplier) {
        return this.createInstance(new DataLayerSupplierImpl(this.getDataLayerSupplier()).setTemplatePath(supplier));
    }

    /**
     * Set the supplier that supplies the page's language data.
     *
     * @param supplier The language data value supplier.
     * @return A new {@link PageDataBuilder}.
     * @see PageData#getLanguage()
     */
    @NotNull
    public PageDataBuilder withLanguage(@NotNull final Supplier<String> supplier) {
        return this.createInstance(new DataLayerSupplierImpl(this.getDataLayerSupplier()).setLanguage(supplier));
    }

    @Override
    @NotNull
    PageDataBuilder createInstance(@NotNull final DataLayerSupplier supplier) {
        return new PageDataBuilder(supplier);
    }

    @NotNull
    @Override
    public PageData build() {
        return new PageDataImpl(this.getDataLayerSupplier());
    }
}
