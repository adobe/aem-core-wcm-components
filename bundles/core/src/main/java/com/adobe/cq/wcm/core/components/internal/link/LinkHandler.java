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
package com.adobe.cq.wcm.core.components.internal.link;

import static com.adobe.cq.wcm.core.components.internal.link.LinkNameConstants.PN_LINK_TARGET;
import static com.adobe.cq.wcm.core.components.internal.link.LinkNameConstants.PN_LINK_URL;
import static com.adobe.cq.wcm.core.components.internal.link.LinkNameConstants.VALID_LINK_TARGETS;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.models.Link;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

/**
 * Simple implementation for resolving and validating links from model's resources.
 * This is a Sling model that can injected into other models using the <code>@Self</code> annotation.
 */
@Model(adaptables=SlingHttpServletRequest.class)
public class LinkHandler {

    @Self
    private SlingHttpServletRequest request;
    @ScriptVariable
    private PageManager pageManager;

    /**
     * Resolve a link from the properties of the given resource.
     * @param resource Resource
     * @return Link may be invalid, but is never null
     */
    public @NotNull Link getLink(@NotNull Resource resource) {
        return getLink(resource, PN_LINK_URL);
    }

    /**
     * Resolve a link from the properties of the given resource.
     * @param resource Resource
     * @param linkURLPropertyName Property name to read link URL from.
     * @return Link may be invalid, but is never null
     */
    public @NotNull Link getLink(@NotNull Resource resource, String linkURLPropertyName) {
        ValueMap props = resource.getValueMap();
        String linkURL = props.get(linkURLPropertyName, String.class);
        String linkTarget = props.get(PN_LINK_TARGET, String.class);
        return getLink(linkURL, linkTarget);
    }

    /**
     * Build a link pointing to the given target page.
     * @param page Target page
     * @return Link may be invalid, but is never null
     */
    @SuppressWarnings("deprecation")
    public @NotNull Link getLink(@Nullable Page page) {
        if (page == null) {
            return getInvalid();
        }
        String linkURL = Utils.getURL(request, page);
        return new LinkImpl(linkURL, null, page);
    }

    /**
     * Build a link with the given Link URL and target.
     * @param linkURL Link URL
     * @param target Target
     * @return Link may be invalid, but is never null
     */
    public @NotNull Link getLink(@Nullable String linkURL, @Nullable String target) {
        String resolvedLinkURL = validateAndResolveLinkURL(linkURL);
        String resolvedLinkTarget = validateAndResolverLinkTarget(target);

        Page targetPage = pageManager.getPage(linkURL);

        return new LinkImpl(resolvedLinkURL, resolvedLinkTarget, targetPage);
    }

    /**
     * Returns an invalid link.
     * @return Invalid link, never null
     */
    public @NotNull Link getInvalid() {
        return new LinkImpl(null, null, null);
    }

    @SuppressWarnings("deprecation")
    private String validateAndResolveLinkURL(String linkURL) {
        if (!StringUtils.isEmpty(linkURL)) {
            return Utils.getURL(request, pageManager, linkURL);
        }
        else {
            return null;
        }
        
    }

    private String validateAndResolverLinkTarget(String linkTarget) {
        if (linkTarget != null && VALID_LINK_TARGETS.contains(linkTarget)) {
            return linkTarget;
        }
        else {
            return null;
        }
    }

}
