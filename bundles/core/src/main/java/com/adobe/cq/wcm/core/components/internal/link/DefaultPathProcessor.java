/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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
package com.adobe.cq.wcm.core.components.internal.link;

import org.apache.commons.httpclient.URI;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.cq.wcm.core.components.services.link.PathProcessor;
import com.day.cq.commons.Externalizer;

@Component(property = Constants.SERVICE_RANKING + ":Integer=" + Integer.MIN_VALUE,
           service = PathProcessor.class)
public class DefaultPathProcessor implements PathProcessor {

    @Reference
    Externalizer externalizer;

    @Override
    public boolean accepts(@NotNull String path, @NotNull SlingHttpServletRequest request) {
        return true;
    }

    @Override
    public @NotNull String sanitize(@NotNull String path, @NotNull SlingHttpServletRequest request) {
        String cp = request.getContextPath();
        if (!StringUtils.isEmpty(cp) && path.startsWith("/") && !path.startsWith(cp + "/")) {
            path = cp + path;
        }
        try {
            final URI uri = new URI(path, false);
            return uri.toString();
        } catch (Exception e) {
            return path;
        }
    }

    @Override
    public @NotNull String map(@NotNull String path, @NotNull SlingHttpServletRequest request) {
        try {
            return StringUtils.defaultString(request.getResourceResolver().map(request, path));
        } catch (Exception e) {
            return path;
        }
    }

    @Override
    public @NotNull String externalize(@NotNull String path, @NotNull SlingHttpServletRequest request) {
        try {
            return externalizer.publishLink(request.getResourceResolver(), path);
        } catch (Exception e) {
            return path;
        }
    }
}
