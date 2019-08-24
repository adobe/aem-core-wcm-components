/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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
package com.adobe.cq.wcm.core.components.internal.servlets.contentfragment;

import java.io.IOException;
import javax.servlet.ServletException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.testing.Utils;
import com.adobe.granite.ui.components.ds.DataSource;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class VariationsDataSourceServletTest extends AbstractContentFragmentDataSourceServletTest {

    /* names of the datasource instances to test */

    private static final String DS_NO_CONFIG = "variations-no-config";
    private static final String DS_FRAGMENT_PATH_NON_EXISTING = "variations-fragment-path-non-existing";
    private static final String DS_COMPONENT_PATH__NON_EXISTING = "variations-component-path-non-existing";
    private static final String DS_FRAGMENT_PATH_INVALID = "variations-fragment-path-invalid";
    private static final String DS_COMPONENT_PATH_INVALID = "variations-component-path-invalid";
    private static final String DS_COMPONENT_PATH_INVALID_FRAGMENT_PATH = "variations-component-path-invalid-fragment-path";
    private static final String DS_FRAGMENT_PATH_TEXT_ONLY = "variations-fragment-path-text-only";
    private static final String DS_COMPONENT_PATH_TEXT_ONLY = "variations-component-path-text-only";
    private static final String DS_FRAGMENT_PATH_STRUCTURED = "variations-fragment-path-structured";
    private static final String DS_COMPONENT_PATH_STRUCTURED = "variations-component-path-structured";
    private static final String DS_FRAGMENT_PATH_OVERRIDE = "variations-fragment-path-override";

    /* names and titles of the elements of both the text-only and structured content fragment */

    private static final String[] VARIATION_NAMES = {"master", "teaser"};
    private static final String[] VARIATION_TITLES = {"Master", "Teaser"};

    private VariationsDataSourceServlet servlet;

    @BeforeEach
    void setUp() {
        super.setUp();
        // create the servlet to test
        servlet = new VariationsDataSourceServlet();
        Utils.setInternalState(servlet, "expressionResolver", expressionResolver);
    }

    @Test
    void testNoConfig()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(servlet, DS_NO_CONFIG);
        assertDataSource(dataSource, new String[0], new String[0]);
    }

    @Test
    void testFragmentPathNonExisting()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(servlet, DS_FRAGMENT_PATH_NON_EXISTING);
        assertDataSource(dataSource, new String[0], new String[0]);
    }

    @Test
    void testComponentPathNonExisting()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(servlet, DS_COMPONENT_PATH__NON_EXISTING);
        assertDataSource(dataSource, new String[0], new String[0]);
    }

    @Test
    void testFragmentPathInvalid()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(servlet, DS_FRAGMENT_PATH_INVALID);
        assertDataSource(dataSource, new String[0], new String[0]);
    }

    @Test
    void testComponentPathInvalid()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(servlet, DS_COMPONENT_PATH_INVALID);
        assertDataSource(dataSource, new String[0], new String[0]);
    }

    @Test
    void testComponentPathInvalidFragmentPath()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(servlet, DS_COMPONENT_PATH_INVALID_FRAGMENT_PATH);
        assertDataSource(dataSource, new String[0], new String[0]);
    }

    @Test
    void testFragmentPathTextOnly()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(servlet, DS_FRAGMENT_PATH_TEXT_ONLY);
        assertDataSource(dataSource, VARIATION_NAMES, VARIATION_TITLES);
    }

    @Test
    void testComponentPathTextOnly()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(servlet, DS_COMPONENT_PATH_TEXT_ONLY);
        assertDataSource(dataSource, VARIATION_NAMES, VARIATION_TITLES);
    }

    @Test
    void testFragmentPathStructured()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(servlet, DS_FRAGMENT_PATH_STRUCTURED);
        assertDataSource(dataSource, VARIATION_NAMES, VARIATION_TITLES);
    }

    @Test
    void testComponentPathStructured()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(servlet, DS_COMPONENT_PATH_STRUCTURED);
        assertDataSource(dataSource, VARIATION_NAMES, VARIATION_TITLES);
    }

    @Test
    void testFragmentPathOverride()
            throws ServletException, IOException {
        DataSource dataSource = getDataSource(servlet, DS_FRAGMENT_PATH_OVERRIDE);
        assertDataSource(dataSource, VARIATION_NAMES, VARIATION_TITLES);
    }

}
