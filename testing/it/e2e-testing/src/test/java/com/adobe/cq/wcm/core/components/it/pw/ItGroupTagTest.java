/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
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
package com.adobe.cq.wcm.core.components.it.pw;

import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.support.AnnotationSupport;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Build-time guard (surefire): CI runs the ITs as one job per {@code playwright-groupN} tag,
 * so an IT class without such a tag would silently never run. Add new ITs to the fastest group.
 */
class ItGroupTagTest {

    private static final Pattern GROUP_TAG = Pattern.compile("playwright-group\\d+");

    @Test
    void everyItClassHasAGroupTag() throws Exception {
        Path root = Paths.get(ItGroupTagTest.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        List<String> itClasses;
        try (Stream<Path> files = Files.walk(root)) {
            itClasses = files
                .map(p -> root.relativize(p).toString())
                .filter(p -> p.endsWith("IT.class") && !p.contains("$"))
                .map(p -> p.substring(0, p.length() - ".class".length()).replace('/', '.').replace('\\', '.'))
                .collect(Collectors.toList());
        }
        assertFalse(itClasses.isEmpty(), "No IT classes found under " + root);

        ClassLoader loader = ItGroupTagTest.class.getClassLoader();
        List<String> ungrouped = itClasses.stream()
            .map(name -> {
                try {
                    return Class.forName(name, false, loader);
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException(e);
                }
            })
            .filter(c -> !Modifier.isAbstract(c.getModifiers()))
            .filter(c -> AnnotationSupport.findRepeatableAnnotations(c, Tag.class).stream()
                .noneMatch(t -> GROUP_TAG.matcher(t.value()).matches()))
            .map(Class::getName)
            .sorted()
            .collect(Collectors.toList());
        assertTrue(ungrouped.isEmpty(),
            "IT classes without a @Tag(\"playwright-groupN\") would never run in CI: " + ungrouped);
    }
}
