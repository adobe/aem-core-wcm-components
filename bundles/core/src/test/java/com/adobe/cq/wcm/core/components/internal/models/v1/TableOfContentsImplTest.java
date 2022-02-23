/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.TableOfContents;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.servlethelpers.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(AemContextExtension.class)
public class TableOfContentsImplTest {

    private static final String TEST_BASE = "/tableofcontents";

    private static final String TEST_ROOT_PAGE = "/content/toc-page";
    private static final String TOC_DEFAULT = TEST_ROOT_PAGE + "/jcr:content/par/tableofcontents-default";
    private static final String TOC_DEFAULT_WITH_POLICY =
        TEST_ROOT_PAGE + "/jcr:content/par/tableofcontents-default-with-policy";
    private static final String TOC_CONFIGURED = TEST_ROOT_PAGE + "/jcr:content/par/tableofcontents-configured";
    private static final String TOC_CONFIGURED_WITH_POLICY =
        TEST_ROOT_PAGE + "/jcr:content/par/tableofcontents-configured-with-policy";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + "/test-content.json", TEST_ROOT_PAGE);
    }

    @Test
    void testTableOfContentsDefault() {
        TableOfContents tableOfContents = getTableOfContentsUnderTest(TOC_DEFAULT);
        assertEquals(TableOfContentsImpl.DEFAULT_TITLE_START_LEVEL, tableOfContents.getTitleStartLevel());
        assertEquals(TableOfContentsImpl.DEFAULT_TITLE_STOP_LEVEL, tableOfContents.getTitleStopLevel());
        assertEquals(TableOfContentsImpl.DEFAULT_LIST_TYPE, tableOfContents.getListType());
        assertNull(tableOfContents.getIncludeClassNames());
        assertNull(tableOfContents.getIgnoreClassNames());
        Utils.testJSONExport(tableOfContents, Utils.getTestExporterJSONPath(TEST_BASE, TOC_DEFAULT));
    }

    @Test
    void testTableOfContentsDefaultWithPolicy() {
        String listType = "ordered";
        Integer titleStartLevel = 2;
        Integer titleStopLevel = 4;
        String[] includeClassNames = new String[]{"toc-include-1", "toc-include-2"};
        String[] ignoreClassNames = new String[]{"toc-ignore-1", "toc-ignore-2"};
        context.contentPolicyMapping(TableOfContentsImpl.RESOURCE_TYPE,
            TableOfContents.PN_RESTRICT_LIST_TYPE, listType,
            TableOfContents.PN_RESTRICT_TITLE_START_LEVEL, titleStartLevel,
            TableOfContents.PN_RESTRICT_TITLE_STOP_LEVEL, titleStopLevel,
            TableOfContents.PN_INCLUDE_CLASS_NAMES, includeClassNames,
            TableOfContents.PN_IGNORE_CLASS_NAMES, ignoreClassNames
        );
        TableOfContents tableOfContents = getTableOfContentsUnderTest(TOC_DEFAULT_WITH_POLICY);
        assertEquals(titleStartLevel, tableOfContents.getTitleStartLevel());
        assertEquals(titleStopLevel, tableOfContents.getTitleStopLevel());
        assertEquals(listType, tableOfContents.getListType());
        assertArrayEquals(includeClassNames, tableOfContents.getIncludeClassNames());
        assertArrayEquals(ignoreClassNames, tableOfContents.getIgnoreClassNames());
        Utils.testJSONExport(tableOfContents, Utils.getTestExporterJSONPath(TEST_BASE, TOC_DEFAULT_WITH_POLICY));
    }

    @Test
    void testTableOfContentsConfigured() {
        TableOfContents tableOfContents = getTableOfContentsUnderTest(TOC_CONFIGURED);
        assertEquals(2, (int)tableOfContents.getTitleStartLevel());
        assertEquals(5, (int)tableOfContents.getTitleStopLevel());
        assertEquals("ordered", tableOfContents.getListType());
        assertNull(tableOfContents.getIncludeClassNames());
        assertNull(tableOfContents.getIgnoreClassNames());
        Utils.testJSONExport(tableOfContents, Utils.getTestExporterJSONPath(TEST_BASE, TOC_CONFIGURED));
    }

    @Test
    void testTableOfContentsConfiguredWithNoRestrictionPolicy() {
        context.contentPolicyMapping(TableOfContentsImpl.RESOURCE_TYPE,
            TableOfContents.PN_RESTRICT_LIST_TYPE, TableOfContentsImpl.NO_RESTRICTION,
            TableOfContents.PN_RESTRICT_TITLE_START_LEVEL, TableOfContentsImpl.NO_RESTRICTION,
            TableOfContents.PN_RESTRICT_TITLE_STOP_LEVEL, TableOfContentsImpl.NO_RESTRICTION
        );
        TableOfContents tableOfContents = getTableOfContentsUnderTest(TOC_CONFIGURED);
        assertEquals(2, (int)tableOfContents.getTitleStartLevel());
        assertEquals(5, (int)tableOfContents.getTitleStopLevel());
        assertEquals("ordered", tableOfContents.getListType());
        assertNull(tableOfContents.getIncludeClassNames());
        assertNull(tableOfContents.getIgnoreClassNames());
        Utils.testJSONExport(tableOfContents, Utils.getTestExporterJSONPath(TEST_BASE, TOC_CONFIGURED));
    }

    @Test
    void testTableOfContentsConfiguredWithPolicy() {
        String listType = "unordered";
        Integer titleStopLevel = 4;
        String[] ignoreClassNames = new String[]{"toc-ignore-1", "toc-ignore-2"};
        context.contentPolicyMapping(TableOfContentsImpl.RESOURCE_TYPE,
            TableOfContents.PN_RESTRICT_LIST_TYPE, listType,
            TableOfContents.PN_RESTRICT_TITLE_START_LEVEL, TableOfContentsImpl.NO_RESTRICTION,
            TableOfContents.PN_RESTRICT_TITLE_STOP_LEVEL, titleStopLevel,
            TableOfContents.PN_IGNORE_CLASS_NAMES, ignoreClassNames
        );
        TableOfContents tableOfContents = getTableOfContentsUnderTest(TOC_CONFIGURED_WITH_POLICY);
        assertEquals(2, (int)tableOfContents.getTitleStartLevel());
        assertEquals(4, (int)tableOfContents.getTitleStopLevel());
        assertEquals(listType, tableOfContents.getListType());
        assertNull(tableOfContents.getIncludeClassNames());
        assertArrayEquals(ignoreClassNames, tableOfContents.getIgnoreClassNames());
        Utils.testJSONExport(tableOfContents, Utils.getTestExporterJSONPath(TEST_BASE, TOC_CONFIGURED_WITH_POLICY));
    }

    private TableOfContents getTableOfContentsUnderTest(String resourcePath) {
        Utils.enableDataLayer(context, true);
        context.currentResource(resourcePath);
        MockSlingHttpServletRequest request = context.request();
        return request.adaptTo(TableOfContents.class);
    }
}
