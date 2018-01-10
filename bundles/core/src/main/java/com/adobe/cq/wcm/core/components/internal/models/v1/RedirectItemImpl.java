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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

public class RedirectItemImpl implements NavigationItem {

    private String redirectTarget;
    private Page page;
    private String url;
    private SlingHttpServletRequest request;

    public RedirectItemImpl(@Nonnull String redirectTarget, @Nonnull SlingHttpServletRequest request) {
        this.redirectTarget = redirectTarget;
        this.request = request;
        this.page = getRedirectPage();
    }

    private Page getRedirectPage() {
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
    public Page getPage() {
        return page;
    }

    @Override
    @Nonnull
    public String getURL() {
        if(url == null) {
            if(page != null) {
                url = Utils.getURL(request, page);
            } else {
                url = redirectTarget;
            }
        }
        return url;
    }
}
