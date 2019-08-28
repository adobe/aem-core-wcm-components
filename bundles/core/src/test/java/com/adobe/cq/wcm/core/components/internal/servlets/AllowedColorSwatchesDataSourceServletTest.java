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

package com.adobe.cq.wcm.core.components.internal.servlets;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.granite.ui.components.Value;
import com.adobe.granite.ui.components.ds.DataSource;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.google.common.base.Function;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AllowedColorSwatchesDataSourceServletTest {

    private static final String COLOR_VALUE_1 = "#FF0000";
    private static final String COLOR_VALUE_2 = "#00FF00";
    private static final String COLOR_VALUE_3 = "#0000FF";

    @Rule
    public AemContext context = CoreComponentTestContext.createContext(null, "/apps");

    @Mock
    private ContentPolicyManager contentPolicyManager;

    @Mock
    private ContentPolicy contentPolicy;

    @Mock
    private ValueMap properties;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private SlingHttpServletRequest request;

    private AllowedColorSwatchesDataSourceServlet dataSourceServlet;

    @Before
    public void setUp() throws Exception {
        dataSourceServlet = new AllowedColorSwatchesDataSourceServlet();
        registerContentPolicyManager();
        when(contentPolicyManager.getPolicy(context.currentResource())).thenReturn(contentPolicy);
        when(contentPolicy.getProperties()).thenReturn(properties);
    }

    @Test
    public void testDataSource() throws Exception {
        String[] expected = new String[] {COLOR_VALUE_1, COLOR_VALUE_2, COLOR_VALUE_3};
        when(properties.get(AllowedColorSwatchesDataSourceServlet.PN_ALLOWED_COLOR_SWATCHES, String[].class)).thenReturn(expected);
        dataSourceServlet.doGet(context.request(), context.response());
        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        dataSource.iterator().forEachRemaining(resource -> {
            ValueMap props = resource.getValueMap();
            String allowedColorSwatch = props.get(AllowedColorSwatchesDataSourceServlet.PN_COLOR_VALUE, String.class);
            assertTrue("Allowed color swatches values are not as expected", Arrays.asList(expected).contains(allowedColorSwatch));
        });
    }

    private void registerContentPolicyManager() {
        context.registerAdapter(ResourceResolver.class, ContentPolicyManager.class, new Function<ResourceResolver, ContentPolicyManager>() {
            @Nullable
            @Override
            public ContentPolicyManager apply(@Nullable ResourceResolver input) {
                return contentPolicyManager;
            }
        });
    }
}
