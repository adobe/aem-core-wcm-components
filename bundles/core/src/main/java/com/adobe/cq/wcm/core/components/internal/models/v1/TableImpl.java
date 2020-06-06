package com.adobe.cq.wcm.core.components.internal.models.v1;

import com.adobe.cq.wcm.core.components.models.Table;
import com.adobe.cq.wcm.core.components.services.table.ResourceReader;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, adapters = Table.class)
public class TableImpl implements Table {

    @ValueMapValue(name = "source", injectionStrategy = InjectionStrategy.OPTIONAL)
    private String source;

    @ValueMapValue(name = "propertyNames", injectionStrategy = InjectionStrategy.OPTIONAL)
    private String[] propertyNames;

    @SlingObject
    private ResourceResolver resourceResolver;

    @OSGiService
    private ResourceReader resourceReader;

    private List<String> formattedPropertyNames;
    private List<List<String>> rows;

    @PostConstruct
    public void init() throws IOException {
        formatPropertyNames();
        rows = new ArrayList<>();
        Resource resource = resourceResolver.getResource(source);
        resourceReader.readData(resource,propertyNames);
    }

    /**
     * This method formats the property name to friendly names by removing jcr: prefix. Formatted
     * property names are used to display the headers in table.
     */
    private void formatPropertyNames() {
        formattedPropertyNames = new ArrayList<>();
        for (String propertyName : propertyNames)
            if (propertyName.contains("jcr:")) {
                formattedPropertyNames.add(propertyName.substring(propertyName.indexOf("jcr:") + 4));
            } else formattedPropertyNames.add(propertyName);
    }

    @Override
    public List<String> getFormattedPropertyNames() {
        return formattedPropertyNames;
    }

    @Override
    public List<List<String>> getRows() {
        return rows;
    }
}
