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
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.i18n.impl.RootResourceBundle;
import org.apache.sling.servlethelpers.MockSlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.mockito.Mockito;

import com.adobe.cq.dam.cfm.content.FragmentRenderService;
import com.adobe.cq.dam.cfm.converter.ContentTypeConverter;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.ExpressionResolver;
import com.adobe.granite.ui.components.ds.DataSource;
import io.wcm.testing.mock.aem.junit.AemContext;

import static com.adobe.cq.wcm.core.components.extension.contentfragment.internal.models.v1.ContentFragmentImplTest.ADAPTER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class AbstractDataSourceServletTest {

    private static final String DATASOURCES_PATH = "/content/datasources";
    private static final String CONTENT_FRAGMENTS_PATH = "/content/dam/contentfragments";

    @ClassRule
    public static final AemContext CONTEXT = CoreComponentTestContext.createContext("/contentfragment", "/content");

    private static final RootResourceBundle RESOURCE_BUNDLE = new RootResourceBundle();

    @BeforeClass
    public static void setUpSuper() throws Exception {
        // load the content fragments
        CONTEXT.load().json("/contentfragment/test-content-dam-contentfragments.json", CONTENT_FRAGMENTS_PATH);
        // load the data sources
        CONTEXT.load().json("/contentfragment/test-content-datasources.json", DATASOURCES_PATH);
        // load the content fragment models
        CONTEXT.load().json("/contentfragment/test-content-conf.json", "/conf/global/settings/dam/cfm/models");
        // register an adapter that adapts resources to (mocks of) content fragments
        CONTEXT.registerAdapter(Resource.class, com.adobe.cq.dam.cfm.ContentFragment.class, ADAPTER);

        // mock resource bundle provider to enable constructing i18n instances
        ResourceBundleProvider resourceBundleProvider = Mockito.mock(ResourceBundleProvider.class);
        CONTEXT.registerService(ResourceBundleProvider.class, resourceBundleProvider);
        CONTEXT.registerService(FragmentRenderService.class, mock(FragmentRenderService.class));
        CONTEXT.registerService(ContentTypeConverter.class, mock(ContentTypeConverter.class));
        Mockito.when(resourceBundleProvider.getResourceBundle(null)).thenReturn(RESOURCE_BUNDLE);
        Mockito.when(resourceBundleProvider.getResourceBundle(null, null)).thenReturn(RESOURCE_BUNDLE);
    }

    protected ExpressionResolver expressionResolver;

    @Before
    public void beforeSuper() throws Exception {
        // mock the expression resolver
        expressionResolver = mock(ExpressionResolver.class);
        when(expressionResolver.resolve(anyString(), anyObject(), anyObject(),
                org.mockito.Matchers.<PageContext>anyObject())).then(returnsFirstArg());
        when(expressionResolver.resolve(anyString(), anyObject(), anyObject(),
                org.mockito.Matchers.<SlingHttpServletRequest>anyObject())).then(returnsFirstArg());
    }

    /**
     * Calls the servlet with the specified datasource resource and returns the resulting datasource.
     */
    protected DataSource getDataSource(AbstractContentFragmentDataSource servlet, String name)
            throws ServletException, IOException {
        // get datasource resource
        ResourceResolver resolver = CONTEXT.resourceResolver();
        Resource dataSource = resolver.getResource(DATASOURCES_PATH + "/" + name);

        // mock the request and request resource
        Resource resource = mock(Resource.class);
        when(resource.getChild(Config.DATASOURCE)).thenReturn(dataSource);
        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(resolver, CONTEXT.bundleContext());
        request.setResource(resource);

        // call the servlet
        servlet.doGet(request, new MockSlingHttpServletResponse());

        // return the resulting datasource
        return (DataSource) request.getAttribute(DataSource.class.getName());
    }

    /**
     * Asserts that the specified {@code dataSource} contains the expected items.
     */
    protected void assertDataSource(DataSource dataSource, String[] names, String[] titles) {
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
