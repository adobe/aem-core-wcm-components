/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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

import com.adobe.cq.wcm.core.components.internal.resource.CoreResourceWrapper;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import java.util.Collections;
import java.util.Optional;

import static com.adobe.cq.wcm.core.components.internal.models.v2.PageImpl.PN_REDIRECT_TARGET;

/**
 * Navigation Item used for page redirects.
 */
public class RedirectItemImpl implements NavigationItem {

    /**
     * The target page that this Redirect Item redirects to (null if page doesn't exist, or redirect does not point to a page).
     */
    private final Page page;

    /**
     * Link that this Redirect Item redirects to.
     */
    protected final Link<?> link;

    /**
     * Construct a Redirect Item.
     *
     * @param redirectTarget The path or URL to redirect to.
     * @param request The current request.
     * @param linkManager The Link Manager.
     */
    public RedirectItemImpl(@NotNull final String redirectTarget,
                            @NotNull final SlingHttpServletRequest request,
                            @NotNull final LinkManager linkManager) {
        Optional<Page> redirectPage = getRedirectPage(request.getResourceResolver(), redirectTarget);
        this.link = redirectPage
            .map(linkManager::get)
            .orElseGet(() -> {
                // this wrapper exists to handle the possibility that `redirectTarget` didn't come from `PN_REDIRECT_TARGET`
                CoreResourceWrapper resourceWrapper = new CoreResourceWrapper(
                    request.getResource(),
                    request.getResource().getResourceType(),
                    Collections.emptyList(),
                    Collections.singletonMap(PN_REDIRECT_TARGET, redirectTarget));
                return linkManager.get(resourceWrapper).withLinkUrlPropertyName(PN_REDIRECT_TARGET);
            })
            .build();

        this.page = Optional.ofNullable(this.link.getReference())
            .filter(ref -> ref instanceof Page)
            .map(ref -> (Page) ref)
            .orElse(redirectPage.orElse(null));
    }

    /**
     * Get the redirect target page.
     *
     * @param resourceResolver A ResourceResolver.
     * @param redirectTarget The target path.
     * @return The target page is redirectTarget references a page, otherwise empty.
     */
    private Optional<Page> getRedirectPage(@NotNull final ResourceResolver resourceResolver, @NotNull final String redirectTarget) {
        return Optional.ofNullable(resourceResolver.getResource(redirectTarget))
            .flatMap(targetResource -> Optional.ofNullable(resourceResolver.adaptTo(PageManager.class))
                .map(pm -> pm.getContainingPage(targetResource)));
    }

    @Override
    @Nullable
    @Deprecated
    public Page getPage() {
        return page;
    }

    @Override
    @Nullable
    @Deprecated
    public String getURL() {
        return link.getURL();
    }
}
