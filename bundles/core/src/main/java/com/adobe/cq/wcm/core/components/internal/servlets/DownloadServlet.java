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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.TimeZone;
import javax.annotation.Nonnull;
import javax.servlet.Servlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import com.day.cq.dam.api.Asset;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;

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

    private static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    private static final String RFC_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final String IF_MODIFIED_SINCE_HEADER = "If-Modified-Since";
    private static final String IF_NONE_MATCH_HEADER = "If-None-Match";

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

        if (checkETagMatch(bytes, request) || checkLastModifiedAfter(asset, request)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
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

    @SuppressWarnings("UnstableApiUsage")
    private void sendResponse(byte[] bytes, Asset asset, SlingHttpServletResponse response, boolean inline) throws IOException {
        response.setContentType(asset.getMimeType());
        response.setContentLength(bytes.length);
        if (inline) {
            response.setHeader(CONTENT_DISPOSITION_HEADER, "inline");
        } else {
            response.setHeader(CONTENT_DISPOSITION_HEADER, "attachment; filename=\"" + asset.getName() + "\"");
        }
        response.setHeader("ETag", ByteSource.wrap(bytes).hash(Hashing.md5()).toString().toUpperCase());
        response.setHeader("Last-Modified", getLastModifiedDate(asset));
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes);
        outputStream.close();
    }

    private String getLastModifiedDate(Asset asset) {
        SimpleDateFormat df = new SimpleDateFormat(RFC_DATE_FORMAT);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(asset.getLastModified());
    }

    @SuppressWarnings("UnstableApiUsage")
    private boolean checkETagMatch (byte [] bytes, SlingHttpServletRequest request) throws IOException {
        boolean match = false;
        if (Collections.list(request.getHeaderNames()).contains(IF_NONE_MATCH_HEADER)) {
            if (StringUtils.equals(request.getHeader(IF_NONE_MATCH_HEADER),
                    ByteSource.wrap(bytes).hash(Hashing.md5()).toString().toUpperCase())) {
                match = true;
            }

        }
        return match;
    }

    private boolean checkLastModifiedAfter(Asset asset, SlingHttpServletRequest request) {
        boolean match = false;
        if (Collections.list(request.getHeaderNames()).contains(IF_MODIFIED_SINCE_HEADER)) {
            String headerDate = request.getHeader(IF_MODIFIED_SINCE_HEADER);
            if (StringUtils.isNotEmpty(headerDate)) {
                SimpleDateFormat df = new SimpleDateFormat(RFC_DATE_FORMAT);
                df.setTimeZone(TimeZone.getTimeZone("GMT"));
                try {
                    long headerTime = df.parse(headerDate).getTime();
                    long assetTime = df.parse(df.format(asset.getLastModified())).getTime();
                    if (headerTime >= assetTime) {
                        match = true;
                    }
                } catch (ParseException e) {
                    // do nothing
                }
            }
        }
        return match;
    }
}
