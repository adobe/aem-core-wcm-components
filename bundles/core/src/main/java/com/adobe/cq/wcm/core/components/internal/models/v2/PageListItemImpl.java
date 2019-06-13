/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.Page;

public class PageListItemImpl extends com.adobe.cq.wcm.core.components.internal.models.v1.PageListItemImpl
        implements ListItem {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageListItemImpl.class);
    protected String linkTrackingCode;

    public PageListItemImpl(@NotNull SlingHttpServletRequest request, @NotNull Page page,
            @NotNull String linkTrackingCode) {
        super(request, page);
        this.linkTrackingCode = linkTrackingCode;
    }

    public String getLinkTrackingCode() {
        return linkTrackingCode;
    }

}
