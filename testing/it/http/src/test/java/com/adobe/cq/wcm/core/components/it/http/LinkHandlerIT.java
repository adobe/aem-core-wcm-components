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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.osgi.OsgiConsoleClient;
import org.apache.sling.testing.clients.util.poller.Polling;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.junit.rules.CQAuthorPublishClassRule;
import com.adobe.cq.testing.junit.rules.CQRule;
import com.adobe.cq.testing.junit.rules.OsgiConfigRestoreRule;

public class LinkHandlerIT {

    @ClassRule
    public static final CQAuthorPublishClassRule cqBaseClassRule = new CQAuthorPublishClassRule();

    @Rule
    public CQRule cqBaseRule = new CQRule(cqBaseClassRule.authorRule, cqBaseClassRule.publishRule);

    @Rule
    public OsgiConfigRestoreRule osgiConfigRestoreRule = new OsgiConfigRestoreRule(cqBaseClassRule.authorRule, RESOURCE_RESOLVER_FACTORY_PID);

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    public static final String RESOURCE_RESOLVER_FACTORY_PID = "org.apache.sling.jcr.resource.internal.JcrResourceResolverFactoryImpl";

    static CQClient adminAuthor;
    static CQClient adminPublish;

    @BeforeClass
    public static void beforeClass() {
        adminAuthor = cqBaseClassRule.authorRule.getAdminClient(CQClient.class);
        adminPublish = cqBaseClassRule.publishRule.getAdminClient(CQClient.class);
    }

    @Test
    public void testMappedLinkedTitle() throws Exception {
        configureResourceResolverFactory();
        new Polling(() -> {
            try {
                String content = adminAuthor.doGet("/content/core-components/simple-page.html", 200).getContent();
                return assertLink(content);
            } catch (ClientException e) {
                return false;
            }
        }).poll(50000, 500);
    }

    private Boolean assertLink(String content) {
        Element element = Jsoup.parse(content).select("#linked-title").select("a.cmp-title__link").first();
        if (element != null) {
            return StringUtils.equals("/simple-page/simple-subpage.html", element.attr("href"));
        }
        return false;
    }

    private void configureResourceResolverFactory() throws ClientException {
        OsgiConsoleClient consoleClient = adminAuthor.adaptTo(OsgiConsoleClient.class);
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("resource.resolver.mapping", new String[]{"/content/core-components/</", "/:/"});
        consoleClient.editConfiguration(RESOURCE_RESOLVER_FACTORY_PID, null, config, 302);
    }
}
