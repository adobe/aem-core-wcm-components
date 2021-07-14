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

package com.adobe.cq.wcm.core.components.it.seljup.components.video;

import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralCheckbox;
import com.adobe.cq.testing.selenium.pagewidgets.sidepanel.SidePanel;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class VideoEditDialog {
    private static String loopEnabled = ".cmp-video__editor coral-checkbox[name='./loopEnabled']";
    private static String hideControl = ".cmp-video__editor coral-checkbox[name='./hideControl']";
    private static String autoplayEnabled = ".cmp-video__editor coral-checkbox[name='./autoplayEnabled']";
    private static String videoFileUpload = "coral-fileupload[name='./videoFile']";
    private static String posterFileUpload = "coral-fileupload[name='./posterFile']";
    private static String assetInSidePanel = "coral-card.cq-draggable[data-path=\"%s\"]";

    public void openVideoTab() {
        $$(".cmp-video__editor coral-tab").get(0).click();
    }

    public void openPosterTab() {
        $$(".cmp-video__editor coral-tab").get(1).click();
    }

    public void openPropertiesTab() {
        $$(".cmp-video__editor coral-tab").get(2).click();
    }

    public void clickLoopEnabled() {
        CoralCheckbox checkbox = new CoralCheckbox(loopEnabled);
        checkbox.click();
    }

    public void clickHideControl() {
        CoralCheckbox checkbox = new CoralCheckbox(hideControl);
        checkbox.click();
    }

    public void clickAutoplayEnabled() {
        CoralCheckbox checkbox = new CoralCheckbox(autoplayEnabled);
        checkbox.click();
    }

    public void uploadVideoFromSidePanel(String videoPath) {
        $(String.format(assetInSidePanel, videoPath)).dragAndDropTo(videoFileUpload);
    }

    public void uploadPosterFromSidePanel(String imagePath) {
        $(String.format(assetInSidePanel, imagePath)).dragAndDropTo(posterFileUpload);
    }
}
