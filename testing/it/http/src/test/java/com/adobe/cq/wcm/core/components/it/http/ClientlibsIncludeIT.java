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

package com.adobe.cq.wcm.core.components.it.http;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.testing.clients.ClientException;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.junit.rules.ErrorCollector;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.junit.assertion.GraniteAssert;
import com.adobe.cq.testing.junit.rules.CQAuthorPublishClassRule;
import com.adobe.cq.testing.junit.rules.CQRule;

@Category({IgnoreOnCloud.class})
public class ClientlibsIncludeIT {

    @ClassRule
    public static final CQAuthorPublishClassRule cqBaseClassRule = new CQAuthorPublishClassRule();

    @Rule
    public CQRule cqBaseRule = new CQRule(cqBaseClassRule.authorRule, cqBaseClassRule.publishRule);

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    private static CQClient adminAuthor;
    private String testPage = "/content/core-components/clientlibs-include-page";
    private final static String REGEX_SCRIPT_ELEMENT = "<script async crossorigin=\"anonymous\" onload=\"console.log..\".*src=\".*/etc.clientlibs/core/wcm/tests/components/clientlibs-include/clientlibs/site.*.js\"></script>";
    private final static String REGEX_LINK_ELEMENT = "<link media=\"print\" rel=\"stylesheet\" href=\".*/etc.clientlibs/core/wcm/tests/components/clientlibs-include/clientlibs/site.*.css\" type=\"text/css\">";

    @BeforeClass
    public static void beforeClass() {
        adminAuthor = cqBaseClassRule.authorRule.getAdminClient(CQClient.class);
    }

    @Test
    public void testJsInclude() throws ClientException {
        String content = adminAuthor.doGet(testPage + ".includejs.html", 200).getContent();
        Assert.assertFalse("The html should not contain any <link> element", StringUtils.contains("<link ", content));
        GraniteAssert.assertRegExFind("Incorrect script and/or script attributes", content, REGEX_SCRIPT_ELEMENT);
    }

    @Test
    public void testCssInclude() throws ClientException {
        String content = adminAuthor.doGet(testPage + ".includecss.html", 200).getContent();
        Assert.assertFalse("The html should not contain any <script> element", StringUtils.contains("<script ", content));
        GraniteAssert.assertRegExFind("Incorrect link and/or link attributes", content, REGEX_LINK_ELEMENT);
    }

    @Test
    public void testAllInclude() throws ClientException {
        String content = adminAuthor.doGet(testPage + ".includeall.html", 200).getContent();
        GraniteAssert.assertRegExFind("Incorrect script and/or script attributes", content, REGEX_SCRIPT_ELEMENT);
        GraniteAssert.assertRegExFind("Incorrect link and/or link attributes", content, REGEX_LINK_ELEMENT);
    }

}
