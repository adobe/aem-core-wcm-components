/*******************************************************************************
 * Copyright 2026 Adobe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * Image v3 editor remote asset (urn:aaid:aem) Dynamic Media dialog behaviour.
 * Depends on {@code imageV3EditorImageTest.js} / {@code image.js} loading first.
 */
function imageV3EditorRemoteAssetIsParentVisible(element) {
    return element?.parentElement?.style.display !== "none";
}

function imageV3EditorRemoteAssetIsGroupVisible(root) {
    const group = root.querySelector(".cmp-image__editor-dynamicmedia");
    return group?.style.display !== "none";
}

function imageV3EditorRemoteAssetCreateDynamicMediaDialogFixture() {
    const root = document.createElement("div");
    root.className = "cmp-image__editor";

    const pageImageCheckbox = document.createElement("coral-checkbox");
    pageImageCheckbox.setAttribute("name", "./imageFromPageImage");
    pageImageCheckbox.checked = false;
    root.appendChild(pageImageCheckbox);

    const group = document.createElement("div");
    group.className = "cmp-image__editor-dynamicmedia";
    group.style.display = "none";
    root.appendChild(group);

    const presetTypeWrapper = document.createElement("div");
    const presetType = document.createElement("div");
    presetType.className = "cmp-image__editor-dynamicmedia-presettype";
    const imagePresetRadio = document.createElement("input");
    imagePresetRadio.type = "radio";
    imagePresetRadio.name = "./dmPresetType";
    imagePresetRadio.value = "imagePreset";
    imagePresetRadio.checked = false;
    const smartCropRadio = document.createElement("input");
    smartCropRadio.type = "radio";
    smartCropRadio.name = "./dmPresetType";
    smartCropRadio.value = "smartCrop";
    presetType.appendChild(imagePresetRadio);
    presetType.appendChild(smartCropRadio);
    presetTypeWrapper.appendChild(presetType);
    group.appendChild(presetTypeWrapper);

    const imagePresetWrapper = document.createElement("div");
    const imagePreset = document.createElement("select");
    imagePreset.className = "cmp-image__editor-dynamicmedia-imagepreset";
    imagePresetWrapper.appendChild(imagePreset);
    group.appendChild(imagePresetWrapper);

    const smartCropWrapper = document.createElement("div");
    const smartCrop = document.createElement("coral-select");
    smartCrop.className = "cmp-image__editor-dynamicmedia-smartcroprendition";
    smartCropWrapper.appendChild(smartCrop);
    group.appendChild(smartCropWrapper);

    const modifiersWrapper = document.createElement("div");
    const modifiers = document.createElement("input");
    modifiers.setAttribute("name", "./imageModifiers");
    modifiersWrapper.appendChild(modifiers);
    group.appendChild(modifiersWrapper);

    return root;
}

