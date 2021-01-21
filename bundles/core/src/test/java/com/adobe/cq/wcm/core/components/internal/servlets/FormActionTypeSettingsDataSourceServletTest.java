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
package com.adobe.cq.wcm.core.components.internal.servlets;

import java.util.ArrayList;
import java.util.function.Function;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.granite.ui.components.ds.DataSource;
import com.day.cq.wcm.foundation.forms.FormsManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class FormActionTypeSettingsDataSourceServletTest {

    private static final String TEST_BASE = "/form/container/datasource/actiontypesettingsdatasource";
    private static final String APPS_ROOT = "/apps";

    public final AemContext context = CoreComponentTestContext.newAemContext();

    @Mock
    private FormsManager formsManagerMock;

    @Mock
    private FormsManager.ComponentDescription description;

    @BeforeEach
    public void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, APPS_ROOT);

        registerFormsManagerAdapter();
        ArrayList<FormsManager.ComponentDescription> componentDescriptions = new ArrayList<>();
        componentDescriptions.add(description);
        when(formsManagerMock.getActions()).thenReturn(componentDescriptions.iterator());
        when(description.getResourceType()).thenReturn("form/action");
    }

    @Test
    public void testDataSource() throws Exception {
        context.currentResource("/apps/actiontypesettingsdatasource");
        FormActionTypeSettingsDataSourceServlet dataSourceServlet = new FormActionTypeSettingsDataSourceServlet();
        dataSourceServlet.doGet(context.request(), context.response());
        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        Resource resource = dataSource.iterator().next();
        assertEquals(resource.getPath(), context.currentResource("/apps/form/action/cq:dialog").getPath());
    }

    private void registerFormsManagerAdapter() {
        context.registerAdapter(ResourceResolver.class, FormsManager.class,
            (Function<ResourceResolver, FormsManager>) input -> formsManagerMock);
    }
}
