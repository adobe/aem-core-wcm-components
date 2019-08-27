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
package com.adobe.cq.wcm.core.components.internal.services.embed;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

@ObjectClassDefinition(
        name = "Core Components oEmbed Client",
        description = "Configuration for defining oEmbed endpoints."
)
public @interface OEmbedClientImplConfiguration {
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
}
