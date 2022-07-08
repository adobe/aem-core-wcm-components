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
     * Defines the possible list types for table of contents
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    enum ListType {
        /**
         * Bulleted list type
         *
         * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
         */
        bulleted("bulleted"),

        /**
         * Numbered list type
         *
         * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
         */
        numbered("numbered");

        private String value;

        ListType(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        /**
         * Returns the HTML tag name for this enum type of list
         * @return HTML tag name
         *
         * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
         */
        public String getTagName() {
            return this == numbered ? "ol" : "ul";
        }

        /**
         * Given a {@link String} <code>value</code>, this method returns the enum's value that corresponds to the
         * provided string representation. If no representation is found, {@link #bulleted} will be returned.
         *
         * @param value the string representation for which an enum value should be returned
         * @return the corresponding enum value, if one was found, or {@link #bulleted}
         *
         * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
         */
        public static ListType fromString(String value) {
            for (ListType type : values()) {
                if (type.value.contentEquals(value)) {
                    return type;
                }
            }
            return bulleted;
        }
    }

    /**
     * Defines the possible heading levels for table of contents corresponding to 'h1' till 'h6'
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    enum HeadingLevel {

        h1("h1"),
        h2("h2"),
        h3("h3"),
        h4("h4"),
        h5("h5"),
        h6("h6");

        private String value;

        HeadingLevel(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public Integer getIntValue() {
            return value.charAt(1) - '0';
        }

        /**
         * Returns the HTML tag name corresponding to this heading level
         * @return HTML tag name
         *
         * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
         */
        public String getTagName() {
            return value;
        }

        /**
         * Given an {@link String} <code>value</code>, this method returns the enum's value that corresponds to the
         * provided string representation.
         * If no representation is found, null will be returned.
         *
         * @param value the string representation for which an enum value should be returned
         * @return the corresponding enum value, if one was found, or null
         *
         * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
         */
        public static HeadingLevel fromString(String value) {
            for (HeadingLevel type : values()) {
                if (type.value.contentEquals(value)) {
                    return type;
                }
            }
            return null;
        }

        /**
         * Given an {@link String} <code>value</code>, this method returns the enum's value that corresponds to the
         * provided string representation.
         * If no representation is found, provided default value will be returned.
         *
         * @param value the string representation for which an enum value should be returned
         * @param defaultLevel default heading level to return
         * @return the corresponding enum value, if one was found, or the provided default heading level
         *
         * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
         */
        public static HeadingLevel fromStringOrDefault(String value, HeadingLevel defaultLevel) {
            HeadingLevel level = fromString(value);
            return level != null ? level : defaultLevel;
        }
    }

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
    String PN_START_LEVEL = "startLevel";

    /**
     * Name of the optional resource property that stores the maximum title level to generate table of contents.
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    String PN_STOP_LEVEL = "stopLevel";

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
    String PN_RESTRICT_START_LEVEL = "restrictStartLevel";

    /**
     * Name of the configuration policy property that controls whether the author should be able to choose the
     * maximum title level to report in the table of contents.
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    String PN_RESTRICT_STOP_LEVEL = "restrictStopLevel";

    /**
     * Name of the configuration policy property, if set, only titles with those class names or contained within
     * elements of the indicated class names will be considered
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    String PN_INCLUDE_CLASSES = "includeClasses";

    /**
     * Name of the configuration policy property, if set, titles with those class names or contained within elements of
     * the indicated class names will be ignored.
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    String PN_IGNORE_CLASSES = "ignoreClasses";

    /**
     * HTML class name added on the final TOC render.
     * It contains the numbered/bulleted nested list of contents of the page.
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    String TOC_CONTENT_CLASS = "cmp-toc__content";

    /**
     * HTML class name added on the TOC placeholder rendered by the component's HTL.
     * It contains all the TOC configuration properties (from its resource and configuration policy properties) as data
     * attributes.
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    String TOC_PLACEHOLDER_CLASS = "cmp-toc__placeholder";

    /**
     * HTML class name added on the TOC template placeholder which is only viewed in EDIT mode on page editor.
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    String TOC_TEMPLATE_PLACEHOLDER_CLASS = "cmp-toc__template-placeholder";

    /**
     * HTML data attribute added on the TOC placeholder containing the configured list type of the TOC .
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    String TOC_DATA_ATTR_LIST_TYPE = "data-cmp-toc-list-type";

    /**
     * HTML data attribute added on the TOC placeholder containing the configured start level of the TOC.
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    String TOC_DATA_ATTR_START_LEVEL = "data-cmp-toc-start-level";

    /**
     * HTML data attribute added on the TOC placeholder containing the configured stop level of the TOC.
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    String TOC_DATA_ATTR_STOP_LEVEL = "data-cmp-toc-stop-level";

    /**
     * HTML data attribute added on the TOC placeholder containing the configured include classes of the TOC as a comma
     * separated list.
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    String TOC_DATA_ATTR_INCLUDE_CLASSES = "data-cmp-toc-include-classes";

    /**
     * HTML data attribute added on the TOC placeholder containing the configured ignore classes of the TOC as a comma
     * separated list.
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    String TOC_DATA_ATTR_IGNORE_CLASSES = "data-cmp-toc-ignore-classes";

    /**
     * Returns the configured list type taking into account the configuration policy
     *
     * @return list type, default is 'unordered'
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    default ListType getListType() {
        return ListType.bulleted;
    }

    /**
     * Returns the start level taking into account the configuration policy
     *
     * @return start level, default is '1'
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    default HeadingLevel getStartLevel() {
        return HeadingLevel.h1;
    }

    /**
     * Returns the stop level taking into account the configuration policy
     *
     * @return stop level, default is '6'
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    default HeadingLevel getStopLevel() {
        return HeadingLevel.h6;
    }

    /**
     * Returns an array of all include class names set in configuration policy
     *
     * @return array of include class names, 'null' if not set
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    default String[] getIncludeClasses() {
        return null;
    }

    /**
     * Returns an array of all ignore class names set in configuration policy
     *
     * @return array of ignore class names, 'null' if not set
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    default String[] getIgnoreClasses() {
        return null;
    }
}
