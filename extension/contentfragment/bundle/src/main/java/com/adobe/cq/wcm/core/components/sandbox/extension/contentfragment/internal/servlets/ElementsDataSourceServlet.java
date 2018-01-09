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
package com.adobe.cq.wcm.core.components.sandbox.extension.contentfragment.internal.servlets;

import java.util.List;
import javax.annotation.Nonnull;
import javax.servlet.Servlet;

import org.apache.commons.collections.IteratorUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.granite.ui.components.ExpressionResolver;

/**
 * Datasource that returns the elements of a content fragment.
 *
 * @see AbstractContentFragmentDataSource
 */
@Component(
        service = { Servlet.class },
        property = {
                "sling.servlet.resourceTypes=" + ElementsDataSourceServlet.RESOURCE_TYPE,
                "sling.servlet.methods=GET",
                "sling.servlet.extensions=html"
        }
)
public class ElementsDataSourceServlet extends AbstractContentFragmentDataSource<ContentElement> {

    /**
     * Defines the resource type for this datasource.
     */
    public final static String RESOURCE_TYPE = "core/wcm/extension/sandbox/components/contentfragment/v1/datasource/elements";

    @Reference
    private ExpressionResolver expressionResolver;

    @Nonnull
    @Override
    protected ExpressionResolver getExpressionResolver() {
        return expressionResolver;
    }

    @Nonnull
    @Override
    protected List<ContentElement> getItems(@Nonnull ContentFragment fragment, @Nonnull SlingHttpServletRequest request) {
        return IteratorUtils.toList(fragment.getElements());
    }

    @Nonnull
    @Override
    protected String getTitle(@Nonnull ContentElement element) {
        return element.getTitle();
    }

    @Nonnull
    @Override
    protected String getValue(@Nonnull ContentElement element) {
        return element.getName();
    }

}
