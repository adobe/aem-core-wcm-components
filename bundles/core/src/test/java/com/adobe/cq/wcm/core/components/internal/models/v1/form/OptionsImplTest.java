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
package com.adobe.cq.wcm.core.components.internal.models.v1.form;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.List;

import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.servlethelpers.MockRequestDispatcherFactory;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.form.OptionItem;
import com.adobe.cq.wcm.core.components.models.form.Options;
import com.adobe.cq.wcm.core.components.models.form.Options.Type;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OptionsImplTest {

    private static final String TEST_BASE = "/form/options";
    private static final String CONTENT_ROOT = "/content/options";
    private static final String OPTIONS_1 = CONTENT_ROOT + "/optionsDefault";
    private static final String OPTIONS_2 = CONTENT_ROOT + "/optionsWithLocalSource";
    private static final String OPTIONS_3 = CONTENT_ROOT + "/optionsWithDatasourceSource";
    private static final String OPTIONS_4 = CONTENT_ROOT + "/optionsWithListSource";
    private static final String OPTIONS_5 = CONTENT_ROOT + "/checkbox";
    private static final String OPTIONS_6 = CONTENT_ROOT + "/radio";
    private static final String OPTIONS_7 = CONTENT_ROOT + "/drop-down";
    private static final String OPTIONS_8 = CONTENT_ROOT + "/multi-drop-down";
    private static final String OPTIONS_9 = CONTENT_ROOT + "/optionsDefault-v2";

    @ClassRule
    public static final AemContext CONTEXT = CoreComponentTestContext.createContext(TEST_BASE, CONTENT_ROOT);

    @BeforeClass
    public static void setUp() {
        FormsHelperStubber.createStub();
    }

    @Test
    public void testOptionsDefaultAttributes() {
        Options options = getOptionsUnderTest(OPTIONS_1);
        assertEquals(Type.CHECKBOX, options.getType());

        String id = "form-options" + "-" + String.valueOf(Math.abs(OPTIONS_1.hashCode() - 1));
        assertEquals(id, options.getId());

        assertEquals(null, options.getName());
        assertEquals(null, options.getValue());
        assertEquals(null, options.getTitle());
        assertEquals(null, options.getHelpMessage());

        List<OptionItem> optionItems = options.getItems();
        assertNotNull(optionItems);
        assertTrue(optionItems.size() == 0);

        Utils.testJSONExport(options, Utils.getTestExporterJSONPath(TEST_BASE, OPTIONS_1));
    }

    @Test
    public void testLocalAsOptionsSource() {
        FormsHelperGetValuesStubMethod.values = new String[] {"local-item2-value"};
        Options options = getOptionsUnderTest(OPTIONS_2);
        assertEquals(Type.CHECKBOX, options.getType());

        String id = "form-options" + "-" + String.valueOf(Math.abs(OPTIONS_2.hashCode() - 1));
        assertEquals(id, options.getId());

        assertEquals("local-name", options.getName());
        assertEquals("local-title", options.getTitle());
        assertEquals("local-helpMessage", options.getHelpMessage());

        List<OptionItem> optionItems = options.getItems();
        assertNotNull(optionItems);
        assertTrue(optionItems.size() == 2);

        evaluateOptionItem(optionItems.get(0), "local-item1-name", "local-item1-value", false, false);
        evaluateOptionItem(optionItems.get(1), "local-item2-name", "local-item2-value", true, true);
        Utils.testJSONExport(options, Utils.getTestExporterJSONPath(TEST_BASE, OPTIONS_2));
        FormsHelperGetValuesStubMethod.values = null;
    }

    @Test
    public void testDatasourceAsOptionsSource() {
        Options options = getOptionsUnderTest(OPTIONS_3);
        List<OptionItem> optionItems = options.getItems();
        assertNotNull(optionItems);
        assertTrue(optionItems.size() == 2);

        evaluateOptionItem(optionItems.get(0), "datasource-item1-name", "datasource-item1-value", true, false);
        evaluateOptionItem(optionItems.get(1), "datasource-item2-name", "datasource-item2-value", false, true);
        Utils.testJSONExport(options, Utils.getTestExporterJSONPath(TEST_BASE, OPTIONS_3));
    }

    @Test
    public void testListAsOptionsSource() {
        Options options = getOptionsUnderTest(OPTIONS_4);
        List<OptionItem> optionItems = options.getItems();
        assertNotNull(optionItems);
        assertTrue(optionItems.size() == 2);

        evaluateOptionItem(optionItems.get(0), "list-item1-name", "list-item1-value", true, false);
        evaluateOptionItem(optionItems.get(1), "list-item2-name", "list-item2-value", false, true);
        Utils.testJSONExport(options, Utils.getTestExporterJSONPath(TEST_BASE, OPTIONS_4));
    }

    @Test
    public void testCheckboxOptionsType() throws Exception {
        Options options = getOptionsUnderTest(OPTIONS_5);
        List<OptionItem> optionItems = options.getItems();
        assertEquals("name1", options.getName());
        assertEquals("jcr:title1", options.getTitle());
        assertEquals("helpMessage1", options.getHelpMessage());
        assertEquals(Type.CHECKBOX, options.getType());

        assertNotNull(optionItems);
        assertTrue(optionItems.size() == 3);

        // test the first option item
        OptionItem item = optionItems.get(0);
        evaluateOptionItem(item, "t1", "v1", true, true);
        item = optionItems.get(1);
        evaluateOptionItem(item, "t2", "v2", true, false);
        item = optionItems.get(2);
        evaluateOptionItem(item, "t3", "v3", false, false);
        Utils.testJSONExport(options, Utils.getTestExporterJSONPath(TEST_BASE, OPTIONS_5));
    }

    @Test
    public void testRadioOptionsType() throws Exception {
        Options options = getOptionsUnderTest(OPTIONS_6);
        assertEquals(Type.RADIO, options.getType());
        Utils.testJSONExport(options, Utils.getTestExporterJSONPath(TEST_BASE, OPTIONS_6));
    }

    @Test
    public void testDropDownOptionsType() throws Exception {
        Options options = getOptionsUnderTest(OPTIONS_7);
        assertEquals(Type.DROP_DOWN, options.getType());
        Utils.testJSONExport(options, Utils.getTestExporterJSONPath(TEST_BASE, OPTIONS_7));
    }

    @Test
    public void testMultiDropDownOptionsType() throws Exception {
        Options options = getOptionsUnderTest(OPTIONS_8);
        assertEquals(Type.MULTI_DROP_DOWN, options.getType());
        Utils.testJSONExport(options, Utils.getTestExporterJSONPath(TEST_BASE, OPTIONS_8));
    }

    @Test
    public void testV2JSONExport() {
        Options options = getOptionsUnderTest(OPTIONS_9);
        Utils.testJSONExport(options, Utils.getTestExporterJSONPath(TEST_BASE, OPTIONS_9));
    }

    private Options getOptionsUnderTest(String resourcePath) {
        Resource resource = CONTEXT.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Did you forget to define test resource " + resourcePath + "?");
        }
        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(CONTEXT.resourceResolver(), CONTEXT.bundleContext());
        MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
        request.setResource(resource);
        SlingBindings bindings = new SlingBindings();
        bindings.put(SlingBindings.RESOURCE, resource);
        bindings.put(SlingBindings.REQUEST, request);
        bindings.put(SlingBindings.RESOLVER, CONTEXT.resourceResolver());
        bindings.put(SlingBindings.RESPONSE, response);
        bindings.put(WCMBindings.PROPERTIES, resource.getValueMap());
        request.setAttribute(SlingBindings.class.getName(), bindings);
        request.setRequestDispatcherFactory(new MockRequestDispatcherFactory() {
            @Override
            public RequestDispatcher getRequestDispatcher(String s, RequestDispatcherOptions requestDispatcherOptions) {
                return null;
            }

            @Override
            public RequestDispatcher getRequestDispatcher(Resource resource, RequestDispatcherOptions requestDispatcherOptions) {
                return new MockRequestDispatcher(requestDispatcherOptions);
            }
        });
        return request.adaptTo(Options.class);
    }

    private void evaluateOptionItem(OptionItem item, String text, String value, boolean selected, boolean disabled) {
        assertEquals(text, item.getText());
        assertEquals(value, item.getValue());
        assertEquals(selected, item.isSelected());
        assertEquals(disabled, item.isDisabled());
    }

    private class MockRequestDispatcher implements RequestDispatcher {


        private RequestDispatcherOptions options;

        MockRequestDispatcher(RequestDispatcherOptions options) {
            this.options = options;
        }

        @Override
        public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
            String forcedResourceType = options.getForceResourceType();
            assertEquals(CONTENT_ROOT + "/dataDatasource/datasource", forcedResourceType);
            Resource dataSourceResource = CONTEXT.resourceResolver().getResource(CONTENT_ROOT + "/dataDatasource/datasource/items");
            SimpleDataSource dataSource = new SimpleDataSource(dataSourceResource.listChildren());
            request.setAttribute(DataSource.class.getName(), dataSource);
        }
    }
}
