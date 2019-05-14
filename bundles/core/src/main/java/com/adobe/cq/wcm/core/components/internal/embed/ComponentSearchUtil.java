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
package com.adobe.cq.wcm.core.components.internal.embed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.foundation.forms.FormsConstants;
import com.day.cq.wcm.foundation.forms.FormsManager.ComponentDescription;

public final class ComponentSearchUtil {
	/** Logger */
	private static final Logger logger = LoggerFactory
			.getLogger(ComponentSearchUtil.class);

	private ComponentSearchUtil() {
		// Constructor private
	}

	public static Collection<ComponentDescription> search(String propValue,
			String propName, ResourceResolver resourceResolver) {
		String[] searchPaths = resourceResolver.getSearchPath();
		for (int i = 0; i < searchPaths.length; i++) {
			searchPaths[i] = searchPaths[i].substring(0,
					searchPaths[i].length() - 1);
		}
		final Map<String, ComponentDescription> map = new HashMap<String, ComponentDescription>();
		final List<String> disabledComponents = new ArrayList<String>();
		for (final String path : searchPaths) {
			final StringBuilder buffer = new StringBuilder("/jcr:root");
			buffer.append(path);
			buffer.append("//* [@");
			buffer.append(propName);
			buffer.append("='");
			buffer.append(propValue);
			buffer.append("']");

			logger.debug("Query: {}", buffer.toString());
			final Iterator<Resource> i = resourceResolver.findResources(
					buffer.toString(), "xpath");
			while (i.hasNext()) {
				final Resource rsrc = i.next();
				// check if disabled
				final ValueMap properties = ResourceUtil.getValueMap(rsrc);
				// get resource type
				final String rt = rsrc.getPath().substring(path.length() + 1);
				if (properties.get(FormsConstants.COMPONENT_PROPERTY_ENABLED,
						Boolean.TRUE)) {
					if (!map.containsKey(rt)
							&& !disabledComponents.contains(rt)) {
						map.put(rt, new ComponentDescriptionImpl(rt,
								ResourceUtil.getName(rsrc), properties));
					}
				} else {
					disabledComponents.add(rt);
				}
			}
		}
		// now sort the entries
		final List<ComponentDescription> entries = new ArrayList<ComponentDescription>(
				map.values());
		Collections.sort(entries);
		return entries;
	}

}
