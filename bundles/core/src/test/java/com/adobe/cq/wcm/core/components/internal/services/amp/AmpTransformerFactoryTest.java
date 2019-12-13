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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.testing.resourceresolver.MockResourceResolverFactory;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.apache.sling.api.resource.ResourceResolverFactory;

@ExtendWith(AemContextExtension.class)
public class AmpTransformerFactoryTest {

    private AemContext context = CoreComponentTestContext.newAemContext();
    private static final String TEST_REGEX = "(?![A-Za-z]{2}:).*";
    private static final String TEST_HEADLIBS = "ampheadlibs.html";
    private AmpTransformerFactory ampTransformerFactory;

    @Test
    public void testValidCfg() {
        context.registerService(ResourceResolverFactory.class, new MockResourceResolverFactory());

        Map<String, Object> configs = getAmpTransformerFactoryConfig();
        ampTransformerFactory = context.registerInjectActivateService(new AmpTransformerFactory(), configs);

        AmpTransformerFactory.Cfg ampCfg = ampTransformerFactory.getCfg();

        assertNotNull(ampTransformerFactory.createTransformer());

        assertNotNull(ampCfg);
        assertEquals(TEST_HEADLIBS, ampCfg.getHeadlibName());
        assertEquals(TEST_REGEX, ampCfg.getHeadlibResourceTypeRegex());

    }

    @Test
    public void testNullCfg() {
        context.registerService(ResourceResolverFactory.class, new MockResourceResolverFactory());

        ampTransformerFactory = context.registerInjectActivateService(new AmpTransformerFactory());

        AmpTransformerFactory.Cfg ampCfg = ampTransformerFactory.getCfg();

        assertNotNull(ampTransformerFactory.createTransformer());
        assertNotNull(ampCfg);
        assertNull(ampCfg.getHeadlibName());
        assertNull(ampCfg.getHeadlibResourceTypeRegex());

    }

    @Test
    public void testResolver() {
        context.registerService(ResourceResolverFactory.class, new MockResourceResolverFactory());

        ampTransformerFactory = context.registerInjectActivateService(new AmpTransformerFactory());

        assertNotNull(ampTransformerFactory.getResolverFactory());
    }


    public static Map<String, Object> getAmpTransformerFactoryConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("getHeadlibName", TEST_HEADLIBS);
        config.put("getHeadlibResourceTypeRegex", TEST_REGEX);

        return config;
    }

}
