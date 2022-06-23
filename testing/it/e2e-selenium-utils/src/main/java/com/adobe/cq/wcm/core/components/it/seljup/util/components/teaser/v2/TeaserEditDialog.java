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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.teaser.v2;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralCheckbox;
import com.codeborne.selenide.WebDriverRunner;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class TeaserEditDialog extends com.adobe.cq.wcm.core.components.it.seljup.util.components.teaser.v1.TeaserEditDialog {

    private static String inheritAltFromPage = ".cmp-teaser__editor coral-checkbox[name='./altValueFromPageImage']";
    private static String isDecorative = ".cmp-teaser__editor coral-checkbox[name='./isDecorative']";
    private static String altText = ".cmp-teaser__editor input[name='./alt']";
    private static String linkTarget = "coral-checkbox[name='./linkTarget']";
    private static String actionLinkTarget = ".cmp-teaser__editor-actionField-linkTarget";
    private static String actionLinkURL = "[data-cmp-teaser-v2-dialog-edit-hook='actionLink']";
    private static String actionText = "[data-cmp-teaser-v2-dialog-edit-hook='actionTitle']";

    @Override
    protected String getActionLinkURLSelector() {
        return actionLinkURL;
    }

    @Override
    protected String getActionTextSelector() {
        return actionText;
    }

    public void openAssetsTab() {
        $$(".cmp-teaser__editor coral-tab").get(2).click();
    }

    public void openLinksTab() {
        $$(".cmp-teaser__editor coral-tab").get(0).click();
    }

    public void checkInheritAltFromPage() {
        CoralCheckbox checkbox = new CoralCheckbox(inheritAltFromPage);
        checkbox.click();
    }

    public void checkIsDecorative() {
        CoralCheckbox checkbox = new CoralCheckbox(isDecorative);
        checkbox.click();
    }

    public void scrollToIsDecorativeCheckbox() {
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", $(isDecorative));
    }

    public boolean isDecorativeChecked() {
        CoralCheckbox checkbox = new CoralCheckbox(isDecorative);
        return checkbox.isChecked();
    }

    public void setAltText(String value) {
        $(altText).clear();
        $(altText).sendKeys(value);
    }

    public void clickLinkTarget() {
        CoralCheckbox checkbox = new CoralCheckbox(linkTarget);
        checkbox.click();
    }

    public void clickLActionLinkTarget() {
        $$(actionLinkTarget).last().click();
    }

}
