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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Modal;
import com.adobe.cq.wcm.core.components.models.ModalFragmentType;

@Model(adaptables = SlingHttpServletRequest.class, adapters = { Modal.class,
		ComponentExporter.class }, resourceType = ModalImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ModalImpl implements Modal {

	protected static final String RESOURCE_TYPE = "core/wcm/components/modal/v1/modal";

	@SlingObject
	private Resource resource;

	@SlingObject
	private ResourceResolver resourceResolver;
	
	@ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
	private String id;

	@ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
	private String title;

	@ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
	private boolean open;

	@ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
	private String type;

	@ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
	private String path;

	@Override
	public String getExportedType() {
		return resource.getResourceType();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public ModalFragmentType getType() {
		return ModalFragmentType.lookupByValue(type);
	}

	@Override
	public String getPath() {
		return path;
	}

}