package com.adobe.cq.wcm.core.components.internal.rewriter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StyleRewriterTest {
    private StyleRewriter underTest;

    @BeforeEach
    void setUp() throws URISyntaxException, IOException {
        underTest =
                new StyleRewriter(new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("stylerewriter/input.html").toURI()))));
    }

    @Test
    void testInlineStyle() throws IOException, URISyntaxException {
        String expectedOutput =
                new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("stylerewriter/inputStyle.txt").toURI())));
        assertEquals(expectedOutput, underTest.getInlineStyle());
    }

    @Test
    void testSelectorMap() {
        Map<String, String> selectorMap = underTest.getSelectorMap();
        assertEquals("font-size: 20px;color: #004488; Margin: 0px;", selectorMap.get("h1"));
    }

    @Test
    void testOutputHtml() throws URISyntaxException, IOException {
        String expectedOutput =
                new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("stylerewriter/output.html").toURI())));
        assertTrue(expectedOutput.replaceAll("\\s+","").equalsIgnoreCase(underTest.getOutputHtml().replaceAll("\\s+","")));
    }
}
