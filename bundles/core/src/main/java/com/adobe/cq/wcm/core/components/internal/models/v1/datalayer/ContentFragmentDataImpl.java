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

import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractComponentImpl;
import com.adobe.cq.wcm.core.components.internal.models.v1.contentfragment.ContentFragmentImpl;
import com.adobe.cq.wcm.core.components.models.contentfragment.DAMContentFragment;
import com.adobe.cq.wcm.core.components.models.datalayer.ContentFragmentData;
import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ContentFragmentDataImpl extends ComponentDataImpl implements ContentFragmentData {

    public ContentFragmentDataImpl(@NotNull AbstractComponentImpl component, @NotNull Resource resource) {
        super(component, resource);
    }

    @Override
    public ElementData[] getElementsData() {
        List<DAMContentFragment.DAMContentElement> elements = ((ContentFragmentImpl)component).getElements();
        if (elements == null) {
            return null;
        }

        List<ElementData> elementsData = new ArrayList<>();
        for (DAMContentFragment.DAMContentElement contentElement : elements) {
            elementsData.add(new ElementDataImpl(contentElement));
        }
        return elementsData.toArray(new ElementData[0]);
    }

    static class ElementDataImpl implements ElementData {

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
            String value = contentElement.getValue(String.class);
            return value != null ? value.toString() : null;
        }
    }
}
