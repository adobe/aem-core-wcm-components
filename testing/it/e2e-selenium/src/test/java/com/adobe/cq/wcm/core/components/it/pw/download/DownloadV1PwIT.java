/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
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
package com.adobe.cq.wcm.core.components.it.pw.download;

import java.util.HashMap;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.pw.ComponentPwBaseTest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_DOWNLOAD_V1;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("playwright-ungrouped")
public class DownloadV1PwIT extends ComponentPwBaseTest {

    protected String resourceType() {
        return RT_DOWNLOAD_V1;
    }

    protected void assertDownloadHeaders(String assetName, String assetPath) throws Exception {
        String componentPath = addStandaloneComponent(resourceType(), "download");
        HashMap<String, String> properties = new HashMap<>();
        properties.put("fileReference", assetPath);
        properties.put("jcr:title", assetName);
        Commons.editNodeProperties(authorClient, componentPath, properties);

        page.navigate(baseUrl + testPage + ".html");
        Locator titleLink = page.locator(".cmp-download__title-link");
        assertThat(titleLink).isVisible();
        String downloadUrl = titleLink.getAttribute("href");
        if (!downloadUrl.startsWith("http")) {
            downloadUrl = baseUrl + (downloadUrl.startsWith("/") ? downloadUrl : "/" + downloadUrl);
        }

        APIResponse response = context.request().get(downloadUrl);
        assertEquals(200, response.status());
        String contentDisposition = response.headers().get("content-disposition");
        assertTrue(contentDisposition != null
            && contentDisposition.startsWith("attachment; filename=\"" + assetName + "\""),
            "Unexpected Content-Disposition: " + contentDisposition);
        response.dispose();
    }

    @Test
    public void downloadFile() throws Exception {
        assertDownloadHeaders("core-comp-test-image.jpg",
            "/content/dam/core-components/core-comp-test-image.jpg");
    }
}
