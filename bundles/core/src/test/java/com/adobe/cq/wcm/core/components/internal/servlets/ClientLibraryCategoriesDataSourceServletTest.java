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
package com.adobe.cq.wcm.core.components.internal.servlets;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.testing.Utils;
import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.components.ds.DataSource;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
public class ClientLibraryCategoriesDataSourceServletTest {

    private static final String TEST_BASE = "/commons/datasources/clientlibrarycategories/v1";
    private static final String TEST_APPS_ROOT = "/apps";
    private static final String[] CLIENTLIB_A_CATEGORIES = new String[] { "clientlib.a.jsonly" };

    private static final String CLIENTLIB_B_PATH = "/apps/clientlibs/b-cssonly";
    private static final String[] CLIENTLIB_B_CATEGORIES = new String[] { "clientlib.b.cssonly" };

    private static final String CLIENTLIB_C_PATH = "/apps/clientlibs/c-jsandcss";
    private static final String[] CLIENTLIB_C_CATEGORIES = new String[] { "clientlib.c.jsandcss" };

    public final AemContext context = CoreComponentTestContext.newAemContext();

    private ClientLibraryCategoriesDataSourceServlet dataSourceServlet;

    @BeforeEach
    public void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, TEST_APPS_ROOT);

        dataSourceServlet = new ClientLibraryCategoriesDataSourceServlet();

        HtmlLibraryManager htmlLibraryManager = mock(HtmlLibraryManager.class);

        Map<String, ClientLibrary> clientLibraries = new HashMap<>();
        when(htmlLibraryManager.getLibraries()).thenReturn(clientLibraries);

        // A. JavaScript only
        ClientLibrary libraryA = mock(ClientLibrary.class);
        when(libraryA.getCategories()).thenReturn(CLIENTLIB_A_CATEGORIES);
        clientLibraries.put(libraryA.getPath(), libraryA);

        // B. CSS only
        ClientLibrary libraryB = mock(ClientLibrary.class);
        when(libraryB.getPath()).thenReturn(CLIENTLIB_B_PATH);
        when(libraryB.getCategories()).thenReturn(CLIENTLIB_B_CATEGORIES);
        clientLibraries.put(libraryB.getPath(), libraryB);

        // C. JavaScript and CSS
        ClientLibrary libraryC = mock(ClientLibrary.class);
        when(libraryC.getPath()).thenReturn(CLIENTLIB_C_PATH);
        when(libraryC.getCategories()).thenReturn(CLIENTLIB_C_CATEGORIES);
        clientLibraries.put(libraryC.getPath(), libraryC);

        Utils.setInternalState(dataSourceServlet, "htmlLibraryManager", htmlLibraryManager);
    }

    @Test
    public void testDataSource() throws Exception {
        context.currentResource("/apps/clientlibrarycategoriesinput");
        dataSourceServlet.doGet(context.request(), context.response());
        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        dataSource.iterator().forEachRemaining(resource -> {
            assertTrue(TextValueDataResourceSource.class.isAssignableFrom(resource.getClass()), "Expected class");
            TextValueDataResourceSource textValueDataResourceSource = (TextValueDataResourceSource) resource;
            assertTrue(Arrays.asList(CLIENTLIB_A_CATEGORIES[0], CLIENTLIB_B_CATEGORIES[0], CLIENTLIB_C_CATEGORIES[0]).contains(textValueDataResourceSource.getText()),
                "Expected type in (clientlib.a.jsonly, clientlib.b.cssonly, clientlib.c.jsandcss)");
            assertTrue(Arrays.asList(CLIENTLIB_A_CATEGORIES[0], CLIENTLIB_B_CATEGORIES[0], CLIENTLIB_C_CATEGORIES[0]).contains(textValueDataResourceSource.getValue()),
                "Expected value in (clientlib.a.jsonly, clientlib.b.cssonly, clientlib.c.jsandcss)");
        });
    }

    @Test
    public void testDataSourceTypeJS() throws Exception {
        context.currentResource("/apps/clientlibrarycategoriesinputjs");
        dataSourceServlet.doGet(context.request(), context.response());
        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        dataSource.iterator().forEachRemaining(resource -> {
            assertTrue(TextValueDataResourceSource.class.isAssignableFrom(resource.getClass()), "Expected class");
            TextValueDataResourceSource textValueDataResourceSource = (TextValueDataResourceSource) resource;
            assertTrue(Arrays.asList(CLIENTLIB_A_CATEGORIES[0], CLIENTLIB_C_CATEGORIES[0]).contains(textValueDataResourceSource.getText()),
                "Expected type in (clientlib.a.jsonly, clientlib.c.jsandcss)");
            assertTrue(Arrays.asList(CLIENTLIB_A_CATEGORIES[0], CLIENTLIB_C_CATEGORIES[0]).contains(textValueDataResourceSource.getValue()),
                "Expected value in (clientlib.a.jsonly, clientlib.c.jsandcss)");
        });
    }

    @Test
    public void testDataSourceTypeCSS() throws Exception {
        context.currentResource("/apps/clientlibrarycategoriesinputcss");
        dataSourceServlet.doGet(context.request(), context.response());
        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        dataSource.iterator().forEachRemaining(resource -> {
            assertTrue(TextValueDataResourceSource.class.isAssignableFrom(resource.getClass()), "Expected class");
            TextValueDataResourceSource textValueDataResourceSource = (TextValueDataResourceSource) resource;
            assertTrue(Arrays.asList(CLIENTLIB_B_CATEGORIES[0], CLIENTLIB_C_CATEGORIES[0]).contains(textValueDataResourceSource.getText()),
                "Expected type in (clientlib.b.cssonly, clientlib.c.jsandcss)");
            assertTrue(Arrays.asList(CLIENTLIB_B_CATEGORIES[0], CLIENTLIB_C_CATEGORIES[0]).contains(textValueDataResourceSource.getValue()),
                "Expected value in (clientlib.b.cssonly, clientlib.c.jsandcss)");
        });
    }
}
