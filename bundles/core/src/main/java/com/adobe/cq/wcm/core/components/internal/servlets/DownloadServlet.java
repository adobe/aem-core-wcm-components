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
package com.adobe.cq.wcm.core.components.internal.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
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
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;

import static com.adobe.cq.wcm.core.components.internal.servlets.DownloadServlet.SELECTOR;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.resourceTypes=dam:Asset",
                "sling.servlet.resourceTypes=nt:file",
                "sling.servlet.selectors=" + SELECTOR
        }
)
public class DownloadServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;


    public static final String SELECTOR = "coredownload";
    public static final String INLINE_SELECTOR = "inline";

    private static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    private static final String RFC_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

    @Override
    protected void doGet(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response)
            throws IOException {

        Asset asset = request.getResource().adaptTo(Asset.class);
        if (asset == null) {
            String filename = request.getRequestPathInfo().getSuffix();
            Resource downloadDataResource = request.getResource().getChild(JcrConstants.JCR_CONTENT);
            if (StringUtils.isNotEmpty(filename) && downloadDataResource != null) {
                byte[] bytes = getByteArray(downloadDataResource);
                if (bytes.length == 0) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                ValueMap valueMap = downloadDataResource.adaptTo(ValueMap.class);
                if (valueMap != null) {
                    Calendar calendar = valueMap.get(JcrConstants.JCR_LASTMODIFIED, Calendar.class);
                    if (calendar != null) {
                        if (checkLastModifiedAfter(calendar.getTimeInMillis(), request)) {
                            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                            return;
                        }

                        String mimeType = valueMap.get(JcrConstants.JCR_MIMETYPE, String.class);
                        if (StringUtils.isNotEmpty(mimeType)) {
                            sendResponse(bytes, mimeType, filename, calendar.getTimeInMillis(), response, checkForInlineSelector(request));
                        } else {
                            response.sendError(HttpServletResponse.SC_NOT_FOUND);
                        }

                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    }
                }
            }
        } else {
            byte[] bytes = getByteArray(asset);
            if (bytes.length == 0) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            if (checkLastModifiedAfter(asset.getLastModified(), request)) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }
            sendResponse(bytes, asset.getMimeType(), asset.getName(), asset.getLastModified(), response, checkForInlineSelector(request));
        }
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

    private byte[] getByteArray(Resource fileResource) throws IOException {
        byte[] bytes = new byte[]{};
        InputStream stream = fileResource.getValueMap().get(JcrConstants.JCR_DATA, InputStream.class);
        if (stream != null) {
            try {
                bytes = IOUtils.toByteArray(stream);
            } finally {
                stream.close();
            }
        }
        return bytes;
    }

    private void sendResponse(byte[] bytes, String mimeType, String filename, long lastModifiedDate, SlingHttpServletResponse response,
                              boolean inline) throws IOException {
        response.setContentType(mimeType);
        response.setContentLength(bytes.length);
        if (inline) {
            response.setHeader(CONTENT_DISPOSITION_HEADER, "inline");
        } else {
            response.setHeader(CONTENT_DISPOSITION_HEADER, "attachment; filename=\"" + filename + "\"");
        }
        response.setHeader(HttpConstants.HEADER_LAST_MODIFIED, getLastModifiedDate(lastModifiedDate));
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes);
        outputStream.close();
    }

    private String getLastModifiedDate(long lastModifiedDate) {
        SimpleDateFormat df = new SimpleDateFormat(RFC_DATE_FORMAT);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(lastModifiedDate);
    }

    private boolean checkLastModifiedAfter(long lastModifiedDate, SlingHttpServletRequest request) {
        boolean match = false;
        if (Collections.list(request.getHeaderNames()).contains(HttpConstants.HEADER_IF_MODIFIED_SINCE)) {
            String headerDate = request.getHeader(HttpConstants.HEADER_IF_MODIFIED_SINCE);
            if (StringUtils.isNotEmpty(headerDate)) {
                SimpleDateFormat df = new SimpleDateFormat(RFC_DATE_FORMAT);
                df.setTimeZone(TimeZone.getTimeZone("GMT"));
                try {
                    long headerTime = df.parse(headerDate).getTime();
                    long assetTime = df.parse(df.format(lastModifiedDate)).getTime();
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
