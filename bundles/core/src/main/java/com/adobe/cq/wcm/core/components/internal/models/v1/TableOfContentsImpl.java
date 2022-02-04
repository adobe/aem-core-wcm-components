/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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

import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.TableOfContents;
import com.day.cq.wcm.api.designer.Style;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.Nullable;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = {
        TableOfContents.class
    },
    resourceType = TableOfContentsImpl.RESOURCE_TYPE
)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class TableOfContentsImpl implements TableOfContents {

    /**
     * The resource type of the component associated with this Sling model.
     */
    public static final String RESOURCE_TYPE = "core/wcm/components/tableofcontents/v1/tableofcontents";

    @Self(injectionStrategy = InjectionStrategy.REQUIRED)
    private SlingHttpServletRequest slingHttpServletRequest;

    @ScriptVariable
    protected Style currentStyle;

    @ValueMapValue(name = TableOfContents.PN_LIST_TYPE, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String listType;

    @ValueMapValue(name = TableOfContents.PN_TITLE_START_LEVEL, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private Integer titleStartLevel;

    @ValueMapValue(name = TableOfContents.PN_TITLE_STOP_LEVEL, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private Integer titleStopLevel;

    private String restrictListType;
    private String restrictTitleStartLevel;
    private String restrictTitleStopLevel;
    private String[] includeClassNames;
    private String[] ignoreClassNames;

    @PostConstruct
    private void initModel() {
        restrictListType = currentStyle.get(PN_RESTRICT_LIST_TYPE, String.class);
        restrictTitleStartLevel = currentStyle.get(PN_RESTRICT_TITLE_START_LEVEL, String.class);
        restrictTitleStopLevel = currentStyle.get(PN_RESTRICT_TITLE_STOP_LEVEL, String.class);
        includeClassNames = currentStyle.get(PN_INCLUDE_CLASS_NAMES, String[].class);
        ignoreClassNames = currentStyle.get(PN_IGNORE_CLASS_NAMES, String[].class);
        slingHttpServletRequest.setAttribute("contains-table-of-contents", true);
    }

    @Override
    public String getListType() {
        return (restrictListType == null || "norestriction".contentEquals(restrictListType))
            ? listType != null ? listType : "unordered"
            : restrictListType;
    }

    @Override
    public Integer getTitleStartLevel() {
        return (restrictTitleStartLevel == null || "norestriction".contentEquals(restrictTitleStartLevel))
            ? titleStartLevel != null ? titleStartLevel : 1
            : Integer.parseInt(restrictTitleStartLevel);
    }

    @Override
    public Integer getTitleStopLevel() {
        return (restrictTitleStopLevel == null || "norestriction".contentEquals(restrictTitleStopLevel))
            ? titleStopLevel != null ? titleStopLevel : 6
            : Integer.parseInt(restrictTitleStopLevel);
    }

    @Override
    public String[] getIncludeClassNames() {
        return includeClassNames != null
            ? Arrays.copyOf(includeClassNames, includeClassNames.length)
            : null;
    }

    @Override
    public String[] getIgnoreClassNames() {
        return ignoreClassNames != null
            ? Arrays.copyOf(ignoreClassNames, ignoreClassNames.length)
            : null;
    }
}
