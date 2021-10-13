/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.formoptions.v2;

import com.adobe.cq.wcm.core.components.it.seljup.util.components.formoptions.BaseFormOptions;

public class FormOptions extends BaseFormOptions {
    public FormOptions() {
        help = ".cmp-form-options__help-message";
        description = ".cmp-form-options__field-description";
        checkbox = ".cmp-form-options__field--checkbox";
        radio = ".cmp-form-options__field--radio";
        dropDown = ".cmp-form-options__field--drop-down";
        multiDropDown = ".cmp-form-options__field--multi-drop-down";
    }
}
