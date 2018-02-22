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
package com.adobe.cq.wcm.core.components.extension.contentfragment.internal.servlets;

import java.io.IOException;
import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import com.adobe.granite.ui.components.ds.DataSource;

@RunWith(MockitoJUnitRunner.class)
public class VariationsDataSourceServletTest extends AbstractDataSourceServletTest {

    /* names of the datasource instances to test */

    private static final String DS_NO_CONFIG                            = "variations-no-config";
    private static final String DS_FRAGMENT_PATH_NON_EXISTING           = "variations-fragment-path-non-existing";
    private static final String DS_COMPONENT_PATH__NON_EXISTING         = "variations-component-path-non-existing";
    private static final String DS_FRAGMENT_PATH_INVALID                = "variations-fragment-path-invalid";
    private static final String DS_COMPONENT_PATH_INVALID               = "variations-component-path-invalid";
    private static final String DS_COMPONENT_PATH_INVALID_FRAGMENT_PATH = "variations-component-path-invalid-fragment-path";
    private static final String DS_FRAGMENT_PATH_TEXT_ONLY              = "variations-fragment-path-text-only";
    private static final String DS_COMPONENT_PATH_TEXT_ONLY             = "variations-component-path-text-only";
    private static final String DS_FRAGMENT_PATH_STRUCTURED             = "variations-fragment-path-structured";
    private static final String DS_COMPONENT_PATH_STRUCTURED            = "variations-component-path-structured";
    private static final String DS_FRAGMENT_PATH_OVERRIDE               = "variations-fragment-path-override";

    /* names and titles of the elements of both the text-only and structured content fragment */

    private static final String[] VARIATION_NAMES = {"master", "teaser"};
    private static final String[] VARIATION_TITLES = {"Master", "Teaser"};

    private VariationsDataSourceServlet servlet;

    @Before
    public void before() throws Exception {
        // create the servlet to test
        servlet = new VariationsDataSourceServlet();
        Whitebox.setInternalState(servlet, "expressionResolver", expressionResolver);
    }

    @Test
    public void testNoConfig()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(servlet, DS_NO_CONFIG);
        assertDataSource(dataSource, new String[0], new String[0]);
    }

    @Test
    public void testFragmentPathNonExisting()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(servlet, DS_FRAGMENT_PATH_NON_EXISTING);
        assertDataSource(dataSource, new String[0], new String[0]);
    }

    @Test
    public void testComponentPathNonExisting()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(servlet, DS_COMPONENT_PATH__NON_EXISTING);
        assertDataSource(dataSource, new String[0], new String[0]);
    }

    @Test
    public void testFragmentPathInvalid()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(servlet, DS_FRAGMENT_PATH_INVALID);
        assertDataSource(dataSource, new String[0], new String[0]);
    }

    @Test
    public void testComponentPathInvalid()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(servlet, DS_COMPONENT_PATH_INVALID);
        assertDataSource(dataSource, new String[0], new String[0]);
    }

    @Test
    public void testComponentPathInvalidFragmentPath()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(servlet, DS_COMPONENT_PATH_INVALID_FRAGMENT_PATH);
        assertDataSource(dataSource, new String[0], new String[0]);
    }

    @Test
    public void testFragmentPathTextOnly()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(servlet, DS_FRAGMENT_PATH_TEXT_ONLY);
        assertDataSource(dataSource, VARIATION_NAMES, VARIATION_TITLES);
    }

    @Test
    public void testComponentPathTextOnly()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(servlet, DS_COMPONENT_PATH_TEXT_ONLY);
        assertDataSource(dataSource, VARIATION_NAMES, VARIATION_TITLES);
    }

    @Test
    public void testFragmentPathStructured()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(servlet, DS_FRAGMENT_PATH_STRUCTURED);
        assertDataSource(dataSource, VARIATION_NAMES, VARIATION_TITLES);
    }

    @Test
    public void testComponentPathStructured()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(servlet, DS_COMPONENT_PATH_STRUCTURED);
        assertDataSource(dataSource, VARIATION_NAMES, VARIATION_TITLES);
    }

    @Test
    public void testFragmentPathOverride()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(servlet, DS_FRAGMENT_PATH_OVERRIDE);
        assertDataSource(dataSource, VARIATION_NAMES, VARIATION_TITLES);
    }

}
