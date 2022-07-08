/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.embed.v1;

import com.adobe.cq.testing.selenium.pagewidgets.common.BaseComponent;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.embed.EmbedEditDialog;

import static com.codeborne.selenide.Selenide.$;

public class Embed extends BaseComponent {

    private static String pinterest = ".cmp-embed [class^='PIN_']";
    private static String facebookPost = ".cmp-embed .fb-post";
    private static String facebookVideo = ".cmp-embed .fb-video";
    private static String flickr = ".cmp-embed [src^='https://live.staticflickr.com']";
    private static String instagram = ".cmp-embed .instagram-media";
    private static String soundcloud = ".cmp-embed [src^='https://w.soundcloud.com/player']";
    private static String twitter = ".cmp-embed .twitter-tweet";
    private static String youtube = ".cmp-embed [src^='https://www.youtube.com/embed']";

    public Embed() {
        super(".cmp-embed");
    }

    public EmbedEditDialog getEmbedEditDialog() {
        return new EmbedEditDialog();
    }

    public boolean isYoutubeEmbedVisible() {
        return $(youtube).isDisplayed();
    }

    public boolean htmlElementExists(String selector) {
        return $(selector).isDisplayed();
    }
}
