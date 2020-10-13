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
package com.adobe.cq.wcm.core.components.models.datalayer.builder;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.models.datalayer.AssetData;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.ContainerData;
import com.adobe.cq.wcm.core.components.models.datalayer.ImageData;
import com.adobe.cq.wcm.core.components.models.datalayer.PageData;
import com.day.cq.dam.api.Asset;
import com.day.cq.tagging.TagConstants;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for DataLayerBuilder.
 */
public final class DataLayerBuilderTest {

    private static final String TEST_BASE = "/databuilder";

    /*
     * Bad values are used to validate that overrides work correctly.
     */
    private static final String BAD_VALUE = "!BAD_VALUE!";
    private static final Date BAD_DATE = new Date(0L);
    private static final String[] BAD_TAGS = new String[] { BAD_VALUE };
    private static final String[] BAD_SHOWN_ITEMS = new String[] { BAD_VALUE };


    /*
     * Expected values.
     */
    private static final String TEST_ID = "TEST_ID";
    private static final String TEST_PARENT_ID = "TEST_PARENT_ID";
    private static final String TEST_DESCRIPTION = "TEST DESCRIPTION";
    private static final String TEST_TEXT = "TEST TEXT";
    private static final String TEST_LINK_URL = "https://www.adobe.com/";
    private static final String TEST_TITLE = "TEST TITLE";
    private static final String TEST_TYPE = "TEST TYPE";
    private static final String TEST_FORMAT = "TEST FORMAT";
    private static final String TEST_LANGUAGE = "TEST LANGUAGE";
    private static final String TEST_TEMPLATE_PATH = "TEST TEMPLATE PATH";
    private static final String TEST_URL = "https://www.aemcomponents.dev/";
    private static final String[] TEST_TAGS = {"TEST_TAG_1", "TEST_TAG_2"};
    private static final String[] TEST_SHOWN_ITEMS = {"TEST_SHOWN_ITEM_1", "TEST_SHOWN_ITEM_2"};
    private static final Date LAST_MODIFIED_DATE = new Date(1592335437174L);


    /**
     * Tests for building ComponentData that is not a sub-type of ComponentData.
     */
    @Nested
    class ComponentDataBuilders {

        private ComponentDataBuilder initialBuilder;

        @BeforeEach
        void setUp() {
            initialBuilder = DataLayerBuilder.forComponent()
                // these values should all be overwritten
                .withId(() -> BAD_VALUE)
                .withDescription(() -> BAD_VALUE)
                .withLastModifiedDate(() -> BAD_DATE)
                .withLinkUrl(() -> BAD_VALUE)
                .withText(() -> BAD_VALUE)
                .withParentId(() -> BAD_VALUE)
                .withTitle(() -> BAD_VALUE)
                .withType(() -> BAD_VALUE);
        }

        @Test
        void forComponentDefaults() {
            ComponentData componentData = DataLayerBuilder.forComponent().withId(() -> TEST_ID).build();
            Assertions.assertEquals(TEST_ID, componentData.getId());
            Assertions.assertNull(componentData.getDescription());
            Assertions.assertNull(componentData.getLastModifiedDate());
            Assertions.assertNull(componentData.getLinkUrl());
            Assertions.assertNull(componentData.getText());
            Assertions.assertNull(componentData.getParentId());
            Assertions.assertNull(componentData.getTitle());
            Assertions.assertNull(componentData.getType());
            Utils.testJSONDataLayer(componentData, Utils.getTestDataModelJSONPath(TEST_BASE, "default"));
        }

