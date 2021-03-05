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

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.common.collect.ImmutableSet;

import static com.adobe.cq.wcm.core.components.commons.link.Link.PN_LINK_TARGET;
import static com.adobe.cq.wcm.core.components.commons.link.Link.PN_LINK_URL;

/**
 * Simple implementation for resolving and validating links from model's resources.
 * This is a Sling model that can be injected into other models using the <code>@Self</code> annotation.
 */
@Model(adaptables=SlingHttpServletRequest.class)
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
    private PageManager pageManager;
    
    /**
     * Resolves a link from the properties of the given resource.
     * @param resource Resource
     *
     * @return Link may be invalid, but is never null
     */
    public @NotNull Link getLink(@NotNull Resource resource) {
        return getLink(resource, PN_LINK_URL);
    }

    /**
     * Resolves a link from the properties of the given resource.
     * @param resource Resource
     * @param linkURLPropertyName Property name to read link URL from.
     *
     * @return Link may be invalid, but is never null
     */
    public @NotNull Link getLink(@NotNull Resource resource, String linkURLPropertyName) {
        ValueMap props = resource.getValueMap();
        String linkURL = props.get(linkURLPropertyName, String.class);
        String linkTarget = props.get(PN_LINK_TARGET, String.class);
        return getLink(linkURL, linkTarget);
    }

    /**
     * Builds a link pointing to the given target page.
     * @param page Target page
     *
     * @return Link may be invalid, but is never null
     */
    public @NotNull Link<Page> getLink(@Nullable Page page) {
        if (page == null) {
            return getInvalid();
        }
        String linkURL = getPageLinkURL(page);
        return new LinkImpl<>(linkURL, null, page);
    }

    /**
     * Builds a link with the given Link URL and target.
     * @param linkURL Link URL
     * @param target Target
     *
     * @return Link may be invalid, but is never null
     */
    public @NotNull Link<Page> getLink(@Nullable String linkURL, @Nullable String target) {
        String resolvedLinkURL = validateAndResolveLinkURL(linkURL);
        String resolvedLinkTarget = validateAndResolveLinkTarget(target);
        Page targetPage = pageManager.getPage(linkURL);
        return new LinkImpl<>(resolvedLinkURL, resolvedLinkTarget, targetPage);
    }

    /**
     * Returns an invalid link.
     *
     * @return Invalid link, never null
     */
    public @NotNull Link getInvalid() {
        return new LinkImpl(null, null, null);
    }

    /**
     * Validates and resolves a link URL.
     * @param linkURL Link URL
     *
     * @return The validated link URL or {@code null} if not valid
     */
    private String validateAndResolveLinkURL(String linkURL) {
        if (!StringUtils.isEmpty(linkURL)) {
            return getLinkURL(linkURL);
        }
        else {
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
     * If the provided {@code path} identifies a {@link Page}, this method will generate the correct URL for the page. Otherwise the
     * original {@code String} is returned.
     * @param path the page path
     *
     * @return the URL of the page identified by the provided {@code path}, or the original {@code path} if this doesn't identify a {@link Page}
     */
    @NotNull
    private String getLinkURL(@NotNull String path) {
        Page page = pageManager.getPage(path);
        if (page != null) {
            return getPageLinkURL(page);
        }
        return path;
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
            pageLinkURL = request.getResourceResolver().map(request, page.getPath()) + ".html";
        } else {
            pageLinkURL = vanityURL;
        }
        return StringUtils.defaultString(request.getContextPath()) + pageLinkURL;
    }

}
