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
package com.adobe.cq.wcm.core.components.internal.models.v3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.settings.SlingSettingsService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.models.v1.PageListItemImpl;
import com.adobe.cq.wcm.core.components.models.List;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Teaser;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.fasterxml.jackson.annotation.JsonProperty;

@Model(adaptables = SlingHttpServletRequest.class, adapters = { List.class,
        ComponentExporter.class }, resourceType = ListImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ListImpl extends com.adobe.cq.wcm.core.components.internal.models.v2.ListImpl {

    protected static final String RESOURCE_TYPE = "core/wcm/components/list/v3/list";

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageImpl.class);
    public final static String APOSTROPHE = "'";
    public final static String SEMICOLON = ";";
    public final static String OBJECT_ID = "s_objectID=";

    private int counter = 0;
    private String compHashCode = StringUtils.EMPTY;
    private String hashCodeValue = StringUtils.EMPTY;
    private boolean trackingEnabled = false;
    private StringBuilder linkTrackingCode;
    private Set<String> runmode;

    @ScriptVariable
    protected com.day.cq.wcm.api.Page currentPage;

    @SlingObject
    private ResourceResolver resourceResolver;

    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable
    private Component component;

    @Inject
    private Resource resource;

    @ScriptVariable
    private ValueMap properties;

    @Inject
    private SlingSettingsService settings;

    @PostConstruct
    protected void initModel() {
        compHashCode = properties.get(List.PN_TRACKING_OBJECT_ID, compHashCode);
        trackingEnabled = properties.get(List.PN_TRACKING_ENABLED, trackingEnabled);       
        hashCodeValue = String.valueOf(Math.abs(resource.getPath().hashCode() - 1));
        super.initModel();
        runmode = settings.getRunModes();
        if (runmode.contains(Externalizer.AUTHOR)) {
            populateObjectId();
        }
        

    }

    public void populateObjectId() {
               ModifiableValueMap map = resource.adaptTo(ModifiableValueMap.class);
        if (null != map && compHashCode.isEmpty()) {
            map.put(List.PN_TRACKING_OBJECT_ID, currentPage.getName() + StringUtils.SPACE + component.getCellName()
                    + StringUtils.SPACE + hashCodeValue);
        }
        try {
            resourceResolver.commit();
        } catch (PersistenceException e) {
            LOGGER.error("Error occured while saving the objectId for {}", currentPage.getName(), e);
        }
    }

    @Override
    @NotNull
    @JsonProperty("items")
    public Collection<ListItem> getListItems() {
        Collection<ListItem> listItems = new ArrayList<>();
        Collection<Page> pages = getPages();
        for (Page page : pages) {
            if (page != null) {
                listItems.add(new com.adobe.cq.wcm.core.components.internal.models.v3.PageListItemImpl(request, page,
                        getLinktrackinCode()));
            }
        }
        return listItems;
    }

    private String getLinktrackinCode() {
        counter++;
        linkTrackingCode = new StringBuilder();
        if(trackingEnabled){
            linkTrackingCode = linkTrackingCode.append(OBJECT_ID).append(APOSTROPHE).append(compHashCode)
                .append(StringUtils.SPACE).append(counter).append(APOSTROPHE).append(SEMICOLON);
        }
        return linkTrackingCode.toString();
    }

}
