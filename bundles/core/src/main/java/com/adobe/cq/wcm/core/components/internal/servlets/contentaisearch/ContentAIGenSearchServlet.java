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
package com.adobe.cq.wcm.core.components.internal.servlets.contentaisearch;

import javax.servlet.Servlet;

import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import com.adobe.cq.wcm.core.components.models.ContentAISupportedSearch;
import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClientException;

/**
 * Servlet exposing the ContentAI Supported Search component's generative-summary endpoint,
 * backed by the Content AI Generative Search API.
 */
@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.methods=GET",
        "sling.servlet.resourceTypes=" + AbstractContentAISearchServlet.RESOURCE_TYPE,
        "sling.servlet.selectors=" + ContentAIGenSearchServlet.SELECTOR,
        "sling.servlet.extensions=" + AbstractContentAISearchServlet.EXTENSION
    }
)
public class ContentAIGenSearchServlet extends AbstractContentAISearchServlet {

    protected static final String SELECTOR = "gensearch";
    private static final long serialVersionUID = 1L;

    @Override
    protected Object executeQuery(@NotNull ContentAISupportedSearch model, @NotNull String query) throws ContentAIClientException {
        return contentAIClient.genSearch(model.getPrimaryContentSource(), model.getContentSourceType(), query);
    }

    @Override
    @NotNull
    protected String getOperationName() {
        return SELECTOR;
    }
}
