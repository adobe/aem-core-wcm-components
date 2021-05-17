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
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.osgi.OsgiConsoleClient;
import org.apache.sling.testing.clients.osgi.OsgiInstanceConfig;
import org.apache.sling.testing.clients.util.config.impl.InstanceConfigCacheImpl;
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

public class LinkHandlerIT {

    @ClassRule
    public static final CQAuthorPublishClassRule cqBaseClassRule = new CQAuthorPublishClassRule();

    @Rule
    public CQRule cqBaseRule = new CQRule(cqBaseClassRule.authorRule, cqBaseClassRule.publishRule);

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    private static final String RESOURCE_RESOLVER_FACTORY_PID = "org.apache.sling.jcr.resource.internal.JcrResourceResolverFactoryImpl";
    private static final int TIMEOUT = 300000;
    private static final int DELAY = 1000;

    static CQClient adminAuthor;
    static CQClient adminPublish;
    static InstanceConfigCacheImpl configs;

    @BeforeClass
    public static void beforeClass() throws Exception {
        adminAuthor = cqBaseClassRule.authorRule.getAdminClient(CQClient.class);
        adminPublish = cqBaseClassRule.publishRule.getAdminClient(CQClient.class);
        configs = new InstanceConfigCacheImpl();
    }

    @Test
    public void testMappedLinkedTitle() throws Exception {
        configs.add(new OsgiInstanceConfig(adminAuthor, RESOURCE_RESOLVER_FACTORY_PID));
        configureResourceResolverFactory();
        new Polling(getLinkCallable(adminAuthor.getUrl().getPath() + "simple-page/simple-subpage.html")).poll(TIMEOUT, DELAY);
        configs.restore();
        new Polling(getLinkCallable(adminAuthor.getUrl().getPath() + "content/core-components/simple-page/simple-subpage.html")).poll(TIMEOUT, DELAY);
    }

    private Callable<Boolean> getLinkCallable(String expectedLinkURL) {
        return () -> {
            try {
                String content = adminAuthor.doGet("/content/core-components/simple-page.html", 200).getContent();
                return assertLink(content, expectedLinkURL);
            } catch (ClientException e) {
                return false;
            }
        };
    }

    private Boolean assertLink(String content, String expectedLinkURL) {
        Element element = Jsoup.parse(content).select("#linked-title").select("a.cmp-title__link").first();
        if (element != null) {
            return StringUtils.equals(expectedLinkURL, element.attr("href"));
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
