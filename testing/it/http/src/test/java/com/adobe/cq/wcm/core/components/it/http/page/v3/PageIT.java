/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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
package com.adobe.cq.wcm.core.components.it.http.page.v3;

import java.util.regex.Pattern;

import org.apache.sling.testing.clients.ClientException;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.junit.assertion.GraniteAssert;
import com.adobe.cq.testing.junit.rules.CQAuthorPublishClassRule;
import com.adobe.cq.testing.junit.rules.CQRule;

public class PageIT {

    @ClassRule
    public static final CQAuthorPublishClassRule cqBaseClassRule = new CQAuthorPublishClassRule();

    @Rule
    public CQRule cqBaseRule = new CQRule(cqBaseClassRule.authorRule, cqBaseClassRule.publishRule);

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    static CQClient adminAuthor;
    static CQClient adminPublish;

    @BeforeClass
    public static void beforeClass() {
        adminAuthor = cqBaseClassRule.authorRule.getAdminClient(CQClient.class);
        adminPublish = cqBaseClassRule.publishRule.getAdminClient(CQClient.class);
    }

    @Test
    public void testResponsiveGridStructure() throws ClientException {
        String content = adminAuthor.doGet("/content/core-components/simple-page/simple-page-v3.html", 200).getContent();
        GraniteAssert.assertRegExFind(content, "Basic Title");
    }

    @Test
    public void testHeadJSLibs() throws ClientException {
        String content = adminAuthor.doGet("/content/core-components/simple-page/simple-page-v3.html", 200).getContent();
        GraniteAssert.assertRegExFind(content, "<script src=\".*/etc.clientlibs/foundation/clientlibs/jquery.*.js\"></script>");
    }

    @Test
    public void testIsClientlibsSync() throws ClientException {
        String message = "The clientlib script should not contain the async attribute";
        // async loading is disabled in the page policy
        String content = adminAuthor.doGet("/content/core-components/simple-page/simple-page-v3-clientlibs-sync.html", 200).getContent();
        Pattern pattern = Pattern.compile("<script src=\".*/etc.clientlibs/core/wcm/components/accordion/v1/accordion/clientlibs/site.*.js\"></script>");
        GraniteAssert.assertRegExFind(message, content, pattern);
    }

    @Test
    public void testIsClientlibsAsync() throws ClientException {
        String message = "The clientlib script should contain the async attribute";
        // async loading is enabled in the page policy
        String content = adminAuthor.doGet("/content/core-components/simple-page/simple-page-v3-clientlibs-async.html", 200).getContent();
        Pattern pattern = Pattern.compile("<script async src=\".*/etc.clientlibs/core/wcm/components/accordion/v1/accordion/clientlibs/site.*.js\"></script>");
        GraniteAssert.assertRegExFind(message, content, pattern);
    }

}