        @Test
        void forComponent() {
            ComponentData componentData = initialBuilder
                .withId(() -> TEST_ID)
                .withDescription(() -> TEST_DESCRIPTION)
                .withLastModifiedDate(() -> LAST_MODIFIED_DATE)
                .withLinkUrl(() -> TEST_LINK_URL)
                .withText(() -> TEST_TEXT)
                .withParentId(() -> TEST_PARENT_ID)
                .withTitle(() -> TEST_TITLE)
                .withType(() -> TEST_TYPE).build();

            Assertions.assertEquals(TEST_ID, componentData.getId());
            Assertions.assertEquals(TEST_DESCRIPTION, componentData.getDescription());
            Assertions.assertEquals(LAST_MODIFIED_DATE, componentData.getLastModifiedDate());
            Assertions.assertEquals(TEST_LINK_URL, componentData.getLinkUrl());
            Assertions.assertEquals(TEST_TEXT, componentData.getText());
            Assertions.assertEquals(TEST_PARENT_ID, componentData.getParentId());
            Assertions.assertEquals(TEST_TITLE, componentData.getTitle());
            Assertions.assertEquals(TEST_TYPE, componentData.getType());

            // test that sequential calls return the same cached response
            Assertions.assertSame(componentData.getId(), componentData.getId());
            Assertions.assertSame(componentData.getDescription(), componentData.getDescription());
            Assertions.assertSame(componentData.getLinkUrl(), componentData.getLinkUrl());
            Assertions.assertSame(componentData.getText(), componentData.getText());
            Assertions.assertSame(componentData.getParentId(), componentData.getParentId());
            Assertions.assertSame(componentData.getTitle(), componentData.getTitle());
            Assertions.assertSame(componentData.getType(), componentData.getType());

            // check that dates and arrays have been defensively copied
            Assertions.assertEquals(componentData.getLastModifiedDate(), componentData.getLastModifiedDate());
            Assertions.assertNotSame(componentData.getLastModifiedDate(), componentData.getLastModifiedDate());


            Utils.testJSONDataLayer(componentData, Utils.getTestDataModelJSONPath(TEST_BASE, "component"));
        }

