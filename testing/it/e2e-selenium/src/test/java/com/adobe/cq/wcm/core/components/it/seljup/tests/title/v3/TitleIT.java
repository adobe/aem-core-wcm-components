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

package com.adobe.cq.wcm.core.components.it.seljup.tests.title.v3;

import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group3")
public class TitleIT extends com.adobe.cq.wcm.core.components.it.seljup.tests.title.v2.TitleIT {

    public void setupResources() {
        clientlibs = Commons.CLIENTLIBS_TITLE_V3;
        titleRT = Commons.RT_TITLE_V3;
    }

    /**
     * Test: set link and target on title
     *
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: set link and target on title")
    public void testSetLinkWithTarget() throws TimeoutException, InterruptedException {
        Commons.openEditDialog(editorPage, cmpPath);
        title.getEditDialog().setLinkURL(redirectPage);
        title.getEditDialog().clickLinkTarget();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        Commons.saveConfigureDialog();
        Commons.closeSidePanel();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        String link = (contextPath != null)? contextPath + redirectPage + ".html": redirectPage + ".html";
        String target = "_blank";
        assertTrue(title.checkLinkPresentWithTarget(link, target),"Title with link " + link + " and target "+ target + " should be present");
    }

}
