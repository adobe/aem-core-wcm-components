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
import java.util.Iterator;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.internal.link.LinkImpl;
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

    protected static final String MIXED_LIST_1 = TEST_PAGE_CONTENT_ROOT + "/mixedListTypeWithNoLinks";
    protected static final String MIXED_LIST_2 = TEST_PAGE_CONTENT_ROOT + "/mixedListTypeWithPageLinks";
    protected static final String MIXED_LIST_3 = TEST_PAGE_CONTENT_ROOT + "/mixedListTypeWithPageAndExternalLinks";
    protected static final String MIXED_LIST_4 = TEST_PAGE_CONTENT_ROOT + "/mixedListTypeWithPageAndExternalLinksSorted";

    protected static final String MIXED_LIST_5 = TEST_PAGE_CONTENT_ROOT + "/mixedListTypeWithPageLinksSorted";
    protected static final String MIXED_LIST_6 = TEST_PAGE_CONTENT_ROOT + "/mixedListTypeWithEmptyLink";

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
        assertEquals("/content/list/jcr:content/root/listRenderedAsTeaserItems", teaserResource0.getPath(), "image resource: path");
        assertEquals("core/wcm/components/teaser/v2/teaser", teaserResource0.getResourceType(), "image resource: resource type");
        assertEquals("/content/list/pages/page_1/page_1_1", linkURL, "image resource: linkURL");
        assertEquals("/content/dam/core/images/Adobe_Systems_logo_and_wordmark.png", fileReference, "image resource: fileReference");
        assertEquals("Page 1.1", title, "image resource: title");
        assertEquals("Description for Page 1.1", description, "image resource: description");

        // The featured image of the page does not exist: the content node of the page is used to render the teaser item
        ListItem item2 = (ListItem) items.toArray()[2];
        Resource teaserResource2 = item2.getTeaserResource();
        assertEquals("/content/list/jcr:content/root/listRenderedAsTeaserItems", teaserResource2.getPath(), "image resource: path");

        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(testBase, LIST_20));
    }

    @Test
    protected void testMixedListTypeWithNoLinks() {
        List list = getListUnderTest(MIXED_LIST_1);

        assertEquals(0, list.getListItems().size());
    }

    @Test
    protected void testMixedListTypeWithPageLinks() {
        List list = getListUnderTest(MIXED_LIST_2);

        assertFalse(list.linkItems());
        assertTrue(list.displayItemAsTeaser());
        assertFalse(list.showModificationDate());
        assertFalse(list.showDescription());

        Collection<ListItem> listItems = list.getListItems();
        assertEquals(2, listItems.size());

        Iterator<ListItem> itemIterator = listItems.iterator();
        // item 1
        ListItem item = itemIterator.next();
        assertEquals("Page One", item.getTitle());
        Link link = item.getLink();
        assertEquals("/content/list/pages/page_1.html", item.getURL());
        assertEquals("/content/list/pages/page_1.html", link.getURL());
        assertEquals("_blank", link.getHtmlAttributes().get(LinkImpl.ATTR_TARGET));
        Resource teaserResource = item.getTeaserResource();
        ValueMap teaserResourceProperties = teaserResource.getValueMap();
        assertEquals("/content/list/pages/page_1", teaserResourceProperties.get(Link.PN_LINK_URL));
        assertEquals("_blank", teaserResourceProperties.get(Link.PN_LINK_TARGET));
    }

    @Test
    protected void testMixedListTypeWithPageAndExternalLinks() {
        List list = getListUnderTest(MIXED_LIST_3);

        assertTrue(list.linkItems());
        assertFalse(list.displayItemAsTeaser());
        assertFalse(list.showModificationDate());
        assertFalse(list.showDescription());

        Collection<ListItem> listItems = list.getListItems();
        // maxItems (2) is ignored
        assertEquals(4, listItems.size());
        Iterator<ListItem> itemIterator = listItems.iterator();
        // item 1
        ListItem item = itemIterator.next();
        assertEquals("Page One", item.getTitle());
        Link link = item.getLink();
        assertEquals("/content/list/pages/page_1.html", item.getURL());
        assertEquals("/content/list/pages/page_1.html", link.getURL());
        assertEquals("_blank", link.getHtmlAttributes().get(LinkImpl.ATTR_TARGET));
        // item 2
        item = itemIterator.next();
        assertEquals("Page 2", item.getTitle());
        link = item.getLink();
        assertEquals("/content/list/pages/page_2.html", item.getURL());
        assertEquals("/content/list/pages/page_2.html", link.getURL());
        assertNull(link.getHtmlAttributes().get(LinkImpl.ATTR_TARGET));
        // item 3
        item = itemIterator.next();
        assertEquals("External Link 1", item.getTitle());
        link = item.getLink();
        assertEquals("http://www.external1.link", item.getURL());
        assertEquals("http://www.external1.link", link.getURL());
        assertEquals("_blank", link.getHtmlAttributes().get(LinkImpl.ATTR_TARGET));
        // item 4
        item = itemIterator.next();
        assertEquals("http://www.external2.link", item.getTitle());
    }

    @Test
    protected void testMixedListTypeWithPageAndExternalLinksSorted() {
        List list = getListUnderTest(MIXED_LIST_4);

        Collection<ListItem> listItems = list.getListItems();
        assertEquals(4, listItems.size());
        Iterator<ListItem> itemIterator = listItems.iterator();
        // item 1
        ListItem item = itemIterator.next();
        assertEquals("External Link 1", item.getTitle());
        // item 2
        item = itemIterator.next();
        assertEquals("http://www.external2.link", item.getTitle());
        // item 3
        item = itemIterator.next();
        assertEquals("Page 2", item.getTitle());
        // item 4
        item = itemIterator.next();
        assertEquals("Page One", item.getTitle());
    }

    @Test
    protected void testMixedListTypeWithPageLinksSorted() {
        List list = getListUnderTest(MIXED_LIST_5);
        Collection<ListItem> listItems = list.getListItems();
        assertEquals(2, listItems.size());

        Iterator<ListItem> itemIterator = listItems.iterator();
        // item 1
        ListItem item = itemIterator.next();
        assertEquals("Page Two", item.getTitle());
        // item 2
        item = itemIterator.next();
        assertEquals("Page One", item.getTitle());
    }

    @Test
    protected void testMixedListTypeWithEmptyLink() {
        List list = getListUnderTest(MIXED_LIST_6);

        assertEquals(2, list.getListItems().size());
    }
}
