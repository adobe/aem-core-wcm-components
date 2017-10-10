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

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.sandbox.models.Image;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImageImplTest extends com.adobe.cq.wcm.core.components.internal.models.v1.ImageImplTest {

    static {
        TEST_BASE = "/sandbox/image";
    }

    @Test
    public void testExportedType() {
        Image image = getImageUnderTest(IMAGE0_PATH);
        assertEquals("core/wcm/components/image/v1/image", image.getExportedType());
    }

    @Test
    public void testImageWithOneSmartSize() throws Exception {
        Image image = getImageUnderTest(IMAGE3_PATH);

        Assert.assertArrayEquals(new int[] { 600 }, image.getSmartSizes());
        Assert.assertArrayEquals(new String[] { "/core/content/test/jcr%3acontent/root/image3.img.600.png" }, image.getSmartImages());
        Assert.assertEquals(false, image.isLazyEnabled());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(TEST_BASE, IMAGE3_PATH));
    }

    @Test
    public void testImageWithMoreThanOneSmartSize() throws Exception {
        Image image = getImageUnderTest(IMAGE0_PATH);

        Assert.assertArrayEquals(new int[] { 600,700,800,2000,2500 }, image.getSmartSizes());
        Assert.assertArrayEquals(new String[] { "/core/content/test/jcr%3acontent/root/image0.img.600.png",
                "/core/content/test/jcr%3acontent/root/image0.img.700.png",
                "/core/content/test/jcr%3acontent/root/image0.img.800.png",
                "/core/content/test/jcr%3acontent/root/image0.img.2000.png",
                "/core/content/test/jcr%3acontent/root/image0.img.2500.png" },
                image.getSmartImages());
        Assert.assertEquals(true, image.isLazyEnabled());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(TEST_BASE, IMAGE0_PATH));
    }

    @Test
    public void testImageWithNoSmartSize() throws Exception {
        Image image = getImageUnderTest(IMAGE4_PATH);

        Assert.assertArrayEquals(new int[] {}, image.getSmartSizes());
        Assert.assertArrayEquals(new String[] {}, image.getSmartImages());
        Assert.assertEquals(true, image.isLazyEnabled());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(TEST_BASE, IMAGE4_PATH));
    }

    @Override
    protected Image getImageUnderTest(String resourcePath) {
        return (Image) super.getImageUnderTest(resourcePath, null, Image.class);
    }
}
