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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.internal.EmbedConstants;
import com.adobe.granite.ui.components.Value;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

@Component(service = { Servlet.class }, property = {
	"sling.servlet.resourceTypes=" + EmbedConstants.ALLOWED_OPTIONS_RESOURCE_TYPE, "sling.servlet.methods=GET",
	"sling.servlet.extensions=html" })
public class EmbedAllowedOptionsDataSourceServlet extends SlingSafeMethodsServlet {

    /**
     * Servelt UUID
     */
    private static final long serialVersionUID = -3528015217249498756L;

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
	    throws ServletException, IOException {
	SimpleDataSource actionTypeDataSource = new SimpleDataSource(getAllowedTypes(request).iterator());
	request.setAttribute(DataSource.class.getName(), actionTypeDataSource);
    }

    private List<Resource> getAllowedTypes(@NotNull SlingHttpServletRequest request) {
	List<Resource> allowedTypes = new ArrayList<>();
	ResourceResolver resolver = request.getResourceResolver();
	Resource contentResource = resolver.getResource((String) request.getAttribute(Value.CONTENTPATH_ATTRIBUTE));
	ContentPolicyManager policyMgr = resolver.adaptTo(ContentPolicyManager.class);
	if (null != contentResource && null != policyMgr) {
	    ContentPolicy policy = policyMgr.getPolicy(contentResource);
	    if (policy != null) {
		ValueMap props = policy.getProperties();
		if (props != null) {
		    String[] embedTypes = props.get(EmbedConstants.PN_EMBED_OPTIONS, String[].class);
		    if (embedTypes != null && embedTypes.length > 0) {
			allowedTypes.add(new EmbeddableTypeResource("Select", "", resolver));
			for (String embedType : embedTypes) {
			    Resource componentResource = resolver.getResource(embedType);
			    if (componentResource != null) {
				allowedTypes.add(new EmbeddableTypeResource(componentResource.getValueMap().get(
					JcrConstants.JCR_TITLE, componentResource.getName()), embedType, resolver));
			    }
			}
		    }
		}
	    }
	}
	return allowedTypes;
    }

    private static class EmbeddableTypeResource extends TextValueDataResourceSource {
	private final String componentTitle;
	private final String resourceType;

	EmbeddableTypeResource(String title, String embedType, ResourceResolver resourceResolver) {
	    super(resourceResolver, StringUtils.EMPTY, NonExistingResource.RESOURCE_TYPE_NON_EXISTING);
	    this.componentTitle = title;
	    this.resourceType = embedType;
	}

	@Override
	protected String getText() {
	    return componentTitle;
	}

	@Override
	protected String getValue() {
	    return resourceType;
	}
    }

}
