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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.commons.mime.MimeTypeService;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.SightlyWCMMode;
import com.adobe.cq.wcm.core.components.internal.Constants;
import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.internal.servlets.AdaptiveImageServlet;
import com.adobe.cq.wcm.core.components.models.Image;
import com.day.cq.commons.DownloadResource;
import com.day.cq.commons.ImageResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

@Model(adaptables = SlingHttpServletRequest.class, adapters = Image.class, resourceType = ImageImpl.RESOURCE_TYPE)
@Exporter(name = Constants.EXPORTER_NAME, extensions = Constants.EXPORTER_EXTENSION)
public class ImageImpl implements Image {

    public static final String RESOURCE_TYPE = "core/wcm/components/image/v1/image";
    private static final String DEFAULT_EXTENSION = "jpeg";

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageImpl.class);
    private static final String DOT = ".";
    private static final String MIME_TYPE_IMAGE_JPEG = "image/jpeg";
    private static final String MIME_TYPE_IMAGE_PREFIX = "image/";
    private static final List<String> NON_SUPPORTED_IMAGE_MIMETYPE = Collections.singletonList("image/svg+xml");

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Resource resource;

    @ScriptVariable
    private PageManager pageManager;

    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    private SightlyWCMMode wcmmode;

    @ScriptVariable
    private Style currentStyle;

    @ScriptVariable
    private ValueMap properties;

    @Inject
    @Source("osgi-services")
    private MimeTypeService mimeTypeService;

    @ValueMapValue(name = DownloadResource.PN_REFERENCE, injectionStrategy = InjectionStrategy.OPTIONAL)
    private String fileReference;

    @ValueMapValue(name = ImageResource.PN_ALT, injectionStrategy = InjectionStrategy.OPTIONAL)
    private String alt;

    @ValueMapValue(name = JcrConstants.JCR_TITLE, injectionStrategy = InjectionStrategy.OPTIONAL)
    private String title;

    @ValueMapValue(name = ImageResource.PN_LINK_URL, injectionStrategy = InjectionStrategy.OPTIONAL)
    private String linkURL;

    private String src;
    protected String[] smartImages = new String[]{};
    protected int[] smartSizes = new int[0];
    private String json;
    private boolean displayPopupTitle;
    private boolean isDecorative;

    protected boolean disableLazyLoading;

    @PostConstruct
    private void initModel() {
        boolean hasContent = false;
        String mimeType = MIME_TYPE_IMAGE_JPEG;
        displayPopupTitle = properties.get(PN_DISPLAY_POPUP_TITLE, currentStyle.get(PN_DISPLAY_POPUP_TITLE, false));
        isDecorative = properties.get(PN_IS_DECORATIVE, currentStyle.get(PN_IS_DECORATIVE, false));

        if (StringUtils.isNotEmpty(fileReference)) {
            // the image is coming from DAM
            final Resource assetResource = request.getResourceResolver().getResource(fileReference);
            if (assetResource != null) {
                Asset asset = assetResource.adaptTo(Asset.class);
                if (asset != null) {
                    mimeType = PropertiesUtil.toString(asset.getMimeType(), MIME_TYPE_IMAGE_JPEG);
                    hasContent = true;
                } else {
                    LOGGER.error("Unable to adapt resource '{}' used by image '{}' to an asset.", fileReference, resource.getPath());
                }
            } else {
                LOGGER.error("Unable to find resource '{}' used by image '{}'.", fileReference, resource.getPath());
            }
        } else {
            Resource file = resource.getChild(DownloadResource.NN_FILE);
            if (file != null) {
                mimeType = PropertiesUtil.toString(file.getResourceMetadata().get(ResourceMetadata.CONTENT_TYPE), MIME_TYPE_IMAGE_JPEG);
                hasContent = true;
            }
        }
        if (hasContent) {
            // validate if correct mime type (i.e. rasterized image)
            if (!mimeType.startsWith(MIME_TYPE_IMAGE_PREFIX)) {
                LOGGER.error("Image at {} uses a binary with a non-image mime type ({})", resource.getPath(), mimeType);
                return;
            }
            if (NON_SUPPORTED_IMAGE_MIMETYPE.contains(mimeType)) {
                LOGGER.error("Image at {} uses binary with a non-supported image mime type ({})", resource.getPath(), mimeType);
                return;
            }
            String extension = mimeTypeService.getExtension(mimeType);
            long lastModifiedDate = 0;
            if (!isWcmModeDisabled()) {
                ValueMap properties = resource.getValueMap();
                Calendar lastModified = properties.get(JcrConstants.JCR_LASTMODIFIED, Calendar.class);
                if (lastModified == null) {
                    lastModified = properties.get(NameConstants.PN_PAGE_LAST_MOD, Calendar.class);
                }
                if (lastModified != null) {
                    lastModifiedDate = lastModified.getTimeInMillis();
                }
            }
            if (extension.equalsIgnoreCase("tif") || extension.equalsIgnoreCase("tiff")) {
                extension = DEFAULT_EXTENSION;
            }
            ResourceResolver resourceResolver = request.getResourceResolver();
            ContentPolicyManager policyManager = resourceResolver.adaptTo(ContentPolicyManager.class);
            if (policyManager != null) {
                ContentPolicy contentPolicy = policyManager.getPolicy(resource);
                if (contentPolicy != null) {
                    disableLazyLoading = contentPolicy.getProperties().get(PN_DESIGN_LAZY_LOADING_ENABLED, false);
                }
                Set<Integer> supportedRenditionWidths = getSupportedRenditionWidths(contentPolicy);
                smartImages = new String[supportedRenditionWidths.size()];
                smartSizes = new int[supportedRenditionWidths.size()];
                int index = 0;
                String escapedResourcePath = Text.escapePath(resource.getPath());
                for (Integer width : supportedRenditionWidths) {
                    smartImages[index] = request.getContextPath() + escapedResourcePath + DOT + AdaptiveImageServlet.DEFAULT_SELECTOR + DOT +
                            width + DOT + extension + (!isWcmModeDisabled() && lastModifiedDate > 0 ? "/" + lastModifiedDate + DOT + extension
                            : "");
                    smartSizes[index] = width;
                    index++;
                }
                src = request.getContextPath() + escapedResourcePath + DOT + AdaptiveImageServlet.DEFAULT_SELECTOR + DOT;
                if (smartSizes.length == 1) {
                    src += smartSizes[0] + DOT + extension;
                } else {
                    src += extension;
                }
                src += !isWcmModeDisabled() && lastModifiedDate > 0 ? "/" + lastModifiedDate + DOT + extension : "";
            }
            if (!isDecorative) {
                linkURL = Utils.getURL(request, pageManager, linkURL);
            } else {
                linkURL = null;
                alt = null;
            }
            buildJson();
        }
    }

    private boolean isWcmModeDisabled() {
        return wcmmode == null || wcmmode.isDisabled();
    }

    @Override
    public String getSrc() {
        return src;
    }

    @Override
    public boolean displayPopupTitle() {
        return displayPopupTitle;
    }

    @Override
    public String getAlt() {
        return alt;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getLink() {
        return linkURL;
    }

    @Override
    public String getFileReference() {
        return fileReference;
    }

    @Override
    public String getJson() {
        return json;
    }

    private void buildJson() {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put(Image.JSON_SMART_SIZES, new JSONArray(Arrays.asList(ArrayUtils.toObject(smartSizes))));
        objectMap.put(Image.JSON_SMART_IMAGES, new JSONArray(Arrays.asList(smartImages)));
        objectMap.put(Image.JSON_LAZY_ENABLED, !disableLazyLoading);
        json = new JSONObject(objectMap).toString();
    }

    private Set<Integer> getSupportedRenditionWidths(ContentPolicy contentPolicy) {
        Set<Integer> allowedRenditionWidths = new TreeSet<>();
        if (contentPolicy != null) {
            String[] supportedWidthsConfig = contentPolicy.getProperties().get(PN_DESIGN_ALLOWED_RENDITION_WIDTHS, new String[0]);
            for (String width : supportedWidthsConfig) {
                try {
                    allowedRenditionWidths.add(Integer.parseInt(width));
                } catch (NumberFormatException e) {
                    LOGGER.error(String.format("Invalid width detected (%s) for content policy configuration.", width), e);
                }
            }
        }
        return allowedRenditionWidths;
    }
}
