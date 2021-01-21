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

import com.adobe.cq.wcm.core.components.internal.models.v1.datalayer.builder.DataLayerSupplierImpl;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.function.Supplier;

/**
 * Data builder for all builders that produce data objects which are or extend {@link ComponentData}.

 * @param <T> The data builder type.
 * @param <K> The data type.
 */
abstract class GenericComponentDataBuilder<T extends GenericComponentDataBuilder<T, K>, K extends ComponentData>
    extends GenericDataBuilder<T, K> {

    /**
     * Construct an Abstract Data Builder.
     *
     * @param supplier The data layer supplier.
     */
    GenericComponentDataBuilder(@NotNull final DataLayerSupplier supplier) {
        super(supplier);
    }

    /**
     *  Set the supplier that supplies the component's type.
     *
     * @param supplier The type value supplier.
     * @return A new data builder.
     * @see ComponentData#getType()
     */
    @NotNull
    public T withType(@NotNull final Supplier<String> supplier) {
        return this.createInstance(new DataLayerSupplierImpl(this.getDataLayerSupplier()).setType(supplier));
    }

    /**
     * Set the supplier that supplies the component's last modified date.
     *
     * @param supplier The last modified date value supplier.
     * @return A new data builder.
     * @see ComponentData#getLastModifiedDate()
     */
    @NotNull
    public T withLastModifiedDate(@NotNull final Supplier<Date> supplier) {
        return this.createInstance(new DataLayerSupplierImpl(this.getDataLayerSupplier()).setLastModifiedDate(supplier));
    }

    /**
     * Set the supplier that supplies the component's parent ID.
     *
     * @param supplier The parent ID value supplier.
     * @return A new data builder.
     * @see ComponentData#getParentId()
     */
    @NotNull
    public T withParentId(@NotNull final Supplier<String> supplier) {
        return this.createInstance(new DataLayerSupplierImpl(this.getDataLayerSupplier()).setParentId(supplier));
    }

    /**
     * Set the supplier that supplies the component's title.
     *
     * @param supplier The title value supplier.
     * @return A new data builder.
     * @see ComponentData#getTitle()
     */
    @NotNull
    public T withTitle(@NotNull final Supplier<String> supplier) {
        return this.createInstance(new DataLayerSupplierImpl(this.getDataLayerSupplier()).setTitle(supplier));
    }

    /**
     * Set the supplier that supplies the component's description.
     *
     * @param supplier The description value supplier.
     * @return A new data builder.
     * @see ComponentData#getDescription()
     */
    @NotNull
    public T withDescription(@NotNull final Supplier<String> supplier) {
        return this.createInstance(new DataLayerSupplierImpl(this.getDataLayerSupplier()).setDescription(supplier));
    }

    /**
     * Set the supplier that supplies the component's text.
     *
     * @param supplier The text value supplier.
     * @return A new data builder.
     * @see ComponentData#getText()
     */
    @NotNull
    public T withText(@NotNull final Supplier<String> supplier) {
        return this.createInstance(new DataLayerSupplierImpl(this.getDataLayerSupplier()).setText(supplier));
    }

    /**
     * Sets the supplier that supplies the component's link URL.
     *
     * @param supplier The link URL value supplier.
     * @return A new data builder.
     * @see ComponentData#getLinkUrl()
     */
    @NotNull
    public T withLinkUrl(@NotNull final Supplier<String> supplier) {
        return this.createInstance(new DataLayerSupplierImpl(this.getDataLayerSupplier()).setLinkUrl(supplier));
    }

}
