/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;
import com.adobe.granite.ui.components.ds.DataSource;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientLibraryCategoriesDataSourceServletTest {

    @Rule
    public AemContext context = CoreComponentTestContext.createContext("/commons/datasources/clientlibrarycategories/v1",
        "/apps");

    private static final String CLIENTLIB_A_PATH = "/apps/clientlibs/a-jsonly";
    private static final String[] CLIENTLIB_A_CATEGORIES = new String[] { "clientlib.a.jsonly" };
    private static final Set<LibraryType> CLIENTLIB_A_TYPES = new HashSet<>(Arrays.asList(LibraryType.JS));

    private static final String CLIENTLIB_B_PATH = "/apps/clientlibs/b-cssonly";
    private static final String[] CLIENTLIB_B_CATEGORIES = new String[] { "clientlib.b.cssonly" };
    private static final Set<LibraryType> CLIENTLIB_B_TYPES = new HashSet<>(Arrays.asList(LibraryType.CSS));

    private static final String CLIENTLIB_C_PATH = "/apps/clientlibs/c-jsandcss";
    private static final String[] CLIENTLIB_C_CATEGORIES = new String[] { "clientlib.c.jsandcss" };
    private static final Set<LibraryType> CLIENTLIB_C_TYPES = new HashSet<>(Arrays.asList(LibraryType.JS, LibraryType.CSS));

    private ClientLibraryCategoriesDataSourceServlet dataSourceServlet;

    @Before
    public void setUp() throws Exception {
        dataSourceServlet = new ClientLibraryCategoriesDataSourceServlet();

        HtmlLibraryManager htmlLibraryManager = mock(HtmlLibraryManager.class);
        when(htmlLibraryManager.isMinifyEnabled()).thenReturn(false);

        Map<String, ClientLibrary> clientLibraries = new HashMap<String, ClientLibrary>();
        when(htmlLibraryManager.getLibraries()).thenReturn(clientLibraries);

        // A. JavaScript only
        ClientLibrary libraryA = mock(ClientLibrary.class);
        when(libraryA.allowProxy()).thenReturn(true);
        when(libraryA.getPath()).thenReturn(CLIENTLIB_A_PATH);
        when(libraryA.getCategories()).thenReturn(CLIENTLIB_A_CATEGORIES);
        when(libraryA.getTypes()).thenReturn(CLIENTLIB_A_TYPES);
        clientLibraries.put(libraryA.getPath(), libraryA);

        // B. CSS only
        ClientLibrary libraryB = mock(ClientLibrary.class);
        when(libraryB.allowProxy()).thenReturn(false);
        when(libraryB.getPath()).thenReturn(CLIENTLIB_B_PATH);
        when(libraryB.getCategories()).thenReturn(CLIENTLIB_B_CATEGORIES);
        when(libraryB.getTypes()).thenReturn(CLIENTLIB_B_TYPES);
        clientLibraries.put(libraryB.getPath(), libraryB);

        // C. JavaScript and CSS
        ClientLibrary libraryC = mock(ClientLibrary.class);
        when(libraryC.allowProxy()).thenReturn(true);
        when(libraryC.getPath()).thenReturn(CLIENTLIB_C_PATH);
        when(libraryC.getCategories()).thenReturn(CLIENTLIB_C_CATEGORIES);
        when(libraryC.getTypes()).thenReturn(CLIENTLIB_C_TYPES);
        clientLibraries.put(libraryC.getPath(), libraryC);

        Whitebox.setInternalState(dataSourceServlet, "htmlLibraryManager", htmlLibraryManager);
    }

    @Test
    public void testDataSource() throws Exception {
        context.currentResource("/apps/clientlibrarycategoriesinput");
        dataSourceServlet.doGet(context.request(), context.response());
        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        dataSource.iterator().forEachRemaining(resource -> {
            assertTrue("Expected class", TextValueDataResourceSource.class.isAssignableFrom(resource.getClass()));
            TextValueDataResourceSource textValueDataResourceSource = (TextValueDataResourceSource) resource;
            assertTrue("Expected type in (clientlib.a.jsonly, clientlib.b.cssonly, clientlib.c.jsandcss)",
                Arrays.asList(CLIENTLIB_A_CATEGORIES[0], CLIENTLIB_B_CATEGORIES[0], CLIENTLIB_C_CATEGORIES[0]).contains(textValueDataResourceSource.getText()));
            assertTrue("Expected value in (clientlib.a.jsonly, clientlib.b.cssonly, clientlib.c.jsandcss)",
                Arrays.asList(CLIENTLIB_A_CATEGORIES[0], CLIENTLIB_B_CATEGORIES[0], CLIENTLIB_C_CATEGORIES[0]).contains(textValueDataResourceSource.getValue()));
        });
    }

    @Test
    public void testDataSourceTypeJS() throws Exception {
        context.currentResource("/apps/clientlibrarycategoriesinputjs");
        dataSourceServlet.doGet(context.request(), context.response());
        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        dataSource.iterator().forEachRemaining(resource -> {
            assertTrue("Expected class", TextValueDataResourceSource.class.isAssignableFrom(resource.getClass()));
            TextValueDataResourceSource textValueDataResourceSource = (TextValueDataResourceSource) resource;
            assertTrue("Expected type in (clientlib.a.jsonly, clientlib.c.jsandcss)",
                Arrays.asList(CLIENTLIB_A_CATEGORIES[0], CLIENTLIB_C_CATEGORIES[0]).contains(textValueDataResourceSource.getText()));
            assertTrue("Expected value in (clientlib.a.jsonly, clientlib.c.jsandcss)",
                Arrays.asList(CLIENTLIB_A_CATEGORIES[0], CLIENTLIB_C_CATEGORIES[0]).contains(textValueDataResourceSource.getValue()));
        });
    }

    @Test
    public void testDataSourceTypeCSS() throws Exception {
        context.currentResource("/apps/clientlibrarycategoriesinputcss");
        dataSourceServlet.doGet(context.request(), context.response());
        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        dataSource.iterator().forEachRemaining(resource -> {
            assertTrue("Expected class", TextValueDataResourceSource.class.isAssignableFrom(resource.getClass()));
            TextValueDataResourceSource textValueDataResourceSource = (TextValueDataResourceSource) resource;
            assertTrue("Expected type in (clientlib.b.cssonly, clientlib.c.jsandcss)",
                Arrays.asList(CLIENTLIB_B_CATEGORIES[0], CLIENTLIB_C_CATEGORIES[0]).contains(textValueDataResourceSource.getText()));
            assertTrue("Expected value in (clientlib.b.cssonly, clientlib.c.jsandcss)",
                Arrays.asList(CLIENTLIB_B_CATEGORIES[0], CLIENTLIB_C_CATEGORIES[0]).contains(textValueDataResourceSource.getValue()));
        });
    }
}
