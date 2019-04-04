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
package com.adobe.cq.wcm.core.components.internal.models.v1.navigation;


import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Cell;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.day.cq.wcm.commons.policy.ContentPolicyStyle;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utility class for styles
 */
public class StyleUtils {
    
    /**
     * Gets the content policy wrapped in a style for a specified page
     * @param currentStyle fallback style in case of nothing found or lacking of rights
     * @param page specified page
     * @return found content policy or provided currentStyle as fallback.
     */
    public static Style getContentPolicyStyleFromPage(Style currentStyle, Page page){
    
        final Resource contentResource = page.getContentResource();
        final ResourceResolver resourceResolver = contentResource.getResourceResolver();
        @Nullable final Designer designer = resourceResolver.adaptTo(Designer.class);
        @Nullable final ContentPolicyManager contentPolicyManager = resourceResolver.adaptTo(ContentPolicyManager.class);
        
        if(designer == null || contentPolicyManager == null){
            return currentStyle;
        }
        
        ContentPolicy policy = contentPolicyManager.getPolicy(contentResource);
        
        if(policy == null){
            return currentStyle;
        }
        
        @Nonnull final Cell cell = designer.getStyle(contentResource).getCell();
        return new ContentPolicyStyle(policy, cell);
        
    }
    
    
    
}
