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

import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.internal.services.documentcloudviewer.DocumentCloudViewerConfigService;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.annotation.Annotation;

class DocumentCloudViewerConfigServiceImplTest {

    @Test
    void testActivate() {
        DocumentCloudViewerConfigServiceImpl service = new DocumentCloudViewerConfigServiceImpl(){
        
            @Override
            public String getReportSuiteId() {
                return "abc";
            }
        
            @Override
            public String getClientId() {
                return "123";
            }
        };

        assertNotNull(service);
        assertEquals("123", service.getClientId());
        assertEquals("abc", service.getReportSuiteId());
    }

    @Test
    void testActivate2() {
        DocumentCloudViewerConfigServiceImpl service = new DocumentCloudViewerConfigServiceImpl();
        DocumentCloudViewerConfig config = new DocumentCloudViewerConfig(){
        
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }
        
            @Override
            public String reportSuiteId() {
                return "activate";
            }
        
            @Override
            public String clientId() {
                return "activate";
            }
        };

        service.activate(config);

        assertEquals("activate", service.getClientId());
        assertEquals("activate", service.getReportSuiteId());
    }

    @Test
    void testNull() {
        DocumentCloudViewerConfigServiceImpl service = new DocumentCloudViewerConfigServiceImpl(){
        
            @Override
            public String getReportSuiteId() {
                return null;
            }
        
            @Override
            public String getClientId() {
                return null;
            }
        };

        service.activate(null);

        assertNotNull(service);
        assertNull(service.getClientId());
        assertNull(service.getReportSuiteId());
    }
}
