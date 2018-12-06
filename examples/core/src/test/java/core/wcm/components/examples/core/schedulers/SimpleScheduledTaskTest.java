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
package core.wcm.components.examples.core.schedulers;

import org.junit.Rule;
import org.junit.Test;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;
import uk.org.lidalia.slf4jtest.TestLoggerFactoryResetRule;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

public class SimpleScheduledTaskTest {

    @Rule
    public final TestLoggerFactoryResetRule testLoggerFactoryResetRule = new TestLoggerFactoryResetRule();

    private SimpleScheduledTask fixture = new SimpleScheduledTask();

    private TestLogger logger = TestLoggerFactory.getTestLogger(fixture.getClass());

    @Test
    public void run() {
        SimpleScheduledTask.Config config = mock(SimpleScheduledTask.Config.class);
        when(config.myParameter()).thenReturn("parameter value");

        fixture.activate(config);
        fixture.run();

        List<LoggingEvent> events = logger.getLoggingEvents();
        assertEquals(1, events.size());
        LoggingEvent event = events.get(0);
        assertEquals(Level.DEBUG, event.getLevel());
        assertEquals(1, event.getArguments().size());
        assertEquals("parameter value", event.getArguments().get(0));
    }
}
