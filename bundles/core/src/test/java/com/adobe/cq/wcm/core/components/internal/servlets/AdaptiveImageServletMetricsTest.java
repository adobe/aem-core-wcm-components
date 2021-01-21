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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.sling.commons.metrics.Counter;
import org.apache.sling.commons.metrics.MetricsService;
import org.apache.sling.commons.metrics.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
public class AdaptiveImageServletMetricsTest {

    public final AemContext context = new AemContext();
    
    
    @BeforeEach
    void setup() {
        MetricsService s = mock (MetricsService.class);
        Timer timer = mock(Timer.class);
        when(timer.time()).thenReturn(mock(Timer.Context.class));
        when(s.timer(Mockito.anyString())).thenReturn(timer);
        when(s.counter(Mockito.anyString())).thenReturn(mock(Counter.class));
        context.registerService(MetricsService.class, s);
    }
    
    @Test
    void test() {
        AdaptiveImageServletMetrics metrics = new AdaptiveImageServletMetrics();
        context.registerInjectActivateService(metrics);
        
        metrics.markOriginalRenditionUsed();
        metrics.markRejectedTooLargeRendition();
        metrics.markServletInvocation();
        metrics.markImageStreamed();
        metrics.markImageErrors();
        Timer.Context c = metrics.startDurationRecording();
        assertNotNull(c); // silly, as it is a mock ...
      
        
    }

}
