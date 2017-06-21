/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.servlethelpers.MockSlingHttpServletRequest;
import org.apache.sling.servlethelpers.MockSlingHttpServletResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.day.cq.wcm.foundation.forms.FormStructureHelperFactory;
import com.day.cq.wcm.foundation.forms.FormsHandlingServletHelper;
import com.day.cq.wcm.foundation.security.SaferSlingPostValidator;

import io.wcm.testing.mock.aem.junit.AemContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CoreFormHandlingServlet.class)
public class CoreFormHandlingServletTest {

    @Mock
    FormStructureHelperFactory formStructureHelperFactory;

    @Mock
    SaferSlingPostValidator saferSlingPostValidator;

    @Mock
    FormsHandlingServletHelper formsHandlingServletHelper;

    @InjectMocks
    CoreFormHandlingServlet servlet;

    @Rule
    public final AemContext context = new AemContext();

    private static final String[] NAME_WHITELIST = {"param-text", "param-button"};

    private static final boolean ALLOW_EXPRESSIONS = false;

    private static final String SELECTOR = "form";

    private static final String EXTENSION = "html";

    @Before
    public void setUp() throws Exception {
        servlet = new CoreFormHandlingServlet();
        MockitoAnnotations.initMocks(this);
        PowerMockito.whenNew(FormsHandlingServletHelper.class).withAnyArguments().thenReturn(formsHandlingServletHelper);
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
    }

    @Test
    public void testDoPost() throws Exception {
        SlingHttpServletRequest request = new MockSlingHttpServletRequest(context.resourceResolver());
        SlingHttpServletResponse response = new MockSlingHttpServletResponse();
        servlet.doPost(request, response);
        verify(formsHandlingServletHelper).doPost(request, response);
    }

    @Test
    public void testDoFilter() throws Exception {
        SlingHttpServletRequest request = new MockSlingHttpServletRequest(context.resourceResolver());
        SlingHttpServletResponse response = new MockSlingHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);
        servlet.doFilter(request, response, filterChain);
        verify(formsHandlingServletHelper).handleFilter(request, response, filterChain, EXTENSION, SELECTOR);
    }
}
