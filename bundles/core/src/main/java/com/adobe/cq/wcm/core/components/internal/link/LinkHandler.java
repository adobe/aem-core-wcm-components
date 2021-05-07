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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.services.link.PathProcessor;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.common.collect.ImmutableSet;

import static com.adobe.cq.wcm.core.components.commons.link.Link.*;
import static com.adobe.cq.wcm.core.components.internal.link.LinkImpl.*;

/**
 * Simple implementation for resolving and validating links from model's resources.
 * This is a Sling model that can be injected into other models using the <code>@Self</code> annotation.
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class LinkHandler {

    /**
     * List of allowed/supported values for link target.
     * <code>_self</code> is used in the edit dialog but not listed as allowed here as we do not
     * want to render a target attribute at all when <code>_self</code> is selected.
     */
    private static final Set<String> VALID_LINK_TARGETS = ImmutableSet.of("_blank", "_parent", "_top");

    /**
     * The current {@link SlingHttpServletRequest}.
     */
    @Self
    private SlingHttpServletRequest request;

    /**
     * Reference to {@link PageManager}
     */
    @ScriptVariable
    @org.apache.sling.models.annotations.Optional
    private PageManager pageManager;

    @OSGiService
    private List<PathProcessor> pathProcessors;

    /**
     * Resolves a link from the properties of the given resource.
     *
     * @param resource Resource
     * @return {@link Optional} of  {@link Link}
     */
    @NotNull
    @SuppressWarnings("rawtypes")
    public Optional<Link> getLink(@NotNull Resource resource) {
        return getLink(resource, PN_LINK_URL);
    }

    /**
     * Resolves a link from the properties of the given resource.
     *
     * @param resource            Resource
     * @param linkURLPropertyName Property name to read link URL from.
     * @return {@link Optional} of  {@link Link}
     */
    @NotNull
    @SuppressWarnings("rawtypes")
    public Optional<Link> getLink(@NotNull Resource resource, String linkURLPropertyName) {
        ValueMap props = resource.getValueMap();
        String linkURL = props.get(linkURLPropertyName, String.class);
        if (linkURL == null) {
            return Optional.empty();
        }
        String linkTarget = props.get(PN_LINK_TARGET, String.class);
        String linkAccessibilityLabel = props.get(PN_LINK_ACCESSIBILITY_LABEL, String.class);
        String linkTitleAttribute = props.get(PN_LINK_TITLE_ATTRIBUTE, String.class);
        return Optional.ofNullable(getLink(linkURL, linkTarget, linkAccessibilityLabel, linkTitleAttribute).orElse(null));
    }

    /**
     * Builds a link pointing to the given target page.
     * @param page Target page
     *
     * @return {@link Optional} of  {@link Link<Page>}
     */
    @NotNull
    public Optional<Link<Page>> getLink(@Nullable Page page) {
        if (page == null) {
            return Optional.empty();
        }
        String linkURL = getPageLinkURL(page);
        return buildLink(linkURL, request, page, null);
    }

    /**
     * Builds a link with the given Link URL and target.
     * @param linkURL Link URL
     * @param target Target
     *
     * @return {@link Optional} of  {@link Link<Page>}
     */
    @NotNull
    public Optional<Link<Page>> getLink(@Nullable String linkURL, @Nullable String target) {
        String resolvedLinkURL = validateAndResolveLinkURL(linkURL);
        String resolvedLinkTarget = validateAndResolveLinkTarget(target);
        Page targetPage = getPage(linkURL).orElse(null);
        return buildLink(resolvedLinkURL, request, targetPage,
                new HashMap<String, String>() {{ put(ATTR_TARGET, resolvedLinkTarget); }});
    }

    /**
     * Builds a link with the given Link URL, target, accessibility label, title.
     * @param linkURL Link URL
     * @param target Target
     * @param linkAccessibilityLabel Link Accessibility Label
     * @param linkTitleAttribute Link Title Attribute
     *
     * @return {@link Optional} of  {@link Link<Page>}
     */
    @NotNull
    public Optional<Link<Page>> getLink(@Nullable String linkURL, @Nullable String target, @Nullable String linkAccessibilityLabel, @Nullable String linkTitleAttribute) {
        String resolvedLinkURL = validateAndResolveLinkURL(linkURL);
        String resolvedLinkTarget = validateAndResolveLinkTarget(target);
        String validatedLinkAccessibilityLabel = validateLinkAccessibilityLabel(linkAccessibilityLabel);
        String validatedLinkTitleAttribute = validateLinkTitleAttribute(linkTitleAttribute);
        Page targetPage = getPage(linkURL).orElse(null);
        return Optional.of(buildLink(resolvedLinkURL, request, targetPage,
                new HashMap<String, String>() {{
                    put(ATTR_TARGET, resolvedLinkTarget);
                    put(ATTR_ARIA_LABEL, validatedLinkAccessibilityLabel);
                    put(ATTR_TITLE, validatedLinkTitleAttribute);
                }}))
                .orElse(null);
    }

    private Optional<Link<Page>> buildLink(String path, SlingHttpServletRequest request, Page page,
                                           Map<String, String> htmlAttributes) {
        if (StringUtils.isNotEmpty(path)) {
            return pathProcessors.stream()
                    .filter(pathProcessor -> pathProcessor.accepts(path, request))
                    .findFirst().map(pathProcessor -> new LinkImpl<>(pathProcessor.sanitize(path, request), pathProcessor.map(path,
                            request), pathProcessor.externalize(path, request), page, htmlAttributes));
        } else {
            return Optional.of(new LinkImpl<>(path, path, path, page, htmlAttributes));
        }
    }

    /**
     * Validates and resolves a link URL.
     *
     * @param linkURL Link URL
     * @return The validated link URL or {@code null} if not valid
     */
    @Nullable
    private String validateAndResolveLinkURL(String linkURL) {
        if (!StringUtils.isEmpty(linkURL)) {
            return getLinkURL(linkURL);
        } else {
            return null;
        }
    }

    /**
     * Validates and resolves the link target.
     * @param linkTarget Link target
     *
     * @return The validated link target or {@code null} if not valid
     */
    private String validateAndResolveLinkTarget(String linkTarget) {
        if (linkTarget != null && VALID_LINK_TARGETS.contains(linkTarget)) {
            return linkTarget;
        }
        else {
            return null;
        }
    }

    /**
     * Validates the link accessibility label.
     * @param linkAccessibilityLabel Link accessibility label
     *
     * @return The validated link accessibility label or {@code null} if not valid
     */
    private String validateLinkAccessibilityLabel(String linkAccessibilityLabel) {
        if (!StringUtils.isBlank(linkAccessibilityLabel)) {
            return linkAccessibilityLabel;
        }
        else {
            return null;
        }
    }

    /**
     * Validates the link title attribute.
     * @param linkTitleAttribute Link title attribute
     *
     * @return The validated link title attribute or {@code null} if not valid
     */
    private String validateLinkTitleAttribute(String linkTitleAttribute) {
        if (!StringUtils.isBlank(linkTitleAttribute)) {
            return linkTitleAttribute;
        }
        else {
            return null;
        }
    }

    /**
     * If the provided {@code path} identifies a {@link Page}, this method will generate the correct URL for the page. Otherwise the
     * original {@code String} is returned.
     * @param path the page path
     *
     * @return the URL of the page identified by the provided {@code path}, or the original {@code path} if this doesn't identify a {@link Page}
     */
    @NotNull
    private String getLinkURL(@NotNull String path) {
        return getPage(path)
                .map(this::getPageLinkURL)
                .orElse(path);
    }

    /**
     * Given a {@link Page}, this method returns the correct URL, taking into account that the provided {@code page} might provide a
     * vanity URL or can be mapped.
     * @param page the page
     *
     * @return the URL of the page identified by the provided {@code path}, or the original {@code path} if this doesn't identify a {@link Page}
     */
    @NotNull
    private String getPageLinkURL(@NotNull Page page) {
        String vanityURL = page.getVanityUrl();
        String pageLinkURL;
        if (StringUtils.isEmpty(vanityURL)) {
            pageLinkURL = page.getPath() + ".html";
        } else {
            pageLinkURL = vanityURL;
        }
        return pageLinkURL;
    }

    /**
     * Given a path, tries to resolve to the corresponding page.
     *
     * @param path The path
     * @return The {@link Page} corresponding to the path
     */
    @NotNull
    private Optional<Page> getPage(@Nullable String path) {
        if (pageManager == null) {
            pageManager = request.getResourceResolver().adaptTo(PageManager.class);
        }
        if (pageManager == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(pageManager.getPage(path));
    }

}
