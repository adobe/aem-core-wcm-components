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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;

import com.adobe.cq.wcm.core.components.internal.services.embed.OEmbedClientImpl;
import com.adobe.cq.wcm.core.components.internal.services.embed.OEmbedClientImplConfigurationFactory;
import com.adobe.cq.wcm.core.components.internal.services.embed.OEmbedResponseImpl;
import com.adobe.cq.wcm.core.components.services.embed.OEmbedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

class OEmbedClientImplTest {

    @Test
    void test() throws NoSuchFieldException, IOException {
        OEmbedClientImpl client = new OEmbedClientImpl();
        OEmbedClientImplConfigurationFactory configurationFactory = new OEmbedClientImplConfigurationFactory();
        configurationFactory.configure(
                new OEmbedClientImplConfigurationFactory.Config() {

                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return null;
                    }

                    @Override
                    public String provider() {
                        return "Test";
                    }

                    @Override
                    public String format() {
                        return OEmbedResponse.Format.JSON.getValue();
                    }

                    @Override
                    public String endpoint() {
                        return "http://test.com/embed";
                    }

                    @Override
                    public String[] scheme() {
                        return new String[]{
                                ".*"
                        };
                    }
                });
        client.bindOEmbedClientImplConfigurationFactory(configurationFactory, new HashMap<>());
        ObjectMapper mapper = Mockito.mock(ObjectMapper.class);
        Mockito.when(mapper.readValue(Mockito.any(URL.class), Mockito.any(Class.class))).thenReturn(new OEmbedResponseImpl());
        FieldSetter.setField(client, client.getClass().getDeclaredField("mapper"), mapper);
        String provider = client.getProvider("http://test.com/mytest");
        assertEquals("Test", provider);
        OEmbedResponse response = client.getResponse("http://test.com/mytest");
        assertNotNull(response);
    }
}
