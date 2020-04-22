/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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
package com.adobe.cq.wcm.core.components.internal.services.documentcloudviewer;

import com.adobe.cq.wcm.core.components.internal.services.documentcloudviewer.DocumentCloudViewerConfigService;
import com.adobe.cq.wcm.core.components.internal.services.documentcloudviewer.DocumentCloudViewerConfig;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

@Component(service = DocumentCloudViewerConfigService.class,configurationPolicy= ConfigurationPolicy.REQUIRE)
@Designate(ocd = DocumentCloudViewerConfig.class)
public class DocumentCloudViewerConfigServiceImpl implements DocumentCloudViewerConfigService {

    private DocumentCloudViewerConfig config;

    @Reference
    private transient SlingSettingsService slingSettingsService;

    @Activate
    public void activate(DocumentCloudViewerConfig config) {
        this.config = config;
    }

    @Override
    public String getClientId() {
        return config.clientId();
    }

    @Override
    public String getReportSuiteId() {
        return config.reportSuiteId();
    }
}