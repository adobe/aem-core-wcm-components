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

package com.adobe.cq.wcm.core.components.internal.models.v1;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.models.CoreComponent;

public class AbstractCoreComponentImpl implements CoreComponent{
	
	@ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    protected String id;
	
	@SlingObject
    private Resource resource;

	@Override
    public String getId() {
//        if (id == null) {
//            id = String.valueOf(Math.abs(resource.getPath().hashCode() - 1));
//        }
        return id;
    }
	
	@NotNull
    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }

}
