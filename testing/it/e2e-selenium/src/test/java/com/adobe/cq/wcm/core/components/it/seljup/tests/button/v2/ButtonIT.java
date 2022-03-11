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

package com.adobe.cq.wcm.core.components.it.seljup.tests.button.v2;

import java.util.concurrent.TimeoutException;

import com.adobe.cq.wcm.core.components.it.seljup.util.components.button.ButtonEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_BUTTON_V2;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group2")
public class ButtonIT extends  com.adobe.cq.wcm.core.components.it.seljup.tests.button.v1.ButtonIT {

    private void setupResources() {
        buttonRT = RT_BUTTON_V2;
        linkPropertyName = "linkURL";
    }

    @BeforeEach
    public void setupBefore() throws Exception {
        setupResources();
        setup();
    }

    /**
     * Test: Set button link with target
     *
     * 1. open the edit dialog
     * 2. set the button link
     * 3. set the button link target
     * 4. close the edit dialog
     * 5. verify the button is an anchor tag with the correct href attribute and link target
     */
    @Test
    @DisplayName("Test: Set button link with target")
    void testSetLinkWithTarget() throws TimeoutException, InterruptedException {
        String link = "https://www.adobe.com";
        String target = "_blank";
        ButtonEditDialog buttonEditDialog = getButtonEditDialog();
        buttonEditDialog.clickLinkTarget();
        buttonEditDialog.setLinkField(link, linkPropertyName);
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(button.checkLinkPresentWithTarget(link, target),"Button with link " + link + " and target "+ target + " should be present");
    }
}
