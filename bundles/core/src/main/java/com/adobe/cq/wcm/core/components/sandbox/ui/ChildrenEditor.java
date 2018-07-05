/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.sandbox.ui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = {SlingHttpServletRequest.class})
public class ChildrenEditor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChildrenEditor.class);

    public final static String RESOURCE_TYPE = "core/wcm/sandbox/commons/ui/childreneditor";

    @Self
    private SlingHttpServletRequest request;

    private Resource parent;

    private List<Resource> children;

    @PostConstruct
    private void initModel() {
        readChildren();
    }

    private void readChildren() {
        children = new ArrayList<>();
        String parentPath = request.getRequestPathInfo().getSuffix();
        if (StringUtils.isNotEmpty(parentPath)) {
            ResourceResolver resolver = request.getResourceResolver();
            parent = resolver.getResource(parentPath);
            if (parent != null) {
                for(Resource res : parent.getChildren()) {
                    children.add(res);
                }
            }
        }
    }

    public List<Resource> getChildren() {
        return children;
    }

    public Resource getParent() {
        return parent;
    }
}
