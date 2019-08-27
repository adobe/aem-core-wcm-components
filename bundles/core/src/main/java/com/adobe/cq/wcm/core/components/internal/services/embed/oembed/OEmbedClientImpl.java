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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.models.embed.oembed.OEmbedClient;
import com.adobe.cq.wcm.core.components.models.embed.oembed.OEmbedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component(
        service = OEmbedClient.class
)
@Designate(
        ocd = OEmbedClientImplConfiguration.class,
        factory = true
)
public class OEmbedClientImpl implements OEmbedClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(OEmbedClientImpl.class);

    private Map<String, OEmbedClientImplConfiguration> configs = new HashMap<>();

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public String getProvider(String url) {
        if (StringUtils.isNotEmpty(url)) {
            for (Map.Entry<String, OEmbedClientImplConfiguration> entry : configs.entrySet()) {
                for (String scheme : entry.getValue().scheme()) {
                    if (Pattern.matches(scheme, url)) {
                        return entry.getKey();
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
        OEmbedClientImplConfiguration configuration = configs.get(provider);
        if (configuration == null) {
            return null;
        }
        if (OEmbedResponse.Format.JSON == OEmbedResponse.Format.fromString(configuration.format())) {
            try {
                URL jsonURL = buildURL(configuration.endpoint(), url, OEmbedResponse.Format.JSON.getValue(), null, null);
                return mapper.readValue(jsonURL, OEmbedResponseImpl.class);
            } catch (IOException ioex) {
                LOGGER.error(ioex.getMessage(), ioex);
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

    @Activate
    protected void activate(OEmbedClientImplConfiguration configuration, BundleContext bundleCtx) {
        configs.put(configuration.provider(), configuration);
    }

    @Deactivate
    protected void deactivate() {
        configs.clear();
    }
}
