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

import org.apache.sling.servlets.annotations.SlingServletFilter;
import org.apache.sling.servlets.annotations.SlingServletFilterScope;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component(service = Filter.class,
    property = {
        Constants.SERVICE_RANKING + "Integer=999"})
@SlingServletFilter(scope = {SlingServletFilterScope.REQUEST},
    pattern = "/content/.*",
    extensions = {"html"},
    methods = {"GET"})
public class TableOfContentsFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        CharResponseWrapper wrapper = new CharResponseWrapper((HttpServletResponseWrapper) response);

        chain.doFilter(request, wrapper);

        PrintWriter responseWriter = response.getWriter();

        if (wrapper.getContentType().contains("text/html")) {
            CharArrayWriter charWriter = new CharArrayWriter();
            String originalContent = wrapper.toString();

            Document document = Jsoup.parse(originalContent);
            Set<String> hTags = new HashSet<>();
            hTags.add("h1");
            hTags.add("h2");
            hTags.add("h3");
            hTags.add("h4");
            hTags.add("h5");
            hTags.add("h6");

            StringBuilder toc = new StringBuilder();
            toc.append("<ol>");
            Elements elements = document.getAllElements();
            for (Element element : elements) {
                if(!hTags.contains(element.tagName()) || element.text().length() == 0) {
                    continue;
                }
                toc.append("<li>");
                String uniqueId = UUID.randomUUID().toString();
                toc.append("<a href=#" + uniqueId + ">");
                toc.append(element.text());
                toc.append("</a>");
                toc.append("</li>");
                element.attr("id", uniqueId);
            }
            toc.append("</ol>");

            elements = document.getElementsByClass("tableofcomponents-placeholder");
            for (Element element : elements) {
                element.append(toc.toString());
            }

//            int indexOfCloseBodyTag = originalContent.indexOf("</body>") - 1;
//
//            charWriter.write(originalContent.substring(0, indexOfCloseBodyTag));
//
//            String copyrightInfo = "<p>Copyright CodeJava.net</p>";
////            String closeHTMLTags = "</body></html>";
//            String closeHTMLTags = originalContent.substring(indexOfCloseBodyTag);
//
//            charWriter.write(copyrightInfo);
//            charWriter.write(closeHTMLTags);

            charWriter.write(document.outerHtml());
            String alteredContent = charWriter.toString();
            response.setContentLength(alteredContent.length());
            responseWriter.write(alteredContent);
        }
    }

    @Override
    public void destroy() {

    }
}
