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

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

import com.adobe.cq.wcm.core.components.models.embed.Embed;

@Component(service = Embed.UrlProcessor.class)
public class YoutubeUrlProcessor implements Embed.UrlProcessor {

    protected static final String NAME = "youtube";

    protected static final String VIDEO_ID = "videoId";

    protected static final String SCHEME = "https?:\\/\\/.*\\.youtube\\.com\\/watch\\?v\\=(.*)";

    private Pattern pattern = Pattern.compile(SCHEME);

    @Override
    public Result process(String url) {
        if (StringUtils.isNotEmpty(url)) {
            Matcher matcher = pattern.matcher(url);
            if (matcher.matches()) {
                return new ProcessorResultImpl(
                    NAME,
                    new HashMap<String, Object>() {{
                        put(VIDEO_ID, matcher.group(1));
                    }});
            }
        }
        return null;
    }

}
