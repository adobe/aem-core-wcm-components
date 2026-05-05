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
package com.adobe.cq.wcm.core.components.internal;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.adobe.cq.dam.cfm.ContentElement;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * {@link ContentFragmentCompositeUtils} integration against CFM extensions is validated on AEM; unit tests cover
 * non-composite and absent-API paths that do not require the extensions bundle on the classpath.
 */
class ContentFragmentCompositeUtilsTest {

    @Test
    void buildCompositeExportReturnsNullForScalarElement() {
        ContentElement scalar = Mockito.mock(ContentElement.class);
        assertNull(ContentFragmentCompositeUtils.buildCompositeExport(scalar, null));
    }

    @Test
    void isCompositeElementIsFalseForPlainMock() {
        ContentElement scalar = Mockito.mock(ContentElement.class);
        assertFalse(ContentFragmentCompositeUtils.isCompositeElement(scalar));
    }
}
