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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.commons;

import com.adobe.cq.testing.selenium.pagewidgets.common.BaseComponent;

import static com.codeborne.selenide.Selenide.$;

public class CQOverlay extends BaseComponent {
    public CQOverlay() {
        super(".cq-Overlay");
    }

    /**
     * Opens the page overlay responsive grid
     * @param pagePath path of the page
     */
    public void openPlaceholder(String pagePath) {
        String placeholder = ".cq-Overlay[data-path='" + pagePath + "/jcr:content/root/responsivegrid/*']";
         $(placeholder).click();
    }


    /**
     * Opens the component's responsive grid
     * @param componentPath path of the component
     */
    public void openComponentOverlay(String componentPath) {
        String placeholder = ".cq-Overlay[data-path='" + componentPath + "']";
        $(placeholder).click();
    }
}
