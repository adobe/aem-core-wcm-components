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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.internal.resource.ImageResourceWrapper;
import com.day.cq.wcm.api.components.Component;
import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class AbstractImageDelegatingModel extends AbstractComponentImpl {

    /**
     * Component property name that indicates which Image Component will perform the image rendering for composed components. When
     * rendering images, the composed components that provide this property will be able to retrieve the content policy defined for the
     * Image Component's resource type.
     */
    public static final String IMAGE_DELEGATE = "imageDelegate";

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractImageDelegatingModel.class);
    private Component component;
    private Resource toBeWrapped;
    private List<String> hiddenProperties;
    private Resource imageResource;

    protected void setImageResource(@NotNull Component component, @NotNull Resource toBeWrapped, @NotNull List<String> hiddenProperties) {
        this.toBeWrapped = toBeWrapped;
        this.component = component;
        this.hiddenProperties = new ArrayList<String>(hiddenProperties);
    }

    @JsonIgnore
    public Resource getImageResource() {
        if (imageResource == null && component != null) {
            String delegateResourceType = component.getProperties().get(IMAGE_DELEGATE, String.class);
            if (StringUtils.isEmpty(delegateResourceType)) {
                LOGGER.error("In order for image rendering delegation to work correctly you need to set up the imageDelegate property on" +
                        " the {} component; its value has to point to the resource type of an image component.", component.getPath());
            } else {
                imageResource = new ImageResourceWrapper(toBeWrapped, delegateResourceType, hiddenProperties);
            }
        }
        return imageResource;
    }

}
