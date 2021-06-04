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
package com.adobe.cq.wcm.core.components.internal.services.sitemap;

import javax.jcr.Session;

import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.serviceusermapping.ServiceUserMapped;
import org.apache.sling.sitemap.generator.SitemapGenerator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.internal.Utils;
import com.day.cq.replication.AgentFilter;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.replication.Replicator;

/**
 * An {@link EventHandler} that schedules updated sitemaps for replication.
 */
@Component(
    service = EventHandler.class,
    property = {
        EventConstants.EVENT_TOPIC + "=" + SitemapGenerator.EVENT_TOPIC_SITEMAP_UPDATED
    }
)
public class SitemapReplicatorImpl implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SitemapReplicatorImpl.class);

    @Reference(target = "(subServiceName=" + Utils.COMPONENTS_SERVICE + ")")
    private ServiceUserMapped serviceUser;
    @Reference
    private Replicator replicator;
    @Reference
    private SlingRepository repository;

    @Override
    public void handleEvent(Event event) {
        Session session = null;
        try {
            session = repository.loginService(Utils.COMPONENTS_SERVICE, repository.getDefaultWorkspace());
            String storagePath = (String) event.getProperty(SitemapGenerator.EVENT_PROPERTY_SITEMAP_STORAGE_PATH);
            ReplicationOptions opts = new ReplicationOptions();
            opts.setFilter(AgentFilter.DEFAULT);
            replicator.replicate(session, ReplicationActionType.ACTIVATE, storagePath, opts);
        } catch (Exception ex) {
            LOG.warn("Failed to queue replication of sitemap: {}", ex.getMessage(), ex);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }
}
