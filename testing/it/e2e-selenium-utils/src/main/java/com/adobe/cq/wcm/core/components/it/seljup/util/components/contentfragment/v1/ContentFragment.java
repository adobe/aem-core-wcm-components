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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.contentfragment.v1;

import com.adobe.cq.testing.selenium.pagewidgets.common.BaseComponent;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.contentfragment.ContentFragmentEditDialog;
import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Selenide.$$;

public class ContentFragment extends BaseComponent {
    public static String contentFragment = ".cmp-contentfragment";
    private static String title = ".cmp-contentfragment__title";
    private static String elements = ".cmp-contentfragment__elements";
    private static String elementTitle = ".cmp-contentfragment__element-title";
    private static String elementValue = ".cmp-contentfragment__element-value";
    public ContentFragment() {
        super(contentFragment);
    }

    public ContentFragmentEditDialog getEditDialog() {
        return new ContentFragmentEditDialog();
    }

    public ElementsCollection getTitle() {
        return $$(title);
    }

    public ElementsCollection getContentFragments() {
        return $$(contentFragment);
    }

    public ElementsCollection getElementTitle() {
        return $$(elementTitle);
    }

    public ElementsCollection getElementValue() {
        return $$(elementValue);
    }

}
