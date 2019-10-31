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
import java.util.Locale;
import java.util.Calendar;
import java.util.Collections;
import java.util.Optional;
import java.util.TimeZone;

import javax.annotation.Nonnull;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.resourceTypes=dam:Asset",
                "sling.servlet.resourceTypes=nt:file",
                "sling.servlet.selectors=" + DownloadServlet.SELECTOR
        }
)
public class DownloadServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(DownloadServlet.class);
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
                sendResource(request, response, filename, downloadDataResource);
            }
        } else {

            if (isUnchanged(asset.getLastModified(), request)) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }
            Optional<InputStream> inputStream = Optional.empty();
            try {
                inputStream = getInputStream(asset);
                if (!inputStream.isPresent()) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_OK);
                sendResponse(inputStream.get(), asset.getOriginal().getSize(), asset.getMimeType(), asset.getName(), asset.getLastModified(), response, checkForInlineSelector(request));
            } finally {
                if (inputStream.isPresent()) {
                    inputStream.get().close();
                }
            }
        }
    }

    private void sendResource(SlingHttpServletRequest request, SlingHttpServletResponse response, String filename,
            Resource downloadDataResource) throws IOException {
        ValueMap valueMap = downloadDataResource.adaptTo(ValueMap.class);
        if (valueMap != null) {
            Calendar calendar = valueMap.get(JcrConstants.JCR_LASTMODIFIED, Calendar.class);
            if (calendar != null) {
                if (isUnchanged(calendar.getTimeInMillis(), request)) {
                    LOG.debug("sending SC_NOT_MODIFIED for {}", request.getResource().getPath());
                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    return;
                }
            }
            Optional<InputStream> inputStream = Optional.empty();
            try {
                inputStream = getInputStream(downloadDataResource);
                if (!inputStream.isPresent()) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                long size = getResourceSize(downloadDataResource);
                long timestamp = 0;
                if (calendar != null) {
                    timestamp = calendar.getTimeInMillis();
                }
                String mimeType = valueMap.get(JcrConstants.JCR_MIMETYPE, String.class);
                if (StringUtils.isNotEmpty(mimeType)) {
                    sendResponse(inputStream.get(), size, mimeType, filename, timestamp, response, checkForInlineSelector(request));
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            } finally {
                if (inputStream.isPresent()) {
                    inputStream.get().close();
                }
            }
        }
    }

    /**
     * Determines the size of a binary resource just by using the JCR API; this hopefully avoids having to read
     * the complete {@code InputStream} to determine the actual size of the binary.
     *
     * @param resource the actual resource
     * @return the size in bytes, -1 if an error occurs, the resource is not backed by a JCR node, or if there is not a {@code JCR_DATA} property
     */
    private long getResourceSize (Resource resource) {
        Node node = resource.adaptTo(Node.class);
        if (node != null) {
            Property jcrData;
            try {
                jcrData = node.getProperty(JcrConstants.JCR_DATA);
                if (jcrData != null) {
                    Binary binary = jcrData.getBinary();
                    if (binary != null) {
                        return binary.getSize();
                    }
                }
            } catch (RepositoryException e) {
                LOG.error("Cannot determine size of binary at {}", resource.getPath(),e);
            }
        }
        return -1;
    }

    private boolean checkForInlineSelector(SlingHttpServletRequest request) {
        return Arrays.asList(request.getRequestPathInfo().getSelectors()).contains(INLINE_SELECTOR);
    }

    private Optional<InputStream> getInputStream (Asset asset) {
        return Optional.ofNullable(asset.getOriginal())
                .map(r -> r.adaptTo(InputStream.class));
    }

    private Optional<InputStream> getInputStream (Resource fileResource) {
        return Optional.ofNullable(fileResource.getValueMap().get(JcrConstants.JCR_DATA, InputStream.class));
    }

    private void sendResponse(InputStream stream, long size, String mimeType, String filename, long lastModifiedDate, SlingHttpServletResponse response,
            boolean inline) throws IOException {
        response.setContentType(mimeType);

        // only set the size contentLength header if we have valid data
        if (size != -1) {
            response.setContentLength((int) size);
        }
        if (inline) {
            response.setHeader(CONTENT_DISPOSITION_HEADER, "inline");
        } else {
            response.setHeader(CONTENT_DISPOSITION_HEADER, "attachment; filename=\"" + filename + "\"");
        }
        if (lastModifiedDate > 0) {
            response.setHeader(HttpConstants.HEADER_LAST_MODIFIED, getLastModifiedDate(lastModifiedDate));
        }
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            IOUtils.copy(stream, outputStream);
        }
    }

    private String getLastModifiedDate(long lastModifiedDate) {
        SimpleDateFormat df = new SimpleDateFormat(RFC_DATE_FORMAT, Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(lastModifiedDate);
    }

    private boolean isUnchanged(long lastModifiedDate, SlingHttpServletRequest request) {
        boolean match = false;
        if (Collections.list(request.getHeaderNames()).contains(HttpConstants.HEADER_IF_MODIFIED_SINCE)) {
            String headerDate = request.getHeader(HttpConstants.HEADER_IF_MODIFIED_SINCE);
            if (StringUtils.isNotEmpty(headerDate)) {
                SimpleDateFormat df = new SimpleDateFormat(RFC_DATE_FORMAT, Locale.US);
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
