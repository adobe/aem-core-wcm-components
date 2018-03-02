/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~   http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.wcm.core.components.internal.models.v2;

import org.apache.jackrabbit.util.Text;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractImageTest;
import com.adobe.cq.wcm.core.components.internal.servlets.AdaptiveImageServlet;
import com.adobe.cq.wcm.core.components.models.Image;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ImageImplTest extends com.adobe.cq.wcm.core.components.internal.models.v1.ImageImplTest {

    protected static String TEST_BASE = "/image/v2";
    protected static String SELECTOR = AdaptiveImageServlet.CORE_DEFAULT_SELECTOR;
    private static final String IMAGE20_PATH = PAGE + "/jcr:content/root/image20";
    private static final String IMAGE21_PATH = PAGE + "/jcr:content/root/image21";

    @BeforeClass
    public static void setUp() {
        AbstractImageTest.internalSetUp(AbstractImageTest.CONTEXT, TEST_BASE);
    }

    public ImageImplTest() {
        testBase = TEST_BASE;
        selector = SELECTOR;
    }

    @Test
    public void testExportedType() {
        Image image = getImageUnderTest(AbstractImageTest.IMAGE0_PATH);
        assertEquals(ImageImpl.RESOURCE_TYPE, image.getExportedType());
    }

    @Test
    public void testImageWithOneSmartSize() {
        Image image = getImageUnderTest(AbstractImageTest.IMAGE3_PATH);

        Assert.assertArrayEquals(new int[] {600}, image.getWidths());
        Assert.assertEquals(false, image.isLazyEnabled());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, AbstractImageTest.IMAGE3_PATH));
    }

    @Test
    public void testImageWithOneSmartSizeAndPolicyDelegate() {
        Image image = getImageUnderTest(AbstractImageTest.IMAGE3_PATH, AbstractImageTest.IMAGE0_PATH);

        Assert.assertArrayEquals(new int[] {600}, image.getWidths());
        Assert.assertEquals(false, image.isLazyEnabled());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, AbstractImageTest.IMAGE3_PATH + "-with-policy-delegate"));
    }

    @Test
    public void testImageWithMoreThanOneSmartSize() {
        Image image = getImageUnderTest(AbstractImageTest.IMAGE0_PATH);

        Assert.assertArrayEquals(new int[] { 600, 700, 800, 2000, 2500 }, image.getWidths());
        Assert.assertEquals(false, image.isLazyEnabled());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, AbstractImageTest.IMAGE0_PATH));
    }

    @Test
    public void testImageWithNoSmartSize() {
        Image image = getImageUnderTest(AbstractImageTest.IMAGE4_PATH);

        Assert.assertArrayEquals(new int[] {}, image.getWidths());
        Assert.assertEquals(false, image.isLazyEnabled());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, AbstractImageTest.IMAGE4_PATH));
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

    @Test
    public void testSimpleDecorativeImage() {
        String escapedResourcePath = AbstractImageTest.IMAGE4_PATH.replace("jcr:content", "_jcr_content");
        com.adobe.cq.wcm.core.components.models.Image image = getImageUnderTest(AbstractImageTest.IMAGE4_PATH);
        assertNull("Did not expect a value for the alt attribute, since the image is marked as decorative.", image.getAlt());
        assertEquals("Adobe Systems Logo and Wordmark", image.getTitle());
        assertTrue("Image should display a caption popup.", image.displayPopupTitle());
        assertNull("Did not expect a link for this image, since it's marked as decorative.", image.getLink());
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".png/1494867377756.png", image.getSrc());
        compareJSON(
                "{\"" + com.adobe.cq.wcm.core.components.models.Image.JSON_SMART_IMAGES + "\":[], \"" + com.adobe.cq.wcm.core.components.models.Image.JSON_SMART_SIZES + "\":[], \"" + com.adobe.cq.wcm.core.components.models.Image.JSON_LAZY_ENABLED +
                        "\":false}",
                image.getJson());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, AbstractImageTest.IMAGE4_PATH));
    }

    @Test
    public void testImageWithTwoOrMoreSmartSizes() {
        String escapedResourcePath = AbstractImageTest.IMAGE0_PATH.replace("jcr:content", "_jcr_content");
        Image image = getImageUnderTest(AbstractImageTest.IMAGE0_PATH);
        assertEquals("Adobe Systems Logo and Wordmark in PNG format", image.getAlt());
        assertEquals("Adobe Systems Logo and Wordmark", image.getTitle());
        assertEquals(IMAGE_FILE_REFERENCE, image.getFileReference());
        String expectedJson = "{\"smartImages\":[\"/core/content/test/_jcr_content/root/image0." + selector + ".600.png/1490005239000.png\"," +
                "\"/core/content/test/_jcr_content/root/image0." + selector + ".700.png/1490005239000.png\",\"/core/content/test/_jcr_content/root/image0" +
                "." + selector + ".800.png/1490005239000.png\",\"/core/content/test/_jcr_content/root/image0." + selector + ".2000.png/1490005239000.png\", " +
                "\"/core/content/test/_jcr_content/root/image0." + selector + ".2500.png/1490005239000.png\"],\"smartSizes\":[600,700,800,2000,2500],\"lazyEnabled\":false}";
        compareJSON(expectedJson, image.getJson());
        assertTrue(image.displayPopupTitle());
        assertEquals(CONTEXT_PATH + "/content/test-image.html", image.getLink());
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".png/1490005239000.png", image.getSrc());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, AbstractImageTest.IMAGE0_PATH));
    }

    @Test
    @Override
    public void testImageFromTemplateStructure() {
        com.adobe.cq.wcm.core.components.models.Image image = getImageUnderTest(TEMPLATE_IMAGE_PATH);
        assertEquals(CONTEXT_PATH + "/content/test." + selector + ".png/structure/jcr%3acontent/root/image_template/1490005239000.png", image.getSrc());
        assertEquals("Adobe Systems Logo and Wordmark in PNG format", image.getAlt());
        assertEquals("Adobe Systems Logo and Wordmark", image.getTitle());
        assertEquals(IMAGE_FILE_REFERENCE, image.getFileReference());
        String expectedJson = "{" +
                "\"smartImages\":[" +
                    "\"/core/content/test." + selector + ".600.png/structure/jcr%3acontent/root/image_template/1490005239000.png\"," +
                    "\"/core/content/test." + selector + ".700.png/structure/jcr%3acontent/root/image_template/1490005239000.png\"," +
                    "\"/core/content/test." + selector + ".800.png/structure/jcr%3acontent/root/image_template/1490005239000.png\"," +
                    "\"/core/content/test." + selector + ".2000.png/structure/jcr%3acontent/root/image_template/1490005239000.png\"," +
                    "\"/core/content/test." + selector + ".2500.png/structure/jcr%3acontent/root/image_template/1490005239000.png\"" +
                "]," +
                "\"smartSizes\":[600,700,800,2000,2500]," +
                "\"lazyEnabled\":false" +
        "}";
        compareJSON(expectedJson, image.getJson());
        assertTrue(image.displayPopupTitle());
        assertEquals(CONTEXT_PATH + "/content/test-image.html", image.getLink());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, TEMPLATE_IMAGE_PATH));
    }

    private Image getImageUnderTest(String resourcePath, String contentPolicyDelegatePath) {
        return getImageUnderTest(resourcePath, Image.class, contentPolicyDelegatePath);
    }
}