describe("Image v3 editor remote asset Dynamic Media", function() {
    let api;
    let fixtureRoot;
    const isParentVisible = imageV3EditorRemoteAssetIsParentVisible;
    const isGroupVisible = imageV3EditorRemoteAssetIsGroupVisible;
    const createDynamicMediaDialogFixture = imageV3EditorRemoteAssetCreateDynamicMediaDialogFixture;

    beforeAll(function() {
        api = globalThis.__IMAGE_V3_EDITOR_TEST_API;
    });

    beforeEach(function() {
        fixtureRoot = createDynamicMediaDialogFixture();
        document.body.appendChild(fixtureRoot);
        api.installRemoteAssetDynamicMediaTestFixture(fixtureRoot);
    });

    afterEach(function() {
        fixtureRoot?.remove();
        fixtureRoot = null;
    });

    describe("isRemoteFileReference", function() {
        it("returns true for the canonical /urn:aaid:aem:<assetID>/seoname.format reference", function() {
            expect(api.isRemoteFileReference("/urn:aaid:aem:abc-123/landscape.jpg")).toBe(true);
        });

        it("returns false for DAM paths and empty values", function() {
            expect(api.isRemoteFileReference("/content/dam/sample.jpg")).toBe(false);
            expect(api.isRemoteFileReference("")).toBe(false);
            expect(api.isRemoteFileReference(null)).toBe(false);
            expect(api.isRemoteFileReference(undefined)).toBe(false);
        });

        it("returns false when urn:aaid:aem is not at the path root", function() {
            expect(api.isRemoteFileReference("urn:aaid:aem:abc-123/landscape.jpg")).toBe(false);
            expect(api.isRemoteFileReference("/content/dam/urn:aaid:aem/asset")).toBe(false);
        });
    });

    describe("processPolarisSmartCropMetadataResponse", function() {
        it("shows preset type, smart crop radio, smart crop dropdown, and image modifiers when smartcrops metadata is present", function() {
            const responseText = JSON.stringify({
                repositoryMetadata: {
                    smartcrops: {
                        Landscape: { width: 16, height: 9 }
                    }
                }
            });

            api.processPolarisSmartCropMetadataResponse(200, responseText);

            const smartCrop = fixtureRoot.querySelector(".cmp-image__editor-dynamicmedia-smartcroprendition");
            const modifiers = fixtureRoot.querySelector("input[name='./imageModifiers']");
            const presetType = fixtureRoot.querySelector(".cmp-image__editor-dynamicmedia-presettype");
            const smartCropRadio = fixtureRoot.querySelector(
                ".cmp-image__editor-dynamicmedia-presettype input[value='smartCrop']"
            );
            expect(isGroupVisible(fixtureRoot)).toBe(true);
            expect(isParentVisible(presetType)).toBe(true);
            expect(smartCropRadio.checked).toBe(true);
            expect(isParentVisible(smartCrop)).toBe(true);
            expect(isParentVisible(modifiers)).toBe(true);
        });

        it("re-shows preset type after a previous no-smartcrops response had hidden it", function() {
            const noSmartCrops = JSON.stringify({
                repositoryMetadata: {}
            });
            const withSmartCrops = JSON.stringify({
                repositoryMetadata: {
                    smartcrops: {
                        Landscape: { width: 16, height: 9 }
                    }
                }
            });

            api.processPolarisSmartCropMetadataResponse(200, noSmartCrops);
            api.processPolarisSmartCropMetadataResponse(200, withSmartCrops);

            const presetType = fixtureRoot.querySelector(".cmp-image__editor-dynamicmedia-presettype");
            expect(isParentVisible(presetType)).toBe(true);
        });

        it("shows image modifiers but hides smart crop, image preset, and preset type when smartcrops metadata is absent", function() {
            const responseText = JSON.stringify({
                repositoryMetadata: {}
            });

            api.processPolarisSmartCropMetadataResponse(200, responseText);

            const smartCrop = fixtureRoot.querySelector(".cmp-image__editor-dynamicmedia-smartcroprendition");
            const modifiers = fixtureRoot.querySelector("input[name='./imageModifiers']");
            const presetType = fixtureRoot.querySelector(".cmp-image__editor-dynamicmedia-presettype");
            const imagePreset = fixtureRoot.querySelector(".cmp-image__editor-dynamicmedia-imagepreset");
            expect(isGroupVisible(fixtureRoot)).toBe(true);
            expect(isParentVisible(modifiers)).toBe(true);
            expect(isParentVisible(smartCrop)).toBe(false);
            expect(isParentVisible(presetType)).toBe(false);
            expect(isParentVisible(imagePreset)).toBe(false);
        });

        it("keeps original preset behaviour by hiding image preset radio parent", function() {
            const responseText = JSON.stringify({
                repositoryMetadata: {
                    smartcrops: {
                        Square: {}
                    }
                }
            });

            api.processPolarisSmartCropMetadataResponse(200, responseText);

            const imagePresetRadio = fixtureRoot.querySelector(
                ".cmp-image__editor-dynamicmedia-presettype input[value='imagePreset']"
            );
            expect(isParentVisible(imagePresetRadio)).toBe(false);
        });

        it("does nothing when metadata request fails", function() {
            api.processPolarisSmartCropMetadataResponse(500, "{}");

            expect(isGroupVisible(fixtureRoot)).toBe(false);
        });
    });
});
