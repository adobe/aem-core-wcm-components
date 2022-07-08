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

package com.adobe.cq.wcm.core.components.it.seljup;

import com.adobe.cq.testing.selenium.junit.extensions.TestContentExtension;
import com.adobe.cq.testing.selenium.utils.TestContentBuilder;
import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.selenium.junit.annotations.Author;
import com.adobe.cq.testing.selenium.UIAbstractTest;
import com.adobe.cq.testing.selenium.pageobject.granite.LoginPage;
import com.adobe.cq.testing.selenium.utils.DisableTour;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static com.adobe.cq.testing.selenium.Constants.GROUPID_CONTENT_AUTHORS;
import static com.adobe.cq.testing.selenium.Constants.RUNMODE_AUTHOR;
import static com.adobe.cq.testing.selenium.pagewidgets.Helpers.setAffinityCookie;

@Execution(ExecutionMode.CONCURRENT)
public abstract class AdminBaseUITest extends UIAbstractTest {

    @RegisterExtension
    protected TestContentExtension testContentAuthor = new TestContentExtension(RUNMODE_AUTHOR);
    protected String rootPage;
    protected CQClient adminClient;
    protected String defaultPageTemplate;
    private String randomPassword = RandomStringUtils.randomAlphabetic(8);

    @BeforeEach
    public void loginBeforeEach(@Author final CQClient adminAuthor, final TestContentBuilder testContentBuilder, final URI baseURI)
        throws ClientException, InterruptedException, IOException, TimeoutException {
        testContentBuilder.withUser(randomPassword, Arrays.asList(GROUPID_CONTENT_AUTHORS, "workflow-users"));
        testContentBuilder.build();

        adminClient = testContentBuilder.getClient();

        rootPage = testContentBuilder.getContentRootPath();

        defaultPageTemplate = testContentBuilder.getDefaultPageTemplatePath();

        // make sure to disable all tours on this new user
        new DisableTour(adminAuthor).disableDefaultTours();

        LoginPage loginPage = new LoginPage(baseURI);
        loginPage.loginAs(adminClient.getUser(), adminClient.getPassword());
        setAffinityCookie(adminClient);
    }

}
