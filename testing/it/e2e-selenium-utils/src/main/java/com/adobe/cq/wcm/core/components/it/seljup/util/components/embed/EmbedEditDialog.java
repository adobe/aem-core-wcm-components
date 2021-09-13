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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.embed;

import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralSelect;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralSelectList;
import com.adobe.cq.testing.selenium.pagewidgets.coral.Dialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static com.codeborne.selenide.Selenide.$;

public class EmbedEditDialog extends Dialog {

    private static String dialog = ".cmp-embed__editor";

    public EditDialogProperties getProperties() {
        return new EditDialogProperties();
    }

    public boolean isVisible() {
        return $(dialog).isDisplayed();
    }

    public static final class EditDialogProperties {
        private static String typeRadio = "[data-cmp-embed-dialog-edit-hook='typeField'] coral-radio[value=\"%s\"]";
        private static String urlField = "[data-cmp-embed-dialog-edit-hook='urlField']";
        private static String urlStatus = "[data-cmp-embed-dialog-edit-hook='urlStatus']";
        private static String embeddableField = "data-cmp-embed-dialog-edit-hook='embeddableField'";
        private static String htmlField = "[data-cmp-embed-dialog-edit-showhidetargetvalue='html']";
        private static String embeddableYoutubeVideoId = "[name='./youtubeVideoId']";
        private static String validUrl = "https://www.youtube.com/watch?v=5vOOa3-fifY";
        private static String invalidUrl = "https://www.youtube.com/watch?v=5vOOa3-fifYinvalid";
        private static String malformedUrl = "malformed";

        public void setUrlField(String url) {
            $(urlField).clear();
            $(urlField).sendKeys(url);
        }

        public void waitForUrlFieldToBeValid() throws InterruptedException {
            final WebDriver webDriver = WebDriverRunner.getWebDriver();
            Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
            new WebDriverWait(webDriver, RequestConstants.TIMEOUT_TIME_SEC).until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".is-invalid" + urlField)));
            Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        }

        public boolean isUrlStatusSet(String value) {
             return $(urlStatus).isDisplayed() && $(urlStatus).getText().trim().contains(value);
        }

        public boolean isUrlStatusVisible() {
            return $(urlStatus).isDisplayed();
        }

        public boolean isUrlFieldInvalid() {
            return $(urlField + ".is-invalid").isDisplayed();
        }

        public void setTypeRadio(String value) {
            $(String.format(typeRadio,value)).click();
        }

        public void setEmbeddableField(String value) throws InterruptedException {
            $( "["+embeddableField + "] > button").click();
            Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
            CoralSelectList coralSelectList = new CoralSelectList($("["+embeddableField + "]"));
            if(!coralSelectList.isVisible()) {
                CoralSelect selectList = new CoralSelect(embeddableField);
                coralSelectList = selectList.openSelectList();
            }

            final WebDriver webDriver = WebDriverRunner.getWebDriver();
            WebElement element = webDriver.findElement(By.cssSelector("coral-selectlist-item[value='" + value + "']"));
            ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", element);
            coralSelectList.selectByValue(value);
        }

        public void setEmbeddableYoutubeVideoId(String videoId) {
            $(embeddableYoutubeVideoId).clear();
            $(embeddableYoutubeVideoId).sendKeys(videoId);
        }

        public void setHtmlField(String value) {
            $(htmlField).clear();
            $(htmlField).sendKeys(value);
        }

        public boolean isUrlStatusEmpty() {
            return $(urlStatus).getText().trim().equals("");
        }

        public String getValidUrl() {
            return validUrl;
        }

        public String getInvalidUrl() {
            return invalidUrl;
        }

        public String getMalformedUrl() {
            return malformedUrl;
        }
    }
}
