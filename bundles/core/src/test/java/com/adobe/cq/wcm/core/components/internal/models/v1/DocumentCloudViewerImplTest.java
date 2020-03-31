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

import org.apache.sling.testing.mock.caconfig.MockContextAwareConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.DocumentCloudViewer;
import com.adobe.cq.wcm.core.config.DocumentCloud;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class DocumentCloudViewerImplTest {

    private static final String TEST_BASE = "/pdf";
    private static final String CONTENT_ROOT = "/content";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        MockContextAwareConfig.registerAnnotationClasses(context, DocumentCloud.class);
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.load().json(TEST_BASE + "/test-conf.json", "/conf/pdf/sling:configs/com.adobe.cq.wcm.core.config.DocumentCloud");
    }

    @Test
    void testPdfComponent() throws Exception {
        context.currentResource(CONTENT_ROOT + "/jcr:content/pdf");
        DocumentCloudViewer documentCloudViewer = context.request().adaptTo(DocumentCloudViewer.class);
        assertEquals("Bodea Brochure.pdf", documentCloudViewer.getFileName());
        assertEquals("https://documentcloud.adobe.com/view-sdk-demo/PDFs/Bodea Brochure.pdf", documentCloudViewer.getFileUrl());
        assertEquals("1234", documentCloudViewer.getClientId());
        Utils.testJSONExport(documentCloudViewer, Utils.getTestExporterJSONPath(TEST_BASE, "pdf"));
    }

}