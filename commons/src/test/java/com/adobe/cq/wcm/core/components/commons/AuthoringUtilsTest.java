/*******************************************************************************
 * Copyright 2015 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.commons;

import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.Test;
import org.mockito.Mockito;

import com.day.cq.wcm.api.AuthoringUIMode;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class AuthoringUtilsTest {

    @Test
    public void testIsTouch() throws Exception {
        SlingHttpServletRequest request = Mockito.mock(SlingHttpServletRequest.class);
        when(request.getAttribute(AuthoringUIMode.REQUEST_ATTRIBUTE_NAME)).thenReturn(AuthoringUIMode.TOUCH);
        assertTrue("Expected to detect touch mode.", AuthoringUtils.isTouch(request));
    }

    @Test
    public void testIsClassic() throws Exception {
        SlingHttpServletRequest request = Mockito.mock(SlingHttpServletRequest.class);
        when(request.getAttribute(AuthoringUIMode.REQUEST_ATTRIBUTE_NAME)).thenReturn(AuthoringUIMode.CLASSIC);
        assertTrue("Expected to detect classic mode.", AuthoringUtils.isClassic(request));
    }
}
