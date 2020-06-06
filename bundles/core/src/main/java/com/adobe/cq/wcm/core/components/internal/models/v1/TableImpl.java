package com.adobe.cq.wcm.core.components.internal.models.v1;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.commons.util.DamUtil;
import com.adobe.cq.wcm.core.components.models.Table;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.nonNull;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, adapters = Table.class)
public class TableImpl implements Table {

    @ValueMapValue(name = "source", injectionStrategy = InjectionStrategy.OPTIONAL)
    private String source;

    @ValueMapValue(name = "propertyNames", injectionStrategy = InjectionStrategy.OPTIONAL)
    private String[] propertyNames;

    @SlingObject
    private ResourceResolver resourceResolver;

    private List<String> formattedPropertyNames;
    private List<List<String>> rows;

    @PostConstruct
    public void init() throws IOException {
        formatPropertyNames();
        rows = new ArrayList<>();
        Resource resource = resourceResolver.getResource(source);
        if (DamUtil.isAsset(resource)) {
            processCSVData(resource);
        } else processResourceData(resource);
    }

    private void processResourceData(Resource resource) {
        Iterator<Resource> children = resourceResolver.listChildren(resource);
        while (children.hasNext()) {
            Resource child = children.next();
            ValueMap props = child.adaptTo(ValueMap.class);
            List<String> row = new ArrayList<>();
            for (String propertyName : propertyNames) {
                String propValue = props != null ? props.get(propertyName, "") : "";
                row.add(propValue);
            }
            rows.add(row);
        }
    }

    private void processCSVData(Resource resource) throws IOException {
        //String[][] csvData;
        Asset asset = DamUtil.resolveToAsset(resource);
        if (nonNull(asset)) {
            Rendition original = asset.getOriginal();
            if (nonNull(original)) {
                InputStream inputStream = original.getStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                List<String[]> lines = getRawCSVDataInList(br);

                final String[] columns = lines.get(0);
                Map<String, Integer> columnIndexMap = getColumnIndexMap(columns);
                //get the column indexes for matching property name
                Map<String, Integer> tableColumnsMap = new HashMap<>();
                for (String propertyName : propertyNames) {
                    tableColumnsMap.put(propertyName, columnIndexMap.getOrDefault(propertyName, -1));
                }
                //remove header row from the list
                lines = lines.subList(1, lines.size() - 1);
                populateRowsForTable(lines, tableColumnsMap);
                br.close();

            }
        }
    }

    private void populateRowsForTable(List<String[]> lines, Map<String, Integer> finalMap) {
        List<String> row = new ArrayList<>();
        for (String[] line : lines) {
            finalMap.forEach((k, v) ->
                    {
                        if (v >= 0) {
                            row.add(line[v]);
                        }
                        else row.add("");
                    }
            );
            rows.add(row);
        }
    }

    private List<String[]> getRawCSVDataInList(BufferedReader br) throws IOException {
        String[][] csvData;
        List<String[]> lines = new ArrayList<>();
        String currentLine;
        while ((currentLine = br.readLine()) != null) {
            lines.add(currentLine.split(","));
        }
        csvData = new String[lines.size()][0];
        lines.toArray(csvData);
        return lines;
    }

    /**
     * @param columns : Array of Column names. This is first row in the CSV file
     * @return : Returns the column name and it's index
     */
    private Map<String, Integer> getColumnIndexMap(String[] columns) {
        return IntStream.range(0, columns.length)
                .boxed()
                .collect(Collectors.toMap(index -> columns[index], index -> index, (a, b) -> b));
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
