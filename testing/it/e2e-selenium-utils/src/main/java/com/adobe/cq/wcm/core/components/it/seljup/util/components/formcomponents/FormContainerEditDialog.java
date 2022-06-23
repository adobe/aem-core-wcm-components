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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.formcomponents;

import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralSelectList;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralSelect;
import com.adobe.cq.testing.selenium.pagewidgets.coral.Dialog;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormContainerEditDialog extends Dialog {

    private static String actionInput = "input[name='./action']";

    public void selectActionType(String action) {
        //Open selectlist
        $( "[name='./actionType'] > button").click();
        CoralSelectList coralSelectList = new CoralSelectList($("[name='./actionType']"));
        if(!coralSelectList.isVisible()) {
            CoralSelect selectList = new CoralSelect("name='./actionType'");
            coralSelectList = selectList.openSelectList();
        }

        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        WebElement element = webDriver.findElement(By.cssSelector("coral-selectlist-item[value='" + action + "']"));
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", element);
        coralSelectList.selectByValue(action);
    }

    public String getActionInputValue() {
        return $(actionInput).getValue();
    }

    public void setActionInputValue(String value) throws InterruptedException {
        $(actionInput).clear();
        $(actionInput).sendKeys(value);
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
    }

    public void setFromField(String value) {
        String fromField = "[name='./from']";
        assertTrue($(fromField).isDisplayed(),"From field should be visible");
        $(fromField).sendKeys(value);
    }

    public void setSubjectField(String value) {
        String subjectField = "[name='./subject']";
        assertTrue($(subjectField).isDisplayed(),"Subject field should be visible");
        $(subjectField).sendKeys(value);
    }

    public void setMailToField(String value) {
        String addMailTo = "coral-multifield[data-granite-coral-multifield-name='./mailto'] > button";
        assertTrue($(addMailTo).isDisplayed(),"Add MailTo should be visible");
        $(addMailTo).click();
        String mailToField = "input[name='./mailto']";
        assertTrue($$(mailToField).last().isDisplayed(),"MailTo field should be visible");
        $$(mailToField).last().sendKeys(value);
    }

    public void setCCField(String value) {
        String addCC = "coral-multifield[data-granite-coral-multifield-name='./cc'] > button";
        assertTrue($(addCC).isDisplayed(),"Add MailTo should be visible");
        $(addCC).click();
        String ccField = "input[name='./cc']";
        assertTrue($$(ccField).last().isDisplayed(),"MailTo field should be visible");
        $$(ccField).last().sendKeys(value);
    }

    public void setMailActionFields(String from, String subject, String[] mailToList, String[] ccList) throws InterruptedException {
        selectActionType("foundation/components/form/actions/mail");
        setFromField(from);
        setSubjectField(subject);
        for(int i = 0; i < mailToList.length; i++) {
            setMailToField(mailToList[i]);
        }
        for(int i = 0; i < ccList.length; i++) {
            setCCField(ccList[i]);
        }
    }
}
