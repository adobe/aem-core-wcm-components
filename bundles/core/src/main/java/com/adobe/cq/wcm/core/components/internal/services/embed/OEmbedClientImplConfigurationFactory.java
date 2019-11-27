/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
package com.adobe.cq.wcm.core.components.internal.services.embed;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

@Component(
    service = OEmbedClientImplConfigurationFactory.class
)
@Designate(
    ocd = OEmbedClientImplConfigurationFactory.Config.class,
    factory = true
)
public class OEmbedClientImplConfigurationFactory {

    private Config config;

    @ObjectClassDefinition(
            name = "Core Components oEmbed Client",
            description = "Configuration for defining oEmbed endpoints."
    )
    public @interface Config {
        @AttributeDefinition(
                name = "Provider Name",
                description = "Name of the oEmbed provider."
        )
        String provider();

        @AttributeDefinition(
                name = "Format",
                description = "Defines the format for the oEmbed response",
                options = {
                        @Option(
                                label = "JSON",
                                value = "json"),
                        @Option(
                                label = "XML",
                                value = "xml"
                        )
                }
        )
        String format();

        @AttributeDefinition(
                name = "API Endpoint",
                description = "Defines the URL where consumers may request representation for this provider."
        )
        String endpoint();

        @AttributeDefinition(
                name = "URL Scheme",
                description = "Describes which URLs provided by the service may have an embedded representation."
        )
        String[] scheme();

        @AttributeDefinition(
            name = "Unsafe Context",
            description = "Describes whether the provider response HTML is allowed to be displayed in an unsafe context."
        )
        boolean unsafeContext() default false;
    }

    @Activate
    @Modified
    void configure(Config config) {
        this.config = config;
    }

    public Config getConfig() {
        return config;
    }
}
