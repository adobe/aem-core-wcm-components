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

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import apps.core.wcm.components.text.v1.text.Text;
import com.adobe.cq.sightly.SightlyWCMMode;
import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.commons.AuthoringUtils;
import com.day.cq.wcm.api.WCMMode;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Text.class, AuthoringUtils.class})
public class TextTest {

    public static final String PLAIN_TEXT = "/content/text/plain";
    public static final String RICH_TEXT = "/content/text/rich";
    public static final String EMPTY_TEXT = "/content/text/empty";

    @Rule
    public final AemContext context = new AemContext();

    @Test
    public void testRegularText() {
        context.load().json("/plain-text.json", PLAIN_TEXT);
        Text text = setupTextObject(PLAIN_TEXT);
        text.activate();
        assertEquals("Expected to retrieve value 'plain'.", "plain", text.getText());
        assertTrue(text.hasContent());
        assertFalse("Expected plain text.", text.getTextIsRich());
        assertNull("Expected a null style since the text value is populated.", text.getCssClass());
        assertEquals("Expected " + Text.CONTEXT_TEXT + " XSS context.", Text.CONTEXT_TEXT, text.getXssContext());
    }

    @Test
    public void testRichText() {
        context.load().json("/rich-text.json", RICH_TEXT);
        Text text = setupTextObject(RICH_TEXT);
        text.activate();
        assertEquals("Expected to retrieve value '<p>rich</p>'.", "<p>rich</p>", text.getText());
        assertTrue(text.hasContent());
        assertTrue("Expected rich text.", text.getTextIsRich());
        assertNull("Expected a null style since the text value is populated.", text.getCssClass());
        assertEquals("Expected " + Text.CONTEXT_HTML + " XSS context.", Text.CONTEXT_HTML, text.getXssContext());
    }

    @Test
    public void testEmptyTextWcmModeEditTouch() {
        PowerMockito.mockStatic(AuthoringUtils.class);
        when(AuthoringUtils.isTouch(context.request())).thenReturn(true);
        context.load().json("/empty-text.json", EMPTY_TEXT);
        Text text = setupTextObject(EMPTY_TEXT);
        WCMMode.EDIT.toRequest(context.request());
        doReturn(new SightlyWCMMode(context.request())).when(text).get(WCMBindings.WCM_MODE, SightlyWCMMode.class);
        text.activate();
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
        context.load().json("/empty-text.json", EMPTY_TEXT);
        Text text = setupTextObject(EMPTY_TEXT);
        doReturn(new SightlyWCMMode(context.request())).when(text).get(WCMBindings.WCM_MODE, SightlyWCMMode.class);
        text.activate();
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
        context.load().json("/empty-text.json", EMPTY_TEXT);
        Text text = setupTextObject(EMPTY_TEXT);
        doReturn(new SightlyWCMMode(context.request())).when(text).get(WCMBindings.WCM_MODE, SightlyWCMMode.class);
        text.activate();
        assertEquals("Expected empty string.", "", text.getText());
        assertFalse(text.hasContent());
        assertFalse("Did not expect rich text.", text.getTextIsRich());
        assertEquals("Expected " + Text.CSS_CLASS_CLASSIC + " style.", Text.CSS_CLASS_CLASSIC, text.getCssClass());
        assertEquals("Expected " + Text.CONTEXT_TEXT + " XSS context.", Text.CONTEXT_TEXT, text.getXssContext());
    }

    private Text setupTextObject(String resourcePath) {
        final Resource resource = context.resourceResolver().getResource(resourcePath);
        ValueMap properties = resource.adaptTo(ValueMap.class);
        Text text = new Text();
        Text spy = PowerMockito.spy(text);
        doReturn(resource).when(spy).getResource();
        doReturn(properties).when(spy).getProperties();
        doReturn(context.request()).when(spy).getRequest();
        return spy;
    }
}
