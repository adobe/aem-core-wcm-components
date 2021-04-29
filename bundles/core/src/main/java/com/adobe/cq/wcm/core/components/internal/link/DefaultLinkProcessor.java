/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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
package com.adobe.cq.wcm.core.components.internal.link;

import java.util.Optional;

import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.services.link.LinkProcessor;
import com.adobe.cq.wcm.core.components.services.link.LinkRequest;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;

@Component(property = Constants.SERVICE_RANKING + ":Integer=" + Integer.MAX_VALUE,
           service = LinkProcessor.class)
public class DefaultLinkProcessor<T> implements LinkProcessor<T> {

    @Reference
    Externalizer externalizer;

    @Override
    public @Nullable Link<T> process(@NotNull LinkRequest<T> linkRequest) {
        return Optional.ofNullable(linkRequest.getPath())
                .map(path -> new LinkImpl<>(path, getProcessedUrl(linkRequest, path), getFullUrl(linkRequest, path),
                linkRequest.getReference(), linkRequest,
                linkRequest.getHtmlAttributes())).orElse(null);
    }

    @Nullable
    private String getFullUrl(@NotNull LinkRequest<T> linkRequest, @NotNull String path) {
        return Optional.ofNullable(linkRequest.getReference())
                .filter(reference -> reference instanceof Page)
                .map(page -> ((Page) page).getContentResource())
                .map(Resource::getResourceResolver)
                .map(rr -> externalizer.publishLink(rr, linkRequest.getPath()))
                .orElse(
                        Optional.ofNullable(linkRequest.getReference())
                                .filter(reference -> reference instanceof Resource)
                                .map(resource -> ((Resource) resource).getResourceResolver())
                                .map(rr -> externalizer.publishLink(rr, path))
                                .orElse(linkRequest.getPath())
                );
    }

    @Nullable
    private String getProcessedUrl(@NotNull LinkRequest<T> linkRequest, @NotNull String path) {
        return Optional.ofNullable(linkRequest.getReference())
                .filter(reference -> reference instanceof Page)
                .map(page -> ((Page) page).getContentResource())
                .map(Resource::getResourceResolver)
                .map(rr -> rr.map(path))
                .orElse(
                        Optional.ofNullable(linkRequest.getReference())
                                .filter(reference -> reference instanceof Resource)
                                .map(resource -> ((Resource) resource).getResourceResolver())
                                .map(rr -> rr.map(path))
                                .orElse(linkRequest.getPath())
                );
    }
}
