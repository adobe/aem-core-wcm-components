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
package com.adobe.cq.wcm.core.components.internal.servlets.contentaisearch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.resource.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClient;
import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClientException;
import com.adobe.cq.wcm.core.components.services.contentai.ContentSourceListItem;
import com.adobe.cq.wcm.core.components.services.contentai.ContentSourceListResult;
import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.Value;
import com.adobe.granite.ui.components.ds.DataSource;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.adobe.cq.wcm.core.components.internal.servlets.TextValueDataResourceSource.PN_TEXT;
import static com.adobe.cq.wcm.core.components.internal.servlets.TextValueDataResourceSource.PN_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class ContentSourcesDataSourceServletTest {

    private final AemContext context = CoreComponentTestContext.newAemContext();

    private ContentSourcesDataSourceServlet underTest;
    private ContentAIClient mockClient;

    @BeforeEach
    void setUp() {
        mockClient = mock(ContentAIClient.class);
        context.registerService(ContentAIClient.class, mockClient);
        underTest = context.registerInjectActivateService(new ContentSourcesDataSourceServlet());
    }

    @Test
    void doGetReturnsFilteredPublicAcquisitionSources() throws Exception {
        ContentSourceListItem acquisition = new ContentSourceListItem();
        acquisition.setName("aem-live");
        acquisition.setDescription("Live site index");
        acquisition.setType("ACQUISITION");
        ContentSourceListItem.ContentSourceConfig config = new ContentSourceListItem.ContentSourceConfig();
        ContentSourceListItem.ContentSourceAccess access = new ContentSourceListItem.ContentSourceAccess();
        access.setPublic(true);
        config.setAccess(access);
        acquisition.setConfig(config);

        ContentSourceListItem privateSource = new ContentSourceListItem();
        privateSource.setName("internal");
        privateSource.setType("ACQUISITION");
        ContentSourceListItem.ContentSourceConfig privateConfig = new ContentSourceListItem.ContentSourceConfig();
        ContentSourceListItem.ContentSourceAccess privateAccess = new ContentSourceListItem.ContentSourceAccess();
        privateAccess.setPublic(false);
        privateConfig.setAccess(privateAccess);
        privateSource.setConfig(privateConfig);

        ContentSourceListItem otherType = new ContentSourceListItem();
        otherType.setName("author-only");
        otherType.setType("AEM_AUTHOR");

        ContentSourceListResult listResult = new ContentSourceListResult();
        listResult.setItems(List.of(acquisition, privateSource, otherType));
        when(mockClient.listContentSources(anyString(), isNull())).thenReturn(listResult);

        context.create().resource("/apps/datasource",
            "sling:resourceType", ContentSourcesDataSourceServlet.RESOURCE_TYPE);
        context.currentResource("/apps/datasource");
        context.request().setParameterMap(java.util.Map.of("contentSourceType", "ACQUISITION"));
        underTest.doGet(context.request(), context.response());

        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        List<String> values = new ArrayList<>();
        List<String> texts = new ArrayList<>();
        Iterator<Resource> iterator = dataSource.iterator();
        while (iterator.hasNext()) {
            Resource option = iterator.next();
            values.add(option.getValueMap().get(PN_VALUE, String.class));
            texts.add(option.getValueMap().get(PN_TEXT, String.class));
        }
        assertEquals(List.of("aem-live"), values);
        assertEquals(List.of("aem-live - Live site index"), texts);
    }

    @Test
    void doGetUsesConfigDescriptionWhenTopLevelMissing() throws Exception {
        ContentSourceListItem acquisition = new ContentSourceListItem();
        acquisition.setName("hotels-demo");
        acquisition.setType("ACQUISITION");
        ContentSourceListItem.ContentSourceConfig config = new ContentSourceListItem.ContentSourceConfig();
        config.setDescription("Demo hotel content index");
        ContentSourceListItem.ContentSourceAccess access = new ContentSourceListItem.ContentSourceAccess();
        access.setPublic(true);
        config.setAccess(access);
        acquisition.setConfig(config);

        ContentSourceListResult listResult = new ContentSourceListResult();
        listResult.setItems(List.of(acquisition));
        when(mockClient.listContentSources(anyString(), isNull())).thenReturn(listResult);

        context.create().resource("/apps/datasource-config-desc",
            "sling:resourceType", ContentSourcesDataSourceServlet.RESOURCE_TYPE);
        context.currentResource("/apps/datasource-config-desc");
        context.request().setParameterMap(java.util.Map.of("contentSourceType", "ACQUISITION"));
        underTest.doGet(context.request(), context.response());

        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        Resource option = dataSource.iterator().next();
        assertEquals("hotels-demo", option.getValueMap().get(PN_VALUE, String.class));
        assertEquals("hotels-demo - Demo hotel content index", option.getValueMap().get(PN_TEXT, String.class));
    }

    @Test
    void doGetReturnsEmptyOnClientError() throws Exception {
        when(mockClient.listContentSources(anyString(), isNull())).thenThrow(new ContentAIClientException("failed", 503));

        context.create().resource("/apps/datasource-error",
            "sling:resourceType", ContentSourcesDataSourceServlet.RESOURCE_TYPE);
        context.currentResource("/apps/datasource-error");
        underTest.doGet(context.request(), context.response());

        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        assertFalse(dataSource.iterator().hasNext());
    }

    @Test
    void doGetHandlesNullFirstPageGracefully() throws Exception {
        // Defensive branch: a null first-page response (rather than a thrown exception) must not NPE.
        when(mockClient.listContentSources(anyString(), isNull())).thenReturn(null);

        context.create().resource("/apps/datasource-null-first-page",
            "sling:resourceType", ContentSourcesDataSourceServlet.RESOURCE_TYPE);
        context.currentResource("/apps/datasource-null-first-page");
        underTest.doGet(context.request(), context.response());

        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        assertFalse(dataSource.iterator().hasNext());
    }

    @Test
    void doGetHandlesNullSubsequentPageGracefully() throws Exception {
        // Defensive branch: a null page returned while following the cursor must stop the loop without NPE.
        ContentSourceListItem page1Item = new ContentSourceListItem();
        page1Item.setName("page-1-source");
        page1Item.setType("ACQUISITION");
        ContentSourceListResult page1 = new ContentSourceListResult();
        page1.setItems(List.of(page1Item));
        page1.setCursor("cursor-page-2");
        when(mockClient.listContentSources(anyString(), isNull())).thenReturn(page1);
        when(mockClient.listContentSources(anyString(), eq("cursor-page-2"))).thenReturn(null);

        context.create().resource("/apps/datasource-null-next-page",
            "sling:resourceType", ContentSourcesDataSourceServlet.RESOURCE_TYPE);
        context.currentResource("/apps/datasource-null-next-page");
        context.request().setParameterMap(java.util.Map.of("contentSourceType", "ACQUISITION"));
        underTest.doGet(context.request(), context.response());

        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        List<String> values = new ArrayList<>();
        Iterator<Resource> iterator = dataSource.iterator();
        while (iterator.hasNext()) {
            values.add(iterator.next().getValueMap().get(PN_VALUE, String.class));
        }
        assertEquals(List.of("page-1-source"), values);
    }

    @Test
    void doGetHandlesPageWithNullItemsGracefully() throws Exception {
        // Defensive branch: a page response with items == null (but a non-null page) must not NPE.
        ContentSourceListResult pageWithNullItems = new ContentSourceListResult();
        when(mockClient.listContentSources(anyString(), isNull())).thenReturn(pageWithNullItems);

        context.create().resource("/apps/datasource-null-items",
            "sling:resourceType", ContentSourcesDataSourceServlet.RESOURCE_TYPE);
        context.currentResource("/apps/datasource-null-items");
        underTest.doGet(context.request(), context.response());

        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        assertFalse(dataSource.iterator().hasNext());
    }

    @Test
    void doGetFollowsCursorAcrossMultiplePages() throws Exception {
        ContentSourceListItem page1Item = new ContentSourceListItem();
        page1Item.setName("page-1-source");
        page1Item.setType("ACQUISITION");
        ContentSourceListResult page1 = new ContentSourceListResult();
        page1.setItems(List.of(page1Item));
        page1.setCursor("cursor-page-2");
        when(mockClient.listContentSources(anyString(), isNull())).thenReturn(page1);

        ContentSourceListItem page2Item = new ContentSourceListItem();
        page2Item.setName("page-2-source");
        page2Item.setType("ACQUISITION");
        ContentSourceListResult page2 = new ContentSourceListResult();
        page2.setItems(List.of(page2Item));
        // no cursor - last page
        when(mockClient.listContentSources(anyString(), eq("cursor-page-2"))).thenReturn(page2);

        context.create().resource("/apps/datasource-paged",
            "sling:resourceType", ContentSourcesDataSourceServlet.RESOURCE_TYPE);
        context.currentResource("/apps/datasource-paged");
        context.request().setParameterMap(java.util.Map.of("contentSourceType", "ACQUISITION"));
        underTest.doGet(context.request(), context.response());

        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        List<String> values = new ArrayList<>();
        Iterator<Resource> iterator = dataSource.iterator();
        while (iterator.hasNext()) {
            values.add(iterator.next().getValueMap().get(PN_VALUE, String.class));
        }
        assertEquals(List.of("page-1-source", "page-2-source"), values);
    }

    @Test
    void doGetStopsFollowingCursorAtSafetyCap() throws Exception {
        // Every page returns a fresh cursor, simulating a misbehaving/looping API; the loop must still terminate.
        when(mockClient.listContentSources(anyString(), isNull())).thenAnswer(invocation -> loopingPage("page-0"));
        when(mockClient.listContentSources(anyString(), org.mockito.ArgumentMatchers.argThat(cursor -> cursor != null)))
            .thenAnswer(invocation -> loopingPage(invocation.getArgument(1)));

        context.create().resource("/apps/datasource-looping",
            "sling:resourceType", ContentSourcesDataSourceServlet.RESOURCE_TYPE);
        context.currentResource("/apps/datasource-looping");
        context.request().setParameterMap(java.util.Map.of("contentSourceType", "ACQUISITION"));

        // Must complete without hanging/OOM despite the endless cursor.
        underTest.doGet(context.request(), context.response());

        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        int count = 0;
        Iterator<Resource> iterator = dataSource.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        assertEquals(50, count); // MAX_PAGES, one item per page
    }

    private ContentSourceListResult loopingPage(String cursorSeed) {
        ContentSourceListItem item = new ContentSourceListItem();
        item.setName("source-" + cursorSeed);
        item.setType("ACQUISITION");
        ContentSourceListResult page = new ContentSourceListResult();
        page.setItems(List.of(item));
        page.setCursor("cursor-after-" + cursorSeed); // always returns another cursor - never terminates on its own
        return page;
    }

    @Test
    void doGetIncludesPublicSourceWithNullConfig() throws Exception {
        ContentSourceListItem acquisition = new ContentSourceListItem();
        acquisition.setName("public-default");
        acquisition.setType("ACQUISITION");

        ContentSourceListResult listResult = new ContentSourceListResult();
        listResult.setItems(List.of(acquisition));
        when(mockClient.listContentSources(anyString(), isNull())).thenReturn(listResult);

        context.create().resource("/apps/datasource-null-config",
            "sling:resourceType", ContentSourcesDataSourceServlet.RESOURCE_TYPE);
        context.currentResource("/apps/datasource-null-config");
        context.request().setParameterMap(java.util.Map.of("contentSourceType", "ACQUISITION"));
        underTest.doGet(context.request(), context.response());

        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        assertEquals("public-default", dataSource.iterator().next().getValueMap().get(PN_VALUE, String.class));
    }

    @Test
    void doGetSkipsBlankIndexName() throws Exception {
        ContentSourceListItem blankName = new ContentSourceListItem();
        blankName.setType("ACQUISITION");

        ContentSourceListResult listResult = new ContentSourceListResult();
        listResult.setItems(List.of(blankName));
        when(mockClient.listContentSources(anyString(), isNull())).thenReturn(listResult);

        context.create().resource("/apps/datasource-blank",
            "sling:resourceType", ContentSourcesDataSourceServlet.RESOURCE_TYPE);
        context.currentResource("/apps/datasource-blank");
        context.request().setParameterMap(java.util.Map.of("contentSourceType", "ACQUISITION"));
        underTest.doGet(context.request(), context.response());

        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        assertFalse(dataSource.iterator().hasNext());
    }

    @Test
    void doGetResolvesTypeFromResourceProperty() throws Exception {
        ContentSourceListItem acquisition = new ContentSourceListItem();
        acquisition.setName("resource-type-source");
        acquisition.setType("CUSTOM");

        ContentSourceListResult listResult = new ContentSourceListResult();
        listResult.setItems(List.of(acquisition));
        when(mockClient.listContentSources(anyString(), isNull())).thenReturn(listResult);

        context.create().resource("/apps/datasource-resource-type",
            "sling:resourceType", ContentSourcesDataSourceServlet.RESOURCE_TYPE,
            "contentSourceType", "CUSTOM");
        context.currentResource("/apps/datasource-resource-type");
        underTest.doGet(context.request(), context.response());

        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        assertEquals("resource-type-source", dataSource.iterator().next().getValueMap().get(PN_VALUE, String.class));
    }

    @Test
    void doGetResolvesTypeFromDatasourceChild() throws Exception {
        ContentSourceListItem acquisition = new ContentSourceListItem();
        acquisition.setName("child-type-source");
        acquisition.setType("CHILD");

        ContentSourceListResult listResult = new ContentSourceListResult();
        listResult.setItems(List.of(acquisition));
        when(mockClient.listContentSources(anyString(), isNull())).thenReturn(listResult);

        context.create().resource("/apps/datasource-child-type",
            "sling:resourceType", ContentSourcesDataSourceServlet.RESOURCE_TYPE);
        context.create().resource("/apps/datasource-child-type/" + Config.DATASOURCE,
            "contentSourceType", "CHILD");
        context.currentResource("/apps/datasource-child-type");
        underTest.doGet(context.request(), context.response());

        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        assertEquals("child-type-source", dataSource.iterator().next().getValueMap().get(PN_VALUE, String.class));
    }

    @Test
    void doGetResolvesTypeFromContentPathAttribute() throws Exception {
        ContentSourceListItem acquisition = new ContentSourceListItem();
        acquisition.setName("content-path-source");
        acquisition.setType("PATH");

        ContentSourceListResult listResult = new ContentSourceListResult();
        listResult.setItems(List.of(acquisition));
        when(mockClient.listContentSources(anyString(), isNull())).thenReturn(listResult);

        context.create().resource("/content/component",
            "contentSourceType", "PATH");
        context.create().resource("/apps/datasource-content-path",
            "sling:resourceType", ContentSourcesDataSourceServlet.RESOURCE_TYPE);
        context.currentResource("/apps/datasource-content-path");
        context.request().setAttribute(Value.CONTENTPATH_ATTRIBUTE, "/content/component");
        underTest.doGet(context.request(), context.response());

        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        assertEquals("content-path-source", dataSource.iterator().next().getValueMap().get(PN_VALUE, String.class));
    }

    @Test
    void doGetUsesDefaultAcquisitionTypeWhenNoParam() throws Exception {
        ContentSourceListItem acquisition = new ContentSourceListItem();
        acquisition.setName("default-type-source");
        acquisition.setType("ACQUISITION");

        ContentSourceListResult listResult = new ContentSourceListResult();
        listResult.setItems(List.of(acquisition));
        when(mockClient.listContentSources(anyString(), isNull())).thenReturn(listResult);

        context.create().resource("/apps/datasource-default-type",
            "sling:resourceType", ContentSourcesDataSourceServlet.RESOURCE_TYPE);
        context.currentResource("/apps/datasource-default-type");
        underTest.doGet(context.request(), context.response());

        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        assertEquals("default-type-source", dataSource.iterator().next().getValueMap().get(PN_VALUE, String.class));
    }

    @Test
    void doGetIgnoresUnresolvedPlaceholderType() throws Exception {
        ContentSourceListItem acquisition = new ContentSourceListItem();
        acquisition.setName("placeholder-type-source");
        acquisition.setType("ACQUISITION");

        ContentSourceListResult listResult = new ContentSourceListResult();
        listResult.setItems(List.of(acquisition));
        when(mockClient.listContentSources(anyString(), isNull())).thenReturn(listResult);

        context.create().resource("/apps/datasource-placeholder-type",
            "sling:resourceType", ContentSourcesDataSourceServlet.RESOURCE_TYPE);
        context.currentResource("/apps/datasource-placeholder-type");
        context.request().setParameterMap(java.util.Map.of("contentSourceType", "${param.contentSourceType}"));
        underTest.doGet(context.request(), context.response());

        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        assertEquals("placeholder-type-source", dataSource.iterator().next().getValueMap().get(PN_VALUE, String.class));
    }
}
