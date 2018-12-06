/*
 *  Copyright 2018 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package core.wcm.components.examples.core.servlets;

import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.Assert.*;

public class SimpleServletTest {

    @Rule
    public final SlingContext context = new SlingContext();

    private SimpleServlet fixture = new SimpleServlet();

    @Test
    public void doGet() throws ServletException, IOException {
        context.build().resource("/content/test", "jcr:title", "resource title").commit();
        context.currentResource("/content/test");

        MockSlingHttpServletRequest request = context.request();
        MockSlingHttpServletResponse response = context.response();

        fixture.doGet(request, response);

        assertEquals("Title = resource title", response.getOutputAsString());
    }
}
