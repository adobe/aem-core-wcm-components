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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.breadcrumb;

import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralCheckbox;

import static com.codeborne.selenide.Selenide.$;

public class BreadcrumbEditDialog {

    private static String startLevel = "input[name='./startLevel']";

    /**
     * Set the hideCurrent selected or unselected
     * @param selected State to be set for hide current
     */
    public void setHideCurrent(boolean selected) {
        CoralCheckbox coralCheckbox = new CoralCheckbox("[name='./hideCurrent']");
        coralCheckbox.setSelected(selected);

    }

    /**
     * Set the showHidden selected or unselected
     * @param selected State to be set for show hidden
     */
    public void setShowHidden(boolean selected) {
        CoralCheckbox coralCheckbox = new CoralCheckbox("[name='./showHidden']");
        coralCheckbox.setSelected(selected);
    }

    /**
     * Get the start level set for breadcrumb component
     * @return start level for breadcrumb component
     */
    public int getStartLevelValue() {
        return Integer.valueOf($(startLevel).getValue());
    }

    /**
     * Set the start level for breadcrumb component
     * @param value value of start level to be set
     * @throws InterruptedException
     */
    public void setStartLevelValue(String value) throws InterruptedException {
        $(startLevel).setValue(value);
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
    }

    /**
     * Check if start level is invalid
     * @return
     */
    public boolean checkInvalidStartLevel() {
        return $(startLevel + ".is-invalid").exists();
    }

}
