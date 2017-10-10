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

package com.adobe.cq.wcm.core.components.internal.servlets;

import java.util.ArrayList;
import javax.annotation.Nullable;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.granite.ui.components.ds.DataSource;
import com.day.cq.wcm.foundation.forms.FormsManager;
import com.google.common.base.Function;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FormActionTypeSettingsDataSourceServletTest {

    @Rule
    public AemContext context = CoreComponentTestContext.createContext("/form/container/datasource/actiontypesettingsdatasource",
            "/apps");

    @Mock
    private FormsManager formsManagerMock;

    @Mock
    private FormsManager.ComponentDescription description;


    private FormActionTypeSettingsDataSourceServlet dataSourceServlet;

    @Before
    public void setUp() throws Exception {
        registerFormsManagerAdapter();
        ArrayList<FormsManager.ComponentDescription> componentDescriptions = new ArrayList<>();
        componentDescriptions.add(description);
        when(formsManagerMock.getActions()).thenReturn(componentDescriptions.iterator());
        when(description.getTitle()).thenReturn("Form Action");
        when(description.getResourceType()).thenReturn("form/action");
    }

    @Test
    public void testDataSource() throws Exception {
        context.currentResource("/apps/actiontypesettingsdatasource");
        dataSourceServlet = new FormActionTypeSettingsDataSourceServlet();
        dataSourceServlet.doGet(context.request(), context.response());
        DataSource dataSource = (DataSource) context.request().getAttribute(
                DataSource.class.getName());
        assertNotNull(dataSource);
        Resource resource = dataSource.iterator().next();
        assertEquals(resource.getPath(), context.currentResource("/apps/form/action/cq:dialog").getPath());
    }

    private void registerFormsManagerAdapter() {
        context.registerAdapter(ResourceResolver.class, FormsManager.class, new Function<ResourceResolver, FormsManager>() {
            @Nullable
            @Override
            public FormsManager apply(@Nullable ResourceResolver input) {
                return formsManagerMock;
            }
        });
    }
}
