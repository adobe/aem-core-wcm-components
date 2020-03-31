package com.adobe.cq.wcm.core.components.internal.models.v1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.DocumentCloudViewer;
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
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
    }

    @Test
    void testPdfComponent() throws Exception {
        context.currentResource(CONTENT_ROOT + "/pdf");
        DocumentCloudViewer documentCloudViewer = context.request().adaptTo(DocumentCloudViewer.class);
        assertEquals("Bodea Brochure.pdf", documentCloudViewer.getFileName());
        assertEquals("https://documentcloud.adobe.com/view-sdk-demo/PDFs/Bodea Brochure.pdf", documentCloudViewer.getFileUrl());
        assertNull(documentCloudViewer.getClientId());
    }

}