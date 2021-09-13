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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.list.v1;

import com.adobe.cq.testing.selenium.pagewidgets.common.BaseComponent;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.list.ListEditDialog;
import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
public class List extends BaseComponent {

    public List() {
        super("");
    }

    private boolean isPagePresentWithText(String text) {
        ElementsCollection items = $$("span");
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).getText().trim().contains(text)) {
                return true;
            }
        }
        return false;
    }

    public ListEditDialog getEditDialog() {
        return new ListEditDialog();
    }


    public boolean isPagePresentInList(String page) {
        return isPagePresentWithText(page);
    }

    public boolean isPagePresentInListAtPosition(int idx, String page) {
        ElementsCollection items = $$("span");
        return items.get(idx).getText().trim().contains(page);
    }

    public int getListLength() {
        return $$(".cmp-list li").size();
    }

    public boolean isPageLinkPresent(String pageName) {
        return $("a[href*='" + pageName + ".html']").isDisplayed();
    }

    public boolean isPagePresentWithDescription(String description) {
        return isPagePresentWithText(description);
    }

    public boolean isPagePresentWithDate(String date) {
        return isPagePresentWithText(date);
    }
}

