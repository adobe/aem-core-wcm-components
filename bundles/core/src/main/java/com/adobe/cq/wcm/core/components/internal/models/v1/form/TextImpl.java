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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.form.FormConstants;
import com.adobe.cq.wcm.core.components.models.form.Text;
import com.day.cq.wcm.foundation.forms.FormStructureHelperFactory;
import com.day.cq.wcm.foundation.forms.FormsHelper;

@Model(adaptables = SlingHttpServletRequest.class,
       adapters = {Text.class, ComponentExporter.class},
       resourceType = {FormConstants.RT_CORE_FORM_TEXT_V1, FormConstants.RT_CORE_FORM_TEXT_V2})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class TextImpl extends AbstractFieldImpl implements Text, ComponentExporter {

    private static final String ID_PREFIX = "form-text";
    private static final String PROP_NAME_DEFAULT = "text";
    private static final String PROP_VALUE_DEFAULT = "";
    private static final String PROP_TITLE_DEFAULT = "Text input field";
    private static final boolean PROP_READONLY_DEFAULT = false;
    private static final boolean PROP_REQUIRED_DEFAULT = false;
    private static final String PROP_REQUIRED_MESSAGE_DEFAULT = "";
    private static final String PROP_CONSTRAINT_MESSAGE_DEFAULT = "";
    private static final String PROP_TYPE_DEFAULT = "text";
    private static final String PROP_HELP_MESSAGE_DEFAULT = "";
    private static final boolean PROP_USE_PLACEHOLDER_DEFAULT = false;
    private static final int PROP_ROWS_DEFAULT = 2;
    private static final boolean PROP_HIDE_TITLE_DEFAULT = false;

    @Self
    private SlingHttpServletRequest slingRequest;

    @ScriptVariable
    private Resource resource;

    @Inject
    private FormStructureHelperFactory formStructureHelperFactory;

    private String[] prefillValues;

    @ValueMapValue
    @Default(values = PROP_HELP_MESSAGE_DEFAULT)
    private String helpMessage;
    private String placeholder;

    @ValueMapValue
    @Default(booleanValues = PROP_USE_PLACEHOLDER_DEFAULT)
    private boolean usePlaceholder;

    @ValueMapValue
    @Default(values = PROP_TYPE_DEFAULT)
    private String type;

    @ValueMapValue
    @Default(booleanValues = PROP_READONLY_DEFAULT)
    private boolean readOnly;

    @ValueMapValue
    @Default(booleanValues = PROP_REQUIRED_DEFAULT)
    private boolean required;

    @ValueMapValue
    @Default(values = PROP_REQUIRED_MESSAGE_DEFAULT)
    private String requiredMessage;

    @ValueMapValue
    @Default(values = PROP_CONSTRAINT_MESSAGE_DEFAULT)
    private String constraintMessage;

    @ValueMapValue
    @Default(intValues = PROP_ROWS_DEFAULT)
    private int rows;

    @ValueMapValue
    @Default(booleanValues = PROP_HIDE_TITLE_DEFAULT)
    private boolean hideTitle;

    @PostConstruct
    private void initModel() {
        slingRequest.setAttribute(FormsHelper.REQ_ATTR_FORM_STRUCTURE_HELPER,
                formStructureHelperFactory.getFormStructureHelper(resource));
        prefillValues = FormsHelper.getValues(slingRequest, resource);
        if (prefillValues == null) {
            prefillValues = new String[]{this.getDefaultValue()};
        }
        if (usePlaceholder) {
            placeholder = helpMessage;
        }
    }

    @Override
    public String getValue() {
        String value = super.getValue();
        if (value.equals(PROP_VALUE_DEFAULT) && prefillValues.length > 0) {
            value = prefillValues[0];
        }
        return value;
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public String getDefaultValue() {
        return PROP_VALUE_DEFAULT;
    }

    @Override
    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public String getRequiredMessage() {
        return requiredMessage;
    }

    @Override
    public String getConstraintMessage() {
        return constraintMessage;
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public String getHelpMessage() {
        return helpMessage;
    }

    @Override
    protected String getIDPrefix() {
        return ID_PREFIX;
    }

    @Override
    protected String getDefaultName() {
        return PROP_NAME_DEFAULT;
    }

    @Override
    protected String getDefaultTitle() {
        return PROP_TITLE_DEFAULT;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean hideTitle() {
        return hideTitle;
    }

}
