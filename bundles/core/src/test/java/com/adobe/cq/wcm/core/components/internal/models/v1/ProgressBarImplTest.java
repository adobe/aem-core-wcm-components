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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.ProgressBar;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
public class ProgressBarImplTest {

    private static final String ROOT = "/content";
    private static final String TEST_BASE = "/progressbar";
    private static final String TEST_ROOT_PAGE = "/content/progressbar";
    private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    private static final String PROGRESSBAR_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/progressbar-1";
    private static final String PROGRESSBAR_2 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/progressbar-2";
    private static final String PROGRESSBAR_3 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/progressbar-3";
    private static final String PROGRESSBAR_4 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/progressbar-4";
    private static final String TEST_APPS_ROOT = "/apps/core/wcm/components";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, ROOT);
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_APPS_JSON, TEST_APPS_ROOT);
    }

    @Test
    public void testCompleted() {
        ProgressBar progressBar = getComponentUnderTest(PROGRESSBAR_1);
        assertEquals(75, progressBar.getCompleted());
        assertEquals(25, progressBar.getRemaining());
        Utils.testJSONExport(progressBar, Utils.getTestExporterJSONPath(TEST_BASE, PROGRESSBAR_1));
    }

    @Test
    public void testDefault() {
        ProgressBar progressBar = getComponentUnderTest(PROGRESSBAR_2);
        assertEquals(0, progressBar.getCompleted());
        assertEquals(100, progressBar.getRemaining());
    }

    @Test
    public void testTooSmall() {
        // completed < 0
        ProgressBar progressBar = getComponentUnderTest(PROGRESSBAR_3);
        assertEquals(0, progressBar.getCompleted());
        assertEquals(100, progressBar.getRemaining());
    }

    @Test
    public void testTooLarge() {
        // completed > 100
        ProgressBar progressBar = getComponentUnderTest(PROGRESSBAR_4);
        assertEquals(100, progressBar.getCompleted());
        assertEquals(0, progressBar.getRemaining());
    }

    protected ProgressBar getComponentUnderTest(String resourcePath) {
        context.currentResource(resourcePath);
        context.request().setContextPath("/core");
        MockSlingHttpServletRequest request = context.request();
        return request.adaptTo(ProgressBar.class);
    }
}
