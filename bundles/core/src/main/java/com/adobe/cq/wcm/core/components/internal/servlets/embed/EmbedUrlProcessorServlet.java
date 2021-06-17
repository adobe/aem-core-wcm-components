/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
package com.adobe.cq.wcm.core.components.internal.servlets.embed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.adobe.cq.wcm.core.components.services.embed.UrlProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet that returns a JSON representation of the registered embed URL processor for the given URL parameter.
 *
 * It returns a 200 (OK) status code if a URL processor is found, 404 (Not Found) otherwise.
 */
@Component(
    service = { Servlet.class },
    property = {
        "sling.servlet.methods=GET",
        "sling.servlet.resourceTypes=" + EmbedUrlProcessorServlet.RESOURCE_TYPE_V1,
        "sling.servlet.resourceTypes=" + EmbedUrlProcessorServlet.RESOURCE_TYPE_V2,
        "sling.servlet.selectors=" + EmbedUrlProcessorServlet.SELECTOR,
        "sling.servlet.extensions=" + EmbedUrlProcessorServlet.EXTENSION
    }
)
public class EmbedUrlProcessorServlet extends SlingSafeMethodsServlet {

    protected static final String RESOURCE_TYPE_V1 = "core/wcm/components/embed/v1/embed";
    protected static final String RESOURCE_TYPE_V2 = "core/wcm/components/embed/v2/embed";
    protected static final String SELECTOR = "urlProcessor";
    protected static final String EXTENSION = "json";

    private static final String PARAM_URL = "url";
    private static final long serialVersionUID = 2187626333327104828L;

    @SuppressFBWarnings(justification = "This field needs to be transient")
    private transient List<UrlProcessor> urlProcessors = new ArrayList<>();

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) throws IOException {
        String url = request.getParameter(PARAM_URL);
        UrlProcessor.Result result = getResult(url);
        if (result != null) {
            writeJson(result, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void writeJson(UrlProcessor.Result result, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), result);
    }

    private UrlProcessor.Result getResult(String url) {
        if (StringUtils.isNotEmpty(url)) {
            for (UrlProcessor urlProcessor : urlProcessors) {
                UrlProcessor.Result result = urlProcessor.process(url);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    @Reference(
        service = UrlProcessor.class,
        policy = ReferencePolicy.DYNAMIC,
        cardinality = ReferenceCardinality.MULTIPLE,
        bind = "bindEmbedUrlProcessor",
        unbind = "unbindEmbedUrlProcessor"
    )
    protected void bindEmbedUrlProcessor(UrlProcessor urlProcessor, Map<?, ?> props) {
        urlProcessors.add(urlProcessor);
    }

    protected void unbindEmbedUrlProcessor(UrlProcessor urlProcessor, Map<?, ?> props) {
        urlProcessors.remove(urlProcessor);
    }

}
