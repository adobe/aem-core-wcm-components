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
package com.adobe.cq.wcm.core.components.it.http;


import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.junit.rules.CQAuthorPublishClassRule;
import com.adobe.cq.testing.junit.rules.CQRule;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.testing.clients.ClientException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ContainerIT {

    @ClassRule
    public static final CQAuthorPublishClassRule cqBaseClassRule = new CQAuthorPublishClassRule();

    @Rule
    public CQRule cqBaseRule = new CQRule(cqBaseClassRule.authorRule, cqBaseClassRule.publishRule);

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    static CQClient adminAuthor;

    static CQClient adminPublish;

    @BeforeClass
    public static void beforeClass() {
        adminAuthor = cqBaseClassRule.authorRule.getAdminClient(CQClient.class);
        adminPublish = cqBaseClassRule.publishRule.getAdminClient(CQClient.class);
    }

    @Test
    public void testResponsivePage() throws ClientException, IOException {
        Tree expected = Tree.read(getClass().getClassLoader().getResourceAsStream("responsive-page.txt"));
        String content = adminAuthor.doGet("/content/core-components/responsive-page.html", 200).getContent();
        Elements html = Jsoup.parse(content).select("html");
        assertStructure(expected, html);
    }

    @Test
    public void testSimplePage() throws ClientException, IOException {
        Tree expected = Tree.read(getClass().getClassLoader().getResourceAsStream("simple-page.txt"));
        String content = adminAuthor.doGet("/content/core-components/simple-page.html", 200).getContent();
        Elements html = Jsoup.parse(content).select("html");
        assertStructure(expected, html);
    }

    private void assertStructure(Tree expected, Elements provided) {
        if (expected == null) {
            return;
        }
        long found = provided.stream().filter(element -> {
            if (element.tagName().equals(expected.getTagName())
                && (expected.getClassNames() == null || expected.getClassNames().isEmpty()
                        || element.classNames().containsAll(expected.getClassNames()))) {
                expected.getChildren().forEach(child -> assertStructure(child, element.children()));
                return true;
            }
            return false;
        }).count();
        if (found <= 0) {
            collector.addError(new Exception(format(expected) + " not found in " +
                provided.stream().map(this::format).collect(Collectors.joining(","))));
        }
    }

    private String format(Tree tree) {
        return tree.getTagName() +
            ((tree.getClassNames() == null || tree.getClassNames().isEmpty()) ?
                "" :
                ("." + String.join(".", tree.getClassNames())));
    }

    private String format(Element element) {
        return element.tagName() +
            ((element.classNames() == null || element.classNames().isEmpty()) ?
                "" :
                ("." + String.join(".", element.classNames())));
    }

    public static class Tree {

        private String tagName;
        private int level;
        private Tree parent;
        private final List<Tree> children = new LinkedList<>();
        private final Set<String> classNames = new LinkedHashSet<>();

        private static final Pattern pattern = Pattern.compile("(?<space>\\s*)(?<tag>\\S*).*");

        public static Tree read(InputStream is) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return read(reader, null);
        }

        private static Tree read(BufferedReader reader, Tree previous) throws IOException {
            String line = reader.readLine();
            if (line == null) {
                return null;
            }
            Tree current = fromLine(line);
            if (current == null) {
                return null;
            }
            while (previous != null && current.level <= previous.level) {
                previous = previous.parent;
            }
            if (previous != null) {
                previous.addChild(current);
            }
            read(reader, current);
            return current;
        }

        private static Tree fromLine(String line) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                String tag = matcher.group("tag");
                String space = matcher.group("space");
                String[] split = tag.split("\\.");
                Tree tree = new Tree();
                tree.setTagName(split[0]);
                Arrays.stream(split).skip(1).forEach(tree::addClassName);
                tree.level = space == null ? 0 : space.length();
                return tree;
            }
            return null;
        }

        public Tree() {
        }

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        public Set<String> getClassNames() {
            return classNames;
        }

        public void addClassName(String className) {
            this.classNames.add(className);
        }

        public List<Tree> getChildren() {
            return children;
        }

        public void addChild(Tree child) {
            this.children.add(child);
            child.parent = this;
        }

        public String toString() {
            return toString(0);
        }

        private String toString(int offset) {
            StringBuffer sb = new StringBuffer();
            sb.append(StringUtils.repeat(" ", offset));
            sb.append(tagName);
            classNames.forEach(prop -> {
                sb.append(".");
                sb.append(prop);
            });
            sb.append("\n");
            children.forEach(child -> sb.append(child.toString(offset + 2)));
            return sb.toString();
        }
    }
}
