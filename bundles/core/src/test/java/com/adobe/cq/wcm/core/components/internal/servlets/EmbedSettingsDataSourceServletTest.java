/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe Systems Incorporated
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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import io.wcm.testing.mock.aem.junit.AemContext;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.granite.ui.components.ds.DataSource;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.google.common.base.Function;

@RunWith(MockitoJUnitRunner.class)
public class EmbedSettingsDataSourceServletTest {

    @Rule
    public AemContext context = CoreComponentTestContext.createContext("/embed/v1/datasource/embedalloweddatasource",
	    "/apps");

    private EmbedSettingsDataSourceServlet dataSourceServlet;

    @Mock
    private ContentPolicyManager contentPolicyManager;

    @Mock
    private ContentPolicy contentPolicy;

    @Before
    public void setUp() {

	Resource policyResource = context.resourceResolver().getResource("/apps/conf/policy_1558011912823");
	ValueMap properties = ResourceUtil.getValueMap(policyResource);
	dataSourceServlet = new EmbedSettingsDataSourceServlet();
	registerContentPolicyManager();
	when(contentPolicyManager.getPolicy(context.currentResource())).thenReturn(contentPolicy);
	when(contentPolicy.getProperties()).thenReturn(properties);
    }

    @Test
    public void testGetSettingsDialogs() throws Exception {
	dataSourceServlet.doGet(context.request(), context.response());
	DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
	assertNotNull(dataSource);
	dataSource.iterator().forEachRemaining(resource -> {
	    assertNotNull(resource);
	});
    }

    private void registerContentPolicyManager() {
	context.registerAdapter(ResourceResolver.class, ContentPolicyManager.class,
		new Function<ResourceResolver, ContentPolicyManager>() {
		    @Nullable
		    @Override
		    public ContentPolicyManager apply(@Nullable ResourceResolver input) {
			return contentPolicyManager;
		    }
		});
    }

}
