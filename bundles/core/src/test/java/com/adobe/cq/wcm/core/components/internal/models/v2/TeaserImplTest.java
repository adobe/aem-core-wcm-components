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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import java.util.Objects;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.internal.servlets.AdaptiveImageServlet;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Teaser;
import com.day.cq.commons.jcr.JcrConstants;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertValidLink;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(AemContextExtension.class)
public class TeaserImplTest extends com.adobe.cq.wcm.core.components.internal.models.v1.TeaserImplTest {

    private static final String TEST_BASE = "/teaser/v2";
    private static final String TEASER_21 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-21";
    private Logger testLogger;

    @BeforeEach
    protected void setUp() throws Exception {
        testBase = TEST_BASE;
        internalSetup();
        testLogger = Utils.mockLogger(TeaserImpl.class, "LOG");
    }

    @Test
    protected void testTeaserWithTitleAndDescriptionFromActions() {
        Teaser teaser = getTeaserUnderTest(TEASER_8);
        assertTrue(teaser.isActionsEnabled(), "Expected teaser with actions");
        assertEquals(2, teaser.getActions().size(), "Expected to find two Actions");
        assertEquals("Teasers Test", teaser.getTitle());
        assertNotNull(teaser.getTitleResource());
        assertEquals("Teasers Test", teaser.getTitleResource().getValueMap().get(JcrConstants.JCR_TITLE));
        assertEquals("Teasers description", teaser.getDescription());
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(testBase, "teaser11"));
    }

    @Test
    @Override
    protected void testFullyConfiguredTeaser() {
        Teaser teaser = getTeaserUnderTest(TEASER_1);
        if (teaser.getImageResource() != null) {
            // let's verify the ValueMap wrapping here
            testImageResourceValueMap(teaser.getImageResource().getValueMap());
            testImageResourceValueMap(Objects.requireNonNull(teaser.getImageResource().adaptTo(ValueMap.class)));
            assertEquals(TEASER_1, teaser.getImageResource().getPath());
        }
        assertEquals(TITLE, teaser.getTitle());
        assertEquals(DESCRIPTION, teaser.getDescription());
        assertValidLink(teaser.getLink(), LINK);
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(testBase, "teaser1"));
    }

    @Test
    @Override
    protected void testTeaserWithoutLink() {
        Teaser teaser = getTeaserUnderTest(TEASER_4);
        // TODO: make the following assert pass
        /*
        assertThat(testLogger.getLoggingEvents(),
                hasItem(debug("Teaser component from /content/teasers/jcr:content/root/responsivegrid/teaser-4 does not define a link.")));
         */
        assertNull(teaser.getLink(), "The link should be null");
    }

    @Test
    @SuppressWarnings("deprecation")
    @Override
    protected void testTeaserWithActions() {
        Teaser teaser = getTeaserUnderTest(TEASER_7);
        assertTrue(teaser.isActionsEnabled(), "Expected teaser with actions");
        assertEquals(2, teaser.getActions().size(), "Expected to find two actions");
        ListItem action = teaser.getActions().get(0);
        assertEquals("http://www.adobe.com", action.getPath(), "Action link does not match");
        assertEquals("Adobe", action.getTitle(), "Action text does not match");
        assertEquals("http://www.adobe.com", action.getURL());
        assertValidLink(action.getLink(), "http://www.adobe.com");
        assertNotNull(action.getButtonResource());
        assertEquals("Adobe", action.getButtonResource().getValueMap().get(JcrConstants.JCR_TITLE), "Action text does not match");
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(testBase, "teaser9"));
    }

    @Test
    protected void testEmptyTeaserImageDelegatingToFeaturedImage() {
        Teaser teaser = getTeaserUnderTest(TEASER_20);
        Resource imageResource = teaser.getImageResource();
        ValueMap imageProperties = imageResource.getValueMap();
        String linkURL = imageProperties.get("linkURL", String.class);
        String fileReference = imageProperties.get("fileReference", String.class);
        assertEquals("/content/teasers/jcr:content/cq:featuredimage", imageResource.getPath(), "image resource: path");
        assertEquals("core/wcm/components/image/v3/image", imageResource.getResourceType(), "image resource: resource type");
        assertEquals("/content/teasers", linkURL, "image resource: linkURL");
        assertEquals("/content/dam/core/images/Adobe_Systems_logo_and_wordmark.png", fileReference, "image resource: fileReference");
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(testBase, "teaser20"));
    }

    @Test
    protected void testInvalidDelegatingTeaser() {
        Teaser teaser = getTeaserUnderTest(TEASER_21);
        ListItem action = teaser.getActions().get(0);
        assertNull(action.getButtonResource());
        verify(testLogger, times(1)).error("no buttonDelegate property set on component " +
                "/apps/core/wcm/components/invalidTeaser/v2/invalidTeaser");
    }

}
