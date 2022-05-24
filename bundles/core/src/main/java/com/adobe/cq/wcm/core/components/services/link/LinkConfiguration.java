/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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
package com.adobe.cq.wcm.core.components.services.link;

import java.util.Map;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.wcm.core.components.models.Page;

/**
 * Interface used to hold the configuration of a link.
 */
public interface LinkConfiguration {

    /**
     * Returns the resource holding the link properties.
     */
    Resource getResource();

    /**
     * Sets the resource holding the link properties.
     *
     * @param resource The resource holding the link properties.
     * @return the link builder holding the link configuration.
     */
    LinkConfiguration setResource(Resource resource);

    /**
     * Returns the name of the property holding the link URL.
     */
    String getResourcePropertyName();

    /**
     * Sets the name of the property holding the link URL.
     *
     * @param linkURLPropertyName The name of the property holding the link URL.
     * @return the link builder holding the link configuration.
     */
    LinkConfiguration setResourcePropertyName(String linkURLPropertyName);

    /**
     * Returns the target page used to define the link.
     */
    Page getTarget();

    /**
     * Sets the page as the link target.
     *
     * @param page The page used as the link target.
     * @return the link builder holding the link configuration.
     */
    LinkConfiguration setTarget(Page page);

    /**
     * Returns the URL used to define the link.
     */
    String getURL();

    /**
     * Sets the link URL.
     *
     * @param URL The link URL.
     * @return the link builder holding the link configuration.
     */
    LinkConfiguration setURL(String URL);

    /**
     * Returns the link attributes.
     */
    Map<String, String> getAttributes();

    /**
     * Sets the link attributes (e.g. target, accessibility label, title).
     *
     * @param attributes The link attributes.
     * @return the link builder holding the link configuration.
     */
    LinkConfiguration setAttributes(Map<String, String> attributes);

}
