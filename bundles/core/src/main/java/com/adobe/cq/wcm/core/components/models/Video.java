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
package com.adobe.cq.wcm.core.components.models;

import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
public interface Video extends Component {

    /**
     * Name of the property that defines the image to be shown while the video is downloading.
     *
     */
    String PN_POSTER_REFERENCE = "posterImageReference";

    /**
     * Name of the property that defines if video controls should be hidden.
     *
     */
    String PN_HIDE_CONTROL = "hideControl";

    /**
     * Name of the property that defines if video looping is enabled.
     *
     */
    String PN_LOOP_ENABLED = "loopEnabled";

    /**
     * Name of the property that defines if video autoplay is enabled.
     *
     */
    String PN_AUTOPLAY_ENABLED = "autoplayEnabled";

    /**
     * Returns the file reference of the current video, if one exists.
     *
     * @return Returns the file reference of the current video, if one exists, or {@code null} otherwise
     */
    default String getFileReference() {
        return null;
    }

    /**
     * Returns the file reference of the image used as a poser for the video
     *
     * @return Returns the file reference of the image used as a poser for the video, or {@code null} if it doesn't exist
     */
    default String getPosterImageReference() {
        return null;
    }

    /**
     * Returns weather the video controls should be hidden or not
     *
     * @return Returns true if video controls should be hidden, false otherwise
     */
    default boolean isHideControl() {
        return false;
    }

    /**
     * Returns weather the video loop should be enabled or not
     *
     * @return Returns true if video loop should be enabled, false otherwise
     */
    default boolean isLoopEnabled() {
        return false;
    }

    /**
     * Returns weather the video autoplay should be enabled or not
     *
     * @return Returns true if video autoplay should be enabled, false otherwise
     */
    default boolean isAutoplayEnabled() {
        return false;
    }

}
