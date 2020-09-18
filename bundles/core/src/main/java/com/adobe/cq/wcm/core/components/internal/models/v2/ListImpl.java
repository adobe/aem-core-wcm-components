/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.models.v1.PageListItemImpl;
import com.adobe.cq.wcm.core.components.models.List;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.fasterxml.jackson.annotation.JsonProperty;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {List.class, ComponentExporter.class}, resourceType = ListImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ListImpl extends com.adobe.cq.wcm.core.components.internal.models.v1.ListImpl implements List {

	protected static final String RESOURCE_TYPE = "core/wcm/components/list/v2/list";

	@Self
	private SlingHttpServletRequest request;

	/**
	 * Result list.
	 */
	private Collection<ListItem> listItems;
	protected Collection<ListItem> links;

	@Override
	@NotNull
	@JsonProperty("items")
	public Collection<ListItem> getListItems() {
		Resource listRes = request.getResource();
		ValueMap listvm = listRes.getValueMap();
		if(listvm.containsKey("pages")) {
			if (this.listItems == null) {
				this.listItems = super.getPages().stream()
						.filter(Objects::nonNull)
						.map(page -> new PageListItemImpl(request, page, getId(), PageListItemImpl.PROP_DISABLE_SHADOWING_DEFAULT))
						.collect(Collectors.toList());
				return this.listItems;
			}
		}
		Collection<ListItem> externalListItems = new ArrayList<>();
		if (this.listItems == null) {
			externalListItems = getLinks();
		}
		return externalListItems;


	}

	public Collection<ListItem> getLinks() {
		Resource listRes = request.getResource();
		Resource childResource = listRes.getChild("external-links");
		if (childResource != null) {
			this.links = populateModel(childResource);
		}
		return this.links;
	}

	public Collection<ListItem> populateModel(Resource resource) {
		links = new ArrayList<>();
		String URL = "";
		String title = "";
		if (resource != null) {
			Iterator<Resource> linkResources = resource.listChildren();
			while (linkResources.hasNext()) {
				Resource childResource = linkResources.next();
				ExternalLinkImpl link = childResource.adaptTo(ExternalLinkImpl.class);
				if(null!=link) {
					URL = link.getURL();
					title = link.getTitle();
					if((null!=URL)&&(null!=title)){
						if(URL.startsWith("/content")){
							link.setURL(URL+".html");
							this.links.add(link);
						}
						else {
							this.links.add(link);
						}
					}
				}
			}
		}
		return this.links;
	}
}
