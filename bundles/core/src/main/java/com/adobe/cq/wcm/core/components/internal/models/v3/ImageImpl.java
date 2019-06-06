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

import java.util.Set;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.wcm.api.Page;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.servlets.AdaptiveImageServlet;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.Teaser;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.components.Component;

@Model(adaptables = SlingHttpServletRequest.class, adapters = { Image.class,
        ComponentExporter.class }, resourceType = ImageImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ImageImpl extends com.adobe.cq.wcm.core.components.internal.models.v2.ImageImpl implements Image {

    public static final String RESOURCE_TYPE = "core/wcm/components/image/v3/image";
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageImpl.class);
    public final static String OBJECT_ID = "s_objectID=";
    public final static String APOSTROPHE = "'";
    public final static String SEMICOLON = ";";

    private String compHashCode = StringUtils.EMPTY;
    private String hashCodeValue = StringUtils.EMPTY;
    private StringBuilder linkTrackingCode;
    private boolean trackingEnabled = false;
    private Set<String> runmode;

    @ScriptVariable
    protected Page currentPage;

    @ScriptVariable
    private Component component;

    @SlingObject
    private ResourceResolver resourceResolver;

    @Inject
    private SlingSettingsService settings;

    @PostConstruct
    protected void initModel() {
        compHashCode = properties.get(Image.PN_TRACKING_OBJECT_ID, compHashCode);
        trackingEnabled = properties.get(Image.PN_TRACKING_ENABLED, trackingEnabled);
        hashCodeValue = String.valueOf(Math.abs(resource.getPath().hashCode() - 1));
        runmode = settings.getRunModes();
        if (runmode.contains(Externalizer.AUTHOR)) {
            populateObjectId();
        }
        super.initModel();

    }

    public void populateObjectId() {
        ModifiableValueMap map = resource.adaptTo(ModifiableValueMap.class);
        if (null != map && compHashCode.isEmpty()) {
            map.put(Image.PN_TRACKING_OBJECT_ID, currentPage.getName() + StringUtils.SPACE + component.getCellName()
                    + StringUtils.SPACE + hashCodeValue);
        }
        try {
            resourceResolver.commit();
        } catch (PersistenceException e) {
            LOGGER.error("Error occured while saving the objectId for {}", currentPage.getName(), e);
        }
    }

    @Override
    public String getLinkTrackingCode() {
        linkTrackingCode = new StringBuilder();
        if (trackingEnabled && !compHashCode.isEmpty()) {
            linkTrackingCode = linkTrackingCode.append(OBJECT_ID).append(APOSTROPHE).append(compHashCode)
                    .append(APOSTROPHE).append(SEMICOLON);
        }
        return linkTrackingCode.toString();
    }

}
