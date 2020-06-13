/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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
package com.adobe.cq.wcm.core.components.internal.services.table;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class CSVResourceProcessorTest {

    private static final String[] HEADER_NAMES = {"email", "name", "gender", "status"};

    private static final String[] RANDOM_HEADER_NAMES = {"name", "email", "gender"};

    @InjectMocks
    private CSVResourceProcessor csvResourceProcessor;

    @Mock
    private Resource resource;

    @Mock
    private Asset asset;

    @Mock
    private Rendition original;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("When all the headers are passed in the same order, than fields are placed in csv file")
    void processData() throws IOException {
        setUpMocks();
        assertEquals(expectedOutput(), csvResourceProcessor.processData(resource, HEADER_NAMES));
    }

    private void setUpMocks() throws FileNotFoundException {
        when(resource.getResourceType()).thenReturn("dam:Asset");
        when(resource.adaptTo(Asset.class)).thenReturn(asset);
        when(asset.getOriginal()).thenReturn(original);

        // Create input stream from csv file in resources dir
        InputStream inputStream = loadCSVStream();
        when(original.getStream()).thenReturn(inputStream);
    }

    @Test
    @DisplayName("When some of the headers are passed in different order, than the headers are placed in csv file")
    void processDataWithScrambledHeaders() throws IOException {
        setUpMocks();
        assertEquals(randomHeaderOutput(), csvResourceProcessor.processData(resource, RANDOM_HEADER_NAMES));
    }

    private InputStream loadCSVStream() throws FileNotFoundException {
        Path resourceDirectory = Paths.get("src", "test", "resources", "table", "test-content.csv");
        return new FileInputStream(resourceDirectory.toFile());
    }

    @Test
    void canProcess() {
        assertTrue(csvResourceProcessor.canProcess("text/csv"));
    }

    private List<List<String>> expectedOutput() {
        List<List<String>> tableData = new ArrayList<>();
        tableData.add(Arrays.asList("test1@sample.com", "test1", "male", "active"));
        tableData.add(Arrays.asList("test2@sample.com", "test2", "female", "active"));
        tableData.add(Arrays.asList("test3@sample.com", "test3", "male", "inactive"));
        tableData.add(Arrays.asList("test4@sample.com", "test4", "female", "inactive"));
        return tableData;
    }

    private List<List<String>> randomHeaderOutput() {
        List<List<String>> tableData = new ArrayList<>();
        tableData.add(Arrays.asList("test1", "test1@sample.com", "male"));
        tableData.add(Arrays.asList("test2", "test2@sample.com", "female"));
        tableData.add(Arrays.asList("test3", "test3@sample.com", "male"));
        tableData.add(Arrays.asList("test4", "test4@sample.com", "female"));
        return tableData;
    }

}
