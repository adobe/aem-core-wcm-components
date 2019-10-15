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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.VariationDef;
import com.adobe.granite.ui.components.ExpressionResolver;
import com.day.cq.i18n.I18n;

/**
 * Datasource that returns the variations of a content fragment.
 *
 * @see AbstractContentFragmentDataSourceServlet
 */
@Component(
        service = {Servlet.class},
        property = {
                "sling.servlet.resourceTypes=" + VariationsDataSourceServlet.RESOURCE_TYPE,
                "sling.servlet.methods=GET",
                "sling.servlet.extensions=html"
        }
)
public class VariationsDataSourceServlet extends AbstractContentFragmentDataSourceServlet<VariationsDataSourceServlet.Variation> {

    /**
     * Represents a variation definition in the context of this datasource.
     */
    protected static class Variation {

        private String name;

        private String title;

        Variation(@NotNull String name, @NotNull String title) {
            this.name = name;
            this.title = title;
        }

        @NotNull
        private String getName() {
            return name;
        }

        @NotNull
        private String getTitle() {
            return title;
        }

    }

    /**
     * Defines the resource type for this datasource.
     */
    public static final String RESOURCE_TYPE = "core/wcm/components/contentfragment/v1/datasource/variations";

    @Reference
    private transient ExpressionResolver expressionResolver;

    @NotNull
    @Override
    protected ExpressionResolver getExpressionResolver() {
        return expressionResolver;
    }

    @NotNull
    @Override
    protected List<Variation> getItems(@NotNull ContentFragment fragment, @NotNull SlingHttpServletRequest request) {
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

    @NotNull
    @Override
    protected String getTitle(@NotNull Variation variation) {
        return variation.getTitle();
    }

    @NotNull
    @Override
    protected String getValue(@NotNull Variation variation) {
        return variation.getName();
    }

}
