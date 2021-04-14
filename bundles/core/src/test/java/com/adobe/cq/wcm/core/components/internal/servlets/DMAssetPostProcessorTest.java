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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

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
    private static final String CORE_IMAGE__EMPTY = PAGE + "/jcr:content/root/image2";
    private static final String CORE_IMAGE__DM_POLICY_ON__NON_DM_ASSET = PAGE + "/jcr:content/root/image31";
    private static final String CORE_IMAGE__DM_POLICY_ON__DM_ASSET_NO_PRESET = PAGE + "/jcr:content/root/image32";
    private static final String CORE_IMAGE__DM_POLICY_ON__DM_ASSET_IMAGE_PRESET = PAGE + "/jcr:content/root/image33";
    private static final String CORE_IMAGE__DM_POLICY_ON__DM_ASSET_SMART_CROP_RENDITION = PAGE + "/jcr:content/root/image36";
    private static final String CORE_IMAGE__DM_POLICY_ON__DM_ASSET_ANIMATED_GIF = PAGE + "/jcr:content/root/image40";
    private static final String CORE_IMAGE__DM_POLICY_ON__NON_DM_ASSET_ANIMATED_GIF = PAGE + "/jcr:content/root/image41";
    private static final String RESPONSIVE_GRID__EMPTY = PAGE + "/jcr:content/root/container";

    private static final String EXPECTED_IMAGE_SERVER_URL = "https://s7d9.scene7.com/is/image/";
    private static final String EXPECTED_IMAGE_SERVER_CONTENT_URL = "https://s7d9.scene7.com/is/content/";

    private DMAssetPostProcessor servlet = null;

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
    @ParameterizedTest
    @EnumSource(
        value = ModificationType.class,
        names = {"CREATE", "MODIFY"}
    )
    public void fromNonDMtoDMAssetNoPreset(ModificationType modificationType) throws Exception {
        String existingComponent = CORE_IMAGE__DM_POLICY_ON__NON_DM_ASSET;
        prepareResource(existingComponent, getFileReference(CORE_IMAGE__DM_POLICY_ON__DM_ASSET_NO_PRESET));
        List<Modification> modifications = prepareModifications(modificationType, existingComponent, true);
        servlet.process(context.request(), modifications);
        Resource resource = context.currentResource();
        assertNotNull(resource);
        assertEquals(EXPECTED_IMAGE_SERVER_URL, resource.getValueMap().get(Image.PN_IMAGE_SERVER_URL, String.class));
        assertNewModification(existingComponent, modifications, 2, Collections.singletonList(new ExpectedModification(ModificationType.CREATE, Image.PN_IMAGE_SERVER_URL)));
    }

    /*
    The case when component with non DM asset gets assigned a DM asset.
    Special case for animated gif as this uses a different path on DM Image Serving Servlet
    Post processor should write image server url property
     */
    @ParameterizedTest
    @EnumSource(
        value = ModificationType.class,
        names = {"CREATE", "MODIFY"}
    )
    public void fromNonDMtoDMAssetAnimatedGif(ModificationType modificationType) throws Exception {
        String existingComponent = CORE_IMAGE__DM_POLICY_ON__NON_DM_ASSET_ANIMATED_GIF;
        prepareResource(existingComponent, getFileReference(CORE_IMAGE__DM_POLICY_ON__DM_ASSET_ANIMATED_GIF));
        List<Modification> modifications = prepareModifications(modificationType, existingComponent, true);
        servlet.process(context.request(), modifications);
        Resource resource = context.currentResource();
        assertNotNull(resource);
        assertEquals(EXPECTED_IMAGE_SERVER_CONTENT_URL, resource.getValueMap().get(Image.PN_IMAGE_SERVER_URL, String.class));
        assertNewModification(existingComponent, modifications, 2, Collections.singletonList(new ExpectedModification(ModificationType.CREATE, Image.PN_IMAGE_SERVER_URL)));
    }

    /*
    The case when component with DM asset gets assigned a non DM asset.
    Post processor should clear image server url property
     */
    @ParameterizedTest
    @EnumSource(
        value = ModificationType.class,
        names = {"CREATE", "MODIFY"}
    )
    public void fromDMNoPresetToNonDMAsset(ModificationType modificationType) throws Exception {
        String existingComponent = CORE_IMAGE__DM_POLICY_ON__DM_ASSET_NO_PRESET;
        prepareResource(existingComponent, getFileReference(CORE_IMAGE__DM_POLICY_ON__NON_DM_ASSET));
        List<Modification> modifications = prepareModifications(modificationType, existingComponent, true);
        servlet.process(context.request(), modifications);
        Resource resource = context.currentResource();
        assertNotNull(resource);
        assertNull(resource.getValueMap().get(Image.PN_IMAGE_SERVER_URL, String.class));
        assertNewModification(existingComponent, modifications, 2, Collections.singletonList(new ExpectedModification(ModificationType.DELETE, Image.PN_IMAGE_SERVER_URL)));
    }

    /*
    The case when component with DM asset gets assigned a non DM asset.
    Post processor should clear image server url property
     */
    @ParameterizedTest
    @EnumSource(
        value = ModificationType.class,
        names = {"CREATE", "MODIFY"}
    )
    public void fromDMImagePresetToNonDMAsset(ModificationType modificationType) throws Exception {
        String existingComponent = CORE_IMAGE__DM_POLICY_ON__DM_ASSET_IMAGE_PRESET;
        prepareResource(existingComponent, getFileReference(CORE_IMAGE__DM_POLICY_ON__NON_DM_ASSET));
        List<Modification> modifications = prepareModifications(modificationType, existingComponent, true);
        servlet.process(context.request(), modifications);
        Resource resource = context.currentResource();
        assertNotNull(resource);
        assertNull(resource.getValueMap().get(Image.PN_IMAGE_SERVER_URL, String.class));
        assertNewModification(existingComponent, modifications, 2, Collections.singletonList(new ExpectedModification(ModificationType.DELETE, Image.PN_IMAGE_SERVER_URL)));
    }

    /*
    The case when component with DM asset gets assigned a non DM asset.
    Post processor should clear image server url property
     */
    @ParameterizedTest
    @EnumSource(
        value = ModificationType.class,
        names = {"CREATE", "MODIFY"}
    )
    public void fromDMSmartCropRenditionToNonDMAsset(ModificationType modificationType) throws Exception {
        String existingComponent = CORE_IMAGE__DM_POLICY_ON__DM_ASSET_SMART_CROP_RENDITION;
        prepareResource(existingComponent, getFileReference(CORE_IMAGE__DM_POLICY_ON__NON_DM_ASSET));
        List<Modification> modifications = prepareModifications(modificationType, existingComponent, true);
        servlet.process(context.request(), modifications);
        Resource resource = context.currentResource();
        assertNotNull(resource);
        assertNull(resource.getValueMap().get(Image.PN_IMAGE_SERVER_URL, String.class));
        assertNewModification(existingComponent, modifications, 2, Arrays.asList(
            new ExpectedModification(ModificationType.DELETE, Image.PN_IMAGE_SERVER_URL),
            new ExpectedModification(ModificationType.DELETE, "smartCropRendition")
        ));
    }

    /*
    The case when component with DM asset gets assigned a non DM asset.
    Post processor should clear image server url property
     */
    @ParameterizedTest
    @EnumSource(
        value = ModificationType.class,
        names = {"CREATE", "MODIFY"}
    )
    public void editModificationDMSmartCropAsset(ModificationType modificationType) throws Exception {
        String existingComponent = CORE_IMAGE__DM_POLICY_ON__DM_ASSET_SMART_CROP_RENDITION;
        prepareResource(existingComponent, getFileReference(CORE_IMAGE__DM_POLICY_ON__NON_DM_ASSET));
        List<Modification> modifications = prepareModifications(modificationType, existingComponent, false);
        servlet.process(context.request(), modifications);
        Resource resource = context.currentResource();
        assertNotNull(resource);
        assertNull(resource.getValueMap().get(Image.PN_IMAGE_SERVER_URL, String.class));
        assertNewModification(existingComponent, modifications, 1, Collections.singletonList(new ExpectedModification(ModificationType.DELETE, Image.PN_IMAGE_SERVER_URL)));
    }

    /*
    Current component hols plain DM image and the asset gets cleared
    Post processor should remove image server url
     */
    @Test
    public void fromDMNoPresetToEmptyAsset() throws Exception {
        String existingComponent = CORE_IMAGE__DM_POLICY_ON__DM_ASSET_NO_PRESET;
        prepareResource(existingComponent, null);

        List<Modification> modifications = prepareModifications(ModificationType.DELETE, existingComponent, true);
        servlet.process(context.request(), modifications);
        Resource resource = context.currentResource();
        assertNotNull(resource);
        assertNull(resource.getValueMap().get(Image.PN_IMAGE_SERVER_URL, String.class));
        assertNewModification(existingComponent, modifications, 2, Collections.singletonList(new ExpectedModification(ModificationType.DELETE, Image.PN_IMAGE_SERVER_URL)));
    }

    /*
    Current component hols DM image with image preset and the asset gets cleared
    Post processor should remove image server url
     */
    @Test
    public void fromDMImagePresetToEmptyAsset() throws Exception {
        String existingComponent = CORE_IMAGE__DM_POLICY_ON__DM_ASSET_IMAGE_PRESET;
        prepareResource(existingComponent, null);

        List<Modification> modifications = prepareModifications(ModificationType.DELETE, existingComponent, true);
        servlet.process(context.request(), modifications);
        Resource resource = context.currentResource();
        assertNotNull(resource);
        assertNull(resource.getValueMap().get(Image.PN_IMAGE_SERVER_URL, String.class));
        assertNewModification(existingComponent, modifications, 2, Collections.singletonList(new ExpectedModification(ModificationType.DELETE, Image.PN_IMAGE_SERVER_URL)));
    }

    /*
    Current component hols DM image with smart crop rendition and the asset gets cleared
    Post processor should remove image server url and smart crop rendition
     */
    @Test
    public void fromDMSmartCropRenditionToEmptyAsset() throws Exception {
        String existingComponent = CORE_IMAGE__DM_POLICY_ON__DM_ASSET_SMART_CROP_RENDITION;
        prepareResource(existingComponent, null);

        List<Modification> modifications = prepareModifications(ModificationType.DELETE, existingComponent, true);
        servlet.process(context.request(), modifications);
        Resource resource = context.currentResource();
        assertNotNull(resource);
        assertNull(resource.getValueMap().get(Image.PN_IMAGE_SERVER_URL, String.class));
        assertNull(resource.getValueMap().get("smartCropRendition", String.class));
        assertNewModification(existingComponent, modifications, 2, Arrays.asList(
            new ExpectedModification(ModificationType.DELETE, Image.PN_IMAGE_SERVER_URL),
            new ExpectedModification(ModificationType.DELETE, "smartCropRendition")
        ));
    }

    /*
    The case when modification type does not match CREATE or MODIFY.
    In the case post processor should not modify IS URL
     */
    @ParameterizedTest
    @EnumSource(
        value = ModificationType.class,
        names = {"CREATE", "MODIFY", "DELETE"},
        mode = EnumSource.Mode.EXCLUDE
    )
    public void fromNonDMtoDMAssetNotSupportedModification(ModificationType modificationType) throws Exception {
        String existingComponent = CORE_IMAGE__DM_POLICY_ON__NON_DM_ASSET;
        prepareResource(existingComponent, getFileReference(CORE_IMAGE__DM_POLICY_ON__DM_ASSET_NO_PRESET));
        List<Modification> modifications = prepareModifications(modificationType, existingComponent, true);
        servlet.process(context.request(), modifications);
        Resource resource = context.currentResource();
        assertNotNull(resource);
        assertNull(resource.getValueMap().get(Image.PN_IMAGE_SERVER_URL, String.class));
        assertEquals(2, modifications.size());
    }

    /*
    The case when modification type does not match CREATE or MODIFY.
    In the case post processor should not modify IS URL
     */
    @ParameterizedTest
    @EnumSource(
        value = ModificationType.class,
        names = {"CREATE", "MODIFY", "DELETE"},
        mode = EnumSource.Mode.EXCLUDE
    )
    public void fromDMtoNonDMAssetNotSupportedModification(ModificationType modificationType) throws Exception {
        String existingComponent = CORE_IMAGE__DM_POLICY_ON__DM_ASSET_NO_PRESET;
        prepareResource(existingComponent, getFileReference(CORE_IMAGE__DM_POLICY_ON__NON_DM_ASSET));
        List<Modification> modifications = prepareModifications(modificationType, existingComponent, true);
        servlet.process(context.request(), modifications);
        Resource resource = context.currentResource();
        assertNotNull(resource);
        assertEquals(EXPECTED_IMAGE_SERVER_URL, resource.getValueMap().get(Image.PN_IMAGE_SERVER_URL, String.class));
        assertEquals(2, modifications.size());
    }

    /*
    The case when modification path does not match to request's resource path.
    In the case post processor should not modify IS URL
     */
    @ParameterizedTest
    @EnumSource(
        value = ModificationType.class,
        names = {"CREATE", "MODIFY"}
    )
    public void fromNonDMtoDMAssetUnexpectedModification(ModificationType modificationType) throws Exception {
        prepareResource(CORE_IMAGE__DM_POLICY_ON__NON_DM_ASSET, getFileReference(CORE_IMAGE__DM_POLICY_ON__DM_ASSET_NO_PRESET));
        List<Modification> modifications = prepareModifications(modificationType, "/content/test/jcr:content/root/unexpected_path", true);
        servlet.process(context.request(), modifications);
        Resource resource = context.currentResource();
        assertNotNull(resource);
        assertNull(resource.getValueMap().get(Image.PN_IMAGE_SERVER_URL, String.class));
        assertEquals(2, modifications.size());
    }

    /*
    The case when modification path does not match to request's resource path.
    In the case post processor should not modify IS URL
     */
    @ParameterizedTest
    @EnumSource(
        value = ModificationType.class,
        names = {"CREATE", "MODIFY"}
    )
    public void fromDMtoNonDMAssetUnexpectedModification(ModificationType modificationType) throws Exception {
        prepareResource(CORE_IMAGE__DM_POLICY_ON__DM_ASSET_NO_PRESET, getFileReference(CORE_IMAGE__DM_POLICY_ON__NON_DM_ASSET));
        List<Modification> modifications = prepareModifications(modificationType, "/content/test/jcr:content/root/unexpected_path", true);
        servlet.process(context.request(), modifications);
        Resource resource = context.currentResource();
        assertNotNull(resource);
        assertEquals(EXPECTED_IMAGE_SERVER_URL, resource.getValueMap().get(Image.PN_IMAGE_SERVER_URL, String.class));
        assertEquals(2, modifications.size());
    }

    /*
    The case when fileReference is broken and points to missing resource
     */
    @Test
    public void corruptedFileReference() throws Exception {
        String existingComponent = CORE_IMAGE__DM_POLICY_ON__DM_ASSET_NO_PRESET;
        prepareResource(existingComponent, "/content/dam/test/corrupted-asset.png");
        List<Modification> modifications = prepareModifications(ModificationType.CREATE, existingComponent, true);
        servlet.process(context.request(), modifications);
        Resource resource = context.currentResource();
        assertNotNull(resource);
        assertEquals(EXPECTED_IMAGE_SERVER_URL, resource.getValueMap().get(Image.PN_IMAGE_SERVER_URL, String.class));
        assertEquals(2, modifications.size());
    }

    /*
    The case when fileReference is broken and points to non-image resource
     */
    @Test
    public void nonImageFileReference() throws Exception {
        String existingComponent = CORE_IMAGE__DM_POLICY_ON__DM_ASSET_NO_PRESET;
        prepareResource(existingComponent, existingComponent);
        List<Modification> modifications = prepareModifications(ModificationType.CREATE, existingComponent, true);
        servlet.process(context.request(), modifications);
        Resource resource = context.currentResource();
        assertNotNull(resource);
        assertEquals(EXPECTED_IMAGE_SERVER_URL, resource.getValueMap().get(Image.PN_IMAGE_SERVER_URL, String.class));
        assertEquals(2, modifications.size());
    }

    /*
    Unlikely case when modifications report fileReference setting but no fileReference is found in request
     */
    @Test
    public void lostFileReferenceModification() throws Exception {
        String existingComponent = CORE_IMAGE__EMPTY;
        prepareResource(existingComponent, null);
        List<Modification> modifications = prepareModifications(ModificationType.CREATE, existingComponent, true);
        servlet.process(context.request(), modifications);
        Resource resource = context.currentResource();
        assertNotNull(resource);
        assertNull(resource.getValueMap().get(Image.PN_IMAGE_SERVER_URL, String.class));
        assertEquals(2, modifications.size());
    }

    private void assertNewModification(String component, List<Modification> modifications, int skipCount, List<ExpectedModification> expectedModifications) {
        assertEquals(expectedModifications.size() + skipCount, modifications.size());
        for (int i = 0; i < expectedModifications.size(); i ++) {
            Modification actualModification = modifications.get(i + skipCount);
            ExpectedModification expectedModification = expectedModifications.get(i);
            assertEquals(component + "/" + expectedModification.name, actualModification.getSource());
            assertNull(actualModification.getDestination());
            assertEquals(actualModification.getType(), expectedModification.type);
        }
    }

    /*
     The case when DM asset drag-n-drop on empty page. This scenario creates Image component with assigned asset.
     Post processor should write image server url property
     */
    @ParameterizedTest
    @EnumSource(
        value = ModificationType.class,
        names = {"CREATE", "MODIFY"}
    )
    public void dragNdropDMimageToEmptyPage(ModificationType modificationType) throws Exception {
        String existingComponent = RESPONSIVE_GRID__EMPTY;
        prepareResource(existingComponent, null);
        List<Modification> modifications = prepareModifications(modificationType, existingComponent + "/image", true);
        servlet.process(context.request(), modifications);
        Resource resource = context.currentResource().getResourceResolver().getResource(existingComponent + "/image");
        Resource containerResource = context.currentResource().getResourceResolver().getResource(existingComponent);
        assertNotNull(resource);
        assertEquals(EXPECTED_IMAGE_SERVER_URL, resource.getValueMap().get(Image.PN_IMAGE_SERVER_URL, String.class));
        assertNotNull(containerResource);
        assertNull(containerResource.getValueMap().get(Image.PN_IMAGE_SERVER_URL, String.class));
        assertEquals(3, modifications.size());
    }

    /**
     * Prepares test resource for the scenario when the existing component is being changed to use the new asset path
     * @param existingComponent existing component path
     * @param newAssetPath new fileReference value
     */
    private void prepareResource(String existingComponent, String newAssetPath) {
        Resource resource = context.currentResource(existingComponent);
        assertNotNull(resource);
        if (newAssetPath != null) {
            ModifiableValueMap map = resource.adaptTo(ModifiableValueMap.class);
            if (map != null) {
                map.put(DownloadResource.PN_REFERENCE, newAssetPath);
            }
        }
    }

    private List<Modification> prepareModifications(ModificationType modificationType, String component, boolean includeFileDelete) {
        List<Modification> modifications = new ArrayList<>();
        modifications.add(new Modification(
            modificationType,
            component + "/" + DownloadResource.PN_REFERENCE,
            null
        ));
        if (includeFileDelete) {
            modifications.add(new Modification(
                ModificationType.DELETE,
                component + "/file",
                null
            ));
        }
        return modifications;
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

    private static class ExpectedModification {
        final ModificationType type;
        final String name;

        public ExpectedModification(ModificationType type, String name) {
            this.type = type;
            this.name = name;
        }
    }
}
