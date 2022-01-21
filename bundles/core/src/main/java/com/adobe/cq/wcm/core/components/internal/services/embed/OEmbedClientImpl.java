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
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.adobe.cq.wcm.core.components.services.embed.OEmbedClient;
import com.adobe.cq.wcm.core.components.services.embed.OEmbedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component(
    service = OEmbedClient.class
)
public class OEmbedClientImpl implements OEmbedClient {

    @Reference
    private HttpClientBuilderFactory httpClientBuilderFactory;

    /**
     * Socket timeout.
     */
    private int soTimeout = 60000;

    /**
     * Connection timeout.
     */
    private int connectionTimeout = 5000;

    private static final Logger LOGGER = LoggerFactory.getLogger(OEmbedClientImpl.class);

    private Map<String, OEmbedClientImplConfigurationFactory.Config> configs = new HashMap<>();

    private ObjectMapper mapper = new ObjectMapper();
    private static JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(OEmbedXMLResponseImpl.class);
        } catch (JAXBException e) {
            LOGGER.error("Failed to retrieve JAXBContext", e);
        }
    }

    @Override
    public String getProvider(String url) {
        if (StringUtils.isNotEmpty(url)) {
            OEmbedClientImplConfigurationFactory.Config config = getConfiguration(url);
            if (config != null) {
                return config.provider();
            }
        }
        return null;
    }

    @Override
    public OEmbedResponse getResponse(String url) {
        OEmbedClientImplConfigurationFactory.Config config = getConfiguration(url);
        if (config == null) {
            return null;
        }
        if (OEmbedResponse.Format.JSON == OEmbedResponse.Format.fromString(config.format())) {
            try {
                String jsonURL = buildURL(config.endpoint(), url, OEmbedResponse.Format.JSON.getValue(), null, null);
                return mapper.readValue(getData(jsonURL), OEmbedJSONResponseImpl.class);
            } catch (IOException ioex) {
                LOGGER.error("Failed to read JSON response", ioex);
            }
        } else if (jaxbContext != null && OEmbedResponse.Format.XML == OEmbedResponse.Format.fromString(config.format())) {
            try {
                String xmlURL = buildURL(config.endpoint(), url, OEmbedResponse.Format.XML.getValue(), null, null);
                try (InputStream xmlStream = getData(xmlURL)) {
                    //Disable XXE
                    SAXParserFactory spf = SAXParserFactory.newInstance();
                    spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
                    spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                    spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

                    //Do unmarshall operation
                    Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(xmlStream));
                    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                    return (OEmbedResponse) jaxbUnmarshaller.unmarshal(xmlSource);
                }
            } catch (SAXException | ParserConfigurationException | JAXBException | IOException e) {
                LOGGER.error("Failed to read JSON response", e);
            }
        }
        return null;
    }

    @Override
    public boolean isUnsafeContext(String url) {
        if (StringUtils.isNotEmpty(url)) {
            OEmbedClientImplConfigurationFactory.Config config = getConfiguration(url);
            if (config != null) {
                return config.unsafeContext();
            }
        }
        return false;
    }

    protected OEmbedClientImplConfigurationFactory.Config getConfiguration(String url) {
        if (!StringUtils.isEmpty(url)) {
           for (OEmbedClientImplConfigurationFactory.Config config : configs.values()) {
               for (String scheme : config.scheme()) {
                   if (Pattern.matches(scheme, url)) {
                       return config;
                   }
               }
            }
        }
        return null;
    }

    protected InputStream getData(String url) throws IOException {
        RequestConfig rc = RequestConfig.custom().setConnectTimeout(connectionTimeout).setSocketTimeout(soTimeout)
            .build();
        HttpClient httpClient;
        if (httpClientBuilderFactory != null
                && httpClientBuilderFactory.newBuilder() != null) {
            httpClient = httpClientBuilderFactory.newBuilder()
                    .setDefaultRequestConfig(rc)
                    .build();
        } else {
            httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(rc)
                    .build();
        }
        HttpResponse response = httpClient.execute(new HttpGet(url));
        return response.getEntity().getContent();
    }

    protected String buildURL(String endpoint, String url, String format, Integer maxWidth, Integer maxHeight) throws MalformedURLException {
        StringBuilder sb = new StringBuilder(endpoint);
        String separator = endpoint.contains("?") ? "&" : "?";
        addURLParameter(sb, separator, "url", url);
        separator = "&";
        addURLParameter(sb, separator, "format", format);
        addURLParameter(sb, separator, "maxwidth", maxWidth);
        addURLParameter(sb, separator, "maxheight", maxHeight);
        return sb.toString();
    }

    protected void addURLParameter(StringBuilder sb, String separator, String name, Object value) {
        if (sb != null && StringUtils.isNotEmpty(separator) && StringUtils.isNotEmpty(name) && value != null) {
            sb.append(separator).append(name).append("=").append(value);
        }
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, updated = "bindOEmbedClientImplConfigurationFactory")
    protected void bindOEmbedClientImplConfigurationFactory(final OEmbedClientImplConfigurationFactory configurationFactory, Map<String, ?> properties) {
        String key = (String) properties.get(Constants.SERVICE_PID);
        configs.put(key, configurationFactory.getConfig());
    }

    protected void unbindOEmbedClientImplConfigurationFactory(final OEmbedClientImplConfigurationFactory configurationFactory, Map<String, ?> properties) {
        String key = (String) properties.get(Constants.SERVICE_PID);
        configs.remove(key, configurationFactory.getConfig());
    }
}
