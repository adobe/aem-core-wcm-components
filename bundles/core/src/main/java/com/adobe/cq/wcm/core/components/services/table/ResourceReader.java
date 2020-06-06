package com.adobe.cq.wcm.core.components.services.table;

import org.apache.sling.api.resource.Resource;

import java.io.IOException;
import java.util.List;

public interface ResourceReader {
    List<List<String>> readData(Resource resource, String[] propertyNames) throws IOException;
}
