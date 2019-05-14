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
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import com.adobe.cq.wcm.core.components.internal.form.FormConstants;
import com.adobe.cq.wcm.core.components.internal.embed.EmbedConstants;
import com.adobe.granite.ui.components.Value;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

@Component(service = { Servlet.class }, property = {
	"sling.servlet.resourceTypes=" + EmbedConstants.EMBED_SETTINGS_RESOURCE_TYPE, "sling.servlet.methods=GET",
	"sling.servlet.extensions=html" })
public class EmbedSettingsDataSourceServlet extends SlingSafeMethodsServlet {

    /**
     * Servlet UUID
     */
    private static final long serialVersionUID = 7672484310019288602L;

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
	    throws ServletException, IOException {
	SimpleDataSource actionTypeSettingsDataSource = new SimpleDataSource(getSettingsDialogs(request).iterator());
	request.setAttribute(DataSource.class.getName(), actionTypeSettingsDataSource);
    }

    private List<Resource> getSettingsDialogs(@NotNull SlingHttpServletRequest request) {
	List<Resource> actionTypeSettingsResources = new ArrayList<>();
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
			for (String embedType : embedTypes) {
			    Resource dialogResource = resolver.getResource(embedType + "/" + FormConstants.NN_DIALOG);
			    if (dialogResource != null) {
				actionTypeSettingsResources.add(dialogResource);
			    }
			}
		    }
		}
	    }
	}
	return actionTypeSettingsResources;
    }
}
