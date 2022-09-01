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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.list;

import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralCheckbox;
import com.adobe.cq.testing.selenium.pagewidgets.coral.Dialog;
import com.adobe.cq.testing.selenium.pagewidgets.cq.AutoCompleteField;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ListEditDialog extends Dialog {
    private static String parentPageField = "[name='./parentPage']";
    private static String childDepthField = "coral-numberinput[name='./childDepth'] > input";
    private static String listFromField = "./listFrom";
    private static String queryField = "input[name='./query']";
    private static String searchInField = "[name='./searchIn']";
    private static String tageSearchRootField = "[name='./tagsSearchRoot']";
    private static String tagsField = "[name='./tags']";
    private static String tagsMatchField = "./tagsMatch";
    private static String orderBy ="./orderBy";
    private static String sortOrder = "./sortOrder";
    private static String maxItems = "coral-numberinput[name='./maxItems'] > input";
    private static SelenideElement settings = $("coral-tab[data-foundation-tracking-event*='item settings']");
    private static String linkItems  = "[name='./linkItems']";
    private static String showDescription = "[name='./showDescription']";
    private static String showModificationDate = "[name='./showModificationDate']";


    public void setParentPage(String path) {
        AutoCompleteField autoCompleteField = new AutoCompleteField("css:" + parentPageField);
        autoCompleteField.sendKeys(path);
        autoCompleteField.suggestions().selectByValue(path);
    }

    public void setChildDepth(String depth) {
        $(childDepthField).clear();
        $(childDepthField).sendKeys(depth);
    }

    public void selectFromList(String value) throws InterruptedException {
        Commons.useDialogSelect(listFromField, value);
    }

    public void addFixedListOptions(String value) throws InterruptedException {
        $("coral-multifield[data-granite-coral-multifield-name='./pages'] > button").click();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        $$("foundation-autocomplete[name='./pages'] input[role='combobox']").last().sendKeys(value);
        $("foundation-autocomplete[name='./pages'] button[value^='" + value + "']").click();
    }

    public void enterSearchQuery(String query) {
        $(queryField).sendKeys(query);
    }

    public void setSearchLocation(String value) {
        Commons.selectInAutocomplete(searchInField, value);
    }

    public void setTageSearchRoot(String path) {
        Commons.selectInAutocomplete(tageSearchRootField, path);
    }

    public void selectInTags(String tag) throws InterruptedException{
        Commons.selectInTags(tagsField, tag);
    }

    public void setTagsMatch(String value) throws InterruptedException {
        Commons.useDialogSelect(tagsMatchField, value);
    }
    public void setOrderBy(String value) throws InterruptedException {
        Commons.useDialogSelect(orderBy, value);
    }

    public void setSortOrder(String value) throws InterruptedException {
        Commons.useDialogSelect(sortOrder, value);
    }

    public void setMaxItems(String value) {
        $(maxItems).clear();
        $(maxItems).sendKeys(value);
    }

    public void openSettings() {
        $(settings).click();
    }

    public void clickLinkItems() {
        CoralCheckbox checkbox = new CoralCheckbox(linkItems);
        checkbox.click();
    }

    public void clickShowDescription() {
        CoralCheckbox checkbox = new CoralCheckbox(showDescription);
        checkbox.click();
    }

    public void clickShowModificationDate() {
        CoralCheckbox checkbox = new CoralCheckbox(showModificationDate);
        checkbox.click();
    }
}
