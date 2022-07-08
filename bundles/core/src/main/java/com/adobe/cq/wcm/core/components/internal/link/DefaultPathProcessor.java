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

import java.util.Optional;

import org.apache.commons.httpclient.URI;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.services.link.PathProcessor;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Designate(ocd = DefaultPathProcessor.Config.class)
@Component(property = Constants.SERVICE_RANKING + ":Integer=" + Integer.MIN_VALUE,
           service = PathProcessor.class)
public class DefaultPathProcessor implements PathProcessor {
    @ObjectClassDefinition(
            name = "Core Components Default Path Processor"
    )
    @interface Config {
        @AttributeDefinition(
                name = "Vanity path rewriting",
                description = "Defines how the vanity url of a page is taken into account while generating the link URLs",
                options = {
                        @Option(label = "never", value = "never"),
                        @Option(label = "always", value = "always"),
                        @Option(label = "on path mapping and externalization", value = "mapping")
                }
        )
        String vanityConfig() default "never";
    }

    public enum VanityConfig {
        NEVER("never"),
        ALWAYS("always"),
        MAPPING("mapping");

        private String value;

        public String getValue() {
            return value;
        }

        VanityConfig(String value) {
            this.value = value;
        }

        public static VanityConfig fromString(String value) {
            for (VanityConfig config :VanityConfig.values()) {
                if (StringUtils.equals(config.value, value)) {
                    return config;
                }
            }
            return NEVER;
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPathProcessor.class);

    @Reference
    Externalizer externalizer;
    private VanityConfig vanityConfig;

    @Activate
    protected void activate(final BundleContext bundleContext, final Config config) {
        vanityConfig = VanityConfig.fromString(config.vanityConfig());
    }

    @Override
    public boolean accepts(@NotNull String path, @NotNull SlingHttpServletRequest request) {
        return true;
    }

    @Override
    public @NotNull String sanitize(@NotNull String path, @NotNull SlingHttpServletRequest request) {
        if (vanityConfig == VanityConfig.ALWAYS) {
            path = getPathOrVanityUrl(path, request.getResourceResolver());
        }
        int fragmentQueryMark = path.indexOf('#');
        if (fragmentQueryMark < 0) {
            fragmentQueryMark = path.indexOf('?');
        }

        final String fragmentQuery;
        if (fragmentQueryMark >= 0) {
            fragmentQuery = path.substring(fragmentQueryMark);
            path = path.substring(0, fragmentQueryMark);
        } else {
            fragmentQuery = null;
        }
        String cp = request.getContextPath();
        if (!StringUtils.isEmpty(cp) && path.startsWith("/") && !path.startsWith(cp + "/")) {
            path = cp + path;
        }

        try {
            final URI uri = new URI(path, false);
            path = uri.toString();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        if (fragmentQuery != null) {
            path = path + fragmentQuery;
        }
        return path;
    }

    @Override
    public @NotNull String map(@NotNull String path, @NotNull SlingHttpServletRequest request) {
        ResourceResolver resourceResolver = request.getResourceResolver();
        String mappedPath;
        try {
            if (vanityConfig == VanityConfig.MAPPING || vanityConfig == VanityConfig.ALWAYS) {
                mappedPath = StringUtils.defaultString(resourceResolver.map(request, getPathOrVanityUrl(path, resourceResolver)));
            } else {
                mappedPath = StringUtils.defaultString(resourceResolver.map(request, path));
            }
        } catch (Exception e) {
            mappedPath = path;
        }
        return mappedPath;
    }


    @Override
    public @NotNull String externalize(@NotNull String path, @NotNull SlingHttpServletRequest request) {
        ResourceResolver resourceResolver = request.getResourceResolver();
        String externalPath;
        try {
            if (vanityConfig == VanityConfig.MAPPING || vanityConfig == VanityConfig.ALWAYS) {
                externalPath = externalizer.publishLink(resourceResolver, getPathOrVanityUrl(path, resourceResolver));
            } else {
                externalPath = externalizer.publishLink(resourceResolver, path);
            }
        } catch (Exception e) {
            externalPath = path;
        }
        return externalPath;
    }

    @NotNull
    private String getPathOrVanityUrl(@NotNull String path, @NotNull ResourceResolver resourceResolver) {
        return Optional.ofNullable(getVanityUrl(path, resourceResolver))
                .filter(StringUtils::isNotBlank)
                .orElse(path);
    }

    @Nullable
    private String getVanityUrl(@NotNull String path, @NotNull ResourceResolver resourceResolver) {
        String vanityUrl = null;
        if (path.endsWith(LinkBuilderImpl.HTML_EXTENSION)) {
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            if (pageManager != null) {
                Page page = pageManager.getPage(path.substring(0, path.lastIndexOf(LinkBuilderImpl.HTML_EXTENSION)));
                if (page != null) {
                    vanityUrl = page.getVanityUrl();
                    if (StringUtils.isNotBlank(vanityUrl) && !vanityUrl.startsWith("/")) {
                      vanityUrl = "/" +  vanityUrl;
                    }
                }
            }
        }
        return vanityUrl;
    }
}