        @Test
        void extendingComponent() {
            // construct an initial component data
            ComponentData originalData = initialBuilder.build();

            // wrap the component data without changing any values
            ComponentData extendedDataNoOverlay = DataLayerBuilder.extending(initialBuilder.build())
                .asComponent()
                .build();

            // assert unchanged
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getId());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getDescription());
            Assertions.assertEquals(BAD_DATE, extendedDataNoOverlay.getLastModifiedDate());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getLinkUrl());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getText());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getParentId());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getTitle());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getType());
            Utils.testJSONDataLayer(extendedDataNoOverlay, Utils.getTestDataModelJSONPath(TEST_BASE, "bad-component"));


            // wrap the component data and change every value
            ComponentData componentData = DataLayerBuilder.extending(originalData)
                .asComponent()
                // the effective values
                .withId(() -> TEST_ID)
                .withDescription(() -> TEST_DESCRIPTION)
                .withLastModifiedDate(() -> LAST_MODIFIED_DATE)
                .withLinkUrl(() -> TEST_LINK_URL)
                .withText(() -> TEST_TEXT)
                .withParentId(() -> TEST_PARENT_ID)
                .withTitle(() -> TEST_TITLE)
                .withType(() -> TEST_TYPE)
                .build();

            // assert proper values returned
            Assertions.assertEquals(TEST_ID, componentData.getId());
            Assertions.assertEquals(TEST_DESCRIPTION, componentData.getDescription());
            Assertions.assertEquals(LAST_MODIFIED_DATE, componentData.getLastModifiedDate());
            Assertions.assertEquals(TEST_LINK_URL, componentData.getLinkUrl());
            Assertions.assertEquals(TEST_TEXT, componentData.getText());
            Assertions.assertEquals(TEST_PARENT_ID, componentData.getParentId());
            Assertions.assertEquals(TEST_TITLE, componentData.getTitle());
            Assertions.assertEquals(TEST_TYPE, componentData.getType());
            Utils.testJSONDataLayer(componentData, Utils.getTestDataModelJSONPath(TEST_BASE, "component"));
        }
    }

    /**
     * Tests for building ContainerData.
     */
    @Nested
    class ContainerDataBuilders {

        private ContainerDataBuilder initialBuilder;

        @BeforeEach
        void setUp() {
            initialBuilder = DataLayerBuilder.forContainer()
                // these values should all be overwritten
                .withId(() -> BAD_VALUE)
                .withDescription(() -> BAD_VALUE)
                .withLastModifiedDate(() -> BAD_DATE)
                .withLinkUrl(() -> BAD_VALUE)
                .withText(() -> BAD_VALUE)
                .withParentId(() -> BAD_VALUE)
                .withTitle(() -> BAD_VALUE)
                .withType(() -> BAD_VALUE)
                .withShownItems(() -> BAD_SHOWN_ITEMS);
        }

        @Test
        void forContainerDefaults() {
            ContainerData componentData = DataLayerBuilder.forContainer().withId(() -> TEST_ID).build();
            Assertions.assertEquals(TEST_ID, componentData.getId());
            Assertions.assertNull(componentData.getDescription());
            Assertions.assertNull(componentData.getLastModifiedDate());
            Assertions.assertNull(componentData.getLinkUrl());
            Assertions.assertNull(componentData.getText());
            Assertions.assertNull(componentData.getParentId());
            Assertions.assertNull(componentData.getTitle());
            Assertions.assertNull(componentData.getType());
            Assertions.assertNull(componentData.getShownItems());
            Utils.testJSONDataLayer(componentData, Utils.getTestDataModelJSONPath(TEST_BASE, "default"));
        }

        @Test
        void forContainer() {
            ContainerData componentData = initialBuilder
                .withId(() -> TEST_ID)
                .withDescription(() -> TEST_DESCRIPTION)
                .withLastModifiedDate(() -> LAST_MODIFIED_DATE)
                .withLinkUrl(() -> TEST_LINK_URL)
                .withText(() -> TEST_TEXT)
                .withParentId(() -> TEST_PARENT_ID)
                .withTitle(() -> TEST_TITLE)
                .withType(() -> TEST_TYPE)
                .withShownItems(() -> TEST_SHOWN_ITEMS)
                .build();

            Assertions.assertEquals(TEST_ID, componentData.getId());
            Assertions.assertEquals(TEST_DESCRIPTION, componentData.getDescription());
            Assertions.assertEquals(LAST_MODIFIED_DATE, componentData.getLastModifiedDate());
            Assertions.assertEquals(TEST_LINK_URL, componentData.getLinkUrl());
            Assertions.assertEquals(TEST_TEXT, componentData.getText());
            Assertions.assertEquals(TEST_PARENT_ID, componentData.getParentId());
            Assertions.assertEquals(TEST_TITLE, componentData.getTitle());
            Assertions.assertEquals(TEST_TYPE, componentData.getType());
            Assertions.assertArrayEquals(TEST_SHOWN_ITEMS, componentData.getShownItems());

            // test that sequential calls return the same cached response
            Assertions.assertSame(componentData.getId(), componentData.getId());
            Assertions.assertSame(componentData.getDescription(), componentData.getDescription());
            Assertions.assertSame(componentData.getLinkUrl(), componentData.getLinkUrl());
            Assertions.assertSame(componentData.getText(), componentData.getText());
            Assertions.assertSame(componentData.getParentId(), componentData.getParentId());
            Assertions.assertSame(componentData.getTitle(), componentData.getTitle());
            Assertions.assertSame(componentData.getType(), componentData.getType());

            // check that dates and arrays have been defensively copied
            Assertions.assertEquals(componentData.getLastModifiedDate(), componentData.getLastModifiedDate());
            Assertions.assertNotSame(componentData.getLastModifiedDate(), componentData.getLastModifiedDate());
            Assertions.assertArrayEquals(componentData.getShownItems(), componentData.getShownItems());
            Assertions.assertNotSame(componentData.getShownItems(), componentData.getShownItems());

            Utils.testJSONDataLayer(componentData, Utils.getTestDataModelJSONPath(TEST_BASE, "container"));
        }

        @Test
        void extendingContainer() {
            // construct an initial component data
            ContainerData originalData = initialBuilder.build();

            // wrap the component data without changing any values
            ContainerData extendedDataNoOverlay = DataLayerBuilder.extending(initialBuilder.build())
                .asContainer()
                .build();

            // assert unchanged
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getId());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getDescription());
            Assertions.assertEquals(BAD_DATE, extendedDataNoOverlay.getLastModifiedDate());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getLinkUrl());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getText());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getParentId());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getTitle());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getType());
            Assertions.assertArrayEquals(BAD_SHOWN_ITEMS, extendedDataNoOverlay.getShownItems());
            Utils.testJSONDataLayer(extendedDataNoOverlay, Utils.getTestDataModelJSONPath(TEST_BASE, "bad-container"));


            // wrap the component data and change every value
            ContainerData componentData = DataLayerBuilder.extending(originalData)
                .asContainer()
                // the effective values
                .withId(() -> TEST_ID)
                .withDescription(() -> TEST_DESCRIPTION)
                .withLastModifiedDate(() -> LAST_MODIFIED_DATE)
                .withLinkUrl(() -> TEST_LINK_URL)
                .withText(() -> TEST_TEXT)
                .withParentId(() -> TEST_PARENT_ID)
                .withTitle(() -> TEST_TITLE)
                .withType(() -> TEST_TYPE)
                .withShownItems(() -> TEST_SHOWN_ITEMS)
                .build();

            // assert proper values returned
            Assertions.assertEquals(TEST_ID, componentData.getId());
            Assertions.assertEquals(TEST_DESCRIPTION, componentData.getDescription());
            Assertions.assertEquals(LAST_MODIFIED_DATE, componentData.getLastModifiedDate());
            Assertions.assertEquals(TEST_LINK_URL, componentData.getLinkUrl());
            Assertions.assertEquals(TEST_TEXT, componentData.getText());
            Assertions.assertEquals(TEST_PARENT_ID, componentData.getParentId());
            Assertions.assertEquals(TEST_TITLE, componentData.getTitle());
            Assertions.assertEquals(TEST_TYPE, componentData.getType());
            Assertions.assertArrayEquals(TEST_SHOWN_ITEMS, componentData.getShownItems());
            Utils.testJSONDataLayer(componentData, Utils.getTestDataModelJSONPath(TEST_BASE, "container"));
        }
    }

    /**
     * Tests for building ImageData for an image component.
     */
    @Nested
    class ImageDataBuilders {
        private ImageComponentDataBuilder initialBuilder;

        @BeforeEach
        void setUp() {
            initialBuilder = DataLayerBuilder.forImageComponent()
                // these values should all be overwritten
                .withId(() -> BAD_VALUE)
                .withDescription(() -> BAD_VALUE)
                .withLastModifiedDate(() -> BAD_DATE)
                .withLinkUrl(() -> BAD_VALUE)
                .withText(() -> BAD_VALUE)
                .withParentId(() -> BAD_VALUE)
                .withTitle(() -> BAD_VALUE)
                .withType(() -> BAD_VALUE)
                .withAssetData(() -> DataLayerBuilder.forAsset()
                    .withId(() -> BAD_VALUE)
                    .withFormat(() -> BAD_VALUE)
                    .withLastModifiedDate(() -> BAD_DATE)
                    .withUrl(() -> BAD_VALUE)
                    .withTags(() -> BAD_TAGS).build());
        }

        @Test
        void forImageDefaults() {
            ImageData componentData = DataLayerBuilder.forImageComponent().withId(() -> TEST_ID).build();
            Assertions.assertEquals(TEST_ID, componentData.getId());
            Assertions.assertNull(componentData.getDescription());
            Assertions.assertNull(componentData.getLastModifiedDate());
            Assertions.assertNull(componentData.getLinkUrl());
            Assertions.assertNull(componentData.getText());
            Assertions.assertNull(componentData.getParentId());
            Assertions.assertNull(componentData.getTitle());
            Assertions.assertNull(componentData.getType());
            Assertions.assertNull(componentData.getAssetData());
            Utils.testJSONDataLayer(componentData, Utils.getTestDataModelJSONPath(TEST_BASE, "default"));
        }

        @Test
        void forImage() {
            ImageData imageData = initialBuilder
                .withId(() -> TEST_ID)
                .withDescription(() -> TEST_DESCRIPTION)
                .withLastModifiedDate(() -> LAST_MODIFIED_DATE)
                .withLinkUrl(() -> TEST_LINK_URL)
                .withText(() -> TEST_TEXT)
                .withParentId(() -> TEST_PARENT_ID)
                .withTitle(() -> TEST_TITLE)
                .withType(() -> TEST_TYPE)
                .withAssetData(() -> DataLayerBuilder.forAsset().withId(() -> TEST_ID)
                    .withFormat(() -> TEST_FORMAT)
                    .withLastModifiedDate(() -> LAST_MODIFIED_DATE)
                    .withUrl(() -> TEST_LINK_URL)
                    .withTags(() -> TEST_TAGS)
                    .build())
                .build();

            Assertions.assertEquals(TEST_ID, imageData.getId());
            Assertions.assertEquals(TEST_DESCRIPTION, imageData.getDescription());
            Assertions.assertEquals(LAST_MODIFIED_DATE, imageData.getLastModifiedDate());
            Assertions.assertEquals(TEST_LINK_URL, imageData.getLinkUrl());
            Assertions.assertEquals(TEST_TEXT, imageData.getText());
            Assertions.assertEquals(TEST_PARENT_ID, imageData.getParentId());
            Assertions.assertEquals(TEST_TITLE, imageData.getTitle());
            Assertions.assertEquals(TEST_TYPE, imageData.getType());
            Assertions.assertNotNull(imageData.getAssetData());

            // test that sequential calls return the same cached response
            Assertions.assertSame(imageData.getId(), imageData.getId());
            Assertions.assertSame(imageData.getDescription(), imageData.getDescription());
            Assertions.assertSame(imageData.getLinkUrl(), imageData.getLinkUrl());
            Assertions.assertSame(imageData.getText(), imageData.getText());
            Assertions.assertSame(imageData.getParentId(), imageData.getParentId());
            Assertions.assertSame(imageData.getTitle(), imageData.getTitle());
            Assertions.assertSame(imageData.getType(), imageData.getType());
            Assertions.assertSame(imageData.getAssetData(), imageData.getAssetData());

            // check that dates and arrays have been defensively copied
            Assertions.assertEquals(imageData.getLastModifiedDate(), imageData.getLastModifiedDate());
            Assertions.assertNotSame(imageData.getLastModifiedDate(), imageData.getLastModifiedDate());

            Utils.testJSONDataLayer(imageData, Utils.getTestDataModelJSONPath(TEST_BASE, "image"));
        }

        @Test
        void extendingImage() {
            // construct an initial component data
            ImageData originalData = initialBuilder.build();

            // wrap the component data without changing any values
            ImageData extendedDataNoOverlay = DataLayerBuilder.extending(initialBuilder.build())
                .asImageComponent()
                .build();

            // assert unchanged
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getId());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getDescription());
            Assertions.assertEquals(BAD_DATE, extendedDataNoOverlay.getLastModifiedDate());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getLinkUrl());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getText());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getParentId());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getTitle());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getType());
            Assertions.assertNotNull(extendedDataNoOverlay.getAssetData());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getAssetData().getId());

            Utils.testJSONDataLayer(extendedDataNoOverlay, Utils.getTestDataModelJSONPath(TEST_BASE, "bad-image"));

            // wrap the component data and change every value
            ImageData componentData = DataLayerBuilder.extending(originalData)
                .asImageComponent()
                // the effective values
                .withId(() -> TEST_ID)
                .withDescription(() -> TEST_DESCRIPTION)
                .withLastModifiedDate(() -> LAST_MODIFIED_DATE)
                .withLinkUrl(() -> TEST_LINK_URL)
                .withText(() -> TEST_TEXT)
                .withParentId(() -> TEST_PARENT_ID)
                .withTitle(() -> TEST_TITLE)
                .withType(() -> TEST_TYPE)
                .withAssetData(() -> DataLayerBuilder.forAsset().withId(() -> TEST_ID)
                    .withFormat(() -> TEST_FORMAT)
                    .withLastModifiedDate(() -> LAST_MODIFIED_DATE)
                    .withUrl(() -> TEST_LINK_URL)
                    .withTags(() -> TEST_TAGS)
                    .build())
                .build();

            // assert proper values returned
            Assertions.assertEquals(TEST_ID, componentData.getId());
            Assertions.assertEquals(TEST_DESCRIPTION, componentData.getDescription());
            Assertions.assertEquals(LAST_MODIFIED_DATE, componentData.getLastModifiedDate());
            Assertions.assertEquals(TEST_LINK_URL, componentData.getLinkUrl());
            Assertions.assertEquals(TEST_TEXT, componentData.getText());
            Assertions.assertEquals(TEST_PARENT_ID, componentData.getParentId());
            Assertions.assertEquals(TEST_TITLE, componentData.getTitle());
            Assertions.assertEquals(TEST_TYPE, componentData.getType());
            Assertions.assertNotNull(componentData.getAssetData());
            Assertions.assertEquals(TEST_ID, componentData.getAssetData().getId());
            Utils.testJSONDataLayer(componentData, Utils.getTestDataModelJSONPath(TEST_BASE, "image"));
        }
    }

    /**
     * Tests for building PageData.
     */
    @Nested
    class PageDataBuilders {
        private PageDataBuilder initialBuilder;

        @BeforeEach
        void setUp() {
            initialBuilder = DataLayerBuilder.forPage()
                // these values should all be overwritten
                .withId(() -> BAD_VALUE)
                .withDescription(() -> BAD_VALUE)
                .withLastModifiedDate(() -> BAD_DATE)
                .withLinkUrl(() -> BAD_VALUE)
                .withText(() -> BAD_VALUE)
                .withParentId(() -> BAD_VALUE)
                .withTitle(() -> BAD_VALUE)
                .withType(() -> BAD_VALUE)
                .withTags(() -> BAD_TAGS)
                .withTemplatePath(() -> BAD_VALUE)
                .withLanguage(() -> BAD_VALUE)
                .withUrl(() -> BAD_VALUE);
        }

        @Test
        void forPageDefaults() {
            PageData componentData = DataLayerBuilder.forPage().withId(() -> TEST_ID).build();
            Assertions.assertEquals(TEST_ID, componentData.getId());
            Assertions.assertNull(componentData.getDescription());
            Assertions.assertNull(componentData.getLastModifiedDate());
            Assertions.assertNull(componentData.getLinkUrl());
            Assertions.assertNull(componentData.getText());
            Assertions.assertNull(componentData.getParentId());
            Assertions.assertNull(componentData.getTitle());
            Assertions.assertNull(componentData.getType());
            Assertions.assertNull(componentData.getTags());
            Assertions.assertNull(componentData.getTemplatePath());
            Assertions.assertNull(componentData.getLanguage());
            Assertions.assertNull(componentData.getUrl());
            Utils.testJSONDataLayer(componentData, Utils.getTestDataModelJSONPath(TEST_BASE, "default"));
        }

        @Test
        void forPage() {
            PageData pageData = initialBuilder
                .withId(() -> TEST_ID)
                .withDescription(() -> TEST_DESCRIPTION)
                .withLastModifiedDate(() -> LAST_MODIFIED_DATE)
                .withLinkUrl(() -> TEST_LINK_URL)
                .withText(() -> TEST_TEXT)
                .withParentId(() -> TEST_PARENT_ID)
                .withTitle(() -> TEST_TITLE)
                .withType(() -> TEST_TYPE)
                .withTags(() -> TEST_TAGS)
                .withLanguage(() -> TEST_LANGUAGE)
                .withUrl(() -> TEST_URL)
                .withTemplatePath(() -> TEST_TEMPLATE_PATH)
                .build();

            Assertions.assertEquals(TEST_ID, pageData.getId());
            Assertions.assertEquals(TEST_DESCRIPTION, pageData.getDescription());
            Assertions.assertEquals(LAST_MODIFIED_DATE, pageData.getLastModifiedDate());
            Assertions.assertEquals(TEST_LINK_URL, pageData.getLinkUrl());
            Assertions.assertEquals(TEST_TEXT, pageData.getText());
            Assertions.assertEquals(TEST_PARENT_ID, pageData.getParentId());
            Assertions.assertEquals(TEST_TITLE, pageData.getTitle());
            Assertions.assertEquals(TEST_TYPE, pageData.getType());
            Assertions.assertArrayEquals(TEST_TAGS, pageData.getTags());
            Assertions.assertEquals(TEST_LANGUAGE, pageData.getLanguage());
            Assertions.assertEquals(TEST_URL, pageData.getUrl());
            Assertions.assertEquals(TEST_TEMPLATE_PATH, pageData.getTemplatePath());
            Utils.testJSONDataLayer(pageData, Utils.getTestDataModelJSONPath(TEST_BASE, "page"));

            // test that sequential calls return the same cached response
            Assertions.assertSame(pageData.getId(), pageData.getId());
            Assertions.assertSame(pageData.getDescription(), pageData.getDescription());
            Assertions.assertSame(pageData.getLinkUrl(), pageData.getLinkUrl());
            Assertions.assertSame(pageData.getText(), pageData.getText());
            Assertions.assertSame(pageData.getParentId(), pageData.getParentId());
            Assertions.assertSame(pageData.getTitle(), pageData.getTitle());
            Assertions.assertSame(pageData.getType(), pageData.getType());
            Assertions.assertSame(pageData.getLanguage(), pageData.getLanguage());
            Assertions.assertSame(pageData.getUrl(), pageData.getUrl());
            Assertions.assertSame(pageData.getTemplatePath(), pageData.getTemplatePath());

            // check that dates and arrays have been defensively copied
            Assertions.assertEquals(pageData.getLastModifiedDate(), pageData.getLastModifiedDate());
            Assertions.assertNotSame(pageData.getLastModifiedDate(), pageData.getLastModifiedDate());
            Assertions.assertArrayEquals(pageData.getTags(), pageData.getTags());
            Assertions.assertNotSame(pageData.getTags(), pageData.getTags());
        }


        @Test
        void extendingPage() {
            // construct an initial component data
            PageData originalData = initialBuilder.build();

            // wrap the component data without changing any values
            PageData extendedDataNoOverlay = DataLayerBuilder.extending(initialBuilder.build())
                .asPage()
                .build();

            // assert unchanged
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getId());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getDescription());
            Assertions.assertEquals(BAD_DATE, extendedDataNoOverlay.getLastModifiedDate());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getLinkUrl());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getText());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getParentId());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getTitle());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getType());
            Assertions.assertArrayEquals(BAD_TAGS, extendedDataNoOverlay.getTags());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getLanguage());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getUrl());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getTemplatePath());
            Utils.testJSONDataLayer(extendedDataNoOverlay, Utils.getTestDataModelJSONPath(TEST_BASE, "bad-page"));

            // wrap the component data and change every value
            PageData componentData = DataLayerBuilder.extending(originalData)
                .asPage()
                // the effective values
                .withId(() -> TEST_ID)
                .withDescription(() -> TEST_DESCRIPTION)
                .withLastModifiedDate(() -> LAST_MODIFIED_DATE)
                .withLinkUrl(() -> TEST_LINK_URL)
                .withText(() -> TEST_TEXT)
                .withParentId(() -> TEST_PARENT_ID)
                .withTitle(() -> TEST_TITLE)
                .withType(() -> TEST_TYPE)
                .withTags(() -> TEST_TAGS)
                .withLanguage(() -> TEST_LANGUAGE)
                .withUrl(() -> TEST_URL)
                .withTemplatePath(() -> TEST_TEMPLATE_PATH)
                .build();

            // assert proper values returned
            Assertions.assertEquals(TEST_ID, componentData.getId());
            Assertions.assertEquals(TEST_DESCRIPTION, componentData.getDescription());
            Assertions.assertEquals(LAST_MODIFIED_DATE, componentData.getLastModifiedDate());
            Assertions.assertEquals(TEST_LINK_URL, componentData.getLinkUrl());
            Assertions.assertEquals(TEST_TEXT, componentData.getText());
            Assertions.assertEquals(TEST_PARENT_ID, componentData.getParentId());
            Assertions.assertEquals(TEST_TITLE, componentData.getTitle());
            Assertions.assertEquals(TEST_TYPE, componentData.getType());
            Assertions.assertArrayEquals(TEST_TAGS, componentData.getTags());
            Assertions.assertEquals(TEST_LANGUAGE, componentData.getLanguage());
            Assertions.assertEquals(TEST_URL, componentData.getUrl());
            Assertions.assertEquals(TEST_TEMPLATE_PATH, componentData.getTemplatePath());
            Utils.testJSONDataLayer(componentData, Utils.getTestDataModelJSONPath(TEST_BASE, "page"));
        }
    }

    /**
     * Tests for building AssetData.
     */
    @Nested
    class AssetDataBuilders {

        private AssetDataBuilder initialBuilder;

        @BeforeEach
        void setUp() {
            initialBuilder =  DataLayerBuilder.forAsset()
                .withId(() -> BAD_VALUE)
                .withFormat(() -> BAD_VALUE)
                .withLastModifiedDate(() -> BAD_DATE)
                .withUrl(() -> BAD_VALUE)
                .withTags(() -> BAD_TAGS);
        }

        @Test
        void forAssetDefaults() {
            AssetData assetData = DataLayerBuilder.forAsset().withId(() -> TEST_ID).build();
            Assertions.assertEquals(TEST_ID, assetData.getId());
            Assertions.assertNull(assetData.getFormat());
            Assertions.assertNull(assetData.getLastModifiedDate());
            Assertions.assertNull(assetData.getUrl());
            Assertions.assertNull(assetData.getTags());
        }

        @Test
        void forAsset() {
            AssetData assetData = initialBuilder
                .withId(() -> TEST_ID)
                .withFormat(() -> TEST_FORMAT)
                .withLastModifiedDate(() -> LAST_MODIFIED_DATE)
                .withUrl(() -> TEST_URL)
                .withTags(() -> TEST_TAGS)
                .build();

            // assert proper values returned
            Assertions.assertEquals(TEST_ID, assetData.getId());
            Assertions.assertEquals(TEST_FORMAT, assetData.getFormat());
            Assertions.assertEquals(LAST_MODIFIED_DATE, assetData.getLastModifiedDate());
            Assertions.assertEquals(TEST_URL, assetData.getUrl());
            Assertions.assertArrayEquals(TEST_TAGS, assetData.getTags());

            // test that sequential calls return the same cached response
            Assertions.assertSame(assetData.getId(), assetData.getId());
            Assertions.assertSame(assetData.getFormat(), assetData.getFormat());
            Assertions.assertSame(assetData.getUrl(), assetData.getUrl());

            // check that dates and arrays have been defensively copied
            Assertions.assertEquals(assetData.getLastModifiedDate(), assetData.getLastModifiedDate());
            Assertions.assertNotSame(assetData.getLastModifiedDate(), assetData.getLastModifiedDate());
            Assertions.assertArrayEquals(assetData.getTags(), assetData.getTags());
            Assertions.assertNotSame(assetData.getTags(), assetData.getTags());
        }

        @Test
        void extendingAsset() {
            // construct an initial component data
            AssetData originalData = initialBuilder.build();

            // wrap the component data without changing any values
            AssetData extendedDataNoOverlay = DataLayerBuilder.extending(
                initialBuilder.build()
            ).build();

            // assert unchanged
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getId());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getFormat());
            Assertions.assertEquals(BAD_DATE, extendedDataNoOverlay.getLastModifiedDate());
            Assertions.assertEquals(BAD_VALUE, extendedDataNoOverlay.getUrl());
            Assertions.assertArrayEquals(BAD_TAGS, extendedDataNoOverlay.getTags());

            // wrap the component data and change every value
            AssetData assetData = DataLayerBuilder.extending(originalData)
                .withId(() -> TEST_ID)
                .withFormat(() -> TEST_FORMAT)
                .withLastModifiedDate(() -> LAST_MODIFIED_DATE)
                .withUrl(() -> TEST_URL)
                .withTags(() -> TEST_TAGS)
                .build();

            // assert proper values returned
            Assertions.assertEquals(TEST_ID, assetData.getId());
            Assertions.assertEquals(TEST_FORMAT, assetData.getFormat());
            Assertions.assertEquals(LAST_MODIFIED_DATE, assetData.getLastModifiedDate());
            Assertions.assertEquals(TEST_URL, assetData.getUrl());
            Assertions.assertArrayEquals(TEST_TAGS, assetData.getTags());

            // test that sequential calls return the same cached response
            Assertions.assertSame(assetData.getId(), assetData.getId());
            Assertions.assertSame(assetData.getFormat(), assetData.getFormat());
            Assertions.assertSame(assetData.getUrl(), assetData.getUrl());

            // check that dates and arrays have been defensively copied
            Assertions.assertEquals(assetData.getLastModifiedDate(), assetData.getLastModifiedDate());
            Assertions.assertNotSame(assetData.getLastModifiedDate(), assetData.getLastModifiedDate());
            Assertions.assertArrayEquals(assetData.getTags(), assetData.getTags());
            Assertions.assertNotSame(assetData.getTags(), assetData.getTags());
        }

        @Test
        void forDamAsset() {
            Asset asset = mock(Asset.class);
            ValueMap valueMap = mock(ValueMap.class);
            when(asset.adaptTo(ValueMap.class)).thenReturn(valueMap);


            // test when last modified date isn't set on the asset
            when(asset.getLastModified()).thenReturn(0L);
            Calendar createdDate = Calendar.getInstance();
            createdDate.setTime(LAST_MODIFIED_DATE);
            when(valueMap.get(JcrConstants.JCR_CREATED, Calendar.class)).thenReturn(createdDate);
            Assertions.assertEquals(LAST_MODIFIED_DATE, DataLayerBuilder.forAsset(asset).build().getLastModifiedDate());

            // tests for everything else
            when(asset.getLastModified()).thenReturn(LAST_MODIFIED_DATE.getTime());
            when(asset.getMetadataValueFromJcr(TagConstants.PN_TAGS)).thenReturn(String.join(",", TEST_TAGS));
            when(asset.getMimeType()).thenReturn(TEST_FORMAT);
            when(asset.getID()).thenReturn(TEST_ID);
            when(asset.getPath()).thenReturn(TEST_URL);

            Assertions.assertEquals(LAST_MODIFIED_DATE, DataLayerBuilder.forAsset(asset).build().getLastModifiedDate());
            Assertions.assertArrayEquals(TEST_TAGS, DataLayerBuilder.forAsset(asset).build().getTags());
            Assertions.assertEquals(TEST_FORMAT, DataLayerBuilder.forAsset(asset).build().getFormat());
            Assertions.assertEquals(TEST_ID, DataLayerBuilder.forAsset(asset).build().getId());
            Assertions.assertEquals(TEST_URL, DataLayerBuilder.forAsset(asset).build().getUrl());
        }
    }
}
