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
package apps.core.wcm.components.title.v1;

import java.util.HashMap;
import java.util.Map;
import javax.script.SimpleBindings;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import apps.core.wcm.components.title.v1.title.Title;
import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.commons.AuthoringUtils;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Title.class, AuthoringUtils.class})
public class TitleTest {

    public static final String TITLE_ROOT = "/content/title";
    public static final String TITLE_RESOURCE_JCR_TITLE = TITLE_ROOT + "/jcr:content/par/title-jcr-title";
    public static final String TITLE_RESOURCE_JCR_TITLE_TYPE = TITLE_ROOT + "/jcr:content/par/title-jcr-title-type";
    public static final String TITLE_NOPROPS = TITLE_ROOT + "/jcr:content/par/title-noprops";
    public static final String TITLE_WRONGTYPE = TITLE_ROOT + "/jcr:content/par/title-wrongtype";

    @Rule
    public final AemContext context = new AemContext();

    @Before
    public void setUp() {
        context.load().json("/test-content.json", TITLE_ROOT);
    }

    @Test
    public void testGetTitleFromResource() {
        Title title = setupTitleObject(TITLE_RESOURCE_JCR_TITLE);
        SimpleBindings bindings = new SimpleBindings();
        title.init(bindings);
        assertEquals("Hello World", title.getText());
        assertNull(title.getElement());
    }

    @Test
    public void testGetTitleFromResourceWithElementInfo() {
        Title title = setupTitleObject(TITLE_RESOURCE_JCR_TITLE_TYPE);
        SimpleBindings bindings = new SimpleBindings();
        title.init(bindings);
        assertEquals("Hello World", title.getText());
        assertEquals("h2", title.getElement());
    }

    @Test
    public void testGetTitleResourcePageStyleType() {
        Title title = setupTitleObject(TITLE_NOPROPS);
        SimpleBindings bindings = new SimpleBindings();
        Page resourcePage = Mockito.mock(Page.class);
        Map<String, Object> rpp = new HashMap<String, Object>(){{
            put(Title.PROP_TITLE, "Resource Page Title");
        }};
        ValueMap resourcePageProperties = new ValueMapDecorator(rpp);
        when(resourcePage.getProperties()).thenReturn(resourcePageProperties);
        bindings.put(WCMBindings.RESOURCE_PAGE, resourcePage);
        Style style = Mockito.mock(Style.class);
        when(style.get(Title.PROP_DEFAULT_TYPE, String.class)).thenReturn("h2");
        bindings.put(WCMBindings.CURRENT_STYLE, style);
        title.init(bindings);
        assertEquals("Resource Page Title", title.getText());
        assertEquals("h2", title.getElement());
    }

    @Test
    public void testGetTitleFromCurrentPageWithWrongElementInfo() {
        Title title = setupTitleObject(TITLE_WRONGTYPE);
        SimpleBindings bindings = new SimpleBindings();
        Map<String, Object> cpp = new HashMap<String, Object>(){{
            put(Title.PROP_TITLE, "Current Page Title");
        }};
        ValueMap currentPageProperties = new ValueMapDecorator(cpp);
        bindings.put(WCMBindings.PAGE_PROPERTIES, currentPageProperties);
        title.init(bindings);
        assertEquals("Current Page Title", title.getText());
        assertNull(title.getElement());
    }

    @Test
    public void testGetTitleFromCurrentPageWithWrongElementInfo2() {
        Title title = setupTitleObject(TITLE_WRONGTYPE);
        SimpleBindings bindings = new SimpleBindings();
        Map<String, Object> cpp = new HashMap<String, Object>(){{
            put(Title.PROP_PAGE_TITLE, "Current Page Title");
        }};
        ValueMap currentPageProperties = new ValueMapDecorator(cpp);
        bindings.put(WCMBindings.PAGE_PROPERTIES, currentPageProperties);
        title.init(bindings);
        assertEquals("Current Page Title", title.getText());
        assertNull(title.getElement());
    }

    @Test
    public void testGetTitleFromCurrentPageName() {
        Title title = setupTitleObject(TITLE_WRONGTYPE);
        SimpleBindings bindings = new SimpleBindings();
        Page currentPage = Mockito.mock(Page.class);
        when(currentPage.getName()).thenReturn("a-page");
        bindings.put(WCMBindings.CURRENT_PAGE, currentPage);
        title.init(bindings);
        assertEquals("a-page", title.getText());
        assertNull(title.getElement());
    }

    private Title setupTitleObject(String resourcePath) {
        final Resource resource = context.resourceResolver().getResource(resourcePath);
        final ValueMap properties = resource.adaptTo(ValueMap.class);
        Title title = new Title();
        Title spy = PowerMockito.spy(title);
        doReturn(resource).when(spy).getResource();
        doReturn(properties).when(spy).getProperties();
        return spy;
    }


}
