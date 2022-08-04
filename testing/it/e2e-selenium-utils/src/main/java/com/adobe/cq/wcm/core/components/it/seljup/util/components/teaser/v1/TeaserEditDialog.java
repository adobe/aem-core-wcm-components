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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.teaser.v1;

import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralCheckbox;
import com.adobe.cq.testing.selenium.pagewidgets.cq.AutoCompleteField;
import com.codeborne.selenide.DragAndDropOptions;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class TeaserEditDialog {
    private static String imageInSidePanel = "coral-card.cq-draggable[data-path='%s']";
    private static String assetUpload = ".cmp-teaser__editor coral-fileupload[name='./file']";
    private static String linkUrl = "[name='./linkURL']";
    private static String actions = ".cmp-teaser__editor-multifield_actions";
    private static String titleFromPage = ".cmp-teaser__editor input[name='./titleFromPage']";
    private static String preTitle = ".cmp-teaser__editor input[name='./pretitle']";
    private static String title = ".cmp-teaser__editor input[name='./jcr:title']";
    private static String descriptionFromPage = ".cmp-teaser__editor input[name='./descriptionFromPage']";
    private static String description = ".cmp-teaser__editor div[name='./jcr:description']";
    private static String actionsEnabled = ".cmp-teaser__editor coral-checkbox[name='./actionsEnabled']";
    private static String actionLinkURL = "[data-cmp-teaser-v1-dialog-edit-hook='actionLink']";
    private static String actionText = "[data-cmp-teaser-v1-dialog-edit-hook='actionTitle']";
    private static String imageFromPageImage = "[name='./imageFromPageImage']";
    private static String titleTypeSelectDropdown = "coral-select[name='./titleType']";
    private static String titleTypeSelectDropdownDefaultSelected = "coral-select[name='./titleType'] coral-select-item[selected]";
    private static String assetWithoutDescriptionErrorMessage = ".cmp-image__editor-alt .coral-Form-errorlabel";
    private static String altTextFromAssetDescription = ".cmp-teaser__editor input[name='./altValueFromDAM']";

    protected String getActionLinkURLSelector() {
        return actionLinkURL;
    }

    protected String getActionTextSelector() {
        return actionText;
    }

    public void uploadImageFromSidePanel(String imagePath) {
        $(String.format(imageInSidePanel,imagePath)).dragAndDropTo(assetUpload, DragAndDropOptions.usingActions());
    }

    public void setLinkURL(String url) {
        AutoCompleteField autoCompleteField = new AutoCompleteField("css:" + linkUrl);
        autoCompleteField.sendKeys(url);
        $("button[is='coral-buttonlist-item'][value='" + url + "']").click();
    }

    public final void openImageTab() {
        $$(".cmp-teaser__editor coral-tab").get(0).click();
    }

    public void openTextTab() {
        $$(".cmp-teaser__editor coral-tab").get(1).click();
    }

    public final void openLinkAndActionsTab() {
        $$(".cmp-teaser__editor coral-tab").get(2).click();
    }

    public void setPreTitle(String value) {
        $(preTitle).clear();
        $(preTitle).sendKeys(value);
    }

    public void setTitle(String value) {
        $(title).clear();
        $(title).sendKeys(value);
    }

    public void setDescription(String value) {
        $(description).clear();
        $(description).sendKeys(value);
    }

    public void clickTitleFromPage() {
        CoralCheckbox checkbox = new CoralCheckbox(titleFromPage);
        checkbox.click();
    }

    public void clickDescriptionFromPage() {
        CoralCheckbox checkbox = new CoralCheckbox(descriptionFromPage);
        checkbox.click();
    }

    public boolean isDescriptionFromPagePresent() {
        return $(descriptionFromPage).isDisplayed();
    }

    public boolean isTitleFromPagePresent() {
        return $(titleFromPage).isDisplayed();
    }

    public boolean isActionsPresent() {
        return $(actions).isDisplayed();
    }

    public boolean isActionEnabledCheckDisabled() {
        return $(actionsEnabled).getAttribute("disabled").equals("true");
    }

    public boolean isActionEnabledChecked() {
        CoralCheckbox checkbox = new CoralCheckbox(actionsEnabled);
        return checkbox.isChecked();
    }

    public void clickActionEnabled() {
        CoralCheckbox checkbox = new CoralCheckbox(actionsEnabled);
        checkbox.click();
    }

    public void addActionLink() {
        $("[coral-multifield-add]").click();
    }

    public void setActionLinkUrl(String url) {
        $$(getActionLinkURLSelector()).last().find("input").sendKeys(url);
        // External Urls will not be present in suggestion
        if(url.startsWith("/content")) {
            $("button[is='coral-buttonlist-item'][value='" + url + "']").click();
        }
    }

    public void setActionText(String value) {
        $$(getActionTextSelector()).last().sendKeys(value);
    }

    public String getTitleValue() {
        return $(title).getValue();
    }

    public boolean isTitleEnabled() {
        return $(title).isEnabled();
    }

    public void checkImageFromPageImage() {
        if ($(imageFromPageImage).exists()) {
            CoralCheckbox checkbox = new CoralCheckbox(imageFromPageImage);
            checkbox.click();
        }
    }

    public Boolean isTitleTypeSelectDropdownDisplayed() {
        return $(titleTypeSelectDropdown).isDisplayed();
    }

    public String getTitleTypeSelectDropdownDefaultSelectedText() {
        return $(titleTypeSelectDropdownDefaultSelected).innerText();
    }

    public String getAssetWithoutDescriptionErrorMessage() {
        return $(assetWithoutDescriptionErrorMessage).innerText();
    }

    public void checkAltTextFromAssetDescription() {
        CoralCheckbox checkbox = new CoralCheckbox(altTextFromAssetDescription);
        checkbox.click();
    }

}
