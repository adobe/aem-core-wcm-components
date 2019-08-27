/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019
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
package com.adobe.cq.wcm.core.components.internal.services.embed.oembed;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.models.embed.oembed.OEmbedClient;
import com.adobe.cq.wcm.core.components.models.embed.oembed.OEmbedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component(
        service = OEmbedClient.class
)
public class OEmbedClientImpl implements OEmbedClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(OEmbedClientImpl.class);

    private List<OEmbedClientImplConfigurationFactory.Config> configs = new ArrayList<>();

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public String getProvider(String url) {
        if (StringUtils.isNotEmpty(url)) {
            for (OEmbedClientImplConfigurationFactory.Config config : configs) {
                for (String scheme : config.scheme()) {
                    if (Pattern.matches(scheme, url)) {
                        return config.provider();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public OEmbedResponse getResponse(String provider, String url) {
        if (StringUtils.isEmpty(provider) || StringUtils.isEmpty(url)) {
            return null;
        }
        OEmbedClientImplConfigurationFactory.Config config = getConfiguration(provider);
        if (config == null) {
            return null;
        }
        if (OEmbedResponse.Format.JSON == OEmbedResponse.Format.fromString(config.format())) {
            try {
                URL jsonURL = buildURL(config.endpoint(), url, OEmbedResponse.Format.JSON.getValue(), null, null);
                return mapper.readValue(jsonURL, OEmbedResponseImpl.class);
            } catch (IOException ioex) {
                LOGGER.error(ioex.getMessage(), ioex);
            }
        }
        return null;
    }

    protected OEmbedClientImplConfigurationFactory.Config getConfiguration(String provider) {
        if (!StringUtils.isEmpty(provider)) {
           for (OEmbedClientImplConfigurationFactory.Config config : configs) {
                if (provider.equals(config.provider())) {
                    return config;
                }
            }
        }
        return null;
    }

    protected URL buildURL(String endpoint, String url, String format, Integer maxWidth, Integer maxHeight) throws MalformedURLException {
        StringBuilder sb = new StringBuilder(endpoint);
        String separator = endpoint.contains("?") ? "&" : "?";
        addURLParameter(sb, separator, "url", url);
        separator = "&";
        addURLParameter(sb, separator, "format", format);
        addURLParameter(sb, separator, "maxwidth", maxWidth);
        addURLParameter(sb, separator, "maxheight", maxHeight);
        return new URL(sb.toString());
    }

    protected void addURLParameter(StringBuilder sb, String separator, String name, Object value) {
        if (sb != null && StringUtils.isNotEmpty(separator) && StringUtils.isNotEmpty(name) && value != null) {
            sb.append(separator).append(name).append("=").append(value);
        }
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, updated = "bindOEmbedClientImplConfigurationFactory")
    protected synchronized void bindOEmbedClientImplConfigurationFactory(final OEmbedClientImplConfigurationFactory configurationFactory, Map<String, ?> properties) {
        LOGGER.warn("bindConfigurationFactory: " + configurationFactory.getConfig().provider());
        configs.add(configurationFactory.getConfig());
    }

    protected synchronized void unbindOEmbedClientImplConfigurationFactory(final OEmbedClientImplConfigurationFactory configurationFactory, Map<String, ?> properties) {
        LOGGER.warn("unbindConfigurationFactory: " + configurationFactory.getConfig().provider());
        configs.remove(configurationFactory.getConfig());
    }
}
