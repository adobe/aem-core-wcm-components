/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
import java.util.Iterator;
import java.util.Locale;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.i18n.impl.RootResourceBundle;
import org.apache.sling.servlethelpers.MockSlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import com.adobe.cq.dam.cfm.content.FragmentRenderService;
import com.adobe.cq.dam.cfm.converter.ContentTypeConverter;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.models.v1.contentfragment.AbstractContentFragmentTest;
import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.ExpressionResolver;
import com.adobe.granite.ui.components.ds.DataSource;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@ExtendWith(AemContextExtension.class)
abstract class AbstractContentFragmentDataSourceServletTest {

    private static final String TEST_BASE = "/contentfragment";
    private static final String DATASOURCES_PATH = "/content/datasources";
    private static final String CONTENT_FRAGMENTS_PATH = "/content/dam/contentfragments";


    private final AemContext context = CoreComponentTestContext.newAemContext();

    private static final RootResourceBundle RESOURCE_BUNDLE = new RootResourceBundle();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, "/content");
        // load the content fragments
        context.load().json(TEST_BASE + "/test-content-dam-contentfragments.json", CONTENT_FRAGMENTS_PATH);
        // load the data sources
        context.load().json(TEST_BASE + "/test-content-datasources.json", DATASOURCES_PATH);
        // load the content fragment models
        context.load().json(TEST_BASE + "/test-content-conf.json", "/conf/global/settings/dam/cfm/models");
        // register an adapter that adapts resources to (mocks of) content fragments
        context.registerAdapter(Resource.class, com.adobe.cq.dam.cfm.ContentFragment.class, AbstractContentFragmentTest.CONTENT_FRAGMENT_ADAPTER);

        // mock resource bundle provider to enable constructing i18n instances
        ResourceBundleProvider resourceBundleProvider = Mockito.mock(ResourceBundleProvider.class, withSettings().lenient());
        context.registerService(ResourceBundleProvider.class, resourceBundleProvider);
        context.registerService(FragmentRenderService.class, mock(FragmentRenderService.class));
        context.registerService(ContentTypeConverter.class, mock(ContentTypeConverter.class));
        Mockito.when(resourceBundleProvider.getResourceBundle(null)).thenReturn(RESOURCE_BUNDLE);
        Mockito.when(resourceBundleProvider.getResourceBundle(null, null)).thenReturn(RESOURCE_BUNDLE);

        // mock the expression resolver
        expressionResolver = mock(ExpressionResolver.class);
        when(expressionResolver.resolve(any(String.class), any(Locale.class), any(), any(SlingHttpServletRequest.class)))
            .then(returnsFirstArg());
    }

    ExpressionResolver expressionResolver;

    /**
     * Calls the servlet with the specified datasource resource and returns the resulting datasource.
     */
    DataSource getDataSource(AbstractContentFragmentDataSourceServlet servlet, String name)
            throws ServletException, IOException {

        // get datasource resource
        ResourceResolver resolver = context.resourceResolver();
        Resource dataSource = resolver.getResource(DATASOURCES_PATH + "/" + name);

        // mock the request and request resource
        Resource resource = mock(Resource.class);
        when(resource.getChild(Config.DATASOURCE)).thenReturn(dataSource);
        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(resolver, context.bundleContext());
        request.setResource(resource);

        // call the servlet
        servlet.doGet(request, new MockSlingHttpServletResponse());

        // return the resulting datasource
        return (DataSource) request.getAttribute(DataSource.class.getName());
    }

    /**
     * Asserts that the specified {@code dataSource} contains the expected items.
     */
    void assertDataSource(DataSource dataSource, String[] names, String[] titles) {
        assertNotNull(dataSource, "Datasource was null");
        Iterator<Resource> iterator = dataSource.iterator();
        for (int i = 0; i < names.length; i++) {
            assertTrue(iterator.hasNext(), "Datasource returned " + i + " items, expected " + names.length);
            ValueMap properties = iterator.next().getValueMap();
            assertEquals(names[i], properties.get("value", String.class));
            assertEquals(titles[i], properties.get("text", String.class));
        }
        assertFalse(iterator.hasNext(), "Datasource returned too many items, expected " + names.length);
    }

}
