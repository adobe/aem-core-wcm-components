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
package com.adobe.cq.wcm.core.components.internal.servlets.contentfragment;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.cq.wcm.core.components.models.contentfragment.ContentFragmentList;
import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.ExpressionResolver;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.day.cq.commons.jcr.JcrConstants;

/**
 * Returns all element names (i.e. dialog fields) of a particular content fragment model. Title will be set to the field
 * name, the value will be the name of the element.
 */
@Component(
        service = {Servlet.class},
        property = {
                "sling.servlet.resourceTypes=" + ModelElementsDataSourceServlet.RESOURCE_TYPE,
                "sling.servlet.methods=GET",
                "sling.servlet.extensions=html"
        }
)
public class ModelElementsDataSourceServlet extends AbstractDataSourceServlet {

    public static final String RESOURCE_TYPE = "core/wcm/components/contentfragmentlist/v1/datasource/elements";

    protected static final String PARAMETER_AND_PN_MODEL_PATH = ContentFragmentList.PN_MODEL_PATH;

    @Reference
    private ExpressionResolver expressionResolver;

    @NotNull
    @Override
    protected ExpressionResolver getExpressionResolver() {
        return expressionResolver;
    }

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) {

        // First try to get the model path from request parameters
        // otherwise determine model path from component resource.
        RequestParameter modelPathRequestParameter = request.getRequestParameter(PARAMETER_AND_PN_MODEL_PATH);

        String modelPath;
        if (modelPathRequestParameter != null) {
            modelPath = modelPathRequestParameter.getString();
        } else {
            Config config = getConfig(request);
            ValueMap componentValueMap = getComponentValueMap(config, request);

            // get model path from component resource
            modelPath = componentValueMap != null ?
                    componentValueMap.get(ContentFragmentList.PN_MODEL_PATH, String.class) : null;
        }

        DataSource dataSource = EmptyDataSource.instance();

        if (modelPath != null) {
            ResourceResolver resourceResolver = request.getResourceResolver();

            String pathToCFModelElements = String.format("%s/%s/model/cq:dialog/content/items",
                    modelPath, JcrConstants.JCR_CONTENT);
            Resource cfModelElementRoot = resourceResolver.getResource(pathToCFModelElements);
            if (cfModelElementRoot != null) {
                Iterator<Resource> resourceIterator = cfModelElementRoot.listChildren();
                List<Resource> resourceList = new LinkedList<>();
                while (resourceIterator.hasNext()) {
                    Resource elementResource = resourceIterator.next();
                    ValueMap valueMap = elementResource.getValueMap();
                    String valueValue = valueMap.get("name", "");
                    String textValue = valueMap.get("fieldLabel", valueValue);
                    Resource syntheticResource = createResource(resourceResolver, textValue, valueValue);
                    resourceList.add(syntheticResource);
                }
                dataSource = new SimpleDataSource(resourceList.iterator());
            }
        }

        request.setAttribute(DataSource.class.getName(), dataSource);
    }
}
