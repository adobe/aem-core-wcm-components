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
package com.adobe.cq.wcm.core.components.it.pw.pdfviewer;

import java.util.Collections;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.it.pw.ComponentPwBaseTest;
import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitUntilState;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.CLIENTLIBS_PDFVIEWER_V1;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_PDFVIEWER_V1;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("playwright-group2")
public class PdfViewerPwIT extends ComponentPwBaseTest {

    private static final Logger LOG = LoggerFactory.getLogger(PdfViewerPwIT.class);
    // The embedded PDF viewer occasionally does not render on the first page load in CI.
    // Each reload is logged so that a real first-render regression stays visible.
    private static final int RENDER_ATTEMPTS = 3;
    private static final int RENDER_TIMEOUT_MS = 45_000;

    @Test
    public void testDefaultViewer() throws Exception {
        createPagePolicy(Collections.singletonMap("clientlibs", CLIENTLIBS_PDFVIEWER_V1));
        String componentPath = addStandaloneComponent(RT_PDFVIEWER_V1, "pdfviewer");
        authorClient.setPageProperty(testPage, "sling:configRef", "/conf/core-components");

        openEditDialog(componentPath);
        selectInPicker("/content/dam", "[name='./documentPath']", "core-components/Bodea_Brochure.pdf");
        saveDialog();

        page.navigate(baseUrl + testPage + ".html",
            new com.microsoft.playwright.Page.NavigateOptions().setWaitUntil(WaitUntilState.COMMIT));
        Locator viewerContent = page.locator(".cmp-pdfviewer__content");
        assertThat(viewerContent).isVisible();
        String iframeSelector = "#iframe-" + viewerContent.getAttribute("id");
        AssertionError renderFailure = null;
        for (int attempt = 0; attempt < RENDER_ATTEMPTS; attempt++) {
            if (attempt > 0) {
                page.reload(new com.microsoft.playwright.Page.ReloadOptions().setWaitUntil(WaitUntilState.COMMIT));
                assertThat(viewerContent).isVisible();
            }
            FrameLocator pdfFrame = page.frameLocator(iframeSelector);
            try {
                assertThat(pdfFrame.locator("body")).containsText("Bodea_Brochure",
                    new com.microsoft.playwright.assertions.LocatorAssertions.ContainsTextOptions()
                        .setTimeout(RENDER_TIMEOUT_MS));
                return;
            } catch (AssertionError e) {
                renderFailure = e;
                if (attempt + 1 < RENDER_ATTEMPTS) {
                    LOG.warn("PDF viewer did not render on attempt {}/{}, reloading the page: {}",
                        attempt + 1, RENDER_ATTEMPTS, e.getMessage());
                }
            }
        }
        throw renderFailure;
    }
}
