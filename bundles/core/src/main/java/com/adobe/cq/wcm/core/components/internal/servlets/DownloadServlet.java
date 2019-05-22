/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe Systems Incorporated
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.servlet.Servlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import com.day.cq.dam.api.Asset;

import static com.adobe.cq.wcm.core.components.internal.servlets.DownloadServlet.SELECTOR;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.resourceTypes=dam/asset",
                "sling.servlet.resourceTypes=dam:Asset",
                "sling.servlet.selectors=" + SELECTOR
        }
)
public class DownloadServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;


    public static final String SELECTOR = "cmp_download";
    public static final String INLINE_SELECTOR = "inline";

    private static final String CONTENT_DISPOSITION = "Content-Disposition";

    @Override
    protected void doGet(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response)
            throws IOException {

        Asset asset = request.getResource().adaptTo(Asset.class);
        if (asset == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        byte[] bytes = getByteArray(asset);

        if (bytes.length == 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        sendResponse(bytes, asset, response, checkForInlineSelector(request));
    }

    private boolean checkForInlineSelector(SlingHttpServletRequest request) {
        return Arrays.asList(request.getRequestPathInfo().getSelectors()).contains(INLINE_SELECTOR);
    }


    private byte[] getByteArray(Asset asset) throws IOException {
        byte[] bytes = new byte[]{};
        Resource resource = asset.getOriginal();
        if (resource != null) {
            InputStream stream = resource.adaptTo(InputStream.class);
            if (stream != null) {
                try {
                    bytes = IOUtils.toByteArray(stream);
                } finally {
                    stream.close();
                }
            }
        }
        return bytes;
    }

    private void sendResponse(byte[] bytes, Asset asset, SlingHttpServletResponse response, boolean inline) throws IOException {
        response.setContentType(asset.getMimeType());
        response.setContentLength(bytes.length);
        if (inline) {
            response.setHeader(CONTENT_DISPOSITION, "inline");
        } else {
            response.setHeader(CONTENT_DISPOSITION, "attachment; filename=\"" + asset.getName() + "\"");
        }
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes);
        outputStream.close();
    }
}
