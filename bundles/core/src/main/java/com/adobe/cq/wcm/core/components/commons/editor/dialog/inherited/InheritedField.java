/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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
package com.adobe.cq.wcm.core.components.commons.editor.dialog.inherited;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.internal.Utils;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Model(adaptables = SlingHttpServletRequest.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class InheritedField {
    private final static Logger log = LoggerFactory.getLogger(InheritedField.class);
    private static final String OVERRIDE_SUFFIX = "_override";
    private static final boolean OVERRIDE_DEFAULT = false;

    /**
     * The current request
     */
    @Self
    private SlingHttpServletRequest slingRequest;

    /**
     * The value map property which should be inherited
     */
    @ValueMapValue
    private String prop;

    /**
     * The HTML class attribute
     */
    @ValueMapValue(name = "granite:class")
    private String attrClass;

    /**
     * The heading for the dialog
     */
    @ValueMapValue
    private String heading;

    /**
     * The path to the page for which the property should be inherited
     */
    private String path;
    /**
     * The page which contains the inherited propery
     */
    private Page containingPage;
    /**
     * The inherited value for the requested property
     */
    private String inheritedValue;
    /**
     * The specified value in case the requested property is set on the current page
     */
    private String specifiedValue;
    /**
     * {@code true} if the requested property should be overridden by the current page, otherwise {@code false}
     */
    private boolean override;

    @PostConstruct
    private void initModel() {
        path = slingRequest.getRequestPathInfo().getSuffix();
        if (StringUtils.isBlank(path)) {
            RequestParameter itemParam = slingRequest.getRequestParameter("item");
            if (itemParam == null) {
                log.error("Suffix and 'item' param are blank");
                return;
            }
            path = itemParam.getString();
        }

        if (StringUtils.isBlank(prop)) {
            log.error("'prop' value is null");
            return;
        }

        ResourceResolver rr = slingRequest.getResourceResolver();
        PageManager pm = rr.adaptTo(PageManager.class);
        if (pm == null) {
            log.error("pagemanager is null");
            return;
        }

        containingPage = pm.getPage(path);
        if (containingPage == null) {
            log.error("page is null");
            return;
        }
        inheritedValue = Utils.getInheritedValue(containingPage.getParent(), prop);

        Resource contentResource = containingPage.getContentResource();
        if (contentResource == null) {
            return;
        }

        ValueMap props = contentResource.adaptTo(ValueMap.class);
        if (props == null) {
            return;
        }

        override = props.get(getProp() + OVERRIDE_SUFFIX, OVERRIDE_DEFAULT);
        specifiedValue = props.get(getProp(), String.class);
    }

    public String getProp() {
        return prop;
    }

    public String getAttrClass() {
        return attrClass;
    }

    public String getHeading() {
        return heading;
    }

    public String getInheritedValue() {
        return inheritedValue;
    }

    public String getSpecifiedValue() {
        return specifiedValue;
    }

    public boolean isOverride() {
        return override;
    }
}
