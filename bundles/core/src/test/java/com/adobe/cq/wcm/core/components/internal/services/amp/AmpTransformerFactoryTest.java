/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
package com.adobe.cq.wcm.core.components.internal.services.amp;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import org.apache.sling.api.resource.ResourceResolverFactory;
import com.adobe.cq.wcm.core.components.internal.services.amp.AmpTransformerFactory;
import com.adobe.cq.wcm.core.components.internal.services.amp.AmpTransformer;

public class AmpTransformerFactoryTest {

    @Mock
    private ResourceResolverFactory resourceResolverFactoryMock;
    @Mock
    private ResourceResolverFactory resourceResolverFactoryMock2;
    @Mock
    private AmpTransformerFactory.Cfg cfgMock;
    @Mock
    private AmpTransformerFactory.Cfg cfgMock2;
    @InjectMocks
    private AmpTransformerFactory atf;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    public void testFactory() {
        assertEquals(AmpTransformer.class, this.atf.createTransformer().getClass());

        this.atf.activate(this.cfgMock);
        assertEquals(this.cfgMock, this.atf.getCfg());
        this.atf.activate(this.cfgMock2);
        assertEquals(this.cfgMock2, this.atf.getCfg());

        this.atf.setResolverFactory(this.resourceResolverFactoryMock);
        assertEquals(this.resourceResolverFactoryMock, this.atf.getResolverFactory());
        this.atf.setResolverFactory(this.resourceResolverFactoryMock2);
        assertEquals(this.resourceResolverFactoryMock2, this.atf.getResolverFactory());
    }
}
