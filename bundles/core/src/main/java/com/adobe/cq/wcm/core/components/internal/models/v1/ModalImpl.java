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

import javax.annotation.PostConstruct;

import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Modal;

@Model(adaptables = SlingHttpServletRequest.class, adapters = { Modal.class,
		ComponentExporter.class }, resourceType = ModalImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ModalImpl implements Modal {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModalImpl.class);
	protected static final String RESOURCE_TYPE = "core/wcm/components/modal/v1/modal";

	private static final String KEY_MODAL_ID = "modalId";
	private static final String XF_PATH_CHECK = "/content/experience-fragments";
	private static final String HTML_EXT = ".html";

	@SlingObject
	private Resource resource;

	@SlingObject
	private ResourceResolver resourceResolver;

	private String modalId;

	@ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
	private String pagePath;

	@ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
	private boolean showModalByDefault;

	@PostConstruct
	private void initModel() {
		if (modalId == null) {
			populateModalProperties();
		}
	}

	void populateModalProperties() {
		String absoluteComponentPath = resource.getPath();
		int index = absoluteComponentPath.indexOf(JcrConstants.JCR_CONTENT);
		String relativeComponentPath = absoluteComponentPath.substring(index);
		modalId = String.valueOf(Math.abs(relativeComponentPath.hashCode() - 1));

		if (pagePath != null && pagePath.startsWith(XF_PATH_CHECK) && !pagePath.contains(HTML_EXT)) {
			pagePath = pagePath.concat(HTML_EXT);
		}

		ModifiableValueMap map = resource.adaptTo(ModifiableValueMap.class);
		if (map != null) {
			map.put(KEY_MODAL_ID, modalId);
		}

		try {
			resourceResolver.commit();
		} catch (PersistenceException e) {
			LOGGER.error("Error occured while saving the modalId for {}", absoluteComponentPath, e);
		}
	}

	@Override
	public String getExportedType() {
		return resource.getResourceType();
	}

	@Override
	public String getModalId() {
		return modalId;
	}

	@Override
	public String getPagePath() {
		return pagePath;
	}

	@Override
	public boolean getShowModalByDefault() {
		return showModalByDefault;
	}

}