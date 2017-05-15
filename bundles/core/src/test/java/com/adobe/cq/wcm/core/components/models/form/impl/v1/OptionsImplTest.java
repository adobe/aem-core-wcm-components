/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2016 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.models.form.impl.v1;

import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.servlethelpers.MockRequestDispatcherFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.adobe.cq.sightly.WCMBindings;
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

    private static final String RESOURCE_PROPERTY = "resource";

    private static final String CONTENT_ROOT = "/content/options";

    @Rule
    public AemContext context = CoreComponentTestContext.createContext("/form/options", CONTENT_ROOT);

    private SlingBindings slingBindings;

    @Before
    public void setUp() {
        slingBindings = (SlingBindings) context.request().getAttribute(SlingBindings.class.getName());
    }

    private void setUpMockDataSource() {
        Resource dataSourceResource = context.resourceResolver().getResource(CONTENT_ROOT + "/dataDatasource/datasource/items");
        SimpleDataSource dataSource = new SimpleDataSource(dataSourceResource.listChildren());
        context.request().setAttribute(DataSource.class.getName(), dataSource);
    }

    private void setUpMockRequestDispatcher() {
        context.request().setRequestDispatcherFactory(new MockRequestDispatcherFactory() {
            @Override
            public RequestDispatcher getRequestDispatcher(String s, RequestDispatcherOptions requestDispatcherOptions) {
                return null;
            }

            @Override
            public RequestDispatcher getRequestDispatcher(Resource resource, RequestDispatcherOptions requestDispatcherOptions) {
                return new MockRequestDispatcher(resource, requestDispatcherOptions);
            }
        });
    }

    @Test
    public void testOptionsDefaultAttributes() {
        Resource optionsRes = context.currentResource(CONTENT_ROOT + "/optionsDefault");
        slingBindings.put(WCMBindings.PROPERTIES, optionsRes.adaptTo(ValueMap.class));
        slingBindings.put(RESOURCE_PROPERTY, optionsRes);
        Options options = context.request().adaptTo(Options.class);
        assertEquals(Type.CHECKBOX, options.getType());

        String id = "form-options" + "-" + String.valueOf(Math.abs(optionsRes.getPath().hashCode() - 1));
        assertEquals(id, options.getId());

        assertEquals(null, options.getName());
        assertEquals(null, options.getValue());
        assertEquals(null, options.getTitle());
        assertEquals(null, options.getHelpMessage());

        List<OptionItem> optionItems = options.getItems();
        assertNotNull(optionItems);
        assertTrue(optionItems.size() == 0);
    }

    @Test
    public void testLocalAsOptionsSource() {
        Resource optionsRes = context.currentResource(CONTENT_ROOT + "/optionsWithLocalSource");
        slingBindings.put(WCMBindings.PROPERTIES, optionsRes.adaptTo(ValueMap.class));
        slingBindings.put(RESOURCE_PROPERTY, optionsRes);
        Options options = context.request().adaptTo(Options.class);
        assertEquals(Type.CHECKBOX, options.getType());

        String id = "form-options" + "-" + String.valueOf(Math.abs(optionsRes.getPath().hashCode() - 1));
        assertEquals(id, options.getId());

        assertEquals("local-name", options.getName());
        assertEquals("local-title", options.getTitle());
        assertEquals("local-helpMessage", options.getHelpMessage());

        List<OptionItem> optionItems = options.getItems();
        assertNotNull(optionItems);
        assertTrue(optionItems.size() == 2);

        evaluateOptionItem(optionItems.get(0), "local-item1-name", "local-item1-value", true, false);
        evaluateOptionItem(optionItems.get(1), "local-item2-name", "local-item2-value", false, true);
    }

    @Test
    public void testDatasourceAsOptionsSource() {
        setUpMockRequestDispatcher();
        Resource optionsRes = context.currentResource(CONTENT_ROOT + "/optionsWithDatasourceSource");
        slingBindings.put(WCMBindings.PROPERTIES, optionsRes.adaptTo(ValueMap.class));
        slingBindings.put(RESOURCE_PROPERTY, optionsRes);
        Options options = context.request().adaptTo(Options.class);
        List<OptionItem> optionItems = options.getItems();
        assertNotNull(optionItems);
        assertTrue(optionItems.size() == 2);

        evaluateOptionItem(optionItems.get(0), "datasource-item1-name", "datasource-item1-value", true, false);
        evaluateOptionItem(optionItems.get(1), "datasource-item2-name", "datasource-item2-value", false, true);
    }

    @Test
    public void testListAsOptionsSource() {
        Resource optionsRes = context.currentResource(CONTENT_ROOT + "/optionsWithListSource");
        slingBindings.put(WCMBindings.PROPERTIES, optionsRes.adaptTo(ValueMap.class));
        slingBindings.put(RESOURCE_PROPERTY, optionsRes);
        Options options = context.request().adaptTo(Options.class);
        List<OptionItem> optionItems = options.getItems();
        assertNotNull(optionItems);
        assertTrue(optionItems.size() == 2);

        evaluateOptionItem(optionItems.get(0), "list-item1-name", "list-item1-value", true, false);
        evaluateOptionItem(optionItems.get(1), "list-item2-name", "list-item2-value", false, true);
    }

    @Test
    public void testCheckboxOptionsType() throws Exception {
        Resource optionsRes = context.currentResource(CONTENT_ROOT + "/checkbox");
        slingBindings.put(WCMBindings.PROPERTIES, optionsRes.adaptTo(ValueMap.class));
        slingBindings.put(RESOURCE_PROPERTY, optionsRes);
        Options options = context.request().adaptTo(Options.class);
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
    }

    @Test
    public void testRadioOptionsType() throws Exception {
        Resource optionsRes = context.currentResource(CONTENT_ROOT + "/radio");
        slingBindings.put(WCMBindings.PROPERTIES, optionsRes.adaptTo(ValueMap.class));
        slingBindings.put(RESOURCE_PROPERTY, optionsRes);
        Options options = context.request().adaptTo(Options.class);
        assertEquals(Type.RADIO, options.getType());
    }

    @Test
    public void testDropDownOptionsType() throws Exception {
        Resource optionsRes = context.currentResource(CONTENT_ROOT + "/drop-down");
        slingBindings.put(WCMBindings.PROPERTIES, optionsRes.adaptTo(ValueMap.class));
        slingBindings.put(RESOURCE_PROPERTY, optionsRes);
        Options options = context.request().adaptTo(Options.class);
        assertEquals(Type.DROP_DOWN, options.getType());
    }

    @Test
    public void testMulitDropDownOptionsType() throws Exception {
        Resource optionsRes = context.currentResource(CONTENT_ROOT + "/multi-drop-down");
        slingBindings.put(WCMBindings.PROPERTIES, optionsRes.adaptTo(ValueMap.class));
        slingBindings.put(RESOURCE_PROPERTY, optionsRes);
        Options options = context.request().adaptTo(Options.class);
        assertEquals(Type.MULTI_DROP_DOWN, options.getType());
    }

    private void evaluateOptionItem(OptionItem item, String text, String value, boolean selected, boolean disabled) {
        assertEquals(text, item.getText());
        assertEquals(value, item.getValue());
        assertEquals(selected, item.isSelected());
        assertEquals(disabled, item.isDisabled());
    }

    private class MockRequestDispatcher implements RequestDispatcher {

        private Resource resource;

        private RequestDispatcherOptions options;

        MockRequestDispatcher(Resource resource, RequestDispatcherOptions options) {
            this.resource = resource;
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
            setUpMockDataSource();
        }
    }
}
