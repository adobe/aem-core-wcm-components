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
package com.adobe.cq.wcm.core.components.internal.embed.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import com.adobe.cq.wcm.core.components.internal.form.FormConstants;
import com.adobe.cq.wcm.core.components.internal.servlets.TextValueDataResourceSource;
import com.adobe.cq.wcm.core.components.internal.embed.ComponentSearchUtil;
import com.adobe.cq.wcm.core.components.internal.embed.EmbedConstants;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.day.cq.wcm.foundation.forms.FormsConstants;
import com.day.cq.wcm.foundation.forms.FormsManager;

@Component(service = { Servlet.class }, property = {
	"sling.servlet.resourceTypes=" + EmbedConstants.ALL_OPTIONS_RESOURCE_TYPE, "sling.servlet.methods=GET",
	"sling.servlet.extensions=html" })
public class AllEmbedOptionsDataSourceServlet extends SlingSafeMethodsServlet {

    /**
     * Servlet UUID
     */
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
	    throws ServletException, IOException {
	SimpleDataSource actionTypeDataSource = new SimpleDataSource(getActionTypeResources(
		request.getResourceResolver()).iterator());
	request.setAttribute(DataSource.class.getName(), actionTypeDataSource);
    }

    private List<Resource> getActionTypeResources(ResourceResolver resourceResolver) {
	List<Resource> actionTypeResources = new ArrayList<>();
	FormsManager formsManager = resourceResolver.adaptTo(FormsManager.class);
	if (formsManager != null) {
	    Iterator<FormsManager.ComponentDescription> actions = ComponentSearchUtil.search(EmbedConstants.EMBEDDABLE_RESOURCE_TYPE,
		    FormsConstants.PROPERTY_RST, resourceResolver).iterator();
	    while (actions.hasNext()) {
		FormsManager.ComponentDescription description = actions.next();
		Resource dialogResource = resourceResolver.getResource(description.getResourceType() + "/"
			+ FormConstants.NN_DIALOG);
		if (dialogResource != null) {
		    actionTypeResources.add(new EmbeddableTypeResource(description, resourceResolver));
		}
	    }
	}
	return actionTypeResources;
    }

    private static class EmbeddableTypeResource extends TextValueDataResourceSource {

	private final FormsManager.ComponentDescription description;

	EmbeddableTypeResource(FormsManager.ComponentDescription description, ResourceResolver resourceResolver) {
	    super(resourceResolver, StringUtils.EMPTY, NonExistingResource.RESOURCE_TYPE_NON_EXISTING);
	    this.description = description;
	}

	@Override
	protected String getText() {
	    return description.getTitle();
	}

	@Override
	protected String getValue() {
	    return description.getResourceType();
	}
    }

}
