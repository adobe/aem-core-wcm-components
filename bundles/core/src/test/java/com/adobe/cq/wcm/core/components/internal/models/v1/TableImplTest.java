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
import com.adobe.cq.wcm.core.components.models.Table;
import com.adobe.cq.wcm.core.components.services.table.ResourceProcessor;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.FieldSetter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class TableImplTest {

    private static final String TEST_BASE = "/table";
    private static final String CONTENT_ROOT = "/content";
    private static final String TEST_ROOT_PAGE = CONTENT_ROOT + TEST_BASE;
    private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    private static final String TABLE_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/table-1";
    private static final String[] HEADER_NAMES = new String[] {"email", "firstName", "gender", "title"};
    private static final String DESCRIPTION = "Dummy description";

    private final AemContext context = CoreComponentTestContext.newAemContext();


    @InjectMocks
    TableImpl table;

    @Mock
    private List<ResourceProcessor> resourceProcessors;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    Resource resource;

    @BeforeEach
    void setUp() throws NoSuchFieldException {
        MockitoAnnotations.initMocks(this);
        FieldSetter.setField(table, getField("resourceResolver"), resourceResolver);
        FieldSetter.setField(table, getField("resourceProcessors"), resourceProcessors);
        FieldSetter.setField(table, getField("headerNames"), HEADER_NAMES);
        FieldSetter.setField(table, getField("description"), DESCRIPTION);
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
        Table table = getTableUnderTest();
        List<String> expectedFormattedHeadersList = Arrays.asList(HEADER_NAMES);
        List<String> formattedHeaders = table.getFormattedHeaderNames();
        assertEquals(expectedFormattedHeadersList, formattedHeaders);
    }

    @Test
    void testGetDescription() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        Table table = getTableUnderTest();
        assertEquals("This is sample Table Description", table.getDescription());

    }

    @Test
    void testTableWithItems() throws IOException {

        List<List<String>> rows = new ArrayList<>();
        List<String> row = new ArrayList<>();
        row.add("sample1@sample.com");
        row.add("sample-1");
        row.add("male");
        row.add("Active-1");
        rows.add(row);
        row.clear();
        row.add("sample-2");
        row.add("female");
        row.add("Active-2");
        rows.add(row);
        row.clear();
        row.add("sample-3");
        row.add("male");
        row.add("Active-3");
        rows.add(row);

//        context.registerService(ResourceProcessor.class, resourceProcessors);
//        Table table = getTableUnderTest();

        when(resourceResolver.getResource("/test/path")).thenReturn(resource);
        ResourceProcessor resourceProcessor = mock(ResourceProcessor.class);

        Iterator<ResourceProcessor> itr = mock(Iterator.class);
        Mockito.when(itr.hasNext()).thenReturn(true, false);
        Mockito.when(itr.next()).thenReturn(Mockito.any(ResourceProcessor.class));
        when(resourceProcessor.canProcess("")).thenReturn(true);
        when(resourceProcessor.processData(resource, HEADER_NAMES)).thenReturn(rows);
        assertEquals(rows, table.getItems());
        Utils.testJSONExport(table, Utils.getTestExporterJSONPath(TEST_BASE, "table-1"));
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
