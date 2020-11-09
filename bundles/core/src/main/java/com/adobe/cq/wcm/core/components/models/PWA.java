/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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

/**
 * Interface for reading progressive web apps configuration
 */
public interface PWA {
    /**
     * Returns true if PWA features are enabled false otherwise
     *
     * @return whether PWA is enabled or not
     * @since com.adobe.cq.wcm.core.components.models 12.17.0
     */
    default boolean isPWAEnabled() { throw new UnsupportedOperationException("Not Implemented"); };

    /**
     * The name of the sites project for this site
     *
     * @return A String that represents the name of the sites project for this site
     * @since com.adobe.cq.wcm.core.components.models 12.17.0
     */
    default String getProjectName() { throw new UnsupportedOperationException("Not Implemented"); };

    /**
     * The theme color of the site sometimes used to color the address bar of the browser
     *
     * @return A Hex String that represents the theme color for this site
     * @since com.adobe.cq.wcm.core.components.models 12.17.0
     */
    default String getThemecolor() { throw new UnsupportedOperationException("Not Implemented"); };

    /**
     * The path to the icon of this PWA
     *
     * @return A String that represents the relative path to the icon of this site
     * @since com.adobe.cq.wcm.core.components.models 12.17.0
     */
    default String getIconPath() { throw new UnsupportedOperationException("Not Implemented"); };

    /**
     * The relative path to the web manifest
     *
     * @return A String that represents the relative path to the web manifest of this site
     * @since com.adobe.cq.wcm.core.components.models 12.17.0
     */
    default String getManifestPath() { throw new UnsupportedOperationException("Not Implemented"); }

    /**
     * The relative path to the web manifest
     *
     * @return A String that represents the relative path to the web manifest of this site
     * @since com.adobe.cq.wcm.core.components.models 12.17.0
     */
    default String getServiceWorkerPath() { throw new UnsupportedOperationException("Not Implemented"); }

}
