package com.adobe.cq.wcm.core.components.internal.servlets;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.testing.MockPublishUtils;
import com.day.cq.commons.DownloadResource;
import com.day.cq.dam.api.s7dam.utils.PublishUtils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.servlets.post.Modification;
import org.apache.sling.servlets.post.ModificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.*;

@ExtendWith(AemContextExtension.class)
public class DMAssetPostProcessorTest {
    private static final String TEST_BASE = "/image/v2";
    private static final String CONTENT_ROOT = "/content";
    private static final String TEST_APPS_ROOT = "/apps/core/wcm/components";

    private static final String PAGE = CONTENT_ROOT + "/test";
    private static final String CORE_IMAGE__DM_POLICY_ON__NON_DM_ASSET = PAGE + "/jcr:content/root/image31";
    private static final String CORE_IMAGE__DM_POLICY_ON__DM_ASSET = PAGE + "/jcr:content/root/image32";

    private static final EnumSet<ModificationType> SUPPORTED_MODIFICATION_TYPES = EnumSet.of(ModificationType.CREATE, ModificationType.MODIFY);
    private static final Set<ModificationType> NOT_SUPPORTED_MODIFICATION_TYPES = EnumSet.complementOf(SUPPORTED_MODIFICATION_TYPES);

    private static final String EXPECTED_IMAGE_SERVER_URL = "https://s7d9.scene7.com/is/image/";

