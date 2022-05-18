/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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
package com.adobe.cq.wcm.core.components.internal.models.v3;

import java.util.ArrayList;
import java.util.List;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.jetbrains.annotations.NotNull;


import org.apache.sling.api.resource.Resource;
import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.link.LinkHandler;
import com.adobe.cq.wcm.core.components.internal.models.v2.RedirectItemImpl;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.adobe.cq.wcm.core.components.models.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Page.class, ContainerExporter.class}, resourceType = PageImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class PageImpl extends com.adobe.cq.wcm.core.components.internal.models.v2.PageImpl implements Page {

    protected static final String RESOURCE_TYPE = "core/wcm/components/page/v3/page";
    public static final String LINK_ATTRIBUTE_NAME = "linkAttributeName";
    public static final String LINK_ATTRIBUTE_VALUE = "linkAttributeValue";
    public static final String RESOURCE_LOADING = "resourceloading";
    public static final String LINK = "link";

    /**
     * Style property name to load custom Javascript libraries asynchronously.
     */
    protected static final String PN_CLIENTLIBS_ASYNC = "clientlibsAsync";

    @Override
    @JsonIgnore
    public boolean isClientlibsAsync() {
        if (currentStyle != null) {
            return currentStyle.get(PN_CLIENTLIBS_ASYNC, false);
        }
        return false;
    }

    protected NavigationItem newRedirectItem(@NotNull String redirectTarget, @NotNull SlingHttpServletRequest request, @NotNull LinkHandler linkHandler) {
        return new RedirectItemImpl(redirectTarget, request, linkHandler);
    }
    
    public String getResourceAttribute() {
		String linkVal = StringUtils.EMPTY;
		if (currentPage.getPath() != null) {
			linkVal = getResourcesOfAttr(currentPage.getContentResource(), linkVal);
		}
		return linkVal;
	}

	public String getResourcesOfAttr(Resource resource, String linkVal) {
		Resource link = resource.getChild(LINK);
		String attrName = StringUtils.EMPTY;
		String attrValue = StringUtils.EMPTY;
		String attributes = StringUtils.EMPTY;
		if (link != null) {
			for (Resource linkres : link.getChildren()) {
				Resource resourceloading = linkres.getChild(RESOURCE_LOADING);
				if (resourceloading != null) {
					String result = StringUtils.EMPTY;
					List<String> list = new ArrayList<String>();
					for (Resource res : resourceloading.getChildren()) {
						attrName = StringUtils.EMPTY;
						attrValue = StringUtils.EMPTY;
						if (res.getValueMap().containsKey(LINK_ATTRIBUTE_NAME)) {
							attrName = res.getValueMap().get(LINK_ATTRIBUTE_NAME, String.class);
						}
						if (res.getValueMap().containsKey(LINK_ATTRIBUTE_VALUE)) {
							attrValue = res.getValueMap().get(LINK_ATTRIBUTE_VALUE, String.class);
						}
						if (attrValue == null || attrValue.equals(StringUtils.EMPTY)) {
							attributes = attrName;
						} else {
							attributes = attrName + "=" + "\"" + attrValue + "\"";
						}

						list.add(attributes);
						result = StringUtils.join(list, " ");
					}
					if (linkVal.equals(StringUtils.EMPTY)) {
						linkVal = "<link " + result + " />";
					} else {
						linkVal = linkVal + "\n" + "\t" + "<link " + result + " />";
					}
				}
			}
		}
		return linkVal;
	}

}
