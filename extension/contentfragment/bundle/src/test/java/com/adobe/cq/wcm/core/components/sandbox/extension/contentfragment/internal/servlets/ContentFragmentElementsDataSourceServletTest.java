/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.sandbox.extension.contentfragment.internal.servlets;

import java.io.IOException;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.servlethelpers.MockSlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.ExpressionResolver;
import com.adobe.granite.ui.components.ds.DataSource;
import io.wcm.testing.mock.aem.junit.AemContext;

import static com.adobe.cq.wcm.core.components.sandbox.extension.contentfragment.internal.models.v1.ContentFragmentImplTest.ADAPTER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContentFragmentElementsDataSourceServletTest {

    /* path and names of the datasource instances to test */

    private static final String DATASOURCES_PATH                         = "/content/datasources";
    private static final String DS_NO_CONFIG                             = "no-config";
    private static final String DS_FRAGMENT_PATH_NON_EXISTING            = "fragment-path-non-existing";
    private static final String DS_COMPONENT_PATH__NON_EXISTING          = "component-path-non-existing";
    private static final String DS_FRAGMENT_PATH_INVALID                 = "fragment-path-invalid";
    private static final String DS_COMPONENT_PATH_INVALID                = "component-path-invalid";
    private static final String DS_COMPONENT_PATH__INVALID_FRAGMENT_PATH = "component-path-invalid-fragment-path";
    private static final String DS_FRAGMENT_PATH_TEXT_ONLY               = "fragment-path-text-only";
    private static final String DS_COMPONENT_PATH_TEXT_ONLY              = "component-path-text-only";
    private static final String DS_FRAGMENT_PATH_STRUCTURED              = "fragment-path-structured";
    private static final String DS_COMPONENT_PATH_STRUCTURED             = "component-path-structured";
    private static final String DS_FRAGMENT_PATH_OVERRIDE                = "fragment-path-override";

    /* names and titles of the elements of both the text-only and structured content fragment */

    private static final String[] ELEMENT_NAMES = {"main", "second"};
    private static final String[] ELEMENT_TITLES = {"Main", "Second"};

    @ClassRule
    public static final AemContext AEM_CONTEXT = CoreComponentTestContext.createContext("/contentfragment", "/content");

    @BeforeClass
    public static void setUp() throws Exception {
        // load the content fragments
        AEM_CONTEXT.load().json("/contentfragment/test-content-dam.json", "/content/dam/contentfragments");
        // load the data sources
        AEM_CONTEXT.load().json("/contentfragment/test-content-datasources.json", DATASOURCES_PATH);
        // load the content fragment models
        AEM_CONTEXT.load().json("/contentfragment/test-content-conf.json", "/conf/global/settings/dam/cfm/models");
        // register an adapter that adapts resources to (mocks of) content fragments
        AEM_CONTEXT.registerAdapter(Resource.class, com.adobe.cq.dam.cfm.ContentFragment.class, ADAPTER);
    }

    private ContentFragmentElementsDataSourceServlet servlet;

    @Before
    public void before() throws Exception {
        // mock the expression resolver
        ExpressionResolver expressionResolver = mock(ExpressionResolver.class);
        when(expressionResolver.resolve(anyString(), anyObject(), anyObject(),
                org.mockito.Matchers.<PageContext>anyObject())).then(returnsFirstArg());
        when(expressionResolver.resolve(anyString(), anyObject(), anyObject(),
                org.mockito.Matchers.<SlingHttpServletRequest>anyObject())).then(returnsFirstArg());

        // create the servlet for the test
        servlet = new ContentFragmentElementsDataSourceServlet();
        Whitebox.setInternalState(servlet, "expressionResolver", expressionResolver);
    }

    @Test
    public void testNoConfig()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(DS_NO_CONFIG);
        assertDataSource(dataSource, new String[0], new String[0]);
    }

    @Test
    public void testFragmentPathNonExisting()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(DS_FRAGMENT_PATH_NON_EXISTING);
        assertDataSource(dataSource, new String[0], new String[0]);
    }

    @Test
    public void testComponentPathNonExisting()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(DS_COMPONENT_PATH__NON_EXISTING);
        assertDataSource(dataSource, new String[0], new String[0]);
    }

    @Test
    public void testFragmentPathInvalid()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(DS_FRAGMENT_PATH_INVALID);
        assertDataSource(dataSource, new String[0], new String[0]);
    }

    @Test
    public void testComponentPathInvalid()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(DS_COMPONENT_PATH_INVALID);
        assertDataSource(dataSource, new String[0], new String[0]);
    }

    @Test
    public void testComponentPathInvalidFragmentPath()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(DS_COMPONENT_PATH__INVALID_FRAGMENT_PATH);
        assertDataSource(dataSource, new String[0], new String[0]);
    }

    @Test
    public void testFragmentPathTextOnly()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(DS_FRAGMENT_PATH_TEXT_ONLY);
        assertDataSource(dataSource, ELEMENT_NAMES, ELEMENT_TITLES);
    }

    @Test
    public void testComponentPathTextOnly()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(DS_COMPONENT_PATH_TEXT_ONLY);
        assertDataSource(dataSource, ELEMENT_NAMES, ELEMENT_TITLES);
    }

    @Test
    public void testFragmentPathStructured()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(DS_FRAGMENT_PATH_STRUCTURED);
        assertDataSource(dataSource, ELEMENT_NAMES, ELEMENT_TITLES);
    }

    @Test
    public void testComponentPathStructured()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(DS_COMPONENT_PATH_STRUCTURED);
        assertDataSource(dataSource, ELEMENT_NAMES, ELEMENT_TITLES);
    }

    @Test
    public void testFragmentPathOverride()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(DS_FRAGMENT_PATH_OVERRIDE);
        assertDataSource(dataSource, ELEMENT_NAMES, ELEMENT_TITLES);
    }

    /* helper methods */

    /**
     * Calls the servlet with the specified datasource resource and returns the resulting datasource.
     */
    private DataSource getDataSource(String name) throws ServletException, IOException {
        // get datasource resource
        ResourceResolver resolver = AEM_CONTEXT.resourceResolver();
        Resource dataSource = resolver.getResource(DATASOURCES_PATH + "/" + name);

        // mock the request and request resource
        Resource resource = mock(Resource.class);
        when(resource.getChild(Config.DATASOURCE)).thenReturn(dataSource);
        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(resolver, AEM_CONTEXT.bundleContext());
        request.setResource(resource);

        // call the servlet
        servlet.doGet(request, new MockSlingHttpServletResponse());

        // return the resulting datasource
        return (DataSource) request.getAttribute(DataSource.class.getName());
    }

    /**
     * Asserts that the specified {@code dataSource} contains the expected items.
     */
    private void assertDataSource(DataSource dataSource, String[] names, String[] titles) {
        assertNotNull("Datasource was null", dataSource);
        Iterator<Resource> iterator = dataSource.iterator();
        for (int i = 0; i < names.length; i++) {
            if (!iterator.hasNext()) {
                fail("Datasource returned " + i + " items, expected " + names.length);
            }
            ValueMap properties = iterator.next().getValueMap();
            assertEquals(names[i], properties.get("value", String.class));
            assertEquals(titles[i], properties.get("text", String.class));
        }
        if (iterator.hasNext()) {
            fail("Datasource returned too many items, expected " + names.length);
        }
    }

}
