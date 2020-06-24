/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.List;
import com.day.cq.wcm.api.designer.Style;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ListImplTest {

    protected static final String TEST_BASE = "/list/v2";
    private static final String CURRENT_PAGE = "/content/list";
    private static final String CONTEXT_PATH = "/context";
    private static final String LIST_1 = "/content/list/listTypes/staticListType";

    @ClassRule
    public static final AemContext CONTEXT = CoreComponentTestContext.createContext(TEST_BASE, "/content/list");

    @BeforeClass
    public static void setUp() {
        CONTEXT.load().json("/list/test-etc.json", "/etc/tags/list");
    }

    @Test
    public void testProperties() {
        List list = getListUnderTest(LIST_1);
        assertTrue(list.showDescription());
        assertTrue(list.showModificationDate());
        assertTrue(list.linkItems());
        assertTrue(list.linkItems());
        assertEquals(2, list.getListItems().size());
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(TEST_BASE, LIST_1));
    }

    private List getListUnderTest(String resourcePath) {
        Utils.enableDataLayerForOldAemContext(CONTEXT, true);
        Resource resource = CONTEXT.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Did you forget to defines test resource " + resourcePath + "?");
        }
        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(CONTEXT.resourceResolver(), CONTEXT.bundleContext());
        request.setResource(resource);
        request.setContextPath(CONTEXT_PATH);
        SlingBindings bindings = new SlingBindings();
        bindings.put(SlingBindings.RESOURCE, resource);
        bindings.put(SlingBindings.REQUEST, request);
        bindings.put(WCMBindings.PROPERTIES, resource.getValueMap());
        Style style = mock(Style.class);
        when(style.get(any(), any(Object.class))).thenAnswer(
                invocation -> invocation.getArguments()[1]
        );
        bindings.put(WCMBindings.CURRENT_STYLE, style);
        bindings.put(WCMBindings.CURRENT_PAGE, CONTEXT.pageManager().getPage(CURRENT_PAGE));
        request.setAttribute(SlingBindings.class.getName(), bindings);
        return request.adaptTo(List.class);
    }

}
