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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.adobe.granite.ui.components.Value;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

import static com.adobe.cq.wcm.core.components.internal.servlets.ContainerBackgroundColorDataSourceServlet.PN_COLOR_NAME;
import static com.adobe.cq.wcm.core.components.internal.servlets.ContainerBackgroundColorDataSourceServlet.PN_COLOR_VALUE;
import static com.adobe.cq.wcm.core.components.internal.servlets.ContainerBackgroundColorDataSourceServlet.NN_SWATCHES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContainerBackgroundColorDataSourceServletTest {

    private static final String CURRENT_RESOURCE = "current";
    private static final String CURRENT_POLICY = "policy";

    private static final String BLACK_VALUE = "#000000";
    private static final String BLACK_NAME = "black";

    @Mock
    private ContentPolicyManager contentPolicyManager;

    @Mock
    private ContentPolicy contentPolicy;

    @Mock
    private ValueMap properties;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private SlingHttpServletResponse response;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    Resource currentResource;

    @Mock
    Resource swatchesList;

    @Captor
    ArgumentCaptor<Object> captor;

    private ContainerBackgroundColorDataSourceServlet dataSourceServlet;

    @Before
    public void setUp() throws Exception {
        dataSourceServlet = new ContainerBackgroundColorDataSourceServlet();

        when(request.getResourceResolver()).thenReturn(resourceResolver);

        when(request.getAttribute(Value.CONTENTPATH_ATTRIBUTE)).thenReturn(CURRENT_RESOURCE);        

        doNothing().when(request).setAttribute(anyString(), captor.capture());
    }

    @Test
    public void testValidDataSource() throws Exception {
        
        when(resourceResolver.getResource(CURRENT_RESOURCE)).thenReturn(currentResource);

        when(resourceResolver.adaptTo(ContentPolicyManager.class)).thenReturn(contentPolicyManager);
        when(contentPolicyManager.getPolicy(currentResource)).thenReturn(contentPolicy);
        when(contentPolicy.getPath()).thenReturn(CURRENT_POLICY);

        ValueMap color = new ValueMapDecorator(new HashMap<>());
        color.put(PN_COLOR_VALUE, BLACK_VALUE);
        color.put(PN_COLOR_NAME, BLACK_NAME);
        List<Resource> colors = new ArrayList<>();
        colors.add(new ValueMapResource(request.getResourceResolver(), new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED,
                color));
        when(swatchesList.listChildren()).thenReturn(colors.iterator());
        when(resourceResolver.getResource(CURRENT_POLICY + "/" + NN_SWATCHES)).thenReturn(swatchesList);
        
        dataSourceServlet.doGet(request, response);
        DataSource dataSource = (DataSource) captor.getValue();
        assertNotNull("DataSource should not be null,", dataSource);
        Iterator<Resource> iterator = dataSource.iterator();
        assertTrue("Iterator should not be empty", iterator.hasNext());
        Resource next = iterator.next();
        assertNotNull("Colors resource should not be null", next);
        assertEquals(BLACK_NAME, next.getValueMap().get(PN_COLOR_NAME));
        assertEquals(BLACK_VALUE, next.getValueMap().get(PN_COLOR_VALUE));
        assertFalse("Iterator should not have more than one resource", iterator.hasNext());
    }
    
    @Test
    public void testInvalidPolicyManager() throws Exception {
        
        when(resourceResolver.getResource(CURRENT_RESOURCE)).thenReturn(currentResource);
        
        List<Resource> colorOptionsList = new ArrayList<>();               
        assertEquals("Should return empty color list because content policy manager is null",dataSourceServlet.getColors(request), colorOptionsList);
    }
}