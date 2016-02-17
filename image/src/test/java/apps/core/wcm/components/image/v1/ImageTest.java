/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package apps.core.wcm.components.image.v1;

import javax.script.Bindings;

import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import apps.core.wcm.components.image.v1.image.Image;
import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.commons.AuthoringUtils;
import com.adobe.cq.wcm.core.components.testing.WCMUsePojoBaseTest;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.designer.Style;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@PrepareForTest({Image.class, AuthoringUtils.class})
public class ImageTest extends WCMUsePojoBaseTest<Image> {

    static {
        TEST_ROOT = "/content/image";
    }

    private static final String IMAGE_DEFAULT_DESIGN_FR = TEST_ROOT + "/imageDefaultDesignFileReference";
    private static final String IMAGE_DEFAULT_DESIGN_FILE = TEST_ROOT + "/imageDefaultDesignFile";
    private static final Style DEFAULT_STYLE = Mockito.mock(Style.class);

    @BeforeClass
    public static void setUpSuite() {
        when(DEFAULT_STYLE.get(eq(Image.DESIGN_PROP_ALLOWED_RENDITION_WIDTHS), any(Integer[].class))).thenReturn(new Integer[0]);
        when(DEFAULT_STYLE.get(Image.DESIGN_PROP_ENFORCE_ASPECT_RATIO, true)).thenReturn(true);
        when(DEFAULT_STYLE.get(Image.DESIGN_PROP_ALLOW_CROPPING, true)).thenReturn(true);
        when(DEFAULT_STYLE.get(Image.DESIGN_PROP_ALLOW_ROTATING, true)).thenReturn(true);
        when(DEFAULT_STYLE.get(Image.DESIGN_PROP_ALLOW_IMAGE_MAPS, true)).thenReturn(true);
        when(DEFAULT_STYLE.get(Image.DESIGN_PROP_ALLOW_LINKING, true)).thenReturn(true);
        when(DEFAULT_STYLE.get(Image.DESIGN_PROP_ALLOW_CAPTION_TEXT, true)).thenReturn(true);
        when(DEFAULT_STYLE.get(Image.DESIGN_PROP_DISPLAY_CAPTION_POPUP, false)).thenReturn(false);
        when(DEFAULT_STYLE.get(eq(Image.DESIGN_PROP_ALLOWED_STYLES), any(String[].class))).thenReturn(null);
    }


    @Test
    public void testDefaultDesignFileReference() {
        Bindings bindings = getResourceBackedBindings(IMAGE_DEFAULT_DESIGN_FR);
        MockSlingHttpServletRequest request = (MockSlingHttpServletRequest) bindings.get(SlingBindings.REQUEST);
        request.setContextPath("");
        bindings.put(WCMBindings.CURRENT_STYLE, DEFAULT_STYLE);
        Image image = getSpiedObject();
        image.init(bindings);
        assertEquals("/content/image/imageDefaultDesignFileReference.img.full.high.jpg", image.getSrc());
        assertNull("Expected a null link since the image has an image map defined.", image.getLink());
        assertNotNull(image.getImageMap());
        assertEquals("Title", image.getTitle());
        assertEquals("Alt", image.getAlt());
        assertEquals("Description", image.getCaption());
        // wcmMode is disabled
        assertNull(image.getCssClass());
    }

    @Test
    public void testDefaultDesignFile() {
        setWCMMode(WCMMode.EDIT);
        PowerMockito.mockStatic(AuthoringUtils.class);
        when(AuthoringUtils.isTouch(context.request())).thenReturn(true);
        Bindings bindings = getResourceBackedBindings(IMAGE_DEFAULT_DESIGN_FILE);
        MockSlingHttpServletRequest request = (MockSlingHttpServletRequest) bindings.get(SlingBindings.REQUEST);
        request.setContextPath("");
        bindings.put(WCMBindings.CURRENT_STYLE, DEFAULT_STYLE);
        Image image = getSpiedObject();
        image.init(bindings);
        assertEquals("/content/image/imageDefaultDesignFile.img.full.high.jpeg", image.getSrc());
        assertEquals("https://www.adobe.com", image.getLink());
        assertNull(image.getImageMap());
        assertEquals("Title", image.getTitle());
        assertEquals("Alt", image.getAlt());
        assertEquals("Description", image.getCaption());
        assertEquals("cq-dd-image " + Image.PLACEHOLDER_TOUCH, image.getCssClass());
    }

}
