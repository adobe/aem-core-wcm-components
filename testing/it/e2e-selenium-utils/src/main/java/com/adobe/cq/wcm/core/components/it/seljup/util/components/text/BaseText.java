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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.text;

import com.adobe.cq.testing.selenium.pagewidgets.common.BaseComponent;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selenide.$;

public class BaseText extends BaseComponent {
    protected static String text;
    protected static String editor;
    protected static String editorConf;
    protected static String rendered;
    protected static String renderedConf;

    public BaseText() {
        super(".cmp-text");
    }

    public void setContent(String value) {
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        WebElement element = webDriver.findElement(By.cssSelector(editor));
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].innerHTML=arguments[1]", element, value);
    }

    public void setText(String textContent) {
        String newTextContent = Selenide.$(text+".cq-Editable-dom[contenteditable]").should(new Condition[]{Condition.exist}).setValue(textContent).getText();
    }

    public String getText() {
        String newTextContent = Selenide.$(text+".cq-Editable-dom[contenteditable]").should(new Condition[]{Condition.exist}).getText();
        return newTextContent;
    }

    public boolean isTextRendered(String textValue) {
        if ($(rendered).isDisplayed()) {
            return $(rendered).innerHtml().trim().equals(textValue);
        }
        return false;
    }

    public TextEditDialog getEditDialog() {
        return new TextEditDialog();
    }

    public boolean isTextRenderedWithXSSProtection(String textValue) {
        if ($(editorConf).isDisplayed()) {
            return $(editorConf).innerHtml().trim().equals(textValue);
        }
        return false;
    }
}
