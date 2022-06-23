/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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
package com.adobe.cq.wcm.core.components.internal.servlets.embed;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import com.adobe.cq.wcm.core.components.internal.servlets.embed.EmbeddablesDataSourceServlet.EmbeddableDescription;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;

/**
 * Data source that returns the design dialog options for all allowed embeddables,
 * optionally preceded and/or succeeded by some other static tabs. 
 */
@Component(
    service = { Servlet.class },
    property = {
        "sling.servlet.resourceTypes=" + EmbedDesignTabsDataSourceServlet.RESOURCE_TYPE_V1,
        "sling.servlet.resourceTypes=" + EmbedDesignTabsDataSourceServlet.RESOURCE_TYPE_V2,
        "sling.servlet.methods=GET",
        "sling.servlet.extensions=html"
    }
)
public class EmbedDesignTabsDataSourceServlet extends SlingSafeMethodsServlet {

    public static final String RESOURCE_TYPE_V1 = "core/wcm/components/embed/v1/datasources/embeddesigntabs";
    public static final String RESOURCE_TYPE_V2 = "core/wcm/components/embed/v2/datasources/embeddesigntabs";

    private static final long serialVersionUID = 7672484310019288602L;
    private static final String NN_DESIGN_DIALOG = "cq:design_dialog";

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) {
        SimpleDataSource embeddableOptionsDataSource = new SimpleDataSource(getEmbedDesignTabs(request).iterator());
        request.setAttribute(DataSource.class.getName(), embeddableOptionsDataSource);
    }

    private List<Resource> getEmbedDesignTabs(@NotNull SlingHttpServletRequest request) {
        List<Resource> embedDesignTabs = new ArrayList<>();
        // first include static tabs below "firsttabs"
        Resource firstTabs = request.getResource().getChild("firsttabs");
        if (firstTabs != null) {
            firstTabs.getChildren().forEach(embedDesignTabs::add);
        }
        ResourceResolver resolver = request.getResourceResolver();
        // then dynamic tabs for embeddables
        for (EmbeddableDescription embeddableDescription : EmbeddablesDataSourceServlet.findEmbeddables(request.getResourceResolver())) {
            Resource embeddableDesignTab = resolver.getResource(embeddableDescription.getResourceType() + "/" + NN_DESIGN_DIALOG);
            if (embeddableDesignTab != null) {
                embedDesignTabs.add(embeddableDesignTab);
            }
        }
        // last include static tabs below "lasttabs"
        Resource lastTabs = request.getResource().getChild("lasttabs");
        if (lastTabs != null) {
            lastTabs.getChildren().forEach(embedDesignTabs::add);
        }
        return embedDesignTabs;
    }
}
