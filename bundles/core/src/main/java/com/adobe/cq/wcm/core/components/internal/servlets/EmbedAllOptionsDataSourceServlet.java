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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import com.adobe.cq.wcm.core.components.internal.EmbedConstants;
import com.adobe.cq.wcm.core.components.internal.form.FormConstants;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.foundation.forms.FormsConstants;
import com.day.cq.wcm.foundation.forms.FormsManager;
import com.day.cq.wcm.foundation.forms.FormsManager.ComponentDescription;

@Component(service = { Servlet.class }, property = {
	"sling.servlet.resourceTypes=" + EmbedConstants.ALL_OPTIONS_RESOURCE_TYPE, "sling.servlet.methods=GET",
	"sling.servlet.extensions=html" })
public class EmbedAllOptionsDataSourceServlet extends SlingSafeMethodsServlet {

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
	    Iterator<FormsManager.ComponentDescription> actions = search(EmbedConstants.EMBEDDABLE_RESOURCE_TYPE,
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

    private Collection<ComponentDescription> search(String propValue, String propName, ResourceResolver resourceResolver) {
	String[] searchPaths = resourceResolver.getSearchPath();
	for (int i = 0; i < searchPaths.length; i++) {
	    searchPaths[i] = searchPaths[i].substring(0, searchPaths[i].length() - 1);
	}
	final Map<String, ComponentDescription> map = new HashMap<>();
	final List<String> disabledComponents = new ArrayList<>();
	for (final String path : searchPaths) {
	    final StringBuilder buffer = new StringBuilder("/jcr:root");
	    buffer.append(path);
	    buffer.append("//* [@");
	    buffer.append(propName);
	    buffer.append("='");
	    buffer.append(propValue);
	    buffer.append("']");

	    final Iterator<Resource> i = resourceResolver.findResources(buffer.toString(), "xpath");
	    while (i.hasNext()) {
		final Resource rsrc = i.next();
		// check if disabled
		final ValueMap properties = ResourceUtil.getValueMap(rsrc);
		// get resource type
		final String rt = rsrc.getPath().substring(path.length() + 1);
		if (properties.get(FormsConstants.COMPONENT_PROPERTY_ENABLED, Boolean.TRUE)) {
		    if (!map.containsKey(rt) && !disabledComponents.contains(rt)) {
			map.put(rt, new EmbedComponentDescriptionImpl(rt, rsrc.getName(), properties));
		    }
		} else {
		    disabledComponents.add(rt);
		}
	    }
	}
	// now sort the entries
	final List<ComponentDescription> entries = new ArrayList<>(map.values());
	Collections.sort(entries);
	return entries;
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

    private static final class EmbedComponentDescriptionImpl implements ComponentDescription {

	private final String resourceType;
	private final String title;
	private final String hint;
	private final int order;

	public EmbedComponentDescriptionImpl(final String rt, final String defaultName, final ValueMap props) {
	    this.resourceType = rt;
	    this.title = props.get(JcrConstants.JCR_TITLE, defaultName);
	    this.order = props.get(FormsConstants.COMPONENT_PROPERTY_ORDER, 0);
	    this.hint = props.get(FormsConstants.COMPONENT_PROPERTY_HINT, String.class);
	}

	/**
	 * @see com.day.cq.wcm.foundation.forms.FormsManager.ComponentDescription#getResourceType()
	 */
	public String getResourceType() {
	    return this.resourceType;
	}

	/**
	 * @see com.day.cq.wcm.foundation.forms.FormsManager.ComponentDescription#getTitle()
	 */
	public String getTitle() {
	    return this.title;
	}

	public int getOrder() {
	    return this.order;
	}

	/**
	 * @see com.day.cq.wcm.foundation.forms.FormsManager.ComponentDescription#getHint()
	 */
	public String getHint() {
	    return this.hint;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(ComponentDescription o) {
	    if (o == null) {
		return 0;
	    }

	    if (this.getClass() != o.getClass()) {
		return 0;
	    }
	    final EmbedComponentDescriptionImpl obj = (EmbedComponentDescriptionImpl) o;
	    if (this.order < obj.order) {
		return -1;
	    } else if (this.order == obj.order) {
		return this.title.compareTo(obj.title);
	    }
	    return 1;
	}

	@Override
	public boolean equals(Object obj) {
	    if (obj == null) {
		return false;
	    }

	    if (this.getClass() != obj.getClass()) {
		return false;
	    }

	    return compareTo((EmbedComponentDescriptionImpl) obj) == 0;
	}

	@Override
	public int hashCode() {
	    return this.title.hashCode() + this.title.hashCode();
	}

    }

}
