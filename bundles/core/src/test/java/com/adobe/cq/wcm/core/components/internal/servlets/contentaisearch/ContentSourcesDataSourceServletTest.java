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
import com.adobe.cq.wcm.core.components.services.contentai.ContentSourceListItem;
import com.adobe.cq.wcm.core.components.services.contentai.ContentSourceListResult;
import com.adobe.granite.ui.components.ds.DataSource;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.adobe.cq.wcm.core.components.internal.servlets.TextValueDataResourceSource.PN_TEXT;
import static com.adobe.cq.wcm.core.components.internal.servlets.TextValueDataResourceSource.PN_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        when(mockClient.listContentSources()).thenReturn(listResult);

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
        when(mockClient.listContentSources()).thenReturn(listResult);

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
}
