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

package com.adobe.cq.wcm.core.components.internal.models.v1;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Video;
import com.day.cq.rewriter.linkchecker.Link;
import com.day.cq.rewriter.linkchecker.LinkChecker;
import com.day.cq.rewriter.linkchecker.LinkValidity;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
public class VideoImplTest {

    private static final String TEST_BASE = "/video";
    private static final String TEST_ROOT_PAGE = "/content/video";
    private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/videoComponent";
    private static final String TEST_EXPORTER_NO_FILEREFERENCE = "/jcr:content/videoComponent-no-fileReference";
    private static final String VIDEO_POSTER_IMAGE_REFERENCE = "/content/dam/core-components-examples/library/aem-corecomponents-logo.svg";
    private static final String VIDEO_FILE_REFERENCE = "/content/dam/some/test/video/sample.mp4";
    private static final String VIDEO_RESOURCE_TYPE = "core/wcm/components/video/v1/video";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    private Link linkMock;

    private Video video;

    protected String testBase;
    protected String resourceType;

    @BeforeEach
    protected void setUp() {
        resourceType = VideoImpl.RESOURCE_TYPE;
        LinkChecker checker = Mockito.spy(LinkChecker.class);
        context.registerService(LinkChecker.class, checker);
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, TEST_ROOT_PAGE);
        video = getVideoUnderTest();
        assert video != null;
        linkMock = Mockito.mock(Link.class);
        Mockito.when(checker.getLink(Mockito.anyString(), Mockito.any())).thenReturn(linkMock);
        Mockito.when(linkMock.getValidity()).thenReturn(LinkValidity.VALID);
    }

    @Test
    @DisplayName("Video Component - Test video image reference")
    protected void testPosterImageReference() {
        assertEquals(VIDEO_POSTER_IMAGE_REFERENCE, video.getPosterImageReference());
        Utils.testJSONExport(video, Utils.getTestExporterJSONPath(TEST_BASE, TEST_ROOT_PAGE_GRID));
    }

    @Test
    @DisplayName("Video Component - Test video file reference")
    protected void testFileReference() {
        assertEquals(VIDEO_FILE_REFERENCE, video.getFileReference());
        Utils.testJSONExport(video, Utils.getTestExporterJSONPath(TEST_BASE, TEST_ROOT_PAGE_GRID));
    }

    @Test
    @DisplayName("Video Component - Video file reference is not valid test")
    protected void testFileReferenceIsNotValid() {
        Mockito.when(linkMock.getValidity()).thenReturn(LinkValidity.INVALID);
        assertNull(video.getFileReference());
        Utils.testJSONExport(video, Utils.getTestExporterJSONPath(TEST_BASE, TEST_EXPORTER_NO_FILEREFERENCE));
    }

    @Test
    @DisplayName("Video Component - Test video autoplay enabled")
    protected void testVideoAutoplayEnabled() {
        assertTrue(video.isAutoplayEnabled());
        Utils.testJSONExport(video, Utils.getTestExporterJSONPath(TEST_BASE, TEST_ROOT_PAGE_GRID));
    }

    @Test
    @DisplayName("Video Component - Test is loop enabled")
    protected void testIsLoopEnabled() {
        assertTrue(video.isLoopEnabled());
        Utils.testJSONExport(video, Utils.getTestExporterJSONPath(TEST_BASE, TEST_ROOT_PAGE_GRID));
    }

    @Test
    @DisplayName("Video Component - Test get exported type")
    protected void testVideoExportedType() {
        assertEquals(VIDEO_RESOURCE_TYPE, video.getExportedType());
        Utils.testJSONExport(video, Utils.getTestExporterJSONPath(TEST_BASE, TEST_ROOT_PAGE_GRID));
    }

    @Test
    @DisplayName("Video Component - Test hide control property")
    protected void testVideoHideControl() {
        assertTrue(video.isHideControl());
        Utils.testJSONExport(video, Utils.getTestExporterJSONPath(TEST_BASE, TEST_ROOT_PAGE_GRID));
    }

    protected Video getVideoUnderTest(Object... properties) {
        Utils.enableDataLayer(context, true);
        final Resource resource = context.currentResource(TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID);
        if (resource != null && properties != null) {
            context.contentPolicyMapping(resource.getResourceType(), properties);
        }
        return context.request().adaptTo(Video.class);
    }

}
