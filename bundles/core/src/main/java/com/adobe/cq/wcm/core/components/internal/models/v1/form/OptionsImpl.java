/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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
package com.adobe.cq.wcm.core.components.internal.models.v1.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Named;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.form.FormConstants;
import com.adobe.cq.wcm.core.components.models.form.OptionItem;
import com.adobe.cq.wcm.core.components.models.form.Options;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;

@Model(adaptables = SlingHttpServletRequest.class,
       adapters = {Options.class, ComponentExporter.class},
       resourceType = {FormConstants.RT_CORE_FORM_OPTIONS_V1, FormConstants.RT_CORE_FORM_OPTIONS_V2})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class OptionsImpl extends AbstractFieldImpl implements Options {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptionsImpl.class);

    private static final String PN_TYPE = "type";
    private static final String OPTION_ITEMS_PATH = "items";
    private static final String ID_PREFIX = "form-options";

    @ChildResource(injectionStrategy = InjectionStrategy.OPTIONAL) @Named(OPTION_ITEMS_PATH)
    @Nullable
    private List<Resource> itemResources;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String helpMessage;

    @ValueMapValue(name = OptionsImpl.PN_TYPE, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String typeString;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String listPath;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String datasourceRT;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL, name = "source")
    @Nullable
    private String sourceString;

    @ScriptVariable
    private Resource resource;

    @ScriptVariable
    private SlingHttpServletResponse response;

    @ScriptVariable
    private ResourceResolver resolver;

    @Self
    private SlingHttpServletRequest request;

    private Type type;
    private List<OptionItem> optionItems;

    @Override
    public List<OptionItem> getItems() {
        if (optionItems == null) {
            populateOptionItems();
        }
        return Collections.unmodifiableList(optionItems);
    }

    @Override
    public String getHelpMessage() {
        return helpMessage;
    }

    @Override
    public Type getType() {
        if (type == null) {
            type = Options.Type.fromString(typeString);
        }
        return type;
    }

    @Override
    protected String getIDPrefix() {
        return ID_PREFIX;
    }

    @Override
    public String getValue() {
        return getDefaultValue();
    }

    @Override
    protected String getDefaultName() {
        return null;
    }

    @Override
    protected String getDefaultValue() {
        return null;
    }

    @Override
    protected String getDefaultTitle() {
        return null;
    }



    /* ------------------------ Internal stuff -------------------------------------------- */
    private void populateOptionItems() {
        this.optionItems = new ArrayList<>();
        Source source = Source.getSource(sourceString);
        if (source == null) {
            populateOptionItemsFromLocal();
        } else {
            switch (source) {
                case DATASOURCE:
                    populateOptionItemsFromDatasource();
                    break;
                case LIST:
                    populateOptionItemsFromList();
                    break;
                default:
                    populateOptionItemsFromLocal();
            }
        }
    }

    private void populateOptionItemsFromLocal() {
        if (itemResources != null) {
            for (Resource itemResource : itemResources) {
                OptionItem optionItem = new OptionItemImpl(request, resource, itemResource);
                if ((optionItem.isDisabled() || StringUtils.isNotBlank(optionItem.getValue()))) {
                    optionItems.add(optionItem);
                }
            }
        }
    }

    private void populateOptionItemsFromList() {
        if (StringUtils.isBlank(listPath)) {
            return;
        }
        Resource parent = resolver.getResource(listPath);
        if (parent != null) {
            for(Resource itemResource: parent.getChildren()) {
                OptionItem optionItem = new OptionItemImpl(request, resource, itemResource);
                if ((optionItem.isDisabled() || StringUtils.isNotBlank(optionItem.getValue()))) {
                    optionItems.add(optionItem);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void populateOptionItemsFromDatasource() {
        if (StringUtils.isBlank(datasourceRT)) {
            return;
        }
        // build the options by running the datasource code (the list is set as a request attribute)
        RequestDispatcherOptions opts = new RequestDispatcherOptions();
        opts.setForceResourceType(datasourceRT);
        RequestDispatcher dispatcher = request.getRequestDispatcher(resource, opts);
        try {
            if (dispatcher != null) {
                dispatcher.include(request, response);
            } else {
                LOGGER.error("Failed to include the datasource at " + datasourceRT);
            }
        } catch (IOException | ServletException|RuntimeException e) {
            LOGGER.error("Failed to include the datasource at " + datasourceRT, e);
        }

        // retrieve the datasource from the request and adapt it to form options
        SimpleDataSource dataSource = (SimpleDataSource) request.getAttribute(DataSource.class.getName());
        if (dataSource != null) {
            Iterator<Resource> itemIterator = dataSource.iterator();
            if (itemIterator != null) {
                while (itemIterator.hasNext()) {
                    Resource itemResource = itemIterator.next();
                    OptionItem optionItem = new OptionItemImpl(request, resource, itemResource);
                    if ((optionItem.isDisabled() || StringUtils.isNotBlank(optionItem.getValue()))) {
                        optionItems.add(optionItem);
                    }
                }
            }
        }
    }

    private enum Source {
        LOCAL("local"),
        LIST("list"),
        DATASOURCE("datasource");

        private String element;

        Source(String element) {
            this.element = element;
        }

        private static Source getSource(String value) {
            for (Source source : values()) {
                if (StringUtils.equalsIgnoreCase(source.element, value)) {
                    return source;
                }
            }
            return null;
        }
    }

}