    private DMAssetPostProcessor servlet = null;;

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    public void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_APPS_JSON, TEST_APPS_ROOT);
        context.load().json("/image/test-content-dam.json", "/content/dam/core/images");
        context.registerService(PublishUtils.class, new MockPublishUtils());
        servlet = context.registerInjectActivateService(new DMAssetPostProcessor());
        context.request().setMethod("POST");
    }

    /*
    The case when component with non DM asset gets assigned a DM asset.
    Post processor should write image server url property
     */
    @Test
    public void fromNonDMtoDMAsset() throws Exception {
        String existingComponent = CORE_IMAGE__DM_POLICY_ON__NON_DM_ASSET;
        prepareResource(existingComponent, getFileReference(CORE_IMAGE__DM_POLICY_ON__DM_ASSET));

        for (ModificationType modificationType : SUPPORTED_MODIFICATION_TYPES) {
            servlet.process(context.request(), Collections.singletonList(new Modification(
                modificationType,
                getFileReferencePath(existingComponent),
                null
            )));
            Resource resource = context.currentResource();
            assertNotNull(resource);
            assertEquals(EXPECTED_IMAGE_SERVER_URL, resource.getValueMap().get(Image.PN_IMAGE_SERVER_URL, String.class));
        }
    }

    /*
    The case when component with DM asset gets assigned a non DM asset.
    Post processor should clear image server url property
     */
    @Test
    public void fromDMtoNonDMAsset() throws Exception {
        String existingComponent = CORE_IMAGE__DM_POLICY_ON__DM_ASSET;
        prepareResource(existingComponent, getFileReference(CORE_IMAGE__DM_POLICY_ON__NON_DM_ASSET));

        for (ModificationType modificationType : SUPPORTED_MODIFICATION_TYPES) {
            servlet.process(context.request(), Collections.singletonList(new Modification(
                modificationType,
                getFileReferencePath(existingComponent),
                null
            )));
            Resource resource = context.currentResource();
            assertNotNull(resource);
            assertNull(resource.getValueMap().get(Image.PN_IMAGE_SERVER_URL, String.class));
        }
    }

    /*
    The case when modification type does not match CREATE or MODIFY.
    In the case post processor should not modify IS URL
     */
    @Test
    public void fromNonDMtoDMAssetNotSupportedModification() throws Exception {
        String existingComponent = CORE_IMAGE__DM_POLICY_ON__NON_DM_ASSET;
        prepareResource(existingComponent, getFileReference(CORE_IMAGE__DM_POLICY_ON__DM_ASSET));

        for (ModificationType modificationType : NOT_SUPPORTED_MODIFICATION_TYPES) {
            servlet.process(context.request(), Collections.singletonList(new Modification(
                modificationType,
                getFileReferencePath(existingComponent),
                null
            )));
            Resource resource = context.currentResource();
            assertNotNull(resource);
            assertNull(resource.getValueMap().get(Image.PN_IMAGE_SERVER_URL, String.class));
        }
    }

    /*
    The case when modification type does not match CREATE or MODIFY.
    In the case post processor should not modify IS URL
     */
    @Test
    public void fromDMtoNonDMAssetNotSupportedModification() throws Exception {
        String existingComponent = CORE_IMAGE__DM_POLICY_ON__DM_ASSET;
        prepareResource(existingComponent, getFileReference(CORE_IMAGE__DM_POLICY_ON__NON_DM_ASSET));

        for (ModificationType modificationType : NOT_SUPPORTED_MODIFICATION_TYPES) {
            servlet.process(context.request(), Collections.singletonList(new Modification(
                modificationType,
                getFileReferencePath(existingComponent),
                null
            )));
            Resource resource = context.currentResource();
            assertNotNull(resource);
            assertEquals(EXPECTED_IMAGE_SERVER_URL, resource.getValueMap().get(Image.PN_IMAGE_SERVER_URL, String.class));
        }
    }

    /*
    The case when modification path does not match to request's resource path.
    In the case post processor should not modify IS URL
     */
    @Test
    public void fromNonDMtoDMAssetUnexpectedModification() throws Exception {
        prepareResource(CORE_IMAGE__DM_POLICY_ON__NON_DM_ASSET, getFileReference(CORE_IMAGE__DM_POLICY_ON__DM_ASSET));
        for (ModificationType modificationType : SUPPORTED_MODIFICATION_TYPES) {
            servlet.process(context.request(), Collections.singletonList(new Modification(
                modificationType,
                "//content/test/jcr:content/root/unexpected_path/" + DownloadResource.PN_REFERENCE,
                null
            )));
            Resource resource = context.currentResource();
            assertNotNull(resource);
            assertNull(resource.getValueMap().get(Image.PN_IMAGE_SERVER_URL, String.class));
        }
    }

    /*
    The case when modification path does not match to request's resource path.
    In the case post processor should not modify IS URL
     */
    @Test
    public void fromDMtoNonDMAssetUnexpectedModification() throws Exception {
        prepareResource(CORE_IMAGE__DM_POLICY_ON__DM_ASSET, getFileReference(CORE_IMAGE__DM_POLICY_ON__NON_DM_ASSET));
        for (ModificationType modificationType : SUPPORTED_MODIFICATION_TYPES) {
            servlet.process(context.request(), Collections.singletonList(new Modification(
                modificationType,
                "//content/test/jcr:content/root/unexpected_path/" + DownloadResource.PN_REFERENCE,
                null
            )));
            Resource resource = context.currentResource();
            assertNotNull(resource);
            assertEquals(EXPECTED_IMAGE_SERVER_URL, resource.getValueMap().get(Image.PN_IMAGE_SERVER_URL, String.class));
        }
    }

    /**
     * Prepares test resource for the scenario when the existing component is being changed to use the new asset path
     * @param existingComponent existing component path
     * @param newAssetPath new fileReference value
     */
    private void prepareResource(String existingComponent, String newAssetPath) {
        Resource resource = context.currentResource(existingComponent);
        assertNotNull(resource);
        ModifiableValueMap map = resource.adaptTo(ModifiableValueMap.class);
        if (map != null) {
            map.put(DownloadResource.PN_REFERENCE, newAssetPath);
        }
    }

    /**
     * Extracts the value of fileReference from test component content
     * @param componentPath component content path
     * @return fileReference from component content
     */
    private String getFileReference(String componentPath) {
        Resource resource = context.currentResource(componentPath);
        assertNotNull(resource);
        ValueMap valueMap = resource.adaptTo(ValueMap.class);
        assertNotNull(valueMap);
        return valueMap.get(getFileReferencePath(componentPath), String.class);
    }

    /**
     * Returns full path to fileReference property to be used in {@link Modification}
     * @param componentPath component content path
     * @return full path to fileReference property
     */
    private static String getFileReferencePath(String componentPath) {
        return componentPath + "/" + DownloadResource.PN_REFERENCE;
    }
}
