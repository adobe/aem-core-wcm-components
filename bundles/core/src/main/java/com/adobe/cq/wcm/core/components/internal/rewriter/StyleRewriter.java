package com.adobe.cq.wcm.core.components.internal.rewriter;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StyleRewriter {

    private static final String STYLE_ELM = "style";
    private static final String STYLE_ATTR = "style";
    private static Logger LOG = LoggerFactory.getLogger(StyleRewriter.class);

    private String inlineStyle;
    private Document document;
    private HashMap<String, String> selectorMap;

    public StyleRewriter(String html) {
        document = Jsoup.parse(html);
    }

    private String extractInlineStyle() {
        Elements elements = document.select(STYLE_ELM);
        StringBuilder stringBuilder = new StringBuilder();
        for (Element element : elements) {
            stringBuilder.append(element.getAllElements().get(0).data().replaceAll(
                    "\n", "").trim());
        }
        return stringBuilder.toString();
    }


    public String getInlineStyle() {
        if (inlineStyle == null) {
            inlineStyle = extractInlineStyle();
        }
        return inlineStyle;
    }

    public Map<String, String> getSelectorMap() {
        if (selectorMap == null) {
            selectorMap = generateSelectorMap();
        }
        return selectorMap;
    }

    public String getOutputHtml() {
        for (String selector : getSelectorMap().keySet()) {
            Elements selectedElements = document.select(selector);
            for (Element selectedElement : selectedElements) {
                String styleAttribute = selectedElement.attr(STYLE_ATTR);
                if (StringUtils.isNotEmpty(styleAttribute)) {
                    selectedElement.attr(STYLE_ATTR, concatenateProperties(styleAttribute, getSelectorMap().get(selector)));
                } else {
                    selectedElement.attr(STYLE_ATTR, getSelectorMap().get(selector));
                }
            }
        }
        return document.outerHtml();
    }

    private HashMap<String, String> generateSelectorMap() {
        HashMap<String, String> map = new HashMap<>();
        StringTokenizer tokenizer = new StringTokenizer(getInlineStyle(), "{}");
        while (tokenizer.countTokens() > 1) {
            String selector = tokenizer.nextToken().trim();
            String properties = tokenizer.nextToken().replaceAll("\"", "'").replaceAll("[ ](?=[ ])|[^-_,:;'%#!A-Za-z0-9 ]+", "").trim();
            if (StringUtils.contains(selector, ",")) {
                StringTokenizer selectorTokens = new StringTokenizer(selector, ",");
                while (selectorTokens.hasMoreTokens()) {
                    addSelectorProperties(selectorTokens.nextToken(), properties, map);
                }
            } else {
                addSelectorProperties(selector, properties, map);
            }
        }
        return map;
    }

    private void addSelectorProperties(String selector, String properties, Map<String, String> selectorMap) {
        try {
            document.select(selector);
            if (selectorMap.containsKey(selector)) {
                selectorMap.put(selector, concatenateProperties(selectorMap.get(selector), properties));
            } else {
                selectorMap.put(selector, properties);
            }
        } catch (Selector.SelectorParseException e) {
            LOG.debug(e.getMessage());
        }
    }

    private String concatenateProperties(String oldProperties, String properties) {
        oldProperties = oldProperties.trim();
        if (!StringUtils.endsWith(oldProperties, ";")) {
            oldProperties += ";";
        }
        return oldProperties + properties;
    }
}
