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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

public class RedirectItemImpl implements NavigationItem {

    private final String redirectTarget;
    private final Page page;
    protected final Link link;

    public RedirectItemImpl(@NotNull String redirectTarget, @NotNull SlingHttpServletRequest request, @NotNull LinkManager linkManager) {
        this.redirectTarget = redirectTarget;
        this.page = getRedirectPage(request);
        this.link = linkManager.get(this.page).build();
    }

    private Page getRedirectPage(@NotNull SlingHttpServletRequest request) {
        Page page = null;
        ResourceResolver resourceResolver = request.getResourceResolver();
        Resource targetResource = resourceResolver.getResource(redirectTarget);
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (pageManager != null && targetResource != null) {
            page = pageManager.getContainingPage(targetResource);
        }
        return page;
    }

    @Override
    @Nullable
    @Deprecated
    public Page getPage() {
        return page;
    }

    @Override
    @Nullable
    public String getURL() {
        return link.getURL();
    }
}
