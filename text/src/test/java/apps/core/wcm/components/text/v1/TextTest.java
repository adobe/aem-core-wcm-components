/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2016 Adobe Systems Incorporated
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
package apps.core.wcm.components.text.v1;

import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import apps.core.wcm.components.text.v1.text.Text;
import com.adobe.cq.wcm.core.components.commons.AuthoringUtils;
import com.adobe.cq.wcm.core.components.testing.WCMUsePojoBaseTest;
import com.day.cq.wcm.api.WCMMode;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@PrepareForTest({Text.class, AuthoringUtils.class})
public class TextTest extends WCMUsePojoBaseTest<Text> {

    static {
        TEST_ROOT = "/content/text";
    }

    public static final String PLAIN_TEXT = TEST_ROOT + "/plain-text";
    public static final String RICH_TEXT = TEST_ROOT + "/rich-text";
    public static final String EMPTY_TEXT = TEST_ROOT + "/empty-text";

    @Test
    public void testRegularText() {
        Text text = getSpiedObject(PLAIN_TEXT);
        assertEquals("Expected to retrieve value 'plain'.", "plain", text.getText());
        assertTrue(text.hasContent());
        assertFalse("Expected plain text.", text.getTextIsRich());
        assertNull("Expected a null style since the text value is populated.", text.getCssClass());
        assertEquals("Expected " + Text.CONTEXT_TEXT + " XSS context.", Text.CONTEXT_TEXT, text.getXssContext());
    }

    @Test
    public void testRichText() {
        Text text = getSpiedObject(RICH_TEXT);
        assertEquals("Expected to retrieve value '<p>rich</p>'.", "<p>rich</p>", text.getText());
        assertTrue(text.hasContent());
        assertTrue("Expected rich text.", text.getTextIsRich());
        assertNull("Expected a null style since the text value is populated.", text.getCssClass());
        assertEquals("Expected " + Text.CONTEXT_HTML + " XSS context.", Text.CONTEXT_HTML, text.getXssContext());
    }

    @Test
    public void testEmptyTextWcmModeEditTouch() {
        setWCMMode(WCMMode.EDIT);
        PowerMockito.mockStatic(AuthoringUtils.class);
        when(AuthoringUtils.isTouch(context.request())).thenReturn(true);
        Text text = getSpiedObject(EMPTY_TEXT);
        assertEquals("Expected empty string.", "", text.getText());
        assertFalse(text.hasContent());
        assertFalse("Did not expect rich text.", text.getTextIsRich());
        assertEquals("Expected " + Text.CSS_CLASS_TOUCH + " style.", Text.CSS_CLASS_TOUCH, text.getCssClass());
        assertEquals("Expected " + Text.CONTEXT_TEXT + " XSS context.", Text.CONTEXT_TEXT, text.getXssContext());
    }

    @Test
    public void testEmptyTextWcmModeDisabledTouch() {
        PowerMockito.mockStatic(AuthoringUtils.class);
        when(AuthoringUtils.isTouch(context.request())).thenReturn(true);
        Text text = getSpiedObject(EMPTY_TEXT);
        assertEquals("Expected empty string.", "", text.getText());
        assertFalse(text.hasContent());
        assertFalse("Did not expect rich text.", text.getTextIsRich());
        assertEquals("Expected " + Text.CSS_CLASS_TOUCH + " style.", Text.CSS_CLASS_TOUCH, text.getCssClass());
        assertEquals("Expected " + Text.CONTEXT_TEXT + " XSS context.", Text.CONTEXT_TEXT, text.getXssContext());
    }

    @Test
    public void testEmptyTextWcmModeDisabledClassic() {
        PowerMockito.mockStatic(AuthoringUtils.class);
        when(AuthoringUtils.isClassic(context.request())).thenReturn(true);
        Text text = getSpiedObject(EMPTY_TEXT);
        assertEquals("Expected empty string.", "", text.getText());
        assertFalse(text.hasContent());
        assertFalse("Did not expect rich text.", text.getTextIsRich());
        assertEquals("Expected " + Text.CSS_CLASS_CLASSIC + " style.", Text.CSS_CLASS_CLASSIC, text.getCssClass());
        assertEquals("Expected " + Text.CONTEXT_TEXT + " XSS context.", Text.CONTEXT_TEXT, text.getXssContext());
    }
}
