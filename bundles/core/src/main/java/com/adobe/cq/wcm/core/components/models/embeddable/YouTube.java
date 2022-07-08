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
package com.adobe.cq.wcm.core.components.models.embeddable;

import java.net.URISyntaxException;

import org.jetbrains.annotations.Nullable;

public interface YouTube extends Embeddable {

    /**
     * Name of the resource property that defines the id of the YouTube video.
     */
    String PN_VIDEO_ID = "youtubeVideoId";

    /**
     * Name of the resource property that defines the width of the iFrame hosting the YouTube video.
     */
    String PN_WIDTH = "youtubeWidth";

    /**
     * Name of the resource property that defines the height of the iFrame hosting the YouTube video.
     */
    String PN_HEIGHT = "youtubeHeight";

    /**
     * Name of the resource property that defines the aspect ratio of the iFrame hosting the YouTube video.
     */
    String PN_ASPECT_RATIO = "youtubeAspectRatio";

    /**
     * Name of the resource property that defines the layout type of the youtube video.
     */
    String PN_LAYOUT = "layout";

    /* The following resource property names are used for optional YouTube player paramters */
    String PN_AUTOPLAY = "youtubeAutoPlay";
    
    String PN_MUTE = "youtubeMute";

    String PN_LOOP = "youtubeLoop";

    String PN_REL = "youtubeRel";

    String PN_PLAYS_INLINE = "youtubePlaysInline";

    String PN_DESIGN_MUTE_ENABLED = "youtubeMuteEnabled";

    String PN_DESIGN_MUTE_DEFAULT_VALUE = "youtubeMuteDefaultValue";
    
    String PN_DESIGN_AUTOPLAY_ENABLED = "youtubeAutoPlayEnabled";

    String PN_DESIGN_AUTOPLAY_DEFAULT_VALUE = "youtubeAutoPlayDefaultValue";

    String PN_DESIGN_LOOP_ENABLED = "youtubeLoopEnabled";

    String PN_DESIGN_LOOP_DEFAULT_VALUE = "youtubeLoopDefaultValue";

    String PN_DESIGN_RELATED_VIDEOS_ENABLED = "youtubeRelatedVideosEnabled";

    String PN_DESIGN_RELATED_VIDEOS_DEFAULT_VALUE = "youtubeRelatedVideosDefaultValue";

    String PN_DESIGN_PLAYS_INLINE_ENABLED = "youtubePlaysInlineEnabled";

    String  PN_DESIGN_PLAYS_INLINE_DEFAULT_VALUE = "youtubePlaysInlineDefaultValue";

    default @Nullable String getIFrameWidth() {
        return null;
    }

    default @Nullable String getIFrameHeight() {
        return null;
    }

    default @Nullable String getIFrameAspectRatio() {
        return null;
    }

    default @Nullable String getLayout() {
        return null;
    }

    default @Nullable String getIFrameSrc() throws URISyntaxException {
        return null;
    }

    default boolean isEmpty() {
        return false;
    }
}
