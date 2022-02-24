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

    public static final String DEFAULT_LIST_TYPE = "unordered";
    public static final Integer DEFAULT_START_LEVEL = 1;
    public static final Integer DEFAULT_STOP_LEVEL = 6;

    public static final String NO_RESTRICTION = "norestriction";

    @Self(injectionStrategy = InjectionStrategy.REQUIRED)
    private SlingHttpServletRequest slingHttpServletRequest;

    @ScriptVariable
    protected Style currentStyle;

    @ValueMapValue(name = TableOfContents.PN_LIST_TYPE, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String listType;

    @ValueMapValue(name = TableOfContents.PN_START_LEVEL, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private Integer startLevel;

    @ValueMapValue(name = TableOfContents.PN_STOP_LEVEL, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private Integer stopLevel;

    private String restrictListType;
    private String restrictStartLevel;
    private String restrictStopLevel;
    private String[] includeClasses;
    private String[] ignoreClasses;

    @PostConstruct
    private void initModel() {
        restrictListType = currentStyle.get(PN_RESTRICT_LIST_TYPE, String.class);
        restrictStartLevel = currentStyle.get(PN_RESTRICT_START_LEVEL, String.class);
        restrictStopLevel = currentStyle.get(PN_RESTRICT_STOP_LEVEL, String.class);
        includeClasses = currentStyle.get(PN_INCLUDE_CLASSES, String[].class);
        ignoreClasses = currentStyle.get(PN_IGNORE_CLASSES, String[].class);
        slingHttpServletRequest.setAttribute("contains-table-of-contents", true);
    }

    @Override
    public String getListType() {
        return (restrictListType == null || NO_RESTRICTION.contentEquals(restrictListType))
            ? listType != null ? listType : DEFAULT_LIST_TYPE
            : restrictListType;
    }

    @Override
    public Integer getStartLevel() {
        return (restrictStartLevel == null || NO_RESTRICTION.contentEquals(restrictStartLevel))
            ? startLevel != null ? startLevel : DEFAULT_START_LEVEL
            : Integer.parseInt(restrictStartLevel);
    }

    @Override
    public Integer getStopLevel() {
        return (restrictStopLevel == null || NO_RESTRICTION.contentEquals(restrictStopLevel))
            ? stopLevel != null ? stopLevel : DEFAULT_STOP_LEVEL
            : Integer.parseInt(restrictStopLevel);
    }

    @Override
    public String[] getIncludeClasses() {
        return includeClasses != null
            ? Arrays.copyOf(includeClasses, includeClasses.length)
            : null;
    }

    @Override
    public String[] getIgnoreClasses() {
        return ignoreClasses != null
            ? Arrays.copyOf(ignoreClasses, ignoreClasses.length)
            : null;
    }
}
