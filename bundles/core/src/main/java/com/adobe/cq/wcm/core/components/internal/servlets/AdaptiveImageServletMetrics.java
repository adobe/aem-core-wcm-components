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
package com.adobe.cq.wcm.core.components.internal.servlets;

import org.apache.sling.commons.metrics.Counter;
import org.apache.sling.commons.metrics.MetricsService;
import org.apache.sling.commons.metrics.Timer;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service=AdaptiveImageServletMetrics.class)
public class AdaptiveImageServletMetrics {
    
    private static final String BASENAME = "com.adobe.cq.wcm.core.components.internal.servlets.AdaptiveImageServlet:";
    
    @Reference
    MetricsService metricsService;
    
    // the total number of invocations of the servlet
    private Counter invocations;
    // how often the original rendition was used as basis for the rendition
    private Counter originalRenditionUsed;
    // how often a base rendition has been rejected because it exceeded the configured limits
    private Counter baseRenditionRejected;
    // how often a new rendition was actually created
    private Counter imageStreamed;
    // record the duration of the request
    private Timer requestDuration;
    // how often the image couldn't served
    private Counter imageErrors;
    
    @Activate
    public void activate() {
        invocations = metricsService.counter(BASENAME + "invocations");
        originalRenditionUsed = metricsService.counter(BASENAME + "original-rendition-used");
        baseRenditionRejected = metricsService.counter(BASENAME + "base-rendition-rejected-because-of-size");
        imageStreamed = metricsService.counter(BASENAME + "rendition-rendered");
        requestDuration = metricsService.timer(BASENAME + "request-duration");
        imageErrors = metricsService.counter(BASENAME + "image-errors");
    }
    
    public void markServletInvocation() {
        invocations.increment();
    }
    
    public void markOriginalRenditionUsed() {
        originalRenditionUsed.increment();
    }
    
    public void markRejectedTooLargeRendition() {
        baseRenditionRejected.increment();
    }
    
    public void markImageStreamed() {
        imageStreamed.increment();
    }

    public void markImageErrors() {
        imageErrors.increment();
    }
    
    public Timer.Context startDurationRecording() {
        return requestDuration.time();
    }
    

}
