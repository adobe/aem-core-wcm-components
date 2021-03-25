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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.client.security.CreateUserRule;
import com.adobe.cq.testing.junit.rules.*;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.SlingClient;
import org.apache.sling.testing.junit.rules.instance.Instance;
import org.junit.ClassRule;
import org.junit.Rule;

import org.junit.jupiter.api.*;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.openqa.selenium.Keys;


import static com.adobe.cq.testing.junit.rules.CQClassRule.DEFAULT_AUTHOR_CONFIG;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("set1")
public class DummyIT extends AuthorBaseUITest {
//    {

//    private static final int TIMEOUT = (int) MINUTES.toMillis(2);
//    public static final String CONTENT_AUTHORS_GROUP = "content-authors";
//
//    @ClassRule
//    public static CQAuthorClassRule cqBaseClassRule = new CQAuthorClassRule();
//
//    public CQRule cqBaseRule = new CQRule(cqBaseClassRule.authorRule);
//    public CreateUserRule userRule = new CreateUserRule(cqBaseClassRule.authorRule, CONTENT_AUTHORS_GROUP);
//    public EmptyPage pageRule = new EmptyPage(userRule.getClientSupplier());
//
//    @Rule
//    public TestRule cqRuleChain = RuleChain.outerRule(cqBaseRule).around(userRule).around(pageRule);

//        public SlingClient authorClient = null;
//
//        private static final int TIMEOUT = (int) MINUTES.toMillis(2);
//        public static final String CONTENT_AUTHORS_GROUP = "content-authors";
//
//        @ClassRule
//        public static CQAuthorClassRule cqBaseClassRule = new CQAuthorClassRule();
//
//        public CQRule cqBaseRule = new CQRule(cqBaseClassRule.authorRule);
//        public CreateUserRule userRule = new CreateUserRule(cqBaseClassRule.authorRule, CONTENT_AUTHORS_GROUP);
//        public EmptyPage pageRule = new EmptyPage(userRule.getClientSupplier());
//
//        @Rule
//        public TestRule cqRuleChain = RuleChain.outerRule(cqBaseRule).around(userRule).around(pageRule);

//        /** ExistingInstance to reserve an Author */
//        public final Instance authorRule = ClassRuleUtils.newInstanceRule(false)
//            .withRunMode("author");


        @BeforeEach
        public void setupBefore() throws ClientException {
        }

        @AfterEach
        public void cleanup() throws ClientException {

        }

    @Test
    @DisplayName("First Test")
    void testFirst() throws ClientException {
//        System.out.println("Page path is " + pageRule.getPath());
//        authorClient = cqBaseClassRule.authorRule.getAdminClient();
//        System.out.println("Ankit Username " + authorClient.getUser() + " Password " + authorClient.getPassword());

        assertTrue(StringUtils.isNotEmpty("abc"), "It did not worked");
    }



}
