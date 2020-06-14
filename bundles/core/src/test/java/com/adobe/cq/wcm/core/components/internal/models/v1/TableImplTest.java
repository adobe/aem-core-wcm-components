/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.services.table.CSVResourceProcessor;
import com.adobe.cq.wcm.core.components.internal.services.table.DefaultResourceProcessor;
import com.adobe.cq.wcm.core.components.models.Table;
import com.adobe.cq.wcm.core.components.models.Title;
import com.adobe.cq.wcm.core.components.services.table.ResourceProcessor;
import com.day.cq.dam.api.Asset;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.FieldSetter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class TableImplTest {

    private static final String TEST_BASE = "/table";
    private static final String CONTENT_ROOT = "/content";
    private static final String TEST_ROOT_PAGE = CONTENT_ROOT + TEST_BASE;
    private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    private static final String TABLE_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/table-1";
    private static final String[] HEADER_NAMES = new String[]{"email", "firstName", "gender", "title"};
    private static final String DESCRIPTION = "Dummy description";
    private static final String SOURCE = "/content/dam/test.csv";
    private static final String TABLE_DESCRIPTION = "This is sample Table Description";
    private static final String TABLE_ARIA_LABEL="Sample Table";
    private static final String DAM_ASSET = "dam:Asset";

    private final AemContext context = CoreComponentTestContext.newAemContext();


    @InjectMocks
    TableImpl table;

    @Mock
    private List<ResourceProcessor> resourceProcessors;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    Resource sourceResource;

    @Mock
    DefaultResourceProcessor defaultResourceProcessor;

    @Mock
    CSVResourceProcessor csvResourceProcessor;

    @Mock
    Asset asset;

    @BeforeEach
    void setUp() throws NoSuchFieldException {
        MockitoAnnotations.initMocks(this);
        FieldSetter.setField(table, getField("resourceResolver"), resourceResolver);
        FieldSetter.setField(table, getField("resourceProcessors"), resourceProcessors);
        FieldSetter.setField(table, getField("headerNames"), HEADER_NAMES);
        FieldSetter.setField(table, getField("description"), DESCRIPTION);
        FieldSetter.setField(table, getField("source"), SOURCE);
        List<ResourceProcessor> resourceProcessors = new ArrayList<>();
        resourceProcessors.add(defaultResourceProcessor);
        resourceProcessors.add(csvResourceProcessor);
        FieldSetter.setField(table, getField("resourceProcessors"), resourceProcessors);
    }

    private Field getField(final String fieldName) throws NoSuchFieldException {
        return table.getClass().getDeclaredField(fieldName);
    }

    @Test
    void testEmptyTable() throws IOException {
        Table table = new TableImpl();
        List<List<String>> items = table.getItems();
        assertTrue("", CollectionUtils.isEmpty(items));
    }

    @Test
    void testGetFormattedHeaders() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.registerService(ResourceProcessor.class, new DefaultResourceProcessor());

        Table table = getTableUnderTest();
        List<String> expectedFormattedHeadersList = Arrays.asList(HEADER_NAMES);
        List<String> formattedHeaders = table.getFormattedHeaderNames();
        assertEquals(expectedFormattedHeadersList, formattedHeaders);
    }

    @Test
    void testGetDescription() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.registerService(ResourceProcessor.class, new DefaultResourceProcessor());
        Table table = getTableUnderTest();
        assertEquals(TABLE_DESCRIPTION, table.getDescription());
    }

    @Test
    void testGetAriaLabel() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.registerService(ResourceProcessor.class, new DefaultResourceProcessor());
        Table table = getTableUnderTest();
        assertEquals(TABLE_ARIA_LABEL, table.getAriaLabel());
    }
    @Test
    void testTableWithItems() throws IOException {
        List<List<String>> rows = setMockRows();
        when(resourceResolver.getResource(SOURCE)).thenReturn(sourceResource);
        when(sourceResource.getResourceType()).thenReturn(DAM_ASSET);
        when(asset.getMimeType()).thenReturn(StringUtils.EMPTY);
        when(sourceResource.adaptTo(Asset.class)).thenReturn(asset);
        when(defaultResourceProcessor.canProcess(StringUtils.EMPTY)).thenReturn(true);
        when(defaultResourceProcessor.processData(sourceResource, HEADER_NAMES)).thenReturn(rows);
        assertEquals(rows, table.getItems());
    }

    @NotNull
    private List<List<String>> setMockRows() {
        List<List<String>> rows = new ArrayList<>();
        List<String> row1 = new ArrayList<>();
        row1.add("sample1@sample.com");
        row1.add("sample-1");
        row1.add("male");
        row1.add("Active-1");
        rows.add(row1);
        List<String> row2 = new ArrayList<>();
        row2.add("sample-2");
        row2.add("female");
        row2.add("Active-2");
        rows.add(row2);
        List<String> row3 = new ArrayList<>();

        row3.add("sample-3");
        row3.add("male");
        row3.add("Active-3");
        rows.add(row3);
        return rows;
    }


    private Table getTableUnderTest() {
        Utils.enableDataLayer(context, true);
        Resource resource = context.currentResource(TABLE_1);

        if (resource == null) {
            throw new IllegalStateException("Did you forget to define test resource " + TABLE_1 + "?");
        }

        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(context.resourceResolver(),
            context.bundleContext());
        SlingBindings bindings = new SlingBindings();
        bindings.put(SlingBindings.RESOURCE, resource);
        bindings.put(SlingBindings.REQUEST, request);
        bindings.put(WCMBindings.PROPERTIES, resource.getValueMap());
        request.setResource(resource);
        request.setAttribute(SlingBindings.class.getName(), bindings);
        return request.adaptTo(Table.class);
    }
}
