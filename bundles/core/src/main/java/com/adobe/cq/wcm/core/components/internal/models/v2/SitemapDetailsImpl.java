/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.sitemap.SitemapInfo;
import org.apache.sling.sitemap.SitemapService;

import com.adobe.cq.wcm.core.components.models.SitemapDetails;
import com.adobe.granite.ui.components.Value;

/**
 * This model is used by the sitemap root dialog field in order to provide additional information for the UI.
 */
@Model(adaptables = SlingHttpServletRequest.class, adapters = { SitemapDetails.class })
public class SitemapDetailsImpl implements SitemapDetails {

    private static final String ONE_DECIMAL_FORMAT = "%.1f";
    private static final String ONE_DECIMAL_W_UNIT_FORMAT = ONE_DECIMAL_FORMAT + " %s";

    private final boolean isSitemapRoot;
    private final boolean beingGenerated;
    private final boolean hasWarning;
    private final List<Detail> details;

    @Inject
    public SitemapDetailsImpl(
        @RequestAttribute(name = Value.CONTENTPATH_ATTRIBUTE, injectionStrategy = InjectionStrategy.OPTIONAL) String contentPath,
        @Self SlingHttpServletRequest request,
        @OSGiService SitemapService sitemapService
    ) {
        Resource resource = Optional.ofNullable(request.getResourceResolver().getResource(contentPath))
            .orElseGet(request::getResource);
        isSitemapRoot = resource.getValueMap().get(SitemapService.PROPERTY_SITEMAP_ROOT, Boolean.FALSE);
        if (isSitemapRoot) {
            beingGenerated = sitemapService.isSitemapGenerationPending(resource);
            Collection<SitemapInfo> infos = sitemapService.getSitemapInfo(resource);
            details = infos.stream().map(SitemapDetailsImpl::newDetail).collect(Collectors.toList());
            hasWarning = !infos.stream().map(SitemapInfo::isWithinLimits).reduce(Boolean::logicalAnd).orElse(Boolean.TRUE);
        } else {
            details = Collections.emptyList();
            hasWarning = false;
            beingGenerated = false;
        }
    }

    @Override public List<Detail> getDetails() {
        return Collections.unmodifiableList(details);
    }

    @Override public boolean isSitemapRoot() {
        return isSitemapRoot;
    }

    @Override public boolean isGenerationPending() {
        return beingGenerated;
    }

    @Override public boolean hasWarning() {
        return hasWarning;
    }

    private static Detail newDetail(SitemapInfo info) {
        int lastDot = info.getUrl().lastIndexOf('.');
        int prevDot = info.getUrl().substring(0, lastDot).lastIndexOf('.');
        String name = info.getUrl().substring(prevDot + 1, lastDot);
        return new Detail() {
            @Override public String getName() {
                return name;
            }

            @Override public String getUrl() {
                return info.getUrl();
            }

            @Override public String getSize() {
                return formatFileSize(info.getSize());
            }

            @Override public String getEntries() {
                return formatNumber(info.getEntries());
            }
        };
    }

    private static String formatFileSize(int bytes) {
        if (bytes > 1024 * 1024) {
            // mb
            return String.format(ONE_DECIMAL_W_UNIT_FORMAT, (float) bytes / (1024 * 1024), "MB");
        } else if (bytes > 1024) {
            return String.format(ONE_DECIMAL_W_UNIT_FORMAT, (float) bytes / 1024, "KB");
        } else if (bytes < 0) {
            return "-";
        } else {
            return String.format("%.1f %s", (float) bytes, "Byte");
        }
    }

    private static String formatNumber(int number) {
        if (number > 1000) {
            return String.format(ONE_DECIMAL_FORMAT, (float) number / 1000);
        } else if (number < 0) {
            return "-";
        } else {
            return String.valueOf(number);
        }
    }
}
