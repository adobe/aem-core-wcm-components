/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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
package com.adobe.cq.wcm.core.extensions.amp.internal;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

@Configuration(label = "AMP Clientlib - Configuration", description = "Client libraries configuration from AMP mode")
public @interface ClientlibCaConfig {

    String DEFAULT_CUSTOM_HEADLIB_INCLUDE_SCRIPT = "customheadlibs.amp.html";
    String DEFAULT_CLIENTLIB_REGEX = ".*\\.amp";

    @Property(label = "Custom headlib include script", description = "")
    String customHeadlibIncludeScript() default DEFAULT_CUSTOM_HEADLIB_INCLUDE_SCRIPT;

    @Property(label = "Clientlib REGEX", description = "")
    String clientlibRegex() default DEFAULT_CLIENTLIB_REGEX;
}
