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
        assertEquals(TableOfContentsImpl.DEFAULT_START_LEVEL, tableOfContents.getStartLevel());
        assertEquals(TableOfContentsImpl.DEFAULT_STOP_LEVEL, tableOfContents.getStopLevel());
        assertEquals(TableOfContentsImpl.DEFAULT_LIST_TYPE, tableOfContents.getListType());
        assertNull(tableOfContents.getIncludeClasses());
        assertNull(tableOfContents.getIgnoreClasses());
        Utils.testJSONExport(tableOfContents, Utils.getTestExporterJSONPath(TEST_BASE, TOC_DEFAULT));
    }

    @Test
    void testTableOfContentsDefaultWithPolicy() {
        TableOfContents.ListType listType = TableOfContents.ListType.numbered;
        TableOfContents.HeadingLevel startLevel = TableOfContents.HeadingLevel.h2;
        TableOfContents.HeadingLevel stopLevel = TableOfContents.HeadingLevel.h4;
        String[] includeClasses = new String[]{"toc-include-1", "toc-include-2"};
        String[] ignoreClasses = new String[]{"toc-ignore-1", "toc-ignore-2"};
        context.contentPolicyMapping(TableOfContentsImpl.RESOURCE_TYPE,
            TableOfContents.PN_RESTRICT_LIST_TYPE, listType.getValue(),
            TableOfContents.PN_RESTRICT_START_LEVEL, startLevel.getValue(),
            TableOfContents.PN_RESTRICT_STOP_LEVEL, stopLevel.getValue(),
            TableOfContents.PN_INCLUDE_CLASSES, includeClasses,
            TableOfContents.PN_IGNORE_CLASSES, ignoreClasses
        );
        TableOfContents tableOfContents = getTableOfContentsUnderTest(TOC_DEFAULT_WITH_POLICY);
        assertEquals(startLevel, tableOfContents.getStartLevel());
        assertEquals(stopLevel, tableOfContents.getStopLevel());
        assertEquals(listType, tableOfContents.getListType());
        assertArrayEquals(includeClasses, tableOfContents.getIncludeClasses());
        assertArrayEquals(ignoreClasses, tableOfContents.getIgnoreClasses());
        Utils.testJSONExport(tableOfContents, Utils.getTestExporterJSONPath(TEST_BASE, TOC_DEFAULT_WITH_POLICY));
    }

    @Test
    void testTableOfContentsConfigured() {
        TableOfContents tableOfContents = getTableOfContentsUnderTest(TOC_CONFIGURED);
        assertEquals(TableOfContents.HeadingLevel.h2, tableOfContents.getStartLevel());
        assertEquals(TableOfContents.HeadingLevel.h5, tableOfContents.getStopLevel());
        assertEquals(TableOfContents.ListType.numbered, tableOfContents.getListType());
        assertNull(tableOfContents.getIncludeClasses());
        assertNull(tableOfContents.getIgnoreClasses());
        Utils.testJSONExport(tableOfContents, Utils.getTestExporterJSONPath(TEST_BASE, TOC_CONFIGURED));
    }

    @Test
    void testTableOfContentsConfiguredWithNoRestrictionPolicy() {
        context.contentPolicyMapping(TableOfContentsImpl.RESOURCE_TYPE,
            TableOfContents.PN_RESTRICT_LIST_TYPE, TableOfContentsImpl.NO_RESTRICTION,
            TableOfContents.PN_RESTRICT_START_LEVEL, TableOfContentsImpl.NO_RESTRICTION,
            TableOfContents.PN_RESTRICT_STOP_LEVEL, TableOfContentsImpl.NO_RESTRICTION
        );
        TableOfContents tableOfContents = getTableOfContentsUnderTest(TOC_CONFIGURED);
        assertEquals(TableOfContents.HeadingLevel.h2, tableOfContents.getStartLevel());
        assertEquals(TableOfContents.HeadingLevel.h5, tableOfContents.getStopLevel());
        assertEquals(TableOfContents.ListType.numbered, tableOfContents.getListType());
        assertNull(tableOfContents.getIncludeClasses());
        assertNull(tableOfContents.getIgnoreClasses());
        Utils.testJSONExport(tableOfContents, Utils.getTestExporterJSONPath(TEST_BASE, TOC_CONFIGURED));
    }

    @Test
    void testTableOfContentsConfiguredWithPolicy() {
        TableOfContents.ListType listType = TableOfContents.ListType.bulleted;
        TableOfContents.HeadingLevel stopLevel = TableOfContents.HeadingLevel.h4;
        String[] ignoreClasses = new String[]{"toc-ignore-1", "toc-ignore-2"};
        context.contentPolicyMapping(TableOfContentsImpl.RESOURCE_TYPE,
            TableOfContents.PN_RESTRICT_LIST_TYPE, listType.getValue(),
            TableOfContents.PN_RESTRICT_START_LEVEL, TableOfContentsImpl.NO_RESTRICTION,
            TableOfContents.PN_RESTRICT_STOP_LEVEL, stopLevel.getValue(),
            TableOfContents.PN_IGNORE_CLASSES, ignoreClasses
        );
        TableOfContents tableOfContents = getTableOfContentsUnderTest(TOC_CONFIGURED_WITH_POLICY);
        assertEquals(TableOfContents.HeadingLevel.h2, tableOfContents.getStartLevel());
        assertEquals(stopLevel, tableOfContents.getStopLevel());
        assertEquals(listType, tableOfContents.getListType());
        assertNull(tableOfContents.getIncludeClasses());
        assertArrayEquals(ignoreClasses, tableOfContents.getIgnoreClasses());
        Utils.testJSONExport(tableOfContents, Utils.getTestExporterJSONPath(TEST_BASE, TOC_CONFIGURED_WITH_POLICY));
    }

    private TableOfContents getTableOfContentsUnderTest(String resourcePath) {
        Utils.enableDataLayer(context, true);
        context.currentResource(resourcePath);
        MockSlingHttpServletRequest request = context.request();
        return request.adaptTo(TableOfContents.class);
    }
}
