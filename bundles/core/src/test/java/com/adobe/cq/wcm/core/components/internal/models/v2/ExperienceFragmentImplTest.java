/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~   http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.wcm.core.components.internal.models.v2;


import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.models.dummy.DummyTestImageModel;
import com.adobe.cq.wcm.core.components.internal.models.dummy.DummyTestTextModel;
import com.adobe.cq.wcm.core.components.models.ExperienceFragment;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.jackrabbit.JcrConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@ExtendWith(AemContextExtension.class)
public class ExperienceFragmentImplTest {
    
    private static final String TEST_BASE = "/experiencefragment/v2";
    private static final String PAGE_PATH = "/content/experience-fragment-page";
    private static final String FRAGMENT_PAGE_LOCATION = "/content/experience-fragments/fragment";
    
    private static final AemContext CONTEXT = CoreComponentTestContext.newAemContext();
    
    @BeforeEach
    void setUp(){
        CONTEXT.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, PAGE_PATH);
        CONTEXT.load().json(TEST_BASE + "/experiencefragment-test-fragment.json", FRAGMENT_PAGE_LOCATION);
        CONTEXT.addModelsForPackage("com.adobe.cq.wcm.core.components.internal.models.dummy");
        CONTEXT.addModelsForClasses("com.adobe.cq.wcm.core.components.internal.models.v2");
        CONTEXT.currentPage(PAGE_PATH);
        CONTEXT.currentResource( PAGE_PATH + "/" + JcrConstants.JCR_CONTENT + "/root/responsivegrid/experiencefragment");
    }
    
    
    @Test
    public void test_export(){
    
        
        ContainerExporter exporter = CONTEXT.request().adaptTo(ContainerExporter.class);
        
        assertNotNull(exporter);
        
        assertEquals(2, exporter.getExportedItems().size());
        
        DummyTestTextModel textExporter = (DummyTestTextModel) exporter.getExportedItems().get("text");
        DummyTestImageModel imageExporter = (DummyTestImageModel) exporter.getExportedItems().get("image");
        
        String[] order = exporter.getExportedItemsOrder();
        
        String[] expectedOrder = new String[]{"text","image"};
        assertArrayEquals(expectedOrder, order);
        
        assertNotNull(textExporter);
        assertNotNull(imageExporter);
        
        assertEquals("Hi there", textExporter.getText());
        assertEquals("/content/dam/we-retail-journal/hq/thunder.png", imageExporter.getUrl());
        
        ExperienceFragment experienceFragment = CONTEXT.request().adaptTo(ExperienceFragment.class);

        String exportedCssClass =  experienceFragment.getCssClassNames();

        assertTrue("css class should always contain 'aem-xf'", exportedCssClass.contains("aem-xf"));
        assertTrue("should contain the css class in the properties", exportedCssClass.contains("testCssClass"));

        assertTrue("path should be configured", experienceFragment.isPathConfigured());
    }
    
    
    
}