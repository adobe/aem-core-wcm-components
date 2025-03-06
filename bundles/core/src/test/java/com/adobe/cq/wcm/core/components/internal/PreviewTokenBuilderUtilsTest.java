/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2025 Adobe
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
package com.adobe.cq.wcm.core.components.internal;

import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PreviewTokenBuilderUtilsTest {

    private static final String ASSET_ID = "testAsset";
    private static final String SECRET_KEY = "testSecretKey";
    private static final String PREVIEW_KEY = "previewKey";

    PreviewTokenBuilderUtils previewTokenBuilderUtils;

    @BeforeEach
    void setUp() {
        System.setProperty(PREVIEW_KEY, SECRET_KEY);
    }

    @AfterAll
    static void tearDown() {
        System.clearProperty(PREVIEW_KEY);
    }

    @Test
    void testBuildPreviewToken_Success() {
        Map.Entry<String, String> result = PreviewTokenBuilderUtils.buildPreviewToken(ASSET_ID);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getValue());
    }

    @Test
    void testBuildPreviewToken_NullAssetId() {
        Map.Entry<String, String> result = PreviewTokenBuilderUtils.buildPreviewToken(null);
        assertNull(result);
    }

    @Test
    void testBuildPreviewToken_MissingSecretKey() {
        System.clearProperty(PREVIEW_KEY);
        Map.Entry<String, String> result = PreviewTokenBuilderUtils.buildPreviewToken(ASSET_ID);
        assertNull(result);
    }
}
