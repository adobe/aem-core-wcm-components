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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.VariationDef;
import com.adobe.granite.ui.components.ExpressionResolver;
import com.day.cq.i18n.I18n;

/**
 * Datasource that returns the variations of a content fragment.
 *
 * @see AbstractContentFragmentDataSource
 */
@Component(
    service = { Servlet.class },
    property = {
        "sling.servlet.resourceTypes="+ VariationsDataSourceServlet.RESOURCE_TYPE,
        "sling.servlet.methods=GET",
        "sling.servlet.extensions=html"
    }
)
public class VariationsDataSourceServlet extends AbstractContentFragmentDataSource<VariationsDataSourceServlet.Variation> {

    /**
     * Represents a variation definition in the context of this datasource.
     */
    protected static class Variation {

        private String name;

        private String title;

        Variation(@Nonnull String name, @Nonnull String title) {
            this.name = name;
            this.title = title;
        }

        @Nonnull
        private String getName() {
            return name;
        }

        @Nonnull
        private String getTitle() {
            return title;
        }

    }

    /**
     * Defines the resource type for this datasource.
     */
    public final static String RESOURCE_TYPE = "core/wcm/extension/components/contentfragment/v1/datasource/variations";

    @Reference
    private ExpressionResolver expressionResolver;

    @Nonnull
    @Override
    protected ExpressionResolver getExpressionResolver() {
        return expressionResolver;
    }

    @Nonnull
    @Override
    protected List<Variation> getItems(@Nonnull ContentFragment fragment, @Nonnull SlingHttpServletRequest request) {
        // add implicit master variation
        List<Variation> variations = new LinkedList<>();
        variations.add(new Variation("master", (new I18n(request)).get("Master")));

        // add explicit variations
        Iterator<VariationDef> iterator = fragment.listAllVariations();
        while (iterator.hasNext()) {
            VariationDef variation = iterator.next();
            variations.add(new Variation(variation.getName(), variation.getTitle()));
        }

        return variations;
    }

    @Nonnull
    @Override
    protected String getTitle(@Nonnull Variation variation) {
        return variation.getTitle();
    }

    @Nonnull
    @Override
    protected String getValue(@Nonnull Variation variation) {
        return variation.getName();
    }

}
