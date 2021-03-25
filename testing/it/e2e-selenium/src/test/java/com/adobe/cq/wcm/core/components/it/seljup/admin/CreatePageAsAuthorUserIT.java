/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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

package com.adobe.cq.wcm.core.components.it.seljup.admin;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.client.security.CreateUserRule;
import com.adobe.cq.testing.junit.rules.CQAuthorClassRule;
import com.adobe.cq.testing.junit.rules.CQAuthorPublishClassRule;
import com.adobe.cq.testing.junit.rules.CQRule;
import com.adobe.cq.testing.junit.rules.EmptyPage;
import org.apache.sling.testing.clients.ClientException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.jupiter.api.*;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

import static java.util.concurrent.TimeUnit.MINUTES;

@Tag("set1")
public class CreatePageAsAuthorUserIT {

    @BeforeEach
    public void setupBefore() throws ClientException {
    }

    @AfterEach
    public void cleanup() throws ClientException {

    }

    private static final int TIMEOUT = (int) MINUTES.toMillis(2);
    public static final String CONTENT_AUTHORS_GROUP = "content-authors";

    @ClassRule
    public static CQAuthorClassRule cqBaseClassRule = new CQAuthorClassRule();

    public CQRule cqBaseRule = new CQRule(cqBaseClassRule.authorRule);
    public CreateUserRule userRule = new CreateUserRule(cqBaseClassRule.authorRule, CONTENT_AUTHORS_GROUP);
    public EmptyPage pageRule = new EmptyPage(userRule.getClientSupplier());

    @Rule
    public TestRule cqRuleChain = RuleChain.outerRule(cqBaseRule).around(userRule).around(pageRule);

    public CQClient authorClient = null;


    /**
     * Verifies that a user belonging to the "Authors" group can create a page
     *
     * @throws InterruptedException if the wait was interrupted
     */
    //@Test
    public void testCreatePageAsAuthor() throws InterruptedException {
//        authorClient = userRule.getClient();
        System.out.println("Path is " + cqBaseClassRule.authorRule.getAdminClient());
//        System.out.println("Ankit Username " + cqBaseClassRule.authorRule.getAdminClient().getUser() + " Password " + cqBaseClassRule.authorRule.getAdminClient().getPassword());
        // This shows that it exists for the author user
      //  Assert.assertTrue(String.format("Page %s not created within %s timeout", pageRule.getPath(), TIMEOUT), userRule.getClient().pageExistsWithRetry(pageRule.getPath(), TIMEOUT));
    }

}
