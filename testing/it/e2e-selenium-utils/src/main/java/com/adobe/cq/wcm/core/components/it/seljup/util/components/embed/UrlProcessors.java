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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.embed;

import com.adobe.cq.testing.selenium.pagewidgets.Helpers;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static com.codeborne.selenide.Selenide.$;

public class UrlProcessors {
    public class OEmbed {
        private String name;
        private String selector;
        private String[] urls;

        public String getName() {
            return name;
        }

        public boolean urlProcessorExits() {
            final WebDriver webDriver = WebDriverRunner.getWebDriver();
            new WebDriverWait(webDriver, RequestConstants.TIMEOUT_TIME_SEC)
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
            Helpers.waitForElementAnimationFinished($(selector));
            return $(selector).isDisplayed();
        }

        public String[] getUrls() {
            return urls;
        }

    }


    public OEmbed getPinterest() {
        OEmbed pinterest = new OEmbed();
        pinterest.name = "Pinterest";
        pinterest.selector = ".cmp-embed [class^='PIN_']";
        pinterest.urls = new String[] {"https://www.pinterest.com/pin/146859637829777606/"};
        return pinterest;
    }

    public OEmbed getFlickr() {
        OEmbed flickr = new OEmbed();
        flickr.name = "Flickr";
        flickr.selector = ".cmp-embed [src^='https://live.staticflickr.com']";
        flickr.urls = new String[] {"https://www.flickr.com/photos/adobe/6951486964/in/album-72157629498635308/"};
        return flickr;
    }

    public OEmbed getSoundCloud() {
        OEmbed soundCloud = new OEmbed();
        soundCloud.name = "SoundCloud";
        soundCloud.selector = ".cmp-embed [src^='https://w.soundcloud.com/player']";
        soundCloud.urls = new String[] {"https://soundcloud.com/adobeexperiencecloud/sets/think-tank-audio-experience"};
        return soundCloud;
    }

    public OEmbed getTwitter() {
        OEmbed twitter = new OEmbed();
        twitter.name = "Twitter";
        twitter.selector = ".cmp-embed .twitter-tweet";
        twitter.urls = new String[] {"https://twitter.com/Adobe/status/1168253464675307525"};
        return twitter;
    }

    public OEmbed getYouTube() {
        OEmbed youTube = new OEmbed();
        youTube.name = "YouTube";
        youTube.selector = ".cmp-embed [src^='https://www.youtube.com/embed']";
        youTube.urls = new String[] {"https://www.youtube.com/watch?v=5vOOa3-fifY", "https://youtu.be/5vOOa3-fifY"};
        return youTube;
    }

}
