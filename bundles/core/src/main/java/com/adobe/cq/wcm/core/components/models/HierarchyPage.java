/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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

import javax.annotation.Nullable;

import org.osgi.annotation.versioning.ProviderType;

import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.hierarchy.HierarchyNodeExporter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Defines the interface used in conjunction with the Sling Model Exporter to expose the mandatory fields and structure of a page and its child pages.
 */
@ProviderType
public interface HierarchyPage extends HierarchyNodeExporter, ContainerExporter {

    /**
     * Title of the page. The page title can be the result of multiple fallbacks
     *
     * @return
     */
    @Nullable
    @JsonProperty("title")
    public String getTitle();

    /**
     * URL to the root model of the App
     *
     * @return
     */
    @Nullable
    @JsonIgnore
    public String getRootUrl();

    /**
     * Root page model of the current hierarchy of pages
     *
     * @return
     */
    @Nullable
    @JsonIgnore
    public HierarchyPage getRootModel();

}
