/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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
package com.adobe.cq.wcm.core.components.internal.servlets;

import java.lang.annotation.Annotation;
import javax.servlet.FilterChain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.testing.Utils;
import com.day.cq.wcm.foundation.forms.FormsHandlingServletHelper;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class CoreFormHandlingServletTest {

    private static final String[] NAME_WHITELIST = {"param-text", "param-button"};
    private static final boolean ALLOW_EXPRESSIONS = false;
    private static final String SELECTOR = "form";
    private static final String EXTENSION = "html";

    @Mock
    FormsHandlingServletHelper formsHandlingServletHelper;

    @InjectMocks
    CoreFormHandlingServlet servlet;

    public static AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    public void setUp() {
        servlet = new CoreFormHandlingServlet();
        servlet.activate(new CoreFormHandlingServlet.Configuration() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String[] name_whitelist() {
                return NAME_WHITELIST;
            }

            @Override
            public boolean allow_expressions() {
                return ALLOW_EXPRESSIONS;
            }
        });
        Utils.setInternalState(servlet, "formsHandlingServletHelper", formsHandlingServletHelper);
    }

    @Test
    public void testDoPost() throws Exception {
        servlet.doPost(context.request(), context.response());
        verify(formsHandlingServletHelper).doPost(context.request(), context.response());
    }

    @Test
    public void testDoFilter() throws Exception {
        FilterChain filterChain = mock(FilterChain.class);
        servlet.doFilter(context.request(), context.response(), filterChain);
        verify(formsHandlingServletHelper).handleFilter(context.request(), context.response(), filterChain, EXTENSION, SELECTOR);
    }
}
