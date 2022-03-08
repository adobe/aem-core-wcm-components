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
         * Unordered list type
         *
         * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
         */
        UNORDERED("unordered"),

        /**
         * Ordered list type
         *
         * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
         */
        ORDERED("ordered");

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
            return this == ORDERED ? "ol" : "ul";
        }

        /**
         * Given a {@link String} <code>value</code>, this method returns the enum's value that corresponds to the
         * provided string representation. If no representation is found, {@link #UNORDERED} will be returned.
         *
         * @param value the string representation for which an enum value should be returned
         * @return the corresponding enum value, if one was found, or {@link #UNORDERED}
         *
         * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
         */
        public static ListType fromString(String value) {
            for (ListType type : values()) {
                if (type.value.contentEquals(value)) {
                    return type;
                }
            }
            return UNORDERED;
        }
    }

    /**
     * Defines the possible heading levels for table of contents corresponding to 'h1' through 'h6'
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    enum HeadingLevel {

        H1(1),
        H2(2),
        H3(3),
        H4(4),
        H5(5),
        H6(6);

        private int value;

        HeadingLevel(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        /**
         * Returns the HTML tag name corresponding to this heading level
         * @return HTML tag name
         *
         * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
         */
        public String getTagName() {
            return "h" + value;
        }

        /**
         * Given an {@link Integer} <code>value</code>, this method returns the enum's value that corresponds to
         * the provided integer representation.
         * If no representation is found, {@link null} will be returned.
         *
         * @param value the integer representation for which an enum value should be returned
         * @return the corresponding enum value, if one was found, or {@link null}
         *
         * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
         */
        public static HeadingLevel fromInteger(Integer value) {
            for (HeadingLevel type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            return null;
        }

        /**
         * Given an {@link Integer} <code>value</code>, this method returns the enum's value that corresponds to
         * the provided integer representation.
         * If no representation is found, provided default value will be returned.
         *
         * @param value the integer representation for which an enum value should be returned
         * @param defaultLevel default heading level to return
         * @return the corresponding enum value, if one was found, or the provided default heading level
         *
         * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
         */
        public static HeadingLevel fromIntegerOrDefault(Integer value, HeadingLevel defaultLevel) {
            HeadingLevel level = fromInteger(value);
            return level != null ? level : defaultLevel;
        }

        /**
         * Given an {@link String} <code>strValue</code>, this method parses string to integer and returns the enum's value
         * that corresponds to the provided integer representation.
         * If no representation is found, {@link null} will be returned.
         *
         * @param strValue the string representation for which an enum value should be returned
         * @return the corresponding enum value, if one was found, or {@link null}
         *
         * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
         */
        public static HeadingLevel fromString(String strValue) {
            return fromInteger(Integer.parseInt(strValue));
        }

        /**
         * Given an {@link String} <code>strValue</code>, this method parses string to integer and returns the enum's value
         * that corresponds to the provided integer representation.
         * If no representation is found, provided default value will be returned.
         *
         * @param strValue the string representation for which an enum value should be returned
         * @param defaultLevel default heading level to return
         * @return the corresponding enum value, if one was found, or the provided default heading level
         *
         * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
         */
        public static HeadingLevel fromStringOrDefault(String strValue, HeadingLevel defaultLevel) {
            HeadingLevel level = fromString(strValue);
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
     * Returns the configured list type taking into account the configuration policy
     *
     * @return list type, default is 'unordered'
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    default ListType getListType() {
        return ListType.UNORDERED;
    }

    /**
     * Returns the start level taking into account the configuration policy
     *
     * @return start level, default is '1'
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    default HeadingLevel getStartLevel() {
        return HeadingLevel.H1;
    }

    /**
     * Returns the stop level taking into account the configuration policy
     *
     * @return stop level, default is '6'
     *
     * @since com.adobe.cq.wcm.core.components.models.tableofcontents 1.0
     */
    default HeadingLevel getStopLevel() {
        return HeadingLevel.H6;
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
