package com.adobe.cq.wcm.core.components.it.seljup.components.FormComponents;

import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.qe.selenium.pagewidgets.coral.CoralSelect;
import com.adobe.qe.selenium.pagewidgets.coral.Dialog;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormContainerConfigDialog extends Dialog {

    private static String actionInput = "input[name='./action']";

    public void selectActionType(String action) {
        CoralSelect coralSelect = new CoralSelect("name='./actionType'");
        coralSelect.selectItemByValue(action);
    }

    public String getActionInputValue() {
        return $(actionInput).getValue();
    }

    public void setActionInputValue(String value) throws InterruptedException {
        $(actionInput).clear();
        $(actionInput).sendKeys(value);
        Commons.webDriverWait(1000);
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

    public void setMailActionFields(String from, String subject, String[] mailToList, String[] ccList) {
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
