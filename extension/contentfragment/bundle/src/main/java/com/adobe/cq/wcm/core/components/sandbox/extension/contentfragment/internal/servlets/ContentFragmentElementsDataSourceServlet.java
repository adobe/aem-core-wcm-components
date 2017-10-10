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

import java.io.IOException;
import java.util.HashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.ExpressionHelper;
import com.adobe.granite.ui.components.ExpressionResolver;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;

import static com.adobe.cq.wcm.core.components.sandbox.extension.contentfragment.models.ContentFragment.PN_PATH;
import static org.apache.sling.api.resource.Resource.RESOURCE_TYPE_NON_EXISTING;

/**
 * Datasource that returns the elements of a content fragment. The content fragment can be specified explicitly or
 * indirectly via an {@code /apps/core/wcm/sandbox/components/contentfragment} component referencing a content fragment.
 */
@Component(
    service = { Servlet.class },
    property = {
        "sling.servlet.resourceTypes="+ ContentFragmentElementsDataSourceServlet.RESOURCE_TYPE,
        "sling.servlet.methods=GET",
        "sling.servlet.extensions=html"
    }
)
public class ContentFragmentElementsDataSourceServlet extends SlingSafeMethodsServlet {

    /**
     * Defines the resource type for this datasource.
     */
    public final static String RESOURCE_TYPE = "core/wcm/extension/sandbox/components/contentfragment/v1/datasource/elements";

    /**
     * Name of the datasource property containing the path to a content fragment for which to return the elements. The
     * value may contain expressions. If set, {@link ContentFragmentElementsDataSourceServlet#PN_COMPONENT_PATH} is
     * discarded.
     */
    public final static String PN_FRAGMENT_PATH = "fragmentPath";

    /**
     * Name of the datasource property containing the path to a
     * {@code /apps/core/wcm/sandbox/components/contentfragment} component. The value may contain expressions. If set,
     * the datasource returns the elements of the content fragment referenced by the component.
     */
    public final static String PN_COMPONENT_PATH = "componentPath";

    @Reference
    private ExpressionResolver expressionResolver;

    @Override
    @SuppressWarnings("unchecked")
    protected void doGet(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response)
            throws ServletException, IOException {
        // return empty datasource by default
        DataSource elements = EmptyDataSource.instance();

        // get content fragment
        ContentFragment fragment = getContentFragment(request);
        if (fragment != null) {
            // transform its elements
            elements = new SimpleDataSource(new TransformIterator(fragment.getElements(), new Transformer() {
                public Object transform(Object input) {
                    ContentElement element = (ContentElement) input;
                    ValueMap properties = new ValueMapDecorator(new HashMap<>());
                    properties.put("value", element.getName());
                    properties.put("text", element.getTitle());
                    return new ValueMapResource(request.getResourceResolver(), new ResourceMetadata(),
                            RESOURCE_TYPE_NON_EXISTING, properties);
                }
            }));
        }

        // provide datasource
        request.setAttribute(DataSource.class.getName(), elements);
    }

    /**
     * Returns content fragment, as configured by the datasource properties, for which to return the elements. If none
     * is correctly configured, then {@code null} is returned.
     */
    @Nullable
    private ContentFragment getContentFragment(@Nonnull SlingHttpServletRequest request) {
        // get datasource configuration
        Resource datasource = request.getResource().getChild(Config.DATASOURCE);
        if (datasource == null) {
            return null;
        }
        Config config = new Config(datasource);

        // get fragment path
        String fragmentPath = getParameter(config, PN_FRAGMENT_PATH, request);

        // if not present or empty, get fragment path via component
        if (StringUtils.isEmpty(fragmentPath)) {
            // get component path
            String componentPath = getParameter(config, PN_COMPONENT_PATH, request);
            if (componentPath == null) {
                return null;
            }

            // get component resource
            Resource component = request.getResourceResolver().getResource(componentPath);
            if (component == null) {
                return null;
            }

            // get fragment path from component resource
            fragmentPath = component.getValueMap().get(PN_PATH, String.class);
        }

        // no fragment path available
        if (StringUtils.isEmpty(fragmentPath)) {
            return null;
        }

        // get content fragment resource
        Resource fragmentResource = request.getResourceResolver().getResource(fragmentPath);
        if (fragmentResource == null) {
            return null;
        }

        // return content fragment
        return fragmentResource.adaptTo(ContentFragment.class);
    }

    /**
     * Reads a parameter from the specified datasource configuration, resolving expressions using the
     * {@link ExpressionResolver}. If the parameter is not found, {@code null} is returned.
     */
    @Nullable
    private String getParameter(@Nonnull Config config, @Nonnull String name,
                                @Nonnull SlingHttpServletRequest request) {
        // get value from configuration
        String value = config.get(name, String.class);
        if (value == null) {
            return null;
        }

        // evaluate value using the expression helper
        ExpressionHelper expressionHelper = new ExpressionHelper(expressionResolver, request);
        return expressionHelper.getString(value);
    }

}
