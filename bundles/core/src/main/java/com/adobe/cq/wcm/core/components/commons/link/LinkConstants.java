/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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
package com.adobe.cq.wcm.core.components.commons.link;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Constants for link handling.
 */
@ProviderType
public final class LinkConstants {

    /**
     * Default property name for storing link URL.
     * All new model implementation should use this name, some of the existing models use other names to store the link URL.
     */
    public static final String PN_LINK_URL = "linkURL";

    /**
     * Property name for storing link target.
     */
    public static final String PN_LINK_TARGET = "linkTarget";

    private LinkConstants() {
        // constants only
    }

}
