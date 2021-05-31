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
package com.adobe.cq.wcm.core.components.internal.models.v3;

import java.util.Collection;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.List;

import com.adobe.cq.wcm.core.components.models.ListItem;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
public class ListImplTest extends com.adobe.cq.wcm.core.components.internal.models.v1.ListImplTest {

    private static final String TEST_BASE = "/list/v3";

    @BeforeEach
    @Override
    public void setUp() {
        testBase = TEST_BASE;
        internalSetup();
        context.load().json(testBase + CoreComponentTestContext.TEST_APPS_JSON, TEST_APPS_ROOT);
    }

    @Test
    @Override
    protected void testProperties() {
        List list = getListUnderTest(LIST_1);
        assertTrue(list.showDescription());
        assertTrue(list.showModificationDate());
        assertTrue(list.linkItems());
        assertTrue(list.linkItems());
        assertEquals(2, list.getListItems().size());
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(testBase, LIST_1));
    }

    @Test
    protected void testListRenderedAsTeaserItems() {
        List list = getListUnderTest(LIST_20);
        assertEquals(3, list.getListItems().size());
        Collection<ListItem> items = list.getListItems();

        // The featured image of the page exists: the featured image node of the page is used to render the teaser item
        ListItem item0 = (ListItem) items.toArray()[0];
        Resource teaserResource0 = item0.getTeaserResource();
        ValueMap teaserProperties = teaserResource0.getValueMap();
        String linkURL = teaserProperties.get("linkURL", String.class);
        String fileReference = teaserProperties.get("fileReference", String.class);
        String title = teaserProperties.get("jcr:title", String.class);
        String description = teaserProperties.get("jcr:description", String.class);
        assertEquals("/content/list/pages/page_1/page_1_1/jcr:content/cq:featuredimage", teaserResource0.getPath(), "image resource: path");
        assertEquals("core/wcm/components/teaser/v2/teaser", teaserResource0.getResourceType(), "image resource: resource type");
        assertEquals("/content/list/pages/page_1/page_1_1", linkURL, "image resource: linkURL");
        assertEquals("/content/dam/core/images/Adobe_Systems_logo_and_wordmark.png", fileReference, "image resource: fileReference");
        assertEquals("Page 1.1", title, "image resource: title");
        assertEquals("Description for Page 1.1", description, "image resource: description");

        // The featured image of the page does not exist: the content node of the page is used to render the teaser item
        ListItem item2 = (ListItem) items.toArray()[2];
        Resource teaserResource2 = item2.getTeaserResource();
        assertEquals("/content/list/pages/page_1/page_1_3/jcr:content", teaserResource2.getPath(), "image resource: path");

        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(testBase, LIST_20));
    }

}
