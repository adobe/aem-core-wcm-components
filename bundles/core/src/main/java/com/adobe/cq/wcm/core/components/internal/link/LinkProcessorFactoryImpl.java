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

import java.util.Map;

import org.apache.sling.commons.osgi.Order;
import org.apache.sling.commons.osgi.RankedServices;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.services.link.LinkProcessor;
import com.adobe.cq.wcm.core.components.services.link.LinkProcessorFactory;
import com.adobe.cq.wcm.core.components.services.link.LinkRequest;

@Component(immediate = true, service = LinkProcessorFactory.class)
public class LinkProcessorFactoryImpl implements LinkProcessorFactory {

    private final RankedServices<LinkProcessor> linkProcessors = new RankedServices<>(Order.ASCENDING);

    @Reference(name = "linkProcessor", cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    protected void bindLinkProcessor(final LinkProcessor linkProcessor, final Map<String, Object> props) {
        synchronized (linkProcessors) {
            linkProcessors.bind(linkProcessor, props);
        }
    }

    protected void unbindLinkProcessor(final LinkProcessor linkProcessor, final Map<String, Object> props) {
        synchronized (linkProcessors) {
            linkProcessors.unbind(linkProcessor, props);
        }
    }

    @Override
    public LinkProcessor getProcessor(LinkRequest linkRequest) {
        for (LinkProcessor linkProcessor: linkProcessors) {
            Link link = linkProcessor.process(linkRequest);
            if (link != null) {
                return linkProcessor;
            }
        }
        return null;
    }
}
