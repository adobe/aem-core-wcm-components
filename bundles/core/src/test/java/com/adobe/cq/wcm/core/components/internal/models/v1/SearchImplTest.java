/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Search;
import com.adobe.cq.wcm.core.components.testing.MockStyle;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class SearchImplTest {

    private static final String TEST_BASE = "/search";

    @Rule
    public AemContext context = CoreComponentTestContext.createContext(TEST_BASE, "/content");

    private static final String TEST_ROOT = "/content/en/search/page";

    private SlingBindings slingBindings;

    @Before
    public void setUp() {
        slingBindings = (SlingBindings) context.request().getAttribute(SlingBindings.class.getName());
        slingBindings.put(WCMBindings.CURRENT_STYLE, slingBindings = (SlingBindings) context.request().getAttribute(SlingBindings.class
                .getName()));
        slingBindings.put(WCMBindings.CURRENT_PAGE, context.currentPage("/content/en/search/page"));
    }

    @Test
    public void testSearchProperties() throws Exception {
        Resource resource = context.currentResource(TEST_ROOT + "/jcr:content/search");
        slingBindings.put(WCMBindings.CURRENT_STYLE, new MockStyle(resource));
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        Search search = context.request().adaptTo(Search.class);
        assertEquals(10, search.getResultsSize());
        assertEquals(3, search.getSearchTermMinimumLength());
        assertEquals("/jcr:content/search", search.getRelativePath());
        assertEquals("core/wcm/components/search/v1/search", search.getExportedType());
    }

}