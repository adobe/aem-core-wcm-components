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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.teaser.v2;

import org.apache.commons.lang3.StringUtils;

import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class Teaser extends com.adobe.cq.wcm.core.components.it.seljup.util.components.teaser.v1.Teaser {

    public TeaserEditDialog getEditDialog() {
        return new TeaserEditDialog();
    }

    public boolean isTeaserLinkPresent(String path, String title) {
        if($("a" + teaserLink + "[href$='" + path + ".html']" ).isDisplayed()) {
            return $("a" + teaserLink + "[href$='" + path + ".html'] " + teaserContent + "  h2").getText().trim().equals(title);
        }
        return false;
    }

    public boolean isTeaserLinkPresentWithTarget(String path, String title, String target) {
        if($("a" + teaserLink + "[href$='" + path + ".html']" ).isDisplayed()) {
            return $("a" + teaserLink + "[href$='" + path + ".html'][target='" + target + "']  " + teaserContent + "  h2").getText().trim().equals(title);
        }
        return false;
    }

    public boolean isTitleLinkPresentWithTarget(String path, String title, String target) {
        if($("a" + teaserTitleLink + "[href$='" + path + ".html']" ).isDisplayed()) {
            return $("a" + teaserTitleLink + "[href$='" + path + ".html'][target='" + target + "'] h2").getText().trim().equals(title);
        }
        return false;
    }

    public boolean isActionLinkPresentWithTarget(String url, String target) {
        ElementsCollection items = $$(teaserActionLink);
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).getText().contains(url) && StringUtils.equals(items.get(i).attr("target"), target)) {
                return true;
            }
        }
        return false;
    }
}
