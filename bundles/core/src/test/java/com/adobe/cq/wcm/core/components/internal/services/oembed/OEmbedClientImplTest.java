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
package com.adobe.cq.wcm.core.components.internal.services.oembed;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;

import static org.junit.Assert.*;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.osgi.framework.BundleContext;

import com.adobe.cq.wcm.core.components.models.oembed.OEmbedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OEmbedClientImplTest {

    @Test
    public void test() throws NoSuchFieldException, IOException {
        OEmbedClientImpl client = new OEmbedClientImpl();
        client.activate(
                new OEmbedClientImplConfiguration() {

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
                        return "http://test.com/oembed";
                    }

                    @Override
                    public String[] scheme() {
                        return new String[] {
                                ".*"
                        };
                    }
                },
                Mockito.mock(BundleContext.class));
        ObjectMapper mapper = Mockito.mock(ObjectMapper.class);
        Mockito.when(mapper.readValue(Mockito.any(URL.class), Mockito.any(Class.class))).thenReturn(new OEmbedResponseImpl());
        FieldSetter.setField(client, client.getClass().getDeclaredField("mapper"), mapper);
        String provider = client.getProvider("http://test.com/mytest");
        assertEquals("Test", provider);
        OEmbedResponse response = client.getResponse(provider, "http://test.com/mytest");
        assertNotNull(response);
    }

}
