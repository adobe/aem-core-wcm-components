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
package com.adobe.cq.wcm.core.components.internal;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

/**
 * Context Aware Configuration Class of the Data Layer
 */
@Configuration(label="Data Layer", description="Configure support for Adobe Client Data Layer")
public @interface DataLayerConfig {

    /**
     *
     * @return {@code true} if the data layer is enabled, {@code false} otherwise. It defaults to {@code false}.
     */
    @Property(label="Data Layer enabled")
    boolean enabled() default false;

    /**
     *
     * @return {@code true} if the data layer client library is not included through the Core Page component,
     * {@code false} otherwise. It defaults to {@code false} (client library included).
     */
    @Property(label="Data Layer client library not included")
    boolean skipClientlibInclude() default false;

}
