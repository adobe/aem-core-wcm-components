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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.form.FormConstants;
import com.adobe.cq.wcm.core.components.models.form.Field;
import com.day.cq.wcm.foundation.forms.FormStructureHelperFactory;
import com.day.cq.wcm.foundation.forms.FormsHelper;

@Model(adaptables = SlingHttpServletRequest.class,
       adapters = {Field.class, ComponentExporter.class},
       resourceType = {FormConstants.RT_CORE_FORM_HIDDEN_V1, FormConstants.RT_CORE_FORM_HIDDEN_V2})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class HiddenImpl extends AbstractFieldImpl {

    static final String PROP_NAME_DEFAULT = "hidden";
    static final String PROP_VALUE_DEFAULT = "";
    static final String ID_PREFIX = "form-hidden";

    @Self
    private SlingHttpServletRequest slingRequest;

    @ScriptVariable
    private Resource resource;

    @OSGiService
    private FormStructureHelperFactory formStructureHelperFactory;

    @PostConstruct
    private void initModel() {
        slingRequest.setAttribute(FormsHelper.REQ_ATTR_FORM_STRUCTURE_HELPER,
                formStructureHelperFactory.getFormStructureHelper(resource));
        String[] prefillValues = FormsHelper.getValues(slingRequest, resource);
        if (prefillValues == null || prefillValues.length == 0) {
            prefillValues = new String[]{PROP_VALUE_DEFAULT};
        }
        if (value == null) {
            value = prefillValues[0];
        }
    }

    @Override
    public String getValue() {
        return value;
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
    public String getHelpMessage() {
        return null;
    }

    @Override
    protected String getDefaultValue() {
        return PROP_VALUE_DEFAULT;
    }

    @Override
    protected String getDefaultTitle() {
        return null;
    }

}
