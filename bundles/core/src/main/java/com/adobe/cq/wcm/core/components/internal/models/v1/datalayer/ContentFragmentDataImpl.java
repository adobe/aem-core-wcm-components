/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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

import com.adobe.cq.wcm.core.components.models.contentfragment.DAMContentFragment;
import com.adobe.cq.wcm.core.components.models.datalayer.ContentFragmentData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerSupplier;
import org.jetbrains.annotations.NotNull;
import java.util.function.Supplier;

public class ContentFragmentDataImpl extends ComponentDataImpl implements ContentFragmentData {

    public ContentFragmentDataImpl(@NotNull final DataLayerSupplier supplier) {
        super(supplier);
    }

    @Override
    public ElementData[] getElementsData() {
        return this.getDataLayerSupplier()
            .getContentFragmentElements()
            .map(Supplier::get)
            .orElse(null);
    }

    public static class ElementDataImpl implements ElementData {

        @NotNull
        private final DAMContentFragment.DAMContentElement contentElement;

        public ElementDataImpl(@NotNull DAMContentFragment.DAMContentElement contentElement) {
            this.contentElement = contentElement;
        }

        @Override
        public String getTitle() {
            return contentElement.getTitle();
        }

        @Override
        public String getText() {
            return contentElement.getValue(String.class);
        }
    }
}
