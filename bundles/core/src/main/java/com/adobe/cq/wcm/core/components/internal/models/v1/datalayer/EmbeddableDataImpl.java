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
package com.adobe.cq.wcm.core.components.internal.models.v1.datalayer;

import java.util.Map;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.models.datalayer.EmbeddableData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerSupplier;

public class EmbeddableDataImpl extends ComponentDataImpl implements EmbeddableData {

    private Map<String, Object> embeddableDetails;

    public EmbeddableDataImpl(@NotNull final DataLayerSupplier supplier) {
        super(supplier);
    }

    @Override
    public Map<String, Object> getEmbeddableDetails() {
        if (this.embeddableDetails == null) {
            this.embeddableDetails = this.getDataLayerSupplier()
                    .getEmbeddableDetails()
                    .map(Supplier::get)
                    .orElse(null);
        }
        return this.embeddableDetails;
    }
}
