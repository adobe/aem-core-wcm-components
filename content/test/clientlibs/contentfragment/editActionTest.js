/*******************************************************************************
 * Copyright 2025 Adobe
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
describe("Test content fragment edit action for", function() {

    beforeAll(function () {
        fixture.setBase('test/fixtures/contentfragment')
        Granite.author.CFM.Fragments.mappings.set(
            "/content/dam/wknd-shared/en/adventures/riverside-camping-australia/riverside-camping-australia",
            { variation: "master" }
        );
        Granite.author.CFM.Fragments.mappings.set(
            "/content/dam/wknd-shared/en/adventures/napa-wine-tasting/napa-wine-tasting",
            { variation: "other" }
        );
    })

    afterAll(function () {
       Granite.author.CFM.Fragments.mappings.clear();
    });

    beforeEach(function () {
        this.result = fixture.load('editActionTest.html');
        spyOn(window, 'open');
    });

    afterEach(function () {
        fixture.cleanup()
    });

    it("empty content fragment", function() {
        const editable = { dom: fixture.el.children.item(0)};
        Granite.author.editor.contentfragment.setUp(editable);

        const canEdit = Granite.author.editor.contentfragment.canEdit(editable);
        Granite.author.editor.contentfragment.setUp(editable);

        expect(canEdit).toBeFalse();
        expect(window.open).toHaveBeenCalledTimes(0);
    });

    it("content fragment master variation and feature toggle disabled", function() {
        Granite.Toggles.enabled = false;
        const editable = { dom: fixture.el.children.item(1)};

        const canEdit = Granite.author.editor.contentfragment.canEdit(editable);
        Granite.author.editor.contentfragment.setUp(editable);

        expect(canEdit).toBeTrue();
        const expectedUrl = '/editor.html/content/dam/wknd-shared/en/adventures/riverside-camping-australia/riverside-camping-australia';
        expect(window.open).toHaveBeenCalledOnceWith(expectedUrl);
    });

    it("content fragment master variation and feature toggle enabled", function() {
        Granite.Toggles.enabled = true;
        const editable = { dom: fixture.el.children.item(1)};

        const canEdit = Granite.author.editor.contentfragment.canEdit(editable);
        Granite.author.editor.contentfragment.setUp(editable);

        expect(canEdit).toBeTrue();
        const expectedUrl = 'https://experience.adobe.com/?repo=localhost#/aem/cf/editor/content/dam/wknd-shared/en/adventures/riverside-camping-australia/riverside-camping-australia';
        expect(window.open).toHaveBeenCalledOnceWith(expectedUrl);
    });

    it("content fragment other variation and feature toggle disabled", function() {
        Granite.Toggles.enabled = false;
        const editable = { dom: fixture.el.children.item(2)};

        const canEdit = Granite.author.editor.contentfragment.canEdit(editable);
        Granite.author.editor.contentfragment.setUp(editable);

        expect(canEdit).toBeTrue();
        const expectedUrl = '/editor.html/content/dam/wknd-shared/en/adventures/napa-wine-tasting/napa-wine-tasting?variation=other';
        expect(window.open).toHaveBeenCalledOnceWith(expectedUrl);
    });

    it("content fragment other variation and feature toggle enabled", function() {
        Granite.Toggles.enabled = true;
        const editable = { dom: fixture.el.children.item(2)};

        const canEdit = Granite.author.editor.contentfragment.canEdit(editable);
        Granite.author.editor.contentfragment.setUp(editable);

        expect(canEdit).toBeTrue();
        const expectedUrl = 'https://experience.adobe.com/?repo=localhost#/aem/cf/editor/content/dam/wknd-shared/en/adventures/napa-wine-tasting/napa-wine-tasting';
        expect(window.open).toHaveBeenCalledOnceWith(expectedUrl);
    });

})
