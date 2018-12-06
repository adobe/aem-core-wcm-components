/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package core.wcm.components.examples.core.models;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import junitx.util.PrivateAccessor;

import org.apache.sling.settings.SlingSettingsService;
import org.junit.Before;
import org.junit.Test;

/**
 * Simple JUnit test verifying the HelloWorldModel
 */
public class HelloWorldModelTest {

    //@Inject
    private HelloWorldModel hello;
    
    private String slingId;
    
    @Before
    public void setup() throws Exception {
        SlingSettingsService settings = mock(SlingSettingsService.class);
        slingId = UUID.randomUUID().toString();
        when(settings.getSlingId()).thenReturn(slingId);

        hello = new HelloWorldModel();
        PrivateAccessor.setField(hello, "settings", settings);
        hello.init();
    }
    
    @Test
    public void testGetMessage() throws Exception {
        // some very basic junit tests
        String msg = hello.getMessage();
        assertNotNull(msg);
        assertTrue(msg.length() > 0);
    }

}
