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

package com.adobe.cq.wcm.core.components.it.seljup.components.embed;

import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralSelect;
import com.adobe.cq.testing.selenium.pagewidgets.coral.Dialog;
import com.adobe.cq.wcm.core.components.it.seljup.constant.CoreComponentConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static com.codeborne.selenide.Selenide.$;

public class EmbedEditDialog extends Dialog {

    private static String dialog = ".cmp-embed__editor";

    private static SelenideElement properties = $("coral-tab[data-foundation-tracking-event*='properties']");

    public EditDialogProperties getProperties() {
        return new EditDialogProperties();
    }

    public boolean isVisible() {
        return $(dialog).isDisplayed();
    }

    public static final class EditDialogProperties {
        private static String typeField = "[data-cmp-embed-dialog-edit-hook='typeField']";
        private static String typeRadio = "[data-cmp-embed-dialog-edit-hook='typeField'] coral-radio[value=\"%s\"]";
        private static String urlField = "[data-cmp-embed-dialog-edit-hook='urlField']";
        private static String urlStatus = "[data-cmp-embed-dialog-edit-hook='urlStatus']";
        private static String embeddableField = "data-cmp-embed-dialog-edit-hook='embeddableField'";
        private static String embeddableFieldButton = "[data-cmp-embed-dialog-edit-hook='embeddableField'] button";
        private static String embeddableFieldSelectList = "[data-cmp-embed-dialog-edit-hook='embeddableField'] coral-selectlist";
        private static String embeddableFieldYoutubeItem = "[data-cmp-embed-dialog-edit-hook='embeddableField'] coral-selectlist-item[value='core/wcm/components/embed/v1/embed/embeddable/youtube']";
        private static String htmlField = "[data-cmp-embed-dialog-edit-showhidetargetvalue='html']";
        private static String embeddableYoutubeVideoId = "[name='./youtubeVideoId']";

        public void setUrlField(String url) throws InterruptedException {
            $(urlField).clear();
            $(urlField).sendKeys(url);
            final WebDriver webDriver = WebDriverRunner.getWebDriver();
            Commons.webDriverWait(CoreComponentConstants.WEBDRIVER_WAIT_TIME_MS);
            new WebDriverWait(webDriver, CoreComponentConstants.TIMEOUT_TIME_SEC).until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".is-invalid" + urlField)));
            Commons.webDriverWait(CoreComponentConstants.WEBDRIVER_WAIT_TIME_MS);
        }

        public boolean isUrlStatusSet(String value) {
             return $(urlStatus).isDisplayed() && $(urlStatus).getText().trim().contains(value);

        }

        public void setTypeRadio(String value) {
            $(String.format(typeRadio,value)).click();
        }

        public void setEmbeddableField(String value) {
            CoralSelect select = new CoralSelect(embeddableField);
            select.selectItemByValue(value);
        }

        public void setEmbeddableYoutubeVideoId(String videoId) {
            $(embeddableYoutubeVideoId).clear();
            $(embeddableYoutubeVideoId).sendKeys(videoId);
        }

        public void setHtmlField(String value) {
            $(htmlField).clear();
            $(htmlField).sendKeys(value);
        }
    }
}
