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
package com.adobe.cq.wcm.core.components.internal.services.embed;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.wcm.core.components.models.Embed;
import com.adobe.cq.wcm.core.components.models.oembed.OembedResponse.Format;

public class AbstractOembedProvider implements Embed.Provider {

    protected String apiEndpoint;
    protected List<String> urlSchemes;
    protected Format format;

    protected URL apiUrl;
    protected String url;
    protected Map<String, Object> options = new HashMap<>();

    protected static final String FORMAT_REGEX = "%{format}";

    protected AbstractOembedProvider(String apiEndpoint, List<String> urlSchemes, Format format) {
        this.apiEndpoint = apiEndpoint;
        this.urlSchemes = urlSchemes;
        this.format = format;
    }

    public boolean accepts(String url) {
        if (StringUtils.isBlank(url)) {
            return false;
        }
        boolean matched = urlSchemes.stream().anyMatch(s -> url.trim().matches(s));
        if (matched) {
            this.url = url;
            buildApiUrl();
            InputStream in = null;
            try {
                in = (this.apiUrl).openStream();
                JsonReader jsonReader = Json.createReader(in);
                JsonObject jsonObject = jsonReader.readObject();
                jsonReader.close();
                options.put("type", jsonObject.getString("type"));
                options.put("html", jsonObject.getString("html"));
                return true;
            } catch (IOException ignore) {
                // ignore
            } finally {
                try {
                    in.close();
                } catch (Exception ignore) {
                    // ignore
                }
            }
        }
        return false;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    private void buildApiUrl() {
        this.apiUrl = null;
        Map<String, String> queryParameters = new HashMap<>();
        StringBuilder queryString = new StringBuilder();
        String url = this.apiEndpoint;
        try {
            if (url.contains(FORMAT_REGEX)) {
                url = url.replaceAll(Pattern.quote(FORMAT_REGEX), this.format.getValue());
            } else {
                queryParameters.put(Parameters.FORMAT.getValue(), this.format.getValue());
            }
            queryParameters.put(Parameters.URL.getValue(), this.url);
            for (Map.Entry<String, String> parameter : queryParameters.entrySet()) {
                if (queryString.length() > 0) {
                    queryString.append('&');
                } else {
                    queryString.append('?');
                }
                queryString.append(parameter.getKey());
                queryString.append('=');
                queryString.append(parameter.getValue());
            }
            this.apiUrl = new URL(url + queryString.toString());
        } catch (MalformedURLException e) {
            // ignore
        }
    }

    /**
     * Enumeration of oEmbed request parameters
     */
    protected enum Parameters {
        URL("url"),
        FORMAT("format"),
        MAX_WIDTH("maxwidth"),
        MAX_HEIGHT("maxheight");

        private String value;

        Parameters(String value) {
            this.value = value;
        }

        public static Parameters fromString(String value) {
            for (Parameters parameters : values()) {
                if (parameters.value.equals(value)) {
                    return parameters;
                }
            }
            return null;
        }

        public String getValue() {
            return value;
        }
    }
}
