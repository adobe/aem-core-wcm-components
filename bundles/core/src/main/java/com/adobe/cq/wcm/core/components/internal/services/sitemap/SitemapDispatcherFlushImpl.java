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

import java.util.Arrays;
import java.util.stream.Stream;

import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.sitemap.generator.SitemapGenerator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.internal.Utils;
import com.day.cq.replication.*;

@Component(
    service = EventHandler.class,
    property = {
        EventConstants.EVENT_TOPIC + "=" + SitemapGenerator.EVENT_TOPIC_SITEMAP_UPDATED,
        EventConstants.EVENT_TOPIC + "=" + SitemapGenerator.EVENT_TOPIC_SITEMAP_PURGED,
        EventConstants.EVENT_TOPIC + "=" + ReplicationEvent.EVENT_TOPIC
    },
    configurationPolicy = ConfigurationPolicy.REQUIRE
)
public class SitemapDispatcherFlushImpl implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SitemapDispatcherFlushImpl.class);
    private static final AgentFilter FLUSH_AGENT_FILTER = agent -> agent.getConfiguration().isSpecific() &&
        agent.getConfiguration().isTriggeredOnReceive();
    private static final String SITEMAP_STORAGE_ROOT = "/var/sitemaps";

    @Reference
    private SlingRepository repository;
    @Reference
    private Replicator replicator;

    @Override
    public void handleEvent(Event event) {
        String[] paths = getFlushPaths(event).toArray(String[]::new);
        if (paths.length == 0) {
            return;
        }

        Session session = null;
        try {
            session = repository.loginService(Utils.COMPONENTS_SERVICE, repository.getDefaultWorkspace());

            ReplicationOptions opts = new ReplicationOptions();
            opts.setSuppressStatusUpdate(true);
            opts.setSuppressVersions(true);
            opts.setUpdateAlias(false);
            opts.setFilter(FLUSH_AGENT_FILTER);

            replicator.replicate(session, ReplicationActionType.DELETE, paths, opts);
        } catch (Exception ex) {
            LOG.warn("Failed to queue flush of sitemap: {}", ex.getMessage(), ex);
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }

    private Stream<String> getFlushPaths(Event event) {
        Stream<String> storagePaths;

        if (event.getTopic().equals(SitemapGenerator.EVENT_TOPIC_SITEMAP_UPDATED)
            || event.getTopic().equals(SitemapGenerator.EVENT_TOPIC_SITEMAP_PURGED)) {
            storagePaths = Stream.of((String) event.getProperty(SitemapGenerator.EVENT_PROPERTY_SITEMAP_STORAGE_PATH));
        } else if (event.getTopic().equals(ReplicationEvent.EVENT_TOPIC) && !event.containsProperty("event.application")) {
            // non-distributed replication events, inspired by the configuration of the ChainReplicationProcessor
            storagePaths = Arrays.stream(ReplicationEvent.fromEvent(event).getReplicationAction().getPaths());
        } else {
            storagePaths = Stream.empty();
        }

        return storagePaths.flatMap(this::getFlushPathsForStoragePath);
    }

    private Stream<String> getFlushPathsForStoragePath(String storagePath) {
        String name = ResourceUtil.getName(storagePath);
        String parentPath = ResourceUtil.getParent(storagePath);
        if (parentPath == null || parentPath.length() < SITEMAP_STORAGE_ROOT.length()) {
            // handle corner cases to prevent any runtime exceptions
            return Stream.empty();
        }

        String sitemapContentRoot = parentPath.substring(SITEMAP_STORAGE_ROOT.length());
        String sitemapContentPath = name.equals("sitemap.xml")
            // for the default name, no selector is added to avoid redundancy
            ? sitemapContentRoot + '.' + name
            // for named sitemaps a sitemap selector is added
            : sitemapContentRoot + ".sitemap." + name;

        // map the storage path to all applicable content paths, that includes the sitemap-index potentially
        return Stream.of(sitemapContentPath, sitemapContentRoot + ".sitemap-index.xml");
    }
}
