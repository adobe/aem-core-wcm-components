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
package com.adobe.cq.wcm.core.components.models;

public interface TableOfContents extends Component {

    /**
     * Name of the optional resource property that stores the list type of table of contents.
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    String PN_LIST_TYPE = "listType";

    /**
     * Name of the optional resource property that stores the minimum title level to generate table of contents.
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    String PN_TITLE_START_LEVEL = "titleStartLevel";

    /**
     * Name of the optional resource property that stores the maximum title level to generate table of contents.
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    String PN_TITLE_STOP_LEVEL = "titleStopLevel";

    /**
     * Name of the configuration policy property that controls whether the author should be able to choose the
     * list type or not.
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    String PN_RESTRICT_LIST_TYPE = "restrictListType";

    /**
     * Name of the configuration policy property that controls whether the author should be able to choose the
     * minimum title level to report in the table of contents.
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    String PN_RESTRICT_TITLE_START_LEVEL = "restrictTitleStartLevel";

    /**
     * Name of the configuration policy property that controls whether the author should be able to choose the
     * maximum title level to report in the table of contents.
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    String PN_RESTRICT_TITLE_STOP_LEVEL = "restrictTitleStopLevel";

    /**
     * Name of the configuration policy property, if set, only titles with those class names or contained within
     * elements of the indicated class names will be considered
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    String PN_INCLUDE_CLASS_NAMES = "includeClassNames";

    /**
     * Name of the configuration policy property, if set, titles with those class names or contained within elements of
     * the indicated class names will be ignored.
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    String PN_IGNORE_CLASS_NAMES = "ignoreClassNames";

    /**
     *
     * @return
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    default String getListType() {
        return "unordered";
    }

    /**
     *
     * @return
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    default Integer getTitleStartLevel() {
        return 1;
    }

    /**
     *
     * @return
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    default Integer getTitleStopLevel() {
        return 6;
    }

    /**
     *
     * @return
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    default String[] getIncludeClassNames() {
        return null;
    }

    /**
     *
     * @return
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    default String[] getIgnoreClassNames() {
        return null;
    }
}
