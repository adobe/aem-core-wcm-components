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
package com.adobe.cq.wcm.core.components.it.http;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.junit.rules.CQAuthorPublishClassRule;
import com.adobe.cq.testing.junit.rules.CQRule;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.testing.clients.ClientException;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.util.Arrays;

public class ImageIT {
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
    public void testResponsivePage() throws ClientException {
        String content = adminAuthor.doGet("/content/core-components/responsive-page.html", 200).getContent();
        Elements html = Jsoup.parse(content).select("html");
        testImages(html);
    }

    @Test
    public void testSimplePage() throws ClientException {
        String content = adminAuthor.doGet("/content/core-components/simple-page.html", 200).getContent();
        Elements html = Jsoup.parse(content).select("html");
        testImages(html);
    }

    public void testImages(Elements html) {
        // Find all image components on page
        html.select("[data-cmp-is=image]").stream().forEach(img -> {
            String imageSource = img.attr("data-cmp-src");
            // Check all defined widths
            String imageWidths = img.attr("data-cmp-widths");
            if (StringUtils.isNotEmpty(imageWidths)) {
                String[] widths = imageWidths.split(",");
                Arrays.stream(widths).forEach(width -> {
                    try {
                        String imagePath = imageSource.replace("{.width}", "." + width);
                        adminAuthor.doGet(adminAuthor.getPath(imagePath).getPath(), 200);
                    } catch (ClientException e) {
                        // Collect errors so we don't stop after the first
                        collector.addError(e);
                    }
                });
            }
        });
    }
}
