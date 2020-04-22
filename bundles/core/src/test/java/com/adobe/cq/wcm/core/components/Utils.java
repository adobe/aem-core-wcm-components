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
package com.adobe.cq.wcm.core.components;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import javax.json.Json;
import javax.json.JsonReader;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.mockito.Mockito;

import com.adobe.cq.wcm.core.components.internal.DataLayerConfig;
import com.adobe.cq.wcm.core.components.internal.jackson.DefaultMethodSkippingModuleProvider;
import com.adobe.cq.wcm.core.components.internal.jackson.PageModuleProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.wcm.testing.mock.aem.junit5.AemContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

/**
 * Testing utilities.
 */
public class Utils {

    /**
     * Provided a {@code model} object and an {@code expectedJsonResource} identifying a JSON file in the class path, this method will
     * test the JSON export of the model and compare it to the JSON object provided by the {@code expectedJsonResource}.
     *
     * @param model                the Sling Model
     * @param expectedJsonResource the class path resource providing the expected JSON object
     */
    public static void testJSONExport(Object model, String expectedJsonResource) {
        Writer writer = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        PageModuleProvider pageModuleProvider = new PageModuleProvider();
        mapper.registerModule(pageModuleProvider.getModule());
        DefaultMethodSkippingModuleProvider defaultMethodSkippingModuleProvider = new DefaultMethodSkippingModuleProvider();
        mapper.registerModule(defaultMethodSkippingModuleProvider.getModule());
        try {
            mapper.writer().writeValue(writer, model);
        } catch (IOException e) {
            fail(String.format("Unable to generate JSON export for model %s: %s", model.getClass().getName(), e.getMessage()));
        }
        JsonReader outputReader = Json.createReader(IOUtils.toInputStream(writer.toString()));
        InputStream is = Utils.class.getResourceAsStream
                (expectedJsonResource);
        if (is != null) {
            JsonReader expectedReader = Json.createReader(is);
            assertEquals(expectedReader.read(), outputReader.read());
        } else {
            fail("Unable to find test file " + expectedJsonResource + ".");
        }
        IOUtils.closeQuietly(is);
    }

    /**
     * Provided a test base folder ({@code testBase}) and a virtual resource path ({@code testResourcePath}), this method generates the
     * class path resource path for the JSON files that represent the expected exporter output for a component. The returned value is
     * generated using the following concatenation operation:
     *
     * <pre>
     *     testBase + '/exporter-' + fileName(testResourcePath) + '.json'
     * </pre>
     *
     * For example:
     * <pre>
     *     testBase = '/form/button'
     *     testResourcePath = '/content/buttons/button'
     *     output = '/form/button/exporter-button.json'
     * </pre>
     *
     * @param testBase         the test base folder (under the {@code src/test/resources} folder)
     * @param testResourcePath the test resource path in the virtual repository
     * @return the expected class path location of the JSON exporter file
     */
    public static String getTestExporterJSONPath(String testBase, String testResourcePath) {
        return testBase + "/exporter-" + FilenameUtils.getName(testResourcePath) + ".json";
    }

    /**
     * Sets the data layer context aware configuration of the AEM test context to enabled/disabled
     *
     * @param context The AEM test context
     * @param enabled {@code true} to enable the data layer, {@code false} to disable it
     */
    public static void enableDataLayer(AemContext context, boolean enabled) {
        ConfigurationBuilder builder = Mockito.mock(ConfigurationBuilder.class);
        DataLayerConfig dataLayerConfig = Mockito.mock(DataLayerConfig.class);
        when(dataLayerConfig.enabled()).thenReturn(enabled);
        when(builder.as(DataLayerConfig.class)).thenReturn(dataLayerConfig);
        context.registerAdapter(Resource.class, ConfigurationBuilder.class, builder);
    }

    /**
     * Sets the data layer context aware configuration of the AEM test context to enabled/disabled
     * for older test context (before io.wcm.testing.mock.aem.junit5.AemContext was introduced)
     *
     * @param context The non-junit5 AEM test context
     * @param enabled {@code true} to enable the data layer, {@code false} to disable it
     */
    public static void enableDataLayerForOldAemContext(io.wcm.testing.mock.aem.junit.AemContext context, boolean enabled) {
        ConfigurationBuilder builder = Mockito.mock(ConfigurationBuilder.class);
        DataLayerConfig dataLayerConfig = Mockito.mock(DataLayerConfig.class);
        when(dataLayerConfig.enabled()).thenReturn(enabled);
        when(builder.as(DataLayerConfig.class)).thenReturn(dataLayerConfig);
        context.registerAdapter(Resource.class, ConfigurationBuilder.class, builder);
    }

}
