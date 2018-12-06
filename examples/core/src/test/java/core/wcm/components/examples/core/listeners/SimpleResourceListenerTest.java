/*
 *  Copyright 2018 Adobe Systems Incorporated
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
package core.wcm.components.examples.core.listeners;

import org.apache.sling.api.SlingConstants;
import org.junit.Test;
import org.osgi.service.event.Event;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.util.Collections;
import java.util.List;
import static org.junit.Assert.*;

public class SimpleResourceListenerTest {

    private SimpleResourceListener fixture = new SimpleResourceListener();

    private TestLogger logger = TestLoggerFactory.getTestLogger(fixture.getClass());

    @Test
    public void handleEvent() {
        Event resourceEvent = new Event("event/topic", Collections.singletonMap(SlingConstants.PROPERTY_PATH, "/content/test"));

        fixture.handleEvent(resourceEvent);

        List<LoggingEvent> events = logger.getLoggingEvents();
        assertEquals(1, events.size());
        LoggingEvent event = events.get(0);
        assertEquals(Level.DEBUG, event.getLevel());
        assertEquals(2, event.getArguments().size());
        assertEquals("event/topic", event.getArguments().get(0));
        assertEquals("/content/test", event.getArguments().get(1));
    }
}
