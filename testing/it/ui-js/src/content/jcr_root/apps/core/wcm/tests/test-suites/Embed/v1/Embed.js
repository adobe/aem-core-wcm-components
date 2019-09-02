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
/* globals hobs,jQuery */
;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    var c = window.CQ.CoreComponentsIT.commons;
    var embed = window.CQ.CoreComponentsIT.Embed.v1;
    var selectors = {
        editDialog: {
            properties: {
                embeddableField: "[data-cmp-embed-dialog-edit-hook='embeddableField']",
                typeField: "[data-cmp-embed-dialog-edit-hook='typeField']",
                typeRadio: "[data-cmp-embed-dialog-edit-hook='typeField'] coral-radio",
                urlField: "[data-cmp-embed-dialog-edit-hook='urlField']",
                urlStatus: "[data-cmp-embed-dialog-edit-hook='urlStatus']"
            }
        },
        embed: {
            self: ".cmp-embed",
            processors: {
                pinterest: ".cmp-embed [class^='PIN_']",
                oEmbed: {
                    facebookPost: ".cmp-embed .fb-post",
                    instagram: ".cmp-embed .instagram-media",
                    soundcloud: ".cmp-embed [src^='https://w.soundcloud.com/player']",
                    twitter: ".cmp-embed .twitter-tweet",
                    youtube: ".cmp-embed [src^='https://www.youtube.com/embed']"
                }
            }
        }
    };
    var urlProcessors = {};
    urlProcessors.pinterest = {
        name: "Pinterest",
        selector: selectors.embed.processors.pinterest,
        urls: [
            "https://www.pinterest.com/pin/146859637829777606/"
        ]
    };
    urlProcessors.oEmbed = {
        facebookPost: {
            name: "Facebook Post",
            selector: selectors.embed.processors.oEmbed.facebookPost,
            urls: [
                "https://www.facebook.com/Adobe/posts/10156804081233871",
                "https://www.facebook.com/Adobe/photos/rpp.305115773870/10156804081143871"
            ]
        },
        instagram: {
            name: "Instagram",
            selector: selectors.embed.processors.oEmbed.instagram,
            urls: [
                "https://www.instagram.com/p/B1wkr19Jq3H/",
                "https://www.instagr.am/p/B1wkr19Jq3H/"
            ]
        },
        soundcloud: {
            name: "SoundCloud",
            selector: selectors.embed.processors.oEmbed.soundcloud,
            urls: [
                "https://soundcloud.com/adobeexperiencecloud/sets/think-tank-audio-experience"
            ]
        },
        twitter: {
            name: "Twitter",
            selector: selectors.embed.processors.oEmbed.twitter,
            urls: [
                "https://twitter.com/Adobe/status/1168253464675307525"
            ]
        },
        youtube: {
            name: "YouTube",
            selector: selectors.embed.processors.oEmbed.youtube,
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
        .addTestCase(embed.tcUrlPinterest(tcExecuteBeforeTest, tcExecuteAfterTest, urlProcessors.pinterest, selectors))
        .addTestCase(embed.tcUrlOEmbedFacebookPost(tcExecuteBeforeTest, tcExecuteAfterTest, urlProcessors.oEmbed.facebookPost, selectors))
        .addTestCase(embed.tcUrlOEmbedInstagram(tcExecuteBeforeTest, tcExecuteAfterTest, urlProcessors.oEmbed.instagram, selectors))
        .addTestCase(embed.tcUrlOEmbedSoundcloud(tcExecuteBeforeTest, tcExecuteAfterTest, urlProcessors.oEmbed.soundcloud, selectors))
        .addTestCase(embed.tcUrlOEmbedTwitter(tcExecuteBeforeTest, tcExecuteAfterTest, urlProcessors.oEmbed.twitter, selectors))
        .addTestCase(embed.tcUrlOEmbedYoutube(tcExecuteBeforeTest, tcExecuteAfterTest, urlProcessors.oEmbed.youtube, selectors));
}(hobs, jQuery));
