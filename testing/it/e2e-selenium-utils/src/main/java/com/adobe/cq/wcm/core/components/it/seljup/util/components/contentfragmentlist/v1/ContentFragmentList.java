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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.contentfragmentlist.v1;

import com.adobe.cq.testing.selenium.pagewidgets.common.BaseComponent;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.contentfragmentlist.ContentFragmentListEditDialog;
import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Selenide.$$;

public class ContentFragmentList extends BaseComponent {

    public static String contentFragmentList = ".cmp-contentfragmentlist";

    public ContentFragmentList() {
        super(contentFragmentList);
    }

    public ElementsCollection getContentFragmentList() {
        return $$(contentFragmentList);
    }

    public ContentFragmentListEditDialog getEditDialog() {
        return new ContentFragmentListEditDialog();
    }
}
