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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.services.embed.OEmbedResponse;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(
    ignoreUnknown = true
)
public class OEmbedJSONResponseImpl implements OEmbedResponse {

    protected String type;
    protected String version;
    protected String title;
    protected String authorName;
    protected String authorUrl;
    protected String providerName;
    protected String providerUrl;
    protected Long cacheAge;
    protected String thumbnailUrl;
    protected String thumbnailWidth;
    protected String thumbnailHeight;
    protected String width;
    protected String height;
    protected String html;
    protected String url;

    @Override
    @NotNull
    public String getType() {
        return type;
    }

    @Override
    @NotNull
    public String getVersion() {
        return version;
    }

    @Override
    @Nullable
    public String getTitle() {
        return title;
    }

    @Override
    @Nullable
    @JsonAlias("author_name")
    public String getAuthorName() {
        return authorName;
    }

    @Override
    @Nullable
    @JsonAlias("author_url")
    public String getAuthorUrl() {
        return authorUrl;
    }

    @Override
    @Nullable
    @JsonAlias("provider_name")
    public String getProviderName() {
        return providerName;
    }

    @Override
    @Nullable
    @JsonAlias("provider_url")
    public String getProviderUrl() {
        return providerUrl;
    }

    @Override
    @Nullable
    @JsonAlias("cache_age")
    public Long getCacheAge() {
        return cacheAge;
    }

    @Override
    @Nullable
    @JsonAlias("thumbnail_url")
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    @Override
    @Nullable
    @JsonAlias("thumbnail_width")
    public String getThumbnailWidth() {
        return thumbnailWidth;
    }

    @Override
    @Nullable
    @JsonAlias("thumbnail_height")
    public String getThumbnailHeight() {
        return thumbnailHeight;
    }

    @Override
    @Nullable
    public String getWidth() {
        return width;
    }

    @Override
    @Nullable
    public String getHeight() {
        return height;
    }

    @Override
    @Nullable
    public String getHtml() {
        return html;
    }

    @Override
    @Nullable
    public String getUrl() {
        return url;
    }
}
