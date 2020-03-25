package com.adobe.cq.wcm.core.components.internal.services.cloudviewer;

import com.adobe.cq.wcm.core.components.internal.services.cloudviewer.CloudViewerConfigService;
import com.adobe.cq.wcm.core.components.internal.services.cloudviewer.CloudViewerConfig;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

@Component(service = CloudViewerConfigService.class,configurationPolicy= ConfigurationPolicy.REQUIRE)
@Designate(ocd = CloudViewerConfig.class)
public class CloudViewerConfigServiceImpl implements CloudViewerConfigService {

    private CloudViewerConfig config;

    @Reference
    private transient SlingSettingsService slingSettingsService;

    @Activate
    public void activate(CloudViewerConfig config) {
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