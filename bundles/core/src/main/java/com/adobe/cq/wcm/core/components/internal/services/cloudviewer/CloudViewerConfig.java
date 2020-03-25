package com.adobe.cq.wcm.core.components.internal.services.cloudviewer;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Document Cloud Viewer - Configuration", description = "Document Cloud Viewer Configuration")
public @interface CloudViewerConfig {

    @AttributeDefinition(name = "Client ID",
            description = "Document Cloud Viewer API Client ID (Required)",
            type = AttributeType.STRING)
    String clientId() default "";

    @AttributeDefinition(name = "Report Suite ID",
            description = "Adobe Analytics Report Suite ID (Optional)",
            type = AttributeType.STRING)
    String reportSuiteId() default "";
}