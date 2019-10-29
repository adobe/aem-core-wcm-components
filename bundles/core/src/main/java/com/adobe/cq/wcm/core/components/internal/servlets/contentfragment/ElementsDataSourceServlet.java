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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Servlet;

import org.apache.commons.collections.IteratorUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.ExpressionResolver;

/**
 * Datasource that returns the elements of a content fragment.
 *
 * @see AbstractContentFragmentDataSourceServlet
 */
@Component(
        service = {Servlet.class},
        property = {
                "sling.servlet.resourceTypes=" + ElementsDataSourceServlet.RESOURCE_TYPE,
                "sling.servlet.methods=GET",
                "sling.servlet.extensions=html"
        }
)
public class ElementsDataSourceServlet extends AbstractContentFragmentDataSourceServlet<ContentElement> {

    /**
     * Defines the resource type for this datasource.
     */
    public static final String RESOURCE_TYPE = "core/wcm/components/contentfragment/v1/datasource/elements";

    /**
     * Defines a parameter name and property name whose value would define whether to return all elements or just
     * multiline text elements.
     */
    private static final String PARAM_AND_PN_DISPLAY_MODE = "displayMode";

    /**
     * Property and parameter value for {@link #PARAM_AND_PN_DISPLAY_MODE}. When the corresponding parameter
     * or property is equal to this, then only multiline text elements are returned.
     */
    private static final String SINGLE_TEXT = "singleText";


    @Reference
    private transient ExpressionResolver expressionResolver;

    @NotNull
    @Override
    protected ExpressionResolver getExpressionResolver() {
        return expressionResolver;
    }

    @NotNull
    @Override
    protected List<ContentElement> getItems(@NotNull ContentFragment fragment, @NotNull SlingHttpServletRequest request) {
        Config config = getConfig(request);
        ValueMap map = getComponentValueMap(config, request);
        String textOnlyParam = request.getParameter(PARAM_AND_PN_DISPLAY_MODE);
        boolean textOnly = map != null && map.containsKey(PARAM_AND_PN_DISPLAY_MODE)
                && map.get(PARAM_AND_PN_DISPLAY_MODE, "multi").equals(SINGLE_TEXT);
        if (textOnlyParam != null) {
            textOnly = textOnlyParam.equals(SINGLE_TEXT);
        }
        if (textOnly) {
            Iterator<ContentElement> elementIterator = fragment.getElements();
            List<ContentElement> elementList = new ArrayList<ContentElement>();
            while (elementIterator.hasNext()) {
                ContentElement element = elementIterator.next();
                String contentType = element.getValue().getContentType();
                if (contentType == null) {
                    contentType = element.getContentType();
                }

                if (contentType != null && contentType.startsWith("text/") &&
                        !element.getValue().getDataType().isMultiValue()) {
                    elementList.add(element);
                }
            }
            return elementList;
        }
        return IteratorUtils.toList(fragment.getElements());
    }

    @NotNull
    @Override
    protected String getTitle(@NotNull ContentElement element) {
        return element.getTitle();
    }

    @NotNull
    @Override
    protected String getValue(@NotNull ContentElement element) {
        return element.getName();
    }

}
