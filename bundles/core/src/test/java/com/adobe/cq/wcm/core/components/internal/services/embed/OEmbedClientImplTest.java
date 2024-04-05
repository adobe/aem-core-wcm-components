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
import java.lang.reflect.Field;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.form.FormHandlerImpl;
import com.adobe.cq.wcm.core.components.services.form.FormHandler;
import io.wcm.testing.mock.aem.junit5.AemContext;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.services.embed.OEmbedClient;
import com.adobe.cq.wcm.core.components.services.embed.OEmbedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OEmbedClientImplTest {

    public final AemContext context = CoreComponentTestContext.newAemContext();
    private OEmbedClientImpl client;

    private OEmbedClientImpl setupForJson() throws IOException {

        context.registerService(HttpClientBuilderFactory.class, HttpClientBuilder::create);
        client = context.registerInjectActivateService(new OEmbedClientImpl());

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
        setField(client.getClass(), "mapper", client, mapper);

        return client;
    }

    @Test
    void testJSON() throws IOException {
        OEmbedClientImpl client = setupForJson();
        String provider = client.getProvider("http://test.com/json");
        assertEquals("Test JSON", provider);
        OEmbedResponse response = client.getResponse("http://test.com/json");
        assertNotNull(response);
        boolean unsafeContext = client.isUnsafeContext("https://test.com/json");
        assertFalse(unsafeContext);
    }

    @Test
    void testJsonReturnsNullOnMalformedUri() throws IOException {
        OEmbedClientImpl client = setupForJson();
        // trailing whitespace will cause an invalid uri
        OEmbedResponse response = client.getResponse("http://test.com/json ");
        assertNull(response);
    }

    private OEmbedClientImpl setupForXml() throws IOException, JAXBException {
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
        setField(client.getClass(), "jaxbContext", client, jaxbContext);
        Unmarshaller unmarshaller = mock(Unmarshaller.class);
        when(jaxbContext.createUnmarshaller()).thenReturn(unmarshaller);
        mockHttpClient(client);
        when(unmarshaller.unmarshal(any(Source.class))).thenReturn(new OEmbedXMLResponseImpl());

        return client;
    }

    @Test
    void testXML() throws JAXBException, IOException {
        OEmbedClientImpl client = setupForXml();
        String provider = client.getProvider("http://test.com/xml");
        assertEquals("Test XML", provider);
        assertNotNull(client.getResponse("http://test.com/xml"));
        boolean unsafeContext = client.isUnsafeContext("https://test.com/json");
        assertFalse(unsafeContext);
    }

    @Test
    void testXMLReturnsNullOnMalformedUri() throws JAXBException, IOException {
        OEmbedClientImpl client = setupForXml();
        // trailing whitespace will cause an invalid uri
        assertNull(client.getResponse("http://test.com/xml "));
    }

    protected void mockHttpClient(OEmbedClient client) throws IOException {
        HttpClientBuilderFactory mockBuilderFactory = mock(HttpClientBuilderFactory.class);
        setField(client.getClass(), "httpClientBuilderFactory", client, mockBuilderFactory);

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

    public static void setField(@NotNull final Class<?> clazz,
                                           @NotNull final String fieldName,
                                           @Nullable final Object target,
                                           @Nullable final Object value) {
        final Field f = FieldUtils.getField(clazz, fieldName, true);
        FieldUtils.removeFinalModifier(f);
        try {
            f.set(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
