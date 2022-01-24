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
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.reflection.FieldSetter;

import com.adobe.cq.wcm.core.components.services.embed.OEmbedClient;
import com.adobe.cq.wcm.core.components.services.embed.OEmbedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OEmbedClientImplTest {

    @Test
    void testJSON() throws NoSuchFieldException, IOException {
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
                        return "Test JSON";
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
                                "http://test\\.com/json.*"
                        };
                    }

                    @Override
                    public boolean unsafeContext() {
                        return false;
                    }
                });

        client.bindOEmbedClientImplConfigurationFactory(configurationFactory, new HashMap<>());
        ObjectMapper mapper = mock(ObjectMapper.class);
        mockHttpClient(client);
        when(mapper.readValue(any(InputStream.class), any(Class.class))).thenReturn(new OEmbedJSONResponseImpl());
        FieldSetter.setField(client, client.getClass().getDeclaredField("mapper"), mapper);
        String provider = client.getProvider("http://test.com/json");
        assertEquals("Test JSON", provider);
        OEmbedResponse response = client.getResponse("http://test.com/json");
        assertNotNull(response);
        boolean unsafeContext = client.isUnsafeContext("https://test.com/json");
        assertFalse(unsafeContext);
    }

    @Test
    void testXML() throws NoSuchFieldException, JAXBException, IOException {
        OEmbedClientImpl client = new OEmbedClientImpl();
        OEmbedClientImplConfigurationFactory configurationFactory = new OEmbedClientImplConfigurationFactory();
        configurationFactory.configure(new OEmbedClientImplConfigurationFactory.Config() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String provider() {
                return "Test XML";
            }

            @Override
            public String format() {
                return OEmbedResponse.Format.XML.getValue();
            }

            @Override
            public String endpoint() {
                return "http://test.com/embed";
            }

            @Override
            public String[] scheme() {
                return new String[]{
                        "http://test\\.com/xml.*"
                };
            }

            @Override
            public boolean unsafeContext() {
                return false;
            }
        });

        client.bindOEmbedClientImplConfigurationFactory(configurationFactory, new HashMap<>());
        JAXBContext jaxbContext = mock(JAXBContext.class);
        FieldSetter.setField(client, client.getClass().getDeclaredField("jaxbContext"), jaxbContext);
        Unmarshaller unmarshaller = mock(Unmarshaller.class);
        when(jaxbContext.createUnmarshaller()).thenReturn(unmarshaller);
        mockHttpClient(client);
        when(unmarshaller.unmarshal(any(Source.class))).thenReturn(new OEmbedXMLResponseImpl());
        String provider = client.getProvider("http://test.com/xml");
        assertEquals("Test XML", provider);
        assertNotNull(client.getResponse("http://test.com/xml"));
        boolean unsafeContext = client.isUnsafeContext("https://test.com/json");
        assertFalse(unsafeContext);
    }

    protected void mockHttpClient(OEmbedClient client) throws NoSuchFieldException, IOException {
        HttpClientBuilderFactory mockBuilderFactory = mock(HttpClientBuilderFactory.class);
        FieldSetter.setField(client, client.getClass().getDeclaredField("httpClientBuilderFactory"), mockBuilderFactory);

        HttpClientBuilder mockBuilder = mock(HttpClientBuilder.class);
        when(mockBuilderFactory.newBuilder()).thenReturn(mockBuilder);

        CloseableHttpClient mockClient = mock(CloseableHttpClient.class);
        when(mockBuilder.setDefaultRequestConfig(any(RequestConfig.class))).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(mockClient);

        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        when(mockClient.execute(any(HttpUriRequest.class))).thenReturn(mockResponse);

        HttpEntity mockEntity = mock(HttpEntity.class);
        when(mockResponse.getEntity()).thenReturn(mockEntity);

        when(mockEntity.getContent()).thenReturn(mock(InputStream.class));
    }
}
