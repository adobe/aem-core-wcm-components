/*******************************************************************************
 * Copyright 2017 Adobe Systems Incorporated
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.adobe.cq.wcm.core.components.sandbox.internal.models.v2;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.sandbox.models.Image;

import static org.junit.Assert.assertEquals;

public class ImageImplTest extends com.adobe.cq.wcm.core.components.internal.models.v1.ImageImplTest {

    protected static String TEST_BASE = "/sandbox/image";
    private static final String IMAGE20_PATH = PAGE + "/jcr:content/root/image20";
    private static final String IMAGE21_PATH = PAGE + "/jcr:content/root/image21";

    @BeforeClass
    public static void setUp() throws IOException {
        internalSetUp(CONTEXT, TEST_BASE);
    }

    public ImageImplTest() {
        testBase = TEST_BASE;
    }

    @Test
    public void testExportedType() {
        Image image = getImageUnderTest(IMAGE0_PATH);
        assertEquals(ImageImpl.RESOURCE_TYPE, image.getExportedType());
    }

    @Test
    public void testImageWithOneSmartSize() {
        Image image = getImageUnderTest(IMAGE3_PATH);

        Assert.assertArrayEquals(new int[] { 600 }, image.getSmartSizes());
        Assert.assertArrayEquals(new String[] { "/core/content/test/jcr%3acontent/root/image3.img.600.png" }, image.getSmartImages());
        Assert.assertEquals(false, image.isLazyEnabled());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE3_PATH));
    }

    @Test
    public void testImageWithMoreThanOneSmartSize() {
        Image image = getImageUnderTest(IMAGE0_PATH);

        Assert.assertArrayEquals(new int[] { 600,700,800,2000,2500 }, image.getSmartSizes());
        Assert.assertArrayEquals(new String[] { "/core/content/test/jcr%3acontent/root/image0.img.600.png/1490005239000.png",
                "/core/content/test/jcr%3acontent/root/image0.img.700.png/1490005239000.png",
                "/core/content/test/jcr%3acontent/root/image0.img.800.png/1490005239000.png",
                "/core/content/test/jcr%3acontent/root/image0.img.2000.png/1490005239000.png",
                "/core/content/test/jcr%3acontent/root/image0.img.2500.png/1490005239000.png" },
                image.getSmartImages());
        Assert.assertEquals(true, image.isLazyEnabled());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE0_PATH));
    }

    @Test
    public void testImageWithNoSmartSize() {
        Image image = getImageUnderTest(IMAGE4_PATH);

        Assert.assertArrayEquals(new int[] {}, image.getSmartSizes());
        Assert.assertArrayEquals(new String[] {}, image.getSmartImages());
        Assert.assertEquals(true, image.isLazyEnabled());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE4_PATH));
    }

    @Test
    public void testImageWithAltAndTitleFromDAM() {
        Image image = getImageUnderTest(IMAGE20_PATH);
        assertEquals("Adobe Systems Logo and Wordmark", image.getTitle());
        assertEquals("Adobe Systems Logo and Wordmark in PNG format", image.getAlt());
        // test fallback to dc:title if dc:description is empty
        image = getImageUnderTest(IMAGE21_PATH);
        assertEquals("Adobe Systems Logo and Wordmark", image.getAlt());
    }

    @Override
    protected Image getImageUnderTest(String resourcePath) {
        return (Image) super.getImageUnderTest(resourcePath, Image.class);
    }
}
