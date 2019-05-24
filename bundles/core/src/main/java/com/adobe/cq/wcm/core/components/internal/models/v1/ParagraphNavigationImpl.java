/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2019 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/
package com.adobe.cq.wcm.core.components.internal.models.v1;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.adobe.cq.wcm.core.components.models.ParagraphNavigation;
import com.day.cq.wcm.api.Page;

@Model(adaptables = SlingHttpServletRequest.class,
    adapters = {ParagraphNavigation.class, ComponentExporter.class},
    resourceType = {ParagraphNavigationImpl.RESOURCE_TYPE})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME , extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ParagraphNavigationImpl implements ParagraphNavigation {

    public static final String RESOURCE_TYPE = "core/wcm/components/paragraphnavigation/v1/paragraphnavigation";

    private static final String TITLE_V1 = "core/wcm/components/title/v1/title";
    private static final String TITLE_V2 = "core/wcm/components/title/v2/title";

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private ResourceResolver resolver;

    @ScriptVariable
    private Page currentPage;

    private List<NavigationItem> items;

    @Override
    public List<NavigationItem> getItems() {
        if (items == null) {
            items = new ArrayList<>();
            Resource resource = request.getResource();
            Resource parent = resource.getParent();
            Iterable<Resource> it = parent.getChildren();
            while (it.iterator().hasNext()) {
                Resource sibling = it.iterator().next();
                // if the resource is a title
                if (StringUtils.equals(sibling.getResourceType(), TITLE_V1)
                    || StringUtils.equals(sibling.getResourceType(), TITLE_V2)
                    || StringUtils.equals(sibling.getResourceSuperType(), TITLE_V1)
                    || StringUtils.equals(sibling.getResourceSuperType(), TITLE_V2)) {
                    NavigationItem item = getNavigationItem(sibling);
                    items.add(item);
                }
            }
        }
        return items;
    }

    private NavigationItem getNavigationItem(Resource item) {
        // get the level
        ValueMap properties = item.adaptTo(ValueMap.class);
        String type = properties.get("./type", String.class);
        int level = -1;
        if (StringUtils.equals(type, "h1")) {
            level = 1;
        } else if (StringUtils.equals(type, "h2")) {
            level = 2;
        } else if (StringUtils.equals(type, "h3")) {
            level = 3;
        } else if (StringUtils.equals(type, "h4")) {
            level = 4;
        } else if (StringUtils.equals(type, "h5")) {
            level = 5;
        } else if (StringUtils.equals(type, "h6")) {
            level = 6;
        }
        return new NavigationItemImpl(null, true, request, level, null);
    }

    @NotNull
    @Override
    public String getExportedType() {
        return request.getResource().getResourceType();
    }

}
