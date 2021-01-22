/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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
package com.adobe.cq.wcm.core.components.internal.form;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Some constants for the form components.
 */
public final class FormConstants {

    private FormConstants() {

    }

    /** The prefixes for all core form related resource types */
    public final static String RT_CORE_FORM_PREFIX = "core/wcm/components/form/";

    /** The resource type for core form container v1 */
    public final static String RT_CORE_FORM_CONTAINER_V1 = RT_CORE_FORM_PREFIX + "container/v1/container";
    /** The resource type for core form container v2 */
    public final static String RT_CORE_FORM_CONTAINER_V2 = RT_CORE_FORM_PREFIX + "container/v2/container";

    /* The resource type prefix for the core form container related datasources */
    public final static String RT_CORE_FORM_CONTAINER_DATASOURCE_V1 = RT_CORE_FORM_PREFIX + "container/v1/datasource";

    /** Array of all resource types for the core form container, including versions */
    public final static Set<String> RT_ALL_CORE_FORM_CONTAINER = Collections.unmodifiableSet(new HashSet<String>() {{
        add(RT_CORE_FORM_CONTAINER_V1);
        add(RT_CORE_FORM_CONTAINER_V2);
    }});

    /** Node-name of the touch based dialog for form actions */
    public static final String NN_DIALOG = "cq:dialog";

    /** The resource type for form button v1 */
    public final static String RT_CORE_FORM_BUTTON_V1 = RT_CORE_FORM_PREFIX + "button/v1/button";
    /** The resource type for form button v2 */
    public final static String RT_CORE_FORM_BUTTON_V2 = RT_CORE_FORM_PREFIX + "button/v2/button";

    /** The resource type for hidden fields v1 */
    public static final String RT_CORE_FORM_HIDDEN_V1 = RT_CORE_FORM_PREFIX + "hidden/v1/hidden";
    /** The resource type for hidden fields v2 */
    public static final String RT_CORE_FORM_HIDDEN_V2 = RT_CORE_FORM_PREFIX + "hidden/v2/hidden";

    /** The resource type for options fields v1 */
    public static final String RT_CORE_FORM_OPTIONS_V1 = RT_CORE_FORM_PREFIX + "options/v1/options";
    /** The resource type for options fields v2 */
    public static final String RT_CORE_FORM_OPTIONS_V2 = RT_CORE_FORM_PREFIX + "options/v2/options";

    /** The resource type for text fields v1 */
    public static final String RT_CORE_FORM_TEXT_V1 = RT_CORE_FORM_PREFIX + "text/v1/text";
    /** The resource type for text fields v2 */
    public static final String RT_CORE_FORM_TEXT_V2 = RT_CORE_FORM_PREFIX + "text/v2/text";
}
