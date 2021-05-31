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

package com.adobe.cq.wcm.core.components.it.seljup.tests.page.v2;

import com.adobe.cq.testing.selenium.pageobject.cq.sites.PropertiesPage;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralCheckbox;
import com.adobe.cq.testing.selenium.pagewidgets.cq.tabs.AdvancedTab;
import com.adobe.cq.testing.selenium.pagewidgets.cq.tabs.BlueprintTab;
import com.adobe.cq.wcm.core.components.it.seljup.categories.IgnoreOn65;
import com.adobe.cq.wcm.core.components.it.seljup.components.page.v1.Page;
import com.adobe.cq.wcm.core.components.it.seljup.constant.CoreComponentConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group3")
public class PageIT extends com.adobe.cq.wcm.core.components.it.seljup.tests.page.v1.PageIT {

    private static String configuration = "/conf/core-components";

    public void setupResources() {
        this.pageRT =  "core/wcm/tests/components/test-page-v2";
        this.segmentPath = "/conf/we-retail/settings/wcm/segments";
    }

    /**
     * Before Test Case
     */
    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setupResources();
        // create the test page
        testPage = Commons.createPage(adminClient, Commons.template, rootPage, "testPage", pageTitle, pageRT, "Test Page", 200);
        page = new Page();
    }

    /**
     * Test: Check the Advanced Configuration option of a page properties.
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Check the Advanced Configuration option of a page properties.")
    public void testdvancedConfigurationPageProperties() throws InterruptedException {
        // Open properties page
        PropertiesPage propertiesPage = new PropertiesPage(testPage);
        propertiesPage.open();

        // open the Advanced tab
        AdvancedTab advancedTab = propertiesPage.clickTab("advanced", AdvancedTab.class);

        // Uncheck the config inheritance
        CoralCheckbox advanceConfigInheritance = advancedTab.advanceConfigInheritance();
        if(advanceConfigInheritance.isChecked()) {
            advanceConfigInheritance.click();
        }
        // set the configuration
        page.setAdvanceConfig(configuration);

        // save the configuration and open again the page property
        propertiesPage.saveAndClose();
        Commons.webDriverWait(CoreComponentConstants.WEBDRIVER_WAIT_TIME_MS);
        propertiesPage.open();
        advancedTab = propertiesPage.clickTab("advanced", AdvancedTab.class);

        // check the configuration
        assertTrue(page.getAdvanceConfig().equals(configuration), "Advance configuration should be set");
    }

    /**
     * Test: Check the Blueprint options of a page properties.
     *
     * @throws ClientException
     * @throws InterruptedException
     */
    @Category(IgnoreOn65.class)
    @Test
    @DisplayName("Test: Check the Blueprint options of a page properties.")
    public void testBlueprintPageProperties() throws ClientException, InterruptedException {
        // create the live copy page, store page path in 'testLiveCopyPagePath'
        String testLiveCopyPagePath = Commons.createLiveCopy(adminClient, testPage, rootPage, "testLiveCopy", "testLiveCopy", 200);

        // Open properties page
        PropertiesPage propertiesPage = new PropertiesPage(testPage);
        propertiesPage.open();

        // open the Blueprint tab
        BlueprintTab blueprintTab = propertiesPage.clickTab("blueprint", BlueprintTab.class);

        BlueprintTab.RolloutDialog rolloutDialog = blueprintTab.rollout();
        Commons.webDriverWait(CoreComponentConstants.WEBDRIVER_WAIT_TIME_MS);
        // check livecopy is present for rollout
        assertTrue(rolloutDialog.numberOfLiveCopies() == 1, "There should be 1 livecopy");
        assertTrue(rolloutDialog.isLiveCopySelected(testLiveCopyPagePath), "Livecopy should be selected");

        // Check cancel rollout
        rolloutDialog.close();

        // Open Blueprint tab
        Commons.webDriverWait(CoreComponentConstants.WEBDRIVER_WAIT_TIME_MS);
        propertiesPage.clickTab("blueprint", BlueprintTab.class);

        rolloutDialog = blueprintTab.rollout();

        // check rollout now
        rolloutDialog.rolloutNow();

        //Delete the created livecopy page
        adminClient.deletePageWithRetry(testLiveCopyPagePath, true,false, CoreComponentConstants.TIMEOUT_TIME_MS, CoreComponentConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
    }

}
