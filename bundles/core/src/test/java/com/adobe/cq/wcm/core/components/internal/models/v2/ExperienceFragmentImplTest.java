package com.adobe.cq.wcm.core.components.internal.models.v2;


import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.models.dummy.DummyTestImageModel;
import com.adobe.cq.wcm.core.components.internal.models.dummy.DummyTestTextModel;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.jackrabbit.JcrConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@ExtendWith(AemContextExtension.class)
public class ExperienceFragmentImplTest {
    
    private static final String TEST_BASE = "/experiencefragment/v2";
    private static final String PAGE_PATH = "/content/experience-fragment-page";
    private static final String FRAGMENT_PAGE_LOCATION = "/content/experience-fragments/fragment";
    
    public static final AemContext CONTEXT =  CoreComponentTestContext.newAemContext();
    
    @BeforeEach
    void setUp(){
        CONTEXT.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, PAGE_PATH);
        CONTEXT.load().json(TEST_BASE + "/experiencefragment-test-fragment.json", FRAGMENT_PAGE_LOCATION);
        CONTEXT.currentPage(PAGE_PATH);
        CONTEXT.currentResource( PAGE_PATH + "/" + JcrConstants.JCR_CONTENT + "/root/responsivegrid/experiencefragment");
        CONTEXT.addModelsForPackage("com.adobe.cq.wcm.core.components.internal.models.dummy");
        CONTEXT.addModelsForClasses("com.adobe.cq.wcm.core.components.internal.models.v2");
    }
    
    
    @Test
    public void test_export(){
    
        ContainerExporter exporter = CONTEXT.request().adaptTo(ContainerExporter.class);
        
        assertNotNull(exporter);
        
        assertEquals(2, exporter.getExportedItems().size());
        
        DummyTestTextModel textExporter = (DummyTestTextModel) exporter.getExportedItems().get("text");
        DummyTestImageModel imageExporter = (DummyTestImageModel) exporter.getExportedItems().get("image");
        
        assertNotNull(textExporter);
        assertNotNull(imageExporter);
        
        assertEquals("Hi there", textExporter.getText());
        assertEquals("/content/dam/we-retail-journal/hq/thunder.png", imageExporter.getUrl());
    }
    
    
    
}