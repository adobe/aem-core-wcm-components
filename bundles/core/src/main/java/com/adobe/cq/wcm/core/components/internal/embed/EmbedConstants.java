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
package com.adobe.cq.wcm.core.components.internal.embed;

import com.adobe.cq.wcm.core.components.internal.form.FormConstants;

public final class EmbedConstants {

    private EmbedConstants() {
	// Constructor private
    }

    public static final String ALLOWED_OPTIONS_RESOURCE_TYPE = FormConstants.RT_CORE_FORM_CONTAINER_DATASOURCE_V1
	    + "/embedoptions";
    public static final String ALL_OPTIONS_RESOURCE_TYPE = FormConstants.RT_CORE_FORM_CONTAINER_DATASOURCE_V1
	    + "/allembedoptions";
    public final static String EMBED_SETTINGS_RESOURCE_TYPE = FormConstants.RT_CORE_FORM_CONTAINER_DATASOURCE_V1
	    + "/embedsettings";
    public static final String PN_EMBED_OPTIONS = "allowedEmbedOptions";
    public static final String EMBEDDABLE_RESOURCE_TYPE = "core/wcm/components/embed/embeddable";

}
