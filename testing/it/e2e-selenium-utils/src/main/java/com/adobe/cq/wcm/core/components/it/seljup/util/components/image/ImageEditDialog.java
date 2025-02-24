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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.image;

import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralCheckbox;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralSelect;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralSelectList;
import com.adobe.cq.testing.selenium.pagewidgets.coral.Dialog;
import com.adobe.cq.testing.selenium.pagewidgets.cq.AutoCompleteField;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.codeborne.selenide.DragAndDropOptions;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static com.codeborne.selenide.Selenide.$;

public class ImageEditDialog extends Dialog {

    private static String fileUpload = "coral-fileupload[name='./file']";

    private static String clearButtonSDK = "button[class='cq-FileUpload-clear _coral-Button _coral-Button--primary _coral-Button--quiet']";

    private static String clearButton65 = "button[class='cq-FileUpload-clear coral3-Button coral3-Button--quiet']";
    private static String imageInSidePanel = "coral-card.cq-draggable[data-path=\"%s\"]";
    private static String altText = "input[name='./alt']";
    private static String linkUrl = "[name='./linkURL']";
    private static String title = "input[name='./jcr:title']";
    private static String popUpTitle = "[name='./displayPopupTitle']";
    public static String decorative = "[name='./isDecorative']";
    public static String disableLazyLoading = "[name='./disableLazyLoading']";
    public static String assetFilter = "[name='assetfilter_image_path']";
    private static SelenideElement assetTab = $(".cq-dialog coral-tab[data-foundation-tracking-event*='asset']");
    private static SelenideElement metadataTab = $("coral-tab[data-foundation-tracking-event*='metadata']");
    private static String altValueFromDAM = "[name='./altValueFromDAM']";
    private static String altValueFromPageImage = "[name='./altValueFromPageImage']";
    private static String imageFromPageImage = "[name='./imageFromPageImage']";
    private static String titleValueFromDAM = "[name='./titleValueFromDAM']";
    private static String altValueDoNotProvide = "[name='./isDecorative']";
    private static String smartCropSelectButton = "[name='./smartCropRendition'] > button";
    private static String linkTarget = "coral-checkbox[name='./linkTarget']";
    private static String smartCropField = ".cmp-image__editor-dynamicmedia-smartcroprendition[name='./smartCropRendition']";

    private static final String AEM_SDK = "sdk";

    public void uploadImageFromSidePanel(String imagePath) {
        $(String.format(imageInSidePanel,imagePath)).dragAndDropTo(fileUpload, DragAndDropOptions.usingActions());
    }

    public void setAltText(String text) {
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, RequestConstants.DURATION_TIMEOUT)
            .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(String.format("%s coral-dialog-header", this.getCssSelector()))));
        content().find(altText).clear();
        content().find(altText).sendKeys(text);
    }

    public void setLinkURL(String url) {
        AutoCompleteField autoCompleteField = new AutoCompleteField("css:" + linkUrl);
        autoCompleteField.sendKeys(url);
        $("button[is='coral-buttonlist-item'][value='" + url + "']").click();
    }

    public void setTitle(String value) {
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, RequestConstants.DURATION_TIMEOUT)
            .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(String.format("%s coral-dialog-header", this.getCssSelector()))));
        content().find(title).clear();
        content().find(title).sendKeys(value);
    }

    public void clickClearButton(String aemVersion) {
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        if (aemVersion.equals(AEM_SDK)) {
            new WebDriverWait(webDriver, RequestConstants.DURATION_TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(String.format("%s coral-dialog-header", this.getCssSelector()))));
            content().find(clearButtonSDK).click();
        } else {
            new WebDriverWait(webDriver, RequestConstants.DURATION_TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(String.format("%s coral-dialog-header", this.getCssSelector()))));
            content().find(clearButton65).click();
        }
    }

    public void checkCaptionAsPopUp() {
        CoralCheckbox checkbox = new CoralCheckbox(popUpTitle);
        checkbox.click();
    }

    public void checkDecorative() {
        CoralCheckbox checkbox = new CoralCheckbox(decorative);
        checkbox.click();
    }

    public void checkDisableLazyLoading() {
        CoralCheckbox checkbox = new CoralCheckbox(disableLazyLoading);
        checkbox.click();
    }

    public void setAssetFilter(String filter) {
        AutoCompleteField autoCompleteField = new AutoCompleteField("css:" + assetFilter);
        autoCompleteField.sendKeys(filter);
        autoCompleteField.suggestions().selectByValue(filter);
    }

    public void openMetadataTab() {
        $(metadataTab).click();
    }

    public void openAssetTab() {
        $(assetTab).click();
    }

    public void checkAltValueFromDAM() {
        CoralCheckbox checkbox = new CoralCheckbox(altValueFromDAM);
        checkbox.click();
    }

    public void checkAltValueFromPageImage() {
        if ($(altValueFromPageImage).exists()) {
            CoralCheckbox checkbox = new CoralCheckbox(altValueFromPageImage);
            checkbox.click();
        }
    }

    public void checkImageFromPageImage() {
        if ($(imageFromPageImage).exists()) {
            CoralCheckbox checkbox = new CoralCheckbox(imageFromPageImage);
            checkbox.click();
        }
    }

    public void checkTitleValueFromDAM() {
        CoralCheckbox checkbox = new CoralCheckbox(titleValueFromDAM);
        checkbox.click();
    }

    public void checkAltValueDoNotProvide() {
        CoralCheckbox checkbox = new CoralCheckbox(altValueDoNotProvide);
        checkbox.click();
    }

    public void clickLinkTarget() {
        CoralCheckbox checkbox = new CoralCheckbox(linkTarget);
        checkbox.click();
    }

    public boolean isTitleFromDAM() {
        CoralCheckbox checkbox = new CoralCheckbox(titleValueFromDAM);
        return checkbox.isChecked();
    }

    public boolean isAltFromDAM() {
        CoralCheckbox checkbox = new CoralCheckbox(altValueFromDAM);
        return checkbox.isChecked();
    }

    public boolean isTitleFromDAMVisible() {
        CoralCheckbox checkbox = new CoralCheckbox(titleValueFromDAM);
        return checkbox.isVisible();
    }

    public boolean isAltFromDAMVisible() {
        CoralCheckbox checkbox = new CoralCheckbox(altValueFromDAM);
        return checkbox.isVisible();
    }

    public boolean isPopUpTitle() {
        CoralCheckbox checkbox = new CoralCheckbox(popUpTitle);
        return checkbox.isChecked();
    }

    public void selectSmartCrop(String cropName) {
        $(smartCropSelectButton).click();
        CoralSelectList coralSelectList = new CoralSelectList($(smartCropField));
        if (!coralSelectList.isVisible()) {
            CoralSelect selectList = new CoralSelect($(smartCropField));
            coralSelectList = selectList.openSelectList();
        }
        coralSelectList.selectByValue(cropName);
    }
}
