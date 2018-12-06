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
package core.wcm.components.examples.it.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.concurrent.Callable;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.junit.annotations.SlingAnnotationsTestRunner;
import org.apache.sling.junit.annotations.TestReference;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import core.wcm.components.examples.core.models.HelloWorldModel;

/** 
 *  Test case which uses OSGi services injection
 *  
 *  <p>It relies on the <tt>ResourceResolverFactory</tt> to create test resources
 *  and then adapt them to the class under test - <tt>HelloWorldModel</tt>.</p>
 */
@RunWith(SlingAnnotationsTestRunner.class)
// ignore possible null pointer exceptions, deprecation warnings, loginAdmin usage, and generic exceptions
@SuppressWarnings({"squid:S2259", "squid:CallToDeprecatedMethod", "AEM Rules:AEM-11", "squid:S00112"})
public class HelloWorldModelServerSideTest {

    @TestReference
    private ResourceResolverFactory rrf;

    @TestReference
    private SlingSettingsService settings;
   
    
    @Before
    public void prepareData() throws Exception {
        new AdminResolverCallable() {
            @Override
            protected void call0(ResourceResolver rr) throws Exception {
                rr.create(rr.getResource("/tmp"), "testResource", Collections.<String, Object> emptyMap());
            }
        }.call();
    }
    
    @After
    public void cleanupData() throws Exception {
        new AdminResolverCallable() {
            @Override
            protected void call0(ResourceResolver rr) throws Exception {
                Resource testResource = rr.getResource("/tmp/testResource");
                if ( testResource != null ) {
                    rr.delete(testResource);
                }
            }
        }.call();
    }
    
    @Test
    public void testHelloWorldModelServerSide() throws Exception {
        
        assertNotNull("Expecting the ResourceResolverFactory to be injected by Sling test runner", rrf);
        assertNotNull("Expecting the SlingSettingsService to be injected by Sling test runner", settings);
        
        new AdminResolverCallable() {
            @Override
            protected void call0(ResourceResolver rr) throws Exception {
                Resource testResource = rr.getResource("/tmp/testResource");
                
                HelloWorldModel hello = testResource.adaptTo(HelloWorldModel.class);
                
                assertNotNull("Expecting HelloWorldModel to be adapted from Resource", hello);

                assertTrue("Expecting the HelloWorldModel to return the slingId as part of the message", 
                        hello.getMessage().contains(settings.getSlingId()));
            }
        }.call();        
    }
    
    
    private abstract class AdminResolverCallable implements Callable<Void> {

        @Override
        public Void call() throws Exception {
            
            if ( rrf == null ) {
                throw new IllegalStateException("ResourceResolverFactory not injected");
            }
            
            @SuppressWarnings("deprecation") // fine for testing
            ResourceResolver rr = rrf.getAdministrativeResourceResolver(null);
            try {
                call0(rr);
                rr.commit();
            } finally {
                if ( rr != null ) {
                    rr.close();
                }
            }               
            return null;
        }
        
        protected abstract void call0(ResourceResolver rr) throws Exception;
        
    }    
}
