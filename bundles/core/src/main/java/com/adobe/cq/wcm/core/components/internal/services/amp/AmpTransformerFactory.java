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
package com.adobe.cq.wcm.core.components.internal.services.amp;

import org.apache.sling.rewriter.Transformer;
import org.apache.sling.rewriter.TransformerFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Instantiates the transformers needed to manipulate and add AMP specific page markup.
 */
@Component(
    property = { "pipeline.type=amp-transformer" },
    service = { TransformerFactory.class },
    configurationPolicy = ConfigurationPolicy.REQUIRE
)
@Designate(
    ocd = AmpTransformerFactory.Cfg.class
)
public class AmpTransformerFactory implements TransformerFactory {

    private AmpTransformerFactory.Cfg cfg;

    /**
     * Reads the service's configuration when the service is started.
     * @param cfg The service's configuration.
     */
    @Activate
    @Modified
    protected void activate(AmpTransformerFactory.Cfg cfg) {
        this.cfg = cfg;
    }

    @Override
    public Transformer createTransformer() {
        return new AmpTransformer(cfg);
    }

    @ObjectClassDefinition(name = "AMP Transformer Factory")
    public @interface Cfg {

        /**
         * The name used for AMP js head library files.
         */
        @AttributeDefinition(
            name = "Headlib Name",
            description = "The name used for AMP js head library files.")
        String getHeadlibName();

        /**
         * Regex defining valid resource type paths while aggregating head libraries.
         */
        @AttributeDefinition(
            name = "Headlib Resource Type Regex",
            description = "Regex defining valid resource type paths while aggregating head libraries.")
        String getHeadlibResourceTypeRegex();
    }

}
