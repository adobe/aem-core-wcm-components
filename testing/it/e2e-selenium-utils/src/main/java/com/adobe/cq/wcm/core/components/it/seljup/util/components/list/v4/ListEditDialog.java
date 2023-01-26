/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2023 Adobe
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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.list.v4;

import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ListEditDialog extends com.adobe.cq.wcm.core.components.it.seljup.util.components.list.v1.ListEditDialog {
    public void addStaticListPage(String value) throws InterruptedException {
        $("coral-multifield[data-granite-coral-multifield-name='./static'] > button").click();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        $$("foundation-autocomplete.cmp-list__editor-static-multifield-linkURL input[role='combobox']").last().sendKeys(value);
        $("foundation-autocomplete.cmp-list__editor-static-multifield-linkURL button[value^='" + value + "']").click();
    }

    public void addStaticListLink(String linkUrl, String linkTitle) throws InterruptedException {
        // add new item
        $("coral-multifield[data-granite-coral-multifield-name='./static'] > button").click();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        // set link URL, also opens the dropdown of autocomplete
        SelenideElement autocompleteInput = $$(".cmp-list__editor-static-multifield-linkURL input[role='combobox']").last();
        autocompleteInput.sendKeys(linkUrl);
        // hide dropdown of autocomplete
        autocompleteInput.sendKeys(Keys.ENTER);
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        // set link text
        $$("input.cmp-list__editor-static-multifield-linkText[type='text']").last().sendKeys(linkTitle);
    }

    public void removeLastStaticListLink() {
        $$(".cmp-list__editor-static-multifield button[handle='remove']").last().click();
    }

    public boolean isMaxItemsDisplayed() {
        return $("coral-numberinput[name='./maxItems']").ancestor(".coral-Form-fieldwrapper").isDisplayed();
    }
}
