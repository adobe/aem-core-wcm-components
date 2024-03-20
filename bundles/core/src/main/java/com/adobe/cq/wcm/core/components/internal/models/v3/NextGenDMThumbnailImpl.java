/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2023 Adobe
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
package com.adobe.cq.wcm.core.components.internal.models.v3;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.ui.wcm.commons.config.NextGenDynamicMediaConfig;
import com.adobe.cq.wcm.core.components.commons.editor.nextgendm.NextGenDMThumbnail;

import static com.adobe.cq.wcm.core.components.internal.models.v3.ImageImpl.isNgdmImageReference;

@Model(adaptables = SlingHttpServletRequest.class,
    adapters = {NextGenDMThumbnail.class},
    resourceType = NextGenDMThumbnailImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class NextGenDMThumbnailImpl implements NextGenDMThumbnail {

    private static final Logger LOGGER = LoggerFactory.getLogger(NextGenDMThumbnailImpl.class);
    protected static final String RESOURCE_TYPE = "core/wcm/components/image/v3/image/nextgendmthumbnail";

    /**
     * The current resource.
     */
    @SlingObject
    protected Resource resource;

    @Self
    private SlingHttpServletRequest request;

    @OSGiService
    @Optional
    private NextGenDynamicMediaConfig nextGenDynamicMediaConfig;

    private String componentPath;

    private String src;

    private String altText = "";

    @PostConstruct
    private void initModel() {
        componentPath = request.getRequestPathInfo().getSuffix();
        Resource component = request.getResourceResolver().getResource(componentPath);
        ValueMap properties  = component.getValueMap();
        String fileReference = properties.get("fileReference", String.class);
        String smartCrop = properties.get("smartCropRendition", String.class);
        ValueMap configs = resource.getValueMap();
        int width = configs.get("width", 480);
        int height = configs.get("height", 480);
        altText = configs.get("alt", "image thumbnail");
        if (isNgdmImageReference(fileReference)) {
            NextGenDMImageURIBuilder builder = new NextGenDMImageURIBuilder(nextGenDynamicMediaConfig, fileReference)
                .withPreferWebp(true)
                .withWidth(width)
                .withHeight(height);
            if (StringUtils.isNotEmpty(smartCrop)) {
                builder.withSmartCrop(smartCrop);
            }
            this.src = builder.build();
        }
    }

    @Override
    public String getSrc() {
        return src;
    }

    @Override
    public String getAlt() {
        return altText;
    }
}
