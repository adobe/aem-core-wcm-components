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
package apps.core.wcm.components.page.v1;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.script.Bindings;

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import apps.core.wcm.components.page.v1.page.Head;
import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.testing.WCMUsePojoBaseTest;
import com.day.cq.wcm.api.designer.Design;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@PrepareForTest(Head.class)
public class HeadTest extends WCMUsePojoBaseTest<Head> {

    public static final String DESIGN_PATH = "/etc/designs/mysite";

    static {
        TEST_ROOT = "/content/mysite";
    }

    public static final String TEMPLATED_PAGE = TEST_ROOT + "/templated-page";

    @Before
    public void setUp() {
        super.setUp();
        context.load().binaryFile("/favicon.ico", DESIGN_PATH + "/favicon.ico");
        context.load().binaryFile("/favicon.png", DESIGN_PATH + "/favicon.png");
        context.load().binaryFile("/static.css", DESIGN_PATH + "/static.css");
        context.load().json("/default-tags.json", "/etc/tags/default");
    }

    @Test
    public void testHead() {
        Head head = getSpiedObject();
        Bindings bindings = getResourceBackedBindings(TEMPLATED_PAGE);
        Design design = mock(Design.class);
        when(design.getPath()).thenReturn(DESIGN_PATH);
        bindings.put(WCMBindings.CURRENT_DESIGN, design);
        head.init(bindings);
        assertEquals("Templated Page", head.getTitle());
        assertEquals(DESIGN_PATH + ".css", head.getDesignPath());
        assertEquals(DESIGN_PATH + "/static.css", head.getStaticDesignPath());
        assertEquals(DESIGN_PATH + "/favicon.ico", head.getICOFavicon());
        assertEquals(DESIGN_PATH + "/favicon.png", head.getPNGFavicon());
        String[] keywordsArray = head.getKeywords();
        assertEquals(3, keywordsArray.length);
        Set<String> keywords = new HashSet<>(keywordsArray.length);
        keywords.addAll(Arrays.asList(keywordsArray));
        assertTrue(keywords.contains("one") && keywords.contains("two") && keywords.contains("three"));
    }

}
