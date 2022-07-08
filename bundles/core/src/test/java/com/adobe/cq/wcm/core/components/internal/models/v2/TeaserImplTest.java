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

import com.adobe.cq.wcm.core.components.models.Component;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Teaser;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertValidLink;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
public class TeaserImplTest extends com.adobe.cq.wcm.core.components.internal.models.v1.TeaserImplTest {

    private static final String TEST_BASE = "/teaser/v2";
    private static final String TEASER_25 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-25";

    @BeforeEach
    protected void setUp() {
        testBase = TEST_BASE;
        internalSetup();
    }

    @Test
    protected void testTeaserWithTitleAndDescriptionFromActions() {
        Teaser teaser = getTeaserUnderTest(TEASER_8);
        assertTrue(teaser.isActionsEnabled(), "Expected teaser with actions");
        assertEquals(2, teaser.getActions().size(), "Expected to find two Actions");
        assertEquals("Teasers Test", teaser.getTitle());
        // < and > are expected escaped, because the page properties provide only a plain text field for the page description
        assertEquals("Teasers description from &lt;page properties&gt;", teaser.getDescription());
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(testBase, "teaser11"));
    }

    @Test
    protected void testTeaserWithTitleAndDescriptionFromCurrentPage() {
        Teaser teaser = getTeaserUnderTest(TEASER_25);
        assertEquals("Teasers Test", teaser.getTitle());
        // < and > are expected escaped, because the page properties provide only a plain text field for the page description
        assertEquals("Teasers description from &lt;page properties&gt;", teaser.getDescription());
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(testBase, "teaser25"));
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
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(testBase, "teaser9"));
    }

    @Test
    protected void testEmptyTeaserImageDelegatingToFeaturedImage() {
        Teaser teaser = getTeaserUnderTest(TEASER_20);
        Resource imageResource = teaser.getImageResource();
        ValueMap imageProperties = imageResource.getValueMap();
        String linkURL = imageProperties.get("linkURL", String.class);
        String fileReference = imageProperties.get("fileReference", String.class);
        assertEquals("/content/teasers/jcr:content/root/responsivegrid/teaser-20", imageResource.getPath(), "image resource: path");
        assertEquals("core/wcm/components/image/v3/image", imageResource.getResourceType(), "image resource: resource type");
        assertEquals("/content/teasers", linkURL, "image resource: linkURL");
        assertNull(fileReference, "image resource: fileReference");
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(testBase, "teaser20"));
    }

    /**
     * Asserts that the ID property on the image resource is set to the Teaser ID + `-image`.
     */
    @Test
    protected void testTeaserImageID() {
        Teaser teaser = getTeaserUnderTest(TEASER_21);
        assertEquals(teaser.getId() + "-image", teaser.getImageResource().getValueMap().get(Component.PN_ID, String.class));
    }

    @Test
    protected void testImageFromPage_withoutLink() {
        Teaser teaser = getTeaserUnderTest(TEASER_21);
        Link link = teaser.getLink();
        assertNull(link);
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(testBase, "teaser21"));
    }

    @Test
    protected void testImageFromPage_withLink() {
        Teaser teaser = getTeaserUnderTest(TEASER_22);
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(testBase, "teaser22"));
    }

    @Test
    protected void testImageFromPage_withInvalidLink() {
        Teaser teaser = getTeaserUnderTest(TEASER_22a);
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(testBase, "teaser22a"));
    }

    @Test
    protected void testImageFromPage_withCta() {
        Teaser teaser = getTeaserUnderTest(TEASER_23);
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(testBase, "teaser23"));
    }

    @Test
    protected void testImageFromPage_withInvalidCta() {
        Teaser teaser = getTeaserUnderTest(TEASER_23a);
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(testBase, "teaser23a"));
    }

    @Test
    protected void testInheritedPageImage_fromTemplate_noLink() {
        Teaser teaser = getTeaserUnderTest(TEMPLATE_TEASER_1);
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(testBase, "template_teaser1"));
    }

    @Test
    protected void testInheritedPageImage_fromTemplate_withLink() {
        Teaser teaser = getTeaserUnderTest(TEMPLATE_TEASER_2);
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(testBase, "template_teaser2"));
    }

    @Test
    protected void testInheritedPageImage_fromTemplate_withCTAs() {
        Teaser teaser = getTeaserUnderTest(TEMPLATE_TEASER_3);
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(testBase, "template_teaser3"));
    }

    @Test
    protected void testImageFromPage_withLink_andTitleEmpty() {
        Teaser teaser = getTeaserUnderTest(TEASER_24);
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(testBase, "teaser24"));
    }

    @Test
    @Override
    protected void testTeaserWithExternalLinkFromAction() {
        Teaser teaser = getTeaserUnderTest(TEASER_7);
        assertEquals("http://www.adobe.com", teaser.getActions().get(0).getURL());
    }
}
