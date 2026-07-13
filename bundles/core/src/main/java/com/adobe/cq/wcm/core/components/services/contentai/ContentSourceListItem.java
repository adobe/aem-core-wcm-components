/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
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
package com.adobe.cq.wcm.core.components.services.contentai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single content source entry from {@code GET /content-sources}.
 *
 * @since com.adobe.cq.wcm.core.components.services.contentai 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentSourceListItem {

    private String name;
    private String id;
    private String description;
    private String type;
    private ContentSourceConfig config;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ContentSourceConfig getConfig() {
        return config;
    }

    public void setConfig(ContentSourceConfig config) {
        this.config = config;
    }

    /**
     * @return {@code true} when {@code config.access.public} is explicitly {@code true}.
     */
    public boolean isPublicAccess() {
        return config != null && config.getAccess() != null && config.getAccess().isPublic();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContentSourceConfig {

        private ContentSourceAccess access;

        public ContentSourceAccess getAccess() {
            return access;
        }

        public void setAccess(ContentSourceAccess access) {
            this.access = access;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContentSourceAccess {

        private boolean publicAccess;

        @JsonProperty("public")
        public boolean isPublic() {
            return publicAccess;
        }

        @JsonProperty("public")
        public void setPublic(boolean value) {
            this.publicAccess = value;
        }
    }
}
