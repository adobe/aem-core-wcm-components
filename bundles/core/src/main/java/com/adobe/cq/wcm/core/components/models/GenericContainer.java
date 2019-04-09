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

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;

@ConsumerType
public interface GenericContainer extends ContainerExporter {
	
    /**
     * Name of the policy property that defines whether or not properties are disabled in color picker
     *
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    String PN_PROPERTIES_DISABLED = "propertiesDisabled";
    
    /**
     * Name of the policy property that defines whether or not colors are disabled
     *
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    String PN_COLORS_DISABLED = "colorsDisabled";
    
    /**
     * Name of the policy property that defines whether or not background image are disabled
     *
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    String PN_IMAGE_DISABLED = "imageDisabled";
    
    /**
     * Name of the resource property that defines background color of container.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    String PN_BACKGROUND_COLOR = "backgroundColor";
    
    /**
     * Name of the resource property that defines background image of container.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    String PN_BACKGROUND_IMAGE = "fileReference";
    
    /**
     * Returns background image source, if one was defined.
     *
     * @return the background image source or {@code null}
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getBackgroundImageSrc() { 
    		throw new UnsupportedOperationException();
    	}
    
    /**
     * Returns background color, if one was defined.
     *
     * @return the background color or {@code null}
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getBackgroundColor() { 
    	throw new UnsupportedOperationException();
    }
    
    /**
     * Returns background style, if one was defined.
     *
     * @return the background style or {@code null}
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getBackgroundStyleString() { 
    	throw new UnsupportedOperationException();
    }
    
    /**
     * Checks if the property tab is hidden.
     *
     * @return {@code true} if the property tab is selected, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default boolean isPropertyDisabled() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Checks if the background color is hidden.
     *
     * @return {@code true} if container background color is selected, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default boolean isColorsDisabled() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Checks if the background image is hidden.
     *
     * @return {@code true} if the image is selected, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default boolean isImageDisabled() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * @see ContainerExporter#getExportedType()
     * @since com.adobe.cq.wcm.core.components.models 12.5.0
     */
    @NotNull
    @Override
    default String getExportedType() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see ContainerExporter#getExportedItems()
     * @since com.adobe.cq.wcm.core.components.models 12.5.0
     */
    @NotNull
    @Override
    default Map<String, ? extends ComponentExporter> getExportedItems() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see ContainerExporter#getExportedItemsOrder()
     * @since com.adobe.cq.wcm.core.components.models 12.5.0
     */
    @NotNull
    @Override
    default String[] getExportedItemsOrder() {
        throw new UnsupportedOperationException();
    }
}
