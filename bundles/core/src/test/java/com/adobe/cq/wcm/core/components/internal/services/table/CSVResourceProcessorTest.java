package com.adobe.cq.wcm.core.components.internal.services.table;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CSVResourceProcessorTest {

    private static final String[] HEADER_NAMES = {"email", "name", "gender", "status"};
    private static final String[] PROP_VALUES = {"test@test.com", "test", "male", "active"};
    public static final String LINE_DATA = "sample, sample@email.com,male,active";

    @InjectMocks
    private CSVResourceProcessor csvResourceProcessor;

    @Mock
    private Resource resource;

    @Mock
    private Asset asset;

    @Mock
    private Rendition original;

    @Mock
    private InputStream inputStream;

    @Mock
    private BufferedReader br;

    @BeforeEach
    void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void processData() throws IOException {
        when(resource.getResourceType()).thenReturn("dam:Asset");
        when(resource.adaptTo(Asset.class)).thenReturn(asset);
        when(asset.getOriginal()).thenReturn(original);

        // Create inputstream from resources csv and add it as return type for below
        InputStream inputStream =loadCSVStream();
        when(original.getStream()).thenReturn(inputStream);
        assertEquals(expectedOutput(),csvResourceProcessor.processData(resource,HEADER_NAMES));
    }

    private InputStream loadCSVStream() throws FileNotFoundException {
        Path resourceDirectory = Paths.get("src","test","resources","table","test-content.csv");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        return  new FileInputStream(new File(absolutePath));

    }

    @Test
    void canProcess() {
        assertTrue(csvResourceProcessor.canProcess("text/csv"));
    }

    private List<List<String>> expectedOutput() {
        List<List<String>> tableData = new ArrayList<>();
        tableData.add(Arrays.asList(new String[]{"test1@test.com", "test1", "male", "active"}));
        tableData.add(Arrays.asList(new String[]{"test2@test.com", "test2", "female", "active"}));
        tableData.add(Arrays.asList(new String[]{"test3@test.com", "test3", "male", "inactive"}));
        tableData.add(Arrays.asList(new String[]{"test4@test.com", "test4", "female", "inactive"}));
        return tableData;
    }

}
