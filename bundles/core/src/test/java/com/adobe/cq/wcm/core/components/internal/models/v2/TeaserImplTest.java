/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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

import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertInvalidLink;
import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertValidLink;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static uk.org.lidalia.slf4jtest.LoggingEvent.debug;

import java.util.Objects;

import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Teaser;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
public class TeaserImplTest extends com.adobe.cq.wcm.core.components.internal.models.v1.TeaserImplTest {

    private static final String TEST_BASE = "/teaser/v2";

    @BeforeEach
    protected void setUp() {
        testBase = TEST_BASE;
        internalSetup();
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
        assertThat(testLogger.getLoggingEvents(),
                hasItem(debug("Teaser component from /content/teasers/jcr:content/root/responsivegrid/teaser-4 does not define a link.")));
        assertInvalidLink(teaser.getLink());
    }

    @Test
    @SuppressWarnings("deprecation")
    @Override
    protected void testTeaserWithActions() {
        Teaser teaser = getTeaserUnderTest(TEASER_7);
        assertTrue("Expected teaser with actions", teaser.isActionsEnabled());
        assertEquals("Expected to find two actions", 2, teaser.getActions().size());
        ListItem action = teaser.getActions().get(0);
        assertEquals("Action link does not match", "http://www.adobe.com", action.getPath());
        assertEquals("Action text does not match", "Adobe", action.getTitle());
        assertEquals("http://www.adobe.com", action.getURL());
        assertValidLink(action.getLink(), "http://www.adobe.com");
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(testBase, "teaser9"));
    }

}
