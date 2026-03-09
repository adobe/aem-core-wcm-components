/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
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
package com.adobe.cq.wcm.core.components.it.support;

import java.io.IOException;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;

/**
 * Local-development mock for the Content Fragment Visualization API.
 *
 * <p>The real API lives behind {@code /adobe/experimental/.../sites/cf/} and is
 * only available on AEM Cloud Service. This servlet provides stub responses so
 * that the authoring UI can be exercised on a local AEM SDK.</p>
 *
 * <p>Requests are redirected here by the companion {@code clientlib-mockvcf}
 * clientlib (via {@code $.ajaxPrefilter}). Uses Sling selectors to distinguish
 * request types:</p>
 * <ul>
 *   <li>{@code /bin/mock/cfvisualization.templates.json?modelId=...} — returns a
 *       JSON list of HTML templates for the Content Fragment Visualization
 *       authoring dialog</li>
 *   <li>{@code /bin/mock/cfvisualization.vcf.html?fragmentId=...&templateId=...}
 *       — returns a Visual Content Fragment (the rendered HTML for a given
 *       Content Fragment + HTML template combination)</li>
 * </ul>
 *
 * <p>This servlet is part of the {@code testing/it/it.core} bundle, which is
 * deployed only to local AEM SDK instances — never to AEM Cloud Service.</p>
 */
@Component(service = Servlet.class)
@SlingServletPaths("/bin/mock/cfvisualization")
public class MockVCFServlet extends SlingAllMethodsServlet {

    private static final String[][] MOCK_TEMPLATES = {
        {"hero-banner", "Hero Banner"},
        {"card", "Card"},
        {"teaser", "Teaser"},
        {"article-header", "Article Header"},
        {"product-detail", "Product Detail"}
    };

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws IOException {

        String selectorString = request.getRequestPathInfo().getSelectorString();
        if (selectorString == null) {
            selectorString = "";
        }

        if ("templates".equals(selectorString)) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(buildTemplatesJson());
            return;
        }

        if ("vcf".equals(selectorString)) {
            String fragmentId = request.getParameter("fragmentId");
            String templateId = request.getParameter("templateId");
            String variation = request.getParameter("variation");
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(buildVcfHtml(fragmentId, templateId, variation));
            return;
        }

        response.setStatus(SlingHttpServletResponse.SC_NOT_FOUND);
    }

    private String buildTemplatesJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"items\":[");
        for (int i = 0; i < MOCK_TEMPLATES.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("{\"id\":\"").append(escapeJson(MOCK_TEMPLATES[i][0]))
              .append("\",\"name\":\"").append(escapeJson(MOCK_TEMPLATES[i][1]))
              .append("\"}");
        }
        sb.append("]}");
        return sb.toString();
    }

    private String buildVcfHtml(String fragmentId, String templateId, String variation) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"cmp-contentfragment__vcf\"")
          .append(" style=\"padding:20px;border:2px dashed #b0b0b0;border-radius:8px;")
          .append("background:#f5f5f5;font-family:Adobe Clean,Helvetica,sans-serif;\">");
        sb.append("<p style=\"margin:0 0 12px;font-size:11px;text-transform:uppercase;")
          .append("letter-spacing:.08em;color:#959595;\">Visual Content Fragment — Mock</p>");
        sb.append("<table style=\"border-collapse:collapse;font-size:14px;color:#333;\">");
        appendRow(sb, "Content Fragment", fragmentId);
        if (templateId != null && !templateId.isEmpty()) {
            appendRow(sb, "HTML Template", templateId);
        }
        if (variation != null && !variation.isEmpty()) {
            appendRow(sb, "Variation", variation);
        }
        sb.append("</table></div>");
        return sb.toString();
    }

    private void appendRow(StringBuilder sb, String label, String value) {
        sb.append("<tr>")
          .append("<td style=\"padding:4px 16px 4px 0;font-weight:bold;color:#6e6e6e;\">")
          .append(escapeHtml(label)).append("</td>")
          .append("<td style=\"padding:4px 0;\">").append(escapeHtml(value)).append("</td>")
          .append("</tr>");
    }

    private static String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("&", "&amp;")
                     .replace("<", "&lt;")
                     .replace(">", "&gt;")
                     .replace("\"", "&quot;");
    }

    private static String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                     .replace("\"", "\\\"");
    }
}
