/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.PrintWriter;

/**
 * HTTP response wrapper for {@link TableOfContentsFilter}.
 * {@link TableOfContentsFilter} stores the page response in this wrapper, modifies it and copies it in the
 * original response
 */
public class CharResponseWrapper extends HttpServletResponseWrapper {
    private CharArrayWriter writer;

    /**
     * Creates a wrapper around the original response object of the HTTP request by overriding the
     * original {@link PrintWriter}. All the HTTP response content after {@link TableOfContentsFilter}
     * is written in this newly created {@link PrintWriter} object
     * @param response - original response object of the HTTP request
     */
    public CharResponseWrapper(HttpServletResponse response) {
        super(response);
        writer = new CharArrayWriter();
    }

    /**
     * Returns the newly created {@link PrintWriter} object created by this wrapper
     * @return - newly created {@link PrintWriter}
     */
    @Override
    public PrintWriter getWriter() {
        return new PrintWriter(writer);
    }

    /**
     * Converts the newly created {@link PrintWriter}'s content to string
     * @return - String containing the content of the {@link PrintWriter}
     */
    @Override
    public String toString() {
        return writer.toString();
    }

}
