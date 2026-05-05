/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
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
package com.adobe.cq.wcm.core.components.models.contentfragment;

import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

/**
 * HTL helper to distinguish composite JSON shapes ({@link Map} vs {@link List}) for nested fields.
 */
public class CompositeValueHelper {

    private final Object value;

    public CompositeValueHelper(@Nullable Object value) {
        this.value = value;
    }

    /**
     * @return {@code true} when the composite payload is multi-valued (JSON array of objects).
     */
    public boolean isList() {
        return value instanceof List;
    }

    /**
     * @return {@code true} when the composite payload is single-valued (JSON object).
     */
    public boolean isMap() {
        return value instanceof Map;
    }

    public Object getValue() {
        return value;
    }
}
