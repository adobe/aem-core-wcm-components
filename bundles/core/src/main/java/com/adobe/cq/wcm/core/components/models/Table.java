package com.adobe.cq.wcm.core.components.models;

import java.util.List;

public interface Table {
    List<String> getFormattedPropertyNames();
    List<List<String>> getRows();
}
