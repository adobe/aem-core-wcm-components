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
package com.adobe.cq.wcm.core.components.models;

import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.export.json.ComponentExporter;

/**
 * Defines the {@code Modal} Sling Model used for the
 * {@code /apps/core/wcm/components/modal} component. This component currently
 * supports using "#{modalId}" in the url
 *
 * @since com.adobe.cq.wcm.core.components.models 12.8.0
 */
@ConsumerType
public interface Modal extends ComponentExporter {

	/**
	 * Returns the modal id based on the hash generated from the component path
	 * 
	 * @return modelId
	 * @since com.adobe.cq.wcm.core.components.models 12.8.0
	 */
	default String getModalId() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the description for the modal
	 * 
	 * @return description
	 * @since com.adobe.cq.wcm.core.components.models 12.8.0
	 */
	default String getDescription() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the boolean value defining whether to show modal by default on page
	 * load or not
	 * 
	 * @return defaultModalShow
	 * @since com.adobe.cq.wcm.core.components.models 12.8.0
	 */
	default boolean getShowModalByDefault() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the type of the fragment chosen
	 * 
	 * @return fragmentType
	 * @since com.adobe.cq.wcm.core.components.models 12.8.0
	 */
	default String getFragmentType() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the content fragment path
	 * 
	 * @return contentFragmentPath
	 * @since com.adobe.cq.wcm.core.components.models 12.8.0
	 */
	default String getContentFragmentPath() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the experience fragment path
	 * 
	 * @return experienceFragmentPath
	 * @since com.adobe.cq.wcm.core.components.models 12.8.0
	 */
	default String getExperienceFragmentPath() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see ComponentExporter#getExportedType()
	 * @since com.adobe.cq.wcm.core.components.models 12.8.0
	 */
	@NotNull
	@Override
	default String getExportedType() {
		throw new UnsupportedOperationException();
	}

}
