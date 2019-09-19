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
package com.adobe.cq.wcm.core.components.internal;

import java.util.Map;

import org.apache.sling.testing.mock.osgi.MapUtil;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class UtilsTest {

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @Test
    void selectorToMap() {
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) context.request().getRequestPathInfo();
        assertThrows(IllegalArgumentException.class, () -> {
            requestPathInfo.setSelectorString("img.foo");
            Utils.selectorToMap(context.request());
        });
        requestPathInfo.setSelectorString("img.foo.bar");
        validateMap(Utils.selectorToMap(context.request()), "foo", "bar");

    }

    private void validateMap(Map<String, String> actual, String... expected) {
        Map<String, Object> expectedMap = MapUtil.toMap(expected);
        assertEquals(actual.size(), expectedMap.size());
        for (String key : expectedMap.keySet()) {
            assertTrue(actual.containsKey(key));
            assertEquals(expectedMap.get(key), actual.get(key));
        }
    }
}