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
package com.adobe.cq.wcm.core.components.internal.helper.image;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

public class AdaptiveImageHelper {

    public static final String IMAGE_RESOURCE_TYPE = "core/wcm/components/image";

    private AdaptiveImageHelper() {

    }

    /**
     * Get an alternative image component candidate {@link Resource}. Alternative image component is used when an image component is part
     * of an editable template or an external image resource is provided as selector.
     *
     * @param suffix the Sling {@link RequestPathInfo} suffix which contains the alternative image component path
     * @param component the current image component instance
     * @return the alternative image component resource or {@code null}
     */
    @Nullable
    public static Resource getComponentCandidate(@NotNull String suffix, Resource component) {
        Resource componentCandidate = null;
        long lastModifiedSuffix = getRequestLastModifiedSuffix(suffix);
        String suffixPath = lastModifiedSuffix == 0 ?
                // no timestamp info, but extension is valid; get resource name
                suffix.substring(0, suffix.lastIndexOf('.')) :
                // timestamp info, get parent path from suffix
                suffix.substring(0, suffix.lastIndexOf("/" + String.valueOf(lastModifiedSuffix)));
        if (StringUtils.isNotEmpty(suffixPath)) {
            ResourceResolver resourceResolver = component.getResourceResolver();
            if (!component.isResourceType(IMAGE_RESOURCE_TYPE)) {
                // image coming from template
                componentCandidate = Optional.ofNullable(resourceResolver.adaptTo(PageManager.class))
                        .map(pageManager -> pageManager.getContainingPage(component))
                        .map(Page::getTemplate)
                        .map(template -> ResourceUtil.normalize(template.getPath() + suffixPath))
                        .map(resourceResolver::getResource).orElse(null);
            }

            if (componentCandidate == null) {
                // image coming from external resource
                Resource externalImageResource = resourceResolver.getResource(suffixPath);
                if (externalImageResource != null && externalImageResource.isResourceType(IMAGE_RESOURCE_TYPE)) {
                    componentCandidate = externalImageResource;
                }
            }
        }
        return componentCandidate;
    }

    /**
     * Extract the lastModified timestamp from the suffix.
     *
     * @param suffix the {@link RequestPathInfo} suffix which contains the timestamp
     * @return the lastModified timestamp as {@code long}, or default value 0 if no timestamp can be extracted.
     */
    public static long getRequestLastModifiedSuffix(@Nullable String suffix) {
        long requestLastModified = 0;
        if (StringUtils.isNotEmpty(suffix) && suffix.contains(".")) {
            // check if the 13 digits UTC milliseconds timestamp, preceded by a forward slash is present in the suffix
            Pattern p = Pattern.compile("\\(|\\)|\\/\\d{13}");
            Matcher m = p.matcher(suffix);
            if (!m.find()) {
                return requestLastModified;
            }
            try {
                requestLastModified = Long.parseLong(ResourceUtil.getName(m.group()));
            } catch (NumberFormatException e) {
                // do nothing
            }
        }
        return requestLastModified;
    }

    /**
     * Get the HTTP redirect location of the image request in case the provided timestamp is outdated or not provided in the current
     * request.
     *
     * @param request the current {@link SlingHttpServletRequest}
     * @param lastModifiedEpoch the latest timestamp of the asset or image component resource
     * @return the redirect location
     */
    @Nullable
    public static String getRedirectLocation(SlingHttpServletRequest request, long lastModifiedEpoch) {
        String redirectLocation = null;
        long lastModifiedSuffix = 0;
        RequestPathInfo requestPathInfo = request.getRequestPathInfo();
        String suffix = requestPathInfo.getSuffix();
        if (StringUtils.isNotEmpty(suffix)) {
            lastModifiedSuffix = getRequestLastModifiedSuffix(suffix);
        }
        if (lastModifiedSuffix > 0) {
            suffix = StringUtils.replace(suffix, String.valueOf(lastModifiedSuffix), String.valueOf(lastModifiedEpoch));
            redirectLocation = Text.escapePath(request.getContextPath() + requestPathInfo.getResourcePath()) + "." +
                    requestPathInfo.getSelectorString() + "." + requestPathInfo.getExtension() + Text.escapePath(suffix);
        } else if (request.getResource().isResourceType(IMAGE_RESOURCE_TYPE)) {
            redirectLocation = Text.escapePath(request.getContextPath() + requestPathInfo.getResourcePath()) + "." +
                    requestPathInfo.getSelectorString() + "." + requestPathInfo.getExtension() + "/" +
                    lastModifiedEpoch + "." + requestPathInfo.getExtension();
        } else {
            String resourcePath = request.getPathInfo();
            String extension = FilenameUtils.getExtension(resourcePath);
            if (StringUtils.isNotEmpty(resourcePath)) {
                if (StringUtils.isNotEmpty(extension)) {
                    resourcePath = resourcePath.substring(0, resourcePath.length() - extension.length() - 1);
                }
                redirectLocation = request.getContextPath() + Text.escapePath(resourcePath) + "/" + lastModifiedEpoch + "." +
                        requestPathInfo.getExtension();
            }
        }
        return redirectLocation;
    }
}
