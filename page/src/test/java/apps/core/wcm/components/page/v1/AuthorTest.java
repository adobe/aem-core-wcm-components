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

import java.io.StringWriter;

import javax.script.Bindings;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;

import apps.core.wcm.components.page.v1.page.Author;
import com.adobe.cq.sightly.WCMBindings;
import com.day.cq.wcm.api.AuthoringUIMode;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.EditContext;
import com.day.cq.wcm.undo.UndoConfigService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@PrepareForTest(Author.class)
public class AuthorTest extends WCMUsePojoBaseTest<Author> {

    static {
        TEST_ROOT = "/content/mysite";
    }

    public static final String TEMPLATED_PAGE = TEST_ROOT + "/templated-page";

    @Test
    public void testAuthor() throws Exception {
        context.request().setAttribute(AuthoringUIMode.REQUEST_ATTRIBUTE_NAME, AuthoringUIMode.TOUCH);
        UndoConfigService undoConfigService = mock(UndoConfigService.class);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                StringWriter stringWriter = invocationOnMock.getArgumentAt(0, StringWriter.class);
                stringWriter.append("undoConfig");
                return null;
            }
        }).when(undoConfigService).writeClientConfig(any(StringWriter.class));
        context.registerService(UndoConfigService.class, undoConfigService);
        Bindings bindings = getResourceBackedBindings(TEMPLATED_PAGE);
        EditContext editContext = mock(EditContext.class);
        Component component = mock(Component.class);
        when(editContext.getComponent()).thenReturn(component);
        when(component.getDialogPath()).thenReturn("/apps/core/wcm/components/page/v1/page/dialog");
        bindings.put(WCMBindings.EDIT_CONTEXT, editContext);
        Author author = getSpiedObject();
        author.init(bindings);
        assertEquals("undoConfig", author.getUndoConfig());
        assertTrue(author.isTouchAuthoring());
        assertEquals("/apps/core/wcm/components/page/v1/page/dialog", author.getDialogPath());
    }

}
