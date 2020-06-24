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
package com.adobe.cq.wcm.core.components.internal.models.v1.form;


import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.models.form.Field;
import com.day.cq.commons.jcr.JcrConstants;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract class which can be used as base class for {@link Field} implementations.
 */
public abstract class AbstractFieldImpl implements Field {

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    protected String id;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL, name = JcrConstants.JCR_TITLE)
    @Nullable
    protected String title;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    protected String name;

    @ValueMapValue
    @Default(values = "")
    protected String value;

    @SlingObject
    private Resource resource;

    /**
     * @return a prefix String which will be used for generating {@link #id} through {@link #getId()}
     */
    protected abstract String getIDPrefix();

    protected abstract String getDefaultName();

    protected abstract String getDefaultValue();

    protected abstract String getDefaultTitle();

    @Override
    public String getId() {
        if (id == null) {
            id = getIDPrefix() + "-" + String.valueOf(Math.abs(resource.getPath().hashCode() - 1));
        }
        return id;
    }

    @Override
    public String getName() {
        if (name == null) {
            name = getDefaultName();
        }
        return name;
    }

    @Override
    public String getValue() {
        if (value == null) {
            value = getDefaultValue();
        }
        return value;
    }

    @Override
    public String getTitle() {
        if (title == null) {
            title = getDefaultTitle();
        }
        return title;
    }

    @NotNull
    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }
}
