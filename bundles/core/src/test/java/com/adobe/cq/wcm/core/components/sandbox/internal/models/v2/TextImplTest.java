/*******************************************************************************
 * Copyright 2017 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.sandbox.internal.models.v2;

import org.junit.Test;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.sandbox.models.Text;

import static org.junit.Assert.assertEquals;

public class TextImplTest extends com.adobe.cq.wcm.core.components.internal.models.v1.TextImplTest {

    protected static String getTestBase() {
        return "/sandbox/text";
    }

    @Test
    public void testExportedType() {
        Text text = getTextUnderTest(Text.class, TEXT_1);
        assertEquals("core/wcm/sandbox/components/text/v2/text", text.getExportedType());
        Utils.testJSONExport(text, Utils.getTestExporterJSONPath(getTestBase(), TEXT_1));
    }
}
