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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.title.v1;

import com.adobe.cq.testing.selenium.pagewidgets.common.BaseComponent;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.title.TitleEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;

import static com.codeborne.selenide.Selenide.$;

public class Title extends BaseComponent {
    public static String title = ".cmp-title";

    public Title() {
        super("title");
    }

    public boolean isTitleSet(String value) {
        return $(title + " h1").getText().trim().equals(value);
    }

    public TitleEditDialog getEditDialog() {
        return new TitleEditDialog();
    }

    public boolean isTitleWithTypePresent(String typeSize) throws InterruptedException {
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        return $(title + " h" + typeSize).isDisplayed();
    }

    public void clickLink() {
        $(".cmp-title__link").click();
    }

    public boolean checkLinkPresentWithTarget(String link, String target) {
        return $("a.cmp-title__link[href='" + link + "'][target='" + target + "']").isDisplayed();
    }
}
