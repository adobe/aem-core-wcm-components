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
package com.adobe.cq.wcm.core.components.extension.contentfragment.internal.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.ExpressionHelper;
import com.adobe.granite.ui.components.ExpressionResolver;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;

import static com.adobe.cq.wcm.core.components.extension.contentfragment.models.ContentFragment.PN_PATH;
import static org.apache.sling.api.resource.Resource.RESOURCE_TYPE_NON_EXISTING;

/**
 * <p>Abstract datasource providing access to a content fragment instance configured by datasource properties. The
 * content fragment to be returned by {@link #getContentFragment(SlingHttpServletRequest)} can be specified via a
 * {@code /apps/core/wcm/extension/components/contentfragment} component (see {@link #PN_COMPONENT_PATH}) or
 * directly by path (see {@link #PN_FRAGMENT_PATH}).</p>
 *
 * <p>Concrete implementations need only return a list of items in
 * {@link #getItems(ContentFragment, SlingHttpServletRequest)} and, for each item, specify their title and value (in
 * {@link #getTitle(Object)} and {@link #getValue(Object)}, respectively).</p>
 */
public abstract class AbstractContentFragmentDataSource<T> extends SlingSafeMethodsServlet {

    /**
     * Name of the datasource property containing the path to a content fragment to use. If set, the value of
     * {@link #PN_COMPONENT_PATH} is ignored. The value may contain expressions.
     */
    public final static String PN_FRAGMENT_PATH = "fragmentPath";

    /**
     * Name of the resource property containing the path to a
     * {@code /apps/core/wcm/extension/components/contentfragment} component. The servlet uses the content
     * fragment referenced by the component. The value may contain expressions.
     */
    public final static String PN_COMPONENT_PATH = "componentPath";

    /**
     * Returns an expression resolver to be used to resolve expressions in the configuration properties (see
     * {@link #PN_FRAGMENT_PATH} and {@link #PN_COMPONENT_PATH}).
     *
     * @return an expression resolver
     */
    @Nonnull
    protected abstract ExpressionResolver getExpressionResolver();

    /**
     * Returns, for the given content fragment, a list of items to include in the datasource.
     *
     * @param fragment a content fragment
     * @param request the request object (can be used for i18n)
     * @return the list of items to include in the datasource
     */
    @Nonnull
    protected abstract List<T> getItems(@Nonnull ContentFragment fragment, @Nonnull SlingHttpServletRequest request);

    /**
     * Returns the title for the given item.
     *
     * @param item an item previously returned by {@link #getItems(ContentFragment, SlingHttpServletRequest)}
     * @return the title of the item to use in the resulting datasource
     */
    @Nonnull
    protected abstract String getTitle(@Nonnull T item);

    /**
     * Returns the value for the given item.
     *
     * @param item an item previously returned by {@link #getItems(ContentFragment, SlingHttpServletRequest)}
     * @return the value of the item to use in the resulting datasource
     */
    @Nonnull
    protected abstract String getValue(@Nonnull T item);

    /**
     * Returns datasource configuration.
     * @param request the request
     * @return datasource configuration.
     */
    Config getConfig(SlingHttpServletRequest request) {
        // get datasource configuration
        Resource datasource = request.getResource().getChild(Config.DATASOURCE);
        if (datasource == null) {
            return null;
        }
        return new Config(datasource);
    }

    /**
     * Get value map corresponding to resource of the component.
     * @param config datasource configuration
     * @param request the request
     * @return value map.
     */
    ValueMap getComponentValueMap(Config config, SlingHttpServletRequest request) {
        if (config == null) {
            return null;
        }
        String componentPath = getParameter(config, PN_COMPONENT_PATH, request);
        if (componentPath == null) {
            return null;
        }

        // get component resource
        Resource component = request.getResourceResolver().getResource(componentPath);
        if (component == null) {
            return null;
        }
        return component.getValueMap();
    }

    @Override
    protected void doGet(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response)
            throws ServletException, IOException {
        // return empty datasource by default
        DataSource elements = EmptyDataSource.instance();
        // get content fragment
        ContentFragment fragment = getContentFragment(request);
        if (fragment != null) {
            // get datasource items from the implementation
            List<T> items = getItems(fragment, request);
            // transform items to resources
            List<Resource> resources = new LinkedList<>();
            ResourceResolver resolver = request.getResourceResolver();
            for (T item : items) {
                Resource resource = createResource(resolver, getTitle(item), getValue(item));
                resources.add(resource);
            }
            // create datasource
            elements = new SimpleDataSource(resources.iterator());
        }
        // provide datasource
        request.setAttribute(DataSource.class.getName(), elements);
    }

    /**
     * Returns the content fragment to use for this datasource, as configured by the datasource properties. If no
     * content fragment is correctly configured, then {@code null} is returned.
     */
    @Nullable
    private ContentFragment getContentFragment(@Nonnull SlingHttpServletRequest request) {

        Config config = getConfig(request);
        ValueMap map = getComponentValueMap(config, request);

        if (config == null) {
            return null;
        }

        // get fragment path
        String fragmentPath = getParameter(config, PN_FRAGMENT_PATH, request);

        // if not present or empty, get fragment path via component
        if (StringUtils.isEmpty(fragmentPath)) {
            // get fragment path from component resource
            fragmentPath = map != null ? map.get(PN_PATH, String.class) : null;
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
        ExpressionHelper expressionHelper = new ExpressionHelper(getExpressionResolver(), request);
        return expressionHelper.getString(value);
    }

    /**
     * Creates a virtual resource to use in the resulting datasource.
     */
    @Nonnull
    private Resource createResource(@Nonnull ResourceResolver resolver, @Nonnull String title, @Nonnull String value) {
        ValueMap properties = new ValueMapDecorator(new HashMap<>());
        properties.put("text", title);
        properties.put("value", value);
        return new ValueMapResource(resolver, new ResourceMetadata(), RESOURCE_TYPE_NON_EXISTING, properties);
    }

}
