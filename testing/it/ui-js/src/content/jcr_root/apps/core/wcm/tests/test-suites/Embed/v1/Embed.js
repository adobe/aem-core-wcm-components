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
;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    var c = window.CQ.CoreComponentsIT.commons;
    var embed = window.CQ.CoreComponentsIT.Embed.v1;
    var selectors = {
        editDialog: {
            self: ".cmp-embed__editor",
            properties: {
                self: "coral-tab:contains('Properties')",
                typeField: "[data-cmp-embed-dialog-edit-hook='typeField']",
                typeRadio: "[data-cmp-embed-dialog-edit-hook='typeField'] coral-radio",
                urlField: "[data-cmp-embed-dialog-edit-hook='urlField']",
                urlStatus: "[data-cmp-embed-dialog-edit-hook='urlStatus']",
                embeddableField: {
                    self: "[data-cmp-embed-dialog-edit-hook='embeddableField']",
                    button: "[data-cmp-embed-dialog-edit-hook='embeddableField'] button",
                    selectList: "[data-cmp-embed-dialog-edit-hook='embeddableField'] coral-selectlist",
                    items: {
                        youtube: "[data-cmp-embed-dialog-edit-hook='embeddableField'] coral-selectlist-item[value='core/wcm/components/embed/v1/embed/embeddable/youtube/v1']"
                    }
                },
                htmlField: "[data-cmp-embed-dialog-edit-showhidetargetvalue='html']",
                embeddables: {
                    youtube: {
                        videoId: "[name='./youtubeVideoId']"
                    }
                }
            }
        },
        embed: {
            self: ".cmp-embed",
            pinterest: ".cmp-embed [class^='PIN_']",
            facebookPost: ".cmp-embed .fb-post",
            facebookVideo: ".cmp-embed .fb-video",
            flickr: ".cmp-embed [src^='https://live.staticflickr.com']",
            instagram: ".cmp-embed .instagram-media",
            soundcloud: ".cmp-embed [src^='https://w.soundcloud.com/player']",
            twitter: ".cmp-embed .twitter-tweet",
            youtube: ".cmp-embed [src^='https://www.youtube.com/embed']"
        }
    };
    /*   var urlValidation = {
        valid: "https://www.youtube.com/watch?v=5vOOa3-fifY",
        invalid: "https://www.youtube.com/watch?v=5vOOa3-fifYinvalid",
        malformed: "malformed",
        blank: ""
    };
    */
    var urlProcessors = {};
    urlProcessors.pinterest = {
        name: "Pinterest",
        selector: selectors.embed.pinterest,
        urls: [
            "https://www.pinterest.com/pin/146859637829777606/"
        ]
    };
    urlProcessors.oEmbed = {
        flickr: {
            name: "Flickr",
            selector: selectors.embed.flickr,
            urls: [
                "https://www.flickr.com/photos/adobe/6951486964/in/album-72157629498635308/"
            ]
        },
        soundcloud: {
            name: "SoundCloud",
            selector: selectors.embed.soundcloud,
            urls: [
                "https://soundcloud.com/adobeexperiencecloud/sets/think-tank-audio-experience"
            ]
        },
        twitter: {
            name: "Twitter",
            selector: selectors.embed.twitter,
            urls: [
                "https://twitter.com/Adobe/status/1168253464675307525"
            ]
        },
        youtube: {
            name: "YouTube",
            selector: selectors.embed.youtube,
            urls: [
                "https://www.youtube.com/watch?v=5vOOa3-fifY",
                "https://youtu.be/5vOOa3-fifY"
            ]
        }
    };

    var tcExecuteBeforeTest = embed.tcExecuteBeforeTest(c.tcExecuteBeforeTest, c.rtEmbed_v1,
        "core/wcm/tests/components/test-page-v2");
    var tcExecuteAfterTest  = embed.tcExecuteAfterTest(c.tcExecuteAfterTest, c.policyPath, c.policyAssignmentPath);

    new h.TestSuite("Embed v1", {
        path: "/apps/core/wcm/tests/core-components-it/Embed/v1/Embed.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false
    })
        // .addTestCase(embed.tcUrlValidation(tcExecuteBeforeTest, tcExecuteAfterTest, urlValidation, selectors))
        .addTestCase(embed.tcUrlOEmbedFlickr(tcExecuteBeforeTest, tcExecuteAfterTest, urlProcessors.oEmbed.flickr, selectors))
        .addTestCase(embed.tcUrlOEmbedSoundcloud(tcExecuteBeforeTest, tcExecuteAfterTest, urlProcessors.oEmbed.soundcloud, selectors))
        .addTestCase(embed.tcUrlOEmbedTwitter(tcExecuteBeforeTest, tcExecuteAfterTest, urlProcessors.oEmbed.twitter, selectors))
        .addTestCase(embed.tcUrlOEmbedYoutube(tcExecuteBeforeTest, tcExecuteAfterTest, urlProcessors.oEmbed.youtube, selectors))
        .addTestCase(embed.tcEmbeddableYoutube(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(embed.tcHtml(tcExecuteBeforeTest, tcExecuteAfterTest, selectors));
}(hobs, jQuery));
