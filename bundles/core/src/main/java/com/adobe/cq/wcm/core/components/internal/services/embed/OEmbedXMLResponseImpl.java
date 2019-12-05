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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.services.embed.OEmbedResponse;

@XmlRootElement(name = "oembed")
public class OEmbedXMLResponseImpl implements OEmbedResponse, Serializable {

    private static final long serialVersionUID = 1L;

    private String type;
    private String title;
    private String version;
    private String authorName;
    private String authorUrl;
    private String providerName;
    private String providerUrl;
    private Long cacheAge;
    private String thumbnailUrl;
    private String thumbnailWidth;
    private String thumbnailHeight;
    private String width;
    private String height;
    private String html;
    private String url;

    @SuppressFBWarnings(justification = "This field needs to be transient")
    protected transient List<Object> any = new ArrayList<>();

    @Override
    public @NotNull String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public @NotNull String getVersion() {
        return version;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public @Nullable String getTitle() {
        return title;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    @XmlElement(name = "author_name")
    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    @Override
    @XmlElement(name = "author_url")
    public @Nullable String getAuthorUrl() {
        return authorUrl;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    @Override
    @XmlElement(name = "provider_name")
    public @Nullable String getProviderName() {
        return providerName;
    }

    public void setProviderUrl(String providerUrl) {
        this.providerUrl = providerUrl;
    }

    @Override
    @XmlElement(name = "provider_url")
    public @Nullable String getProviderUrl() {
        return providerUrl;
    }

    public void setCacheAge(Long cacheAge) {
        this.cacheAge = cacheAge;
    }

    @Override
    @XmlElement(name = "cache_age")
    public @Nullable Long getCacheAge() {
        return cacheAge;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    @XmlElement(name = "thumbnail_url")
    public @Nullable String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailWidth(String thumbnailWidth) {
        this.thumbnailWidth = thumbnailWidth;
    }

    @Override
    @XmlElement(name = "thumbnail_width")
    public @Nullable String getThumbnailWidth() {
        return thumbnailWidth;
    }

    public void setThumbnailHeight(String thumbnailHeight) {
        this.thumbnailHeight = thumbnailHeight;
    }

    @Override
    @XmlElement(name = "thumbnail_height")
    public @Nullable String getThumbnailHeight() {
        return thumbnailHeight;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    @Override
    public @Nullable String getWidth() {
        return width;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    @Override
    public @Nullable String getHeight() {
        return height;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    @Override
    public @Nullable String getHtml() {
        return html;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public @Nullable String getUrl() {
        return url;
    }

    @XmlAnyElement(lax = true)
    public List<Object> getAny() {
        return any;
    }
}
