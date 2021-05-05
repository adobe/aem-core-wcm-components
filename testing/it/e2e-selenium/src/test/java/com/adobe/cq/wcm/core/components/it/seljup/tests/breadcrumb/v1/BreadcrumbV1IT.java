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

package com.adobe.cq.wcm.core.components.it.seljup.tests.breadcrumb.v1;

import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.components.breadcrumb.v1.BreadcrumbItems;
import com.adobe.cq.wcm.core.components.it.seljup.tests.breadcrumb.BreadcrumbTests;
import java.util.concurrent.TimeoutException;

import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("group2")
public class BreadcrumbV1IT extends AuthorBaseUITest {

    protected BreadcrumbTests breadcrumbTests;

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        breadcrumbTests = new BreadcrumbTests();
        breadcrumbTests.setup(adminClient, Commons.rtBreadcrumb_v1, rootPage, defaultPageTemplate, "/core/wcm/components/breadcrumb/v1/breadcrumb/clientlibs/site.css", new BreadcrumbItems());
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        breadcrumbTests.cleanup(adminClient);
    }

    /**
     * Test: Set the Hide Current flag
     */
    @Test
    @DisplayName("Test: Set the Hide Current flag")
    public void testHideCurrent() throws TimeoutException, InterruptedException {
        breadcrumbTests.testHideCurrent();
    }

    /**
     * Test: Set the Show Hidden flag
     */
    @Test
    @DisplayName("Test: Set the Show Hidden flag")
    public void testShowHidden() throws InterruptedException, ClientException, TimeoutException {
        breadcrumbTests.testShowHidden(adminClient);
    }

    /**
     * Test: Change the start level
     */
    @Test
    @DisplayName("Test: Change the start level")
    public void changeStartLevel() throws InterruptedException, TimeoutException {
        breadcrumbTests.changeStartLevel();
    }

    /**
     * Test: Set the start level to lowest allowed value of 0.
     * This shouldn't render anything since level 0 is not a valid page.
     */
    @Test
    @DisplayName("Test: Set the start level to invalid value of 0.")
    public void setZeroStartLevel() throws InterruptedException, TimeoutException {
        breadcrumbTests.setZeroStartLevel();
    }

    /**
     * Test: Set the start level to the highest possible value 100.
     * This shouldn't render anything since level 100 is higher the the current's page level.
     */
    @Test
    @DisplayName("Test: Set the start level to the highest possible value 100")
    public void set100StartLevel() throws InterruptedException, TimeoutException {
        breadcrumbTests.set100StartLevel();
    }

}
