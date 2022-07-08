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
package com.adobe.cq.wcm.core.components.it.http.experiencefragment.v2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.sling.testing.clients.ClientException;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ErrorCollector;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.junit.assertion.GraniteAssert;
import com.adobe.cq.testing.junit.rules.CQAuthorPublishClassRule;
import com.adobe.cq.testing.junit.rules.CQRule;
import com.adobe.cq.wcm.core.components.it.http.IgnoreOn64;
import com.adobe.cq.wcm.core.components.it.http.IgnoreOn65;
import com.google.common.collect.ImmutableList;

public class ExperienceFragmentIT {

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
    @Category({IgnoreOn64.class, IgnoreOn65.class})
    public void testAppliedStylesJson() throws ClientException, IOException {
        String expectedJson = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("xf-page.model.json"),
                StandardCharsets.UTF_8);
        String json = adminAuthor.doGet("/content/core-components/simple-page/test-page-xf-component.model.json", 200).getContent();
        GraniteAssert.assertJsonEquals(expectedJson, json, ImmutableList.of("lastModifiedDate", "repo:modifyDate", "xdm:language",
                "language", "components", "repo:path"));
    }

    @Test
    @Category({IgnoreOn64.class, IgnoreOn65.class})
    public void testDuplicateKeyIssueJson() throws ClientException, IOException {
        String expectedJson = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("simple-page.model.json"),
                StandardCharsets.UTF_8);
        String json = adminAuthor.doGet("/content/core-components/simple-page.model.json", 200).getContent();
        GraniteAssert.assertJsonEquals(expectedJson, json, ImmutableList.of("lastModifiedDate", "repo:modifyDate", "xdm:language", "xdm:text",
                "language", "components", "text", "repo:path"));
    }
}
