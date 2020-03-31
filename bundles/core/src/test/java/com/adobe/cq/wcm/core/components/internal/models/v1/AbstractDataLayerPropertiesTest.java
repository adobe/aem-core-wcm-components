/*******************************************************************************
 * Copyright 2017 Adobe
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.adobe.cq.wcm.core.components.internal.models.v1;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class AbstractDataLayerPropertiesTest {

    @Test
    public void testThrowsException() {
        AbstractDataLayerProperties adlp = new MockAbstractDataLayerProperties();

        assertThrows(UnsupportedOperationException.class, adlp::getDataLayerJson, "The data layer provider does not throw exception");
    }


    private static class MockAbstractDataLayerProperties extends AbstractDataLayerProperties {

        @Override
        public boolean isDataLayerEnabled() {
            return false;
        }
    }
}
