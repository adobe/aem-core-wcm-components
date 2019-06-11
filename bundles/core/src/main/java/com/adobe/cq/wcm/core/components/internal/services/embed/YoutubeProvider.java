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

import org.osgi.service.component.annotations.Component;

import com.adobe.cq.wcm.core.components.models.Embed;

@Component(service = Embed.Provider.class)
public class YoutubeProvider extends AbstractRegexMatchProvider {

    public YoutubeProvider() {
        super("https?:\\/\\/.*\\.youtube\\.com\\/watch\\?v\\=(.*)");
    }

    private String videoId;

    @Override
    public boolean accepts(String url) {
        if (super.accepts(url)) {
            videoId = matcher.group(1);
            options.put("videoId", videoId);
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "youtube";
    }

}
