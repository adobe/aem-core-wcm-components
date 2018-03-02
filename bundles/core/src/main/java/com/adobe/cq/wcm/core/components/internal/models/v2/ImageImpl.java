/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.servlets.AdaptiveImageServlet;
import com.adobe.cq.wcm.core.components.models.Image;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Image.class, ComponentExporter.class}, resourceType = ImageImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ImageImpl extends com.adobe.cq.wcm.core.components.internal.models.v1.ImageImpl implements Image {

    public static final String RESOURCE_TYPE = "core/wcm/components/image/v2/image";
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageImpl.class);
    private static final String SRC_URI_TEMPLATE_WIDTH_VAR = "{.width}";
    private static final String CONTENT_POLICY_DELEGATE_PATH = "contentPolicyDelegatePath";

    private String srcUriTemplate;

    public ImageImpl() {
        selector = AdaptiveImageServlet.CORE_DEFAULT_SELECTOR;
    }

    @PostConstruct
    protected void initModel() {
        super.initModel();
        boolean altValueFromDAM = properties.get(PN_ALT_VALUE_FROM_DAM, currentStyle.get(PN_ALT_VALUE_FROM_DAM, true));
        boolean titleValueFromDAM = properties.get(PN_TITLE_VALUE_FROM_DAM, currentStyle.get(PN_TITLE_VALUE_FROM_DAM, true));
        displayPopupTitle = properties.get(PN_DISPLAY_POPUP_TITLE, currentStyle.get(PN_DISPLAY_POPUP_TITLE, true));
        if (StringUtils.isNotEmpty(fileReference)) {
            // the image is coming from DAM
            final Resource assetResource = request.getResourceResolver().getResource(fileReference);
            if (assetResource != null) {
                Asset asset = assetResource.adaptTo(Asset.class);
                if (asset != null) {
                    if (!isDecorative && altValueFromDAM) {
                        String damDescription = asset.getMetadataValue(DamConstants.DC_DESCRIPTION);
                        if(StringUtils.isEmpty(damDescription)) {
                            damDescription = asset.getMetadataValue(DamConstants.DC_TITLE);
                        }
                        if (StringUtils.isNotEmpty(damDescription)) {
                            alt = damDescription;
                        }
                    }
                    if (titleValueFromDAM) {
                        String damTitle = asset.getMetadataValue(DamConstants.DC_TITLE);
                        if (StringUtils.isNotEmpty(damTitle)) {
                            title = damTitle;
                        }
                    }
                } else {
                    LOGGER.error("Unable to adapt resource '{}' used by image '{}' to an asset.", fileReference,
                            request.getResource().getPath());
                }
            } else {
                LOGGER.error("Unable to find resource '{}' used by image '{}'.", fileReference, request.getResource().getPath());
            }
        }
        if (hasContent) {
            disableLazyLoading = currentStyle.get(PN_DESIGN_LAZY_LOADING_ENABLED, true);

            srcUriTemplate = baseResourcePath + DOT + selector +
                    SRC_URI_TEMPLATE_WIDTH_VAR + DOT + extension +
                    (inTemplate ? templateRelativePath : "") + (lastModifiedDate > 0 ? "/" + lastModifiedDate + DOT + extension : "");

            // if content policy delegate path is provided pass it to the image Uri
            String policyDelegatePath = request.getParameter(CONTENT_POLICY_DELEGATE_PATH);
            if (StringUtils.isNotBlank(policyDelegatePath)) {
                srcUriTemplate += "?" + CONTENT_POLICY_DELEGATE_PATH + "=" + policyDelegatePath;
                src += "?" + CONTENT_POLICY_DELEGATE_PATH + "=" + policyDelegatePath;
            }

            buildJson();
        }
    }

    @Nonnull
    @Override
    public int[] getWidths() {
        return Arrays.copyOf(smartSizes, smartSizes.length);
    }

    @Override
    public String getSrcUriTemplate() {
        return srcUriTemplate;
    }

    @Override
    public boolean isLazyEnabled() {
        return !disableLazyLoading;
    }

}
