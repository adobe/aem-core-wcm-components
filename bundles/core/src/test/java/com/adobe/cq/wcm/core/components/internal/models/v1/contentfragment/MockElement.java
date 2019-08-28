/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
package com.adobe.cq.wcm.core.components.internal.models.v1.contentfragment;

import java.util.LinkedHashMap;
import java.util.Map;

public class MockElement {

    public String name;
    public String title;
    boolean isMultiValued;
    public String contentType;
    public String[] values;
    public boolean isMultiLine;
    public String htmlValue;
    public String typeString;
    public Map<String, MockVariation> variations = new LinkedHashMap<>();

    public MockElement() {
    }

    public MockElement(String name, String title, String contentType, String value, boolean isMultiLine, String htmlValue) {
        this(name, title, contentType, new String[]{value}, isMultiLine, htmlValue);
        this.isMultiValued = false;
    }

    public MockElement(String name, String title, String contentType, String[] values, boolean isMultiLine,
                       String htmlValue) {
        this.name = name;
        this.title = title;
        this.contentType = contentType;
        this.isMultiValued = true;
        this.values = values;
        this.isMultiLine = isMultiLine;
        this.htmlValue = htmlValue;
    }

    public void addVariation(String name, String title, String contentType, String[] values, boolean isMultiline,
                             String htmlValue) {
        variations.put(name, new MockVariation(name, title, contentType, values, isMultiline, htmlValue));
    }

    public void addVariation(String name, String title, String contentType, String value, boolean isMultiline,
                             String htmlValue) {
        variations.put(name, new MockVariation(name, title, contentType, value, isMultiline, htmlValue));
    }
}
