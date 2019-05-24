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
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.EmbedConstants;
import com.adobe.cq.wcm.core.components.internal.EmbedConstants.EmbedOptions;
import com.adobe.cq.wcm.core.components.models.Embed;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Model(adaptables = SlingHttpServletRequest.class, adapters = { Embed.class, ComponentExporter.class }, resourceType = { EmbedImpl.RESOURCE_TYPE_V1 })
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class EmbedImpl implements Embed {

    protected static final String RESOURCE_TYPE_V1 = "core/wcm/components/embed/v1/embed";

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL, name = "embedoption")
    private String embedOption;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String embedType;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String markup;

    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @JsonIgnore
    private Style currentStyle;

    @Inject
    private Resource resource;

    @PostConstruct
    private void initModel() {

	if (currentStyle != null) {
	    boolean isHTMLEnabled = currentStyle.get(EmbedConstants.PN_ENABLE_HTML_MODE, false);
	    boolean isSelectionEnabled = currentStyle.get(EmbedConstants.PN_ENABLE_SELECTION_MODE, false);
	    if (!isHTMLEnabled) {
		markup = null;
	    }

	    if (!isSelectionEnabled) {
		embedType = null;
	    }

	    if (isHTMLEnabled && isSelectionEnabled && null != embedOption) {
		if (embedOption.equalsIgnoreCase(EmbedOptions.HTML.toString())) {
		    embedType = null;
		} else if (embedOption.equalsIgnoreCase(EmbedOptions.EMBEDDABLE.toString())) {
		    markup = null;
		}
	    }
	}
    }

    @Override
    public String getEmbedMode() {
	return embedOption;
    }

    @Override
    public String getMarkup() {
	return markup;
    }

    @Override
    public String getEmbedType() {
	return embedType;
    }

    @NotNull
    @Override
    public String getExportedType() {
	return resource.getResourceType();
    }
}
