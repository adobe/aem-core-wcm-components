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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import com.adobe.granite.ui.components.Value;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

@Component(
        service = { Servlet.class },
        property = {
                "sling.servlet.resourceTypes="+ ContainerBackgroundColorDataSourceServlet.RESOURCE_TYPE,
                "sling.servlet.methods=GET",
                "sling.servlet.extensions=html"
        }
)
public class ContainerBackgroundColorDataSourceServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = -4952312991923817824L;
	protected static final String NN_SWATCHES = "cq:swatches";
	protected static final String PN_COLOR_VALUE ="value";
	protected static final String PN_COLOR_NAME ="color";
	protected static final String RESOURCE_TYPE = "core/wcm/components/container/v1/container/allowedcolors";

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
            throws ServletException, IOException {
        SimpleDataSource backgroundColorDataSource = new SimpleDataSource(getColors(request).iterator());
        request.setAttribute(DataSource.class.getName(), backgroundColorDataSource);
    }

	protected List<Resource> getColors(@NotNull SlingHttpServletRequest request) {
		List<Resource> colors = new ArrayList<>();
		ResourceResolver resolver = request.getResourceResolver();
		Resource contentResource = resolver.getResource((String) request.getAttribute(Value.CONTENTPATH_ATTRIBUTE));
		ContentPolicyManager policyMgr = resolver.adaptTo(ContentPolicyManager.class);
		if (policyMgr == null || contentResource == null) {
			return colors;
		}
		ContentPolicy policy = policyMgr.getPolicy(contentResource);
		if (policy == null) {
			return colors;
		}
		ValueMap color = null;
		Resource swatches = resolver.getResource(policy.getPath() + "/" + NN_SWATCHES);
		if (swatches == null) {
			return colors;
		}
		Iterator<Resource> swatchesIterator = swatches.listChildren();
		while (swatchesIterator.hasNext()) {
			Resource childres = swatchesIterator.next();
			color = new ValueMapDecorator(new HashMap<String, Object>());
			ValueMap childResValueMap = childres.getValueMap();
			if (childResValueMap.containsKey(PN_COLOR_VALUE) && childResValueMap.containsKey(PN_COLOR_NAME)) {
				color.put(PN_COLOR_VALUE, childResValueMap.get(PN_COLOR_VALUE, String.class));
				color.put(PN_COLOR_NAME, childResValueMap.get(PN_COLOR_NAME, String.class));
				colors.add(new ValueMapResource(resolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED,
						color));
			}
		}

		return colors;
	}

}
