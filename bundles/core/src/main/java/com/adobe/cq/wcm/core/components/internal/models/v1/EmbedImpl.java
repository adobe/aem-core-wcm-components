/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Embed;
import com.adobe.cq.wcm.core.components.services.embed.UrlProcessor;
import com.day.cq.wcm.api.designer.Style;

@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = { Embed.class, ComponentExporter.class },
    resourceType = {EmbedImpl.RESOURCE_TYPE_V1, EmbedImpl.RESOURCE_TYPE_V2}
)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
public class EmbedImpl extends AbstractComponentImpl implements Embed {

    protected static final String RESOURCE_TYPE_V1 = "core/wcm/components/embed/v1/embed";
    protected static final String RESOURCE_TYPE_V2 = "core/wcm/components/embed/v2/embed";

    @ValueMapValue(name = PN_TYPE, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String type;

    @ValueMapValue(name = PN_URL, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String url;

    @ValueMapValue(name = PN_HTML, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String html;

    @ValueMapValue(name = PN_EMBEDDABLE_RESOURCE_TYPE, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String embeddableResourceType;

    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private Style currentStyle;

    @Inject @Optional
    private List<UrlProcessor> urlProcessors;

    @Inject
    private Resource resource;

    private Type embedType;
    private UrlProcessor.Result result;

    @PostConstruct
    private void initModel() {
        embedType = Type.fromString(type);
        if (embedType == null || embedType != Type.URL) {
            url = null;
        }
        if (embedType == null || embedType != Type.HTML) {
            html = null;
        }
        if (embedType == null || embedType != Type.EMBEDDABLE) {
            embeddableResourceType = null;
        }
        if (currentStyle != null) {
            boolean urlDisabled = currentStyle.get(PN_DESIGN_URL_DISABLED, false);
            boolean htmlDisabled = currentStyle.get(PN_DESIGN_HTML_DISABLED, false);
            boolean embeddablesDisabled = currentStyle.get(PN_DESIGN_EMBEDDABLES_DISABLED, false);
            if (urlDisabled) {
                url = null;
            }
            if (htmlDisabled) {
                html = null;
            }
            if (embeddablesDisabled) {
                embeddableResourceType = null;
            }
        }
        if (StringUtils.isNotEmpty(url) && urlProcessors != null) {
            for (UrlProcessor urlProcessor : urlProcessors) {
                UrlProcessor.Result result = urlProcessor.process(url);
                if (result != null) {
                    this.result = result;
                    break;
                }
            }
        }
    }

    @Nullable
    @Override
    public Type getType() {
        return embedType;
    }

    @Nullable
    @Override
    public String getUrl() {
        return url;
    }

    @Nullable
    @Override
    public UrlProcessor.Result getResult() {
        return result;
    }

    @Nullable
    @Override
    public String getHtml() {
        return html;
    }

    @Nullable
    @Override
    public String getEmbeddableResourceType() {
        return embeddableResourceType;
    }

    @NotNull
    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }
}
