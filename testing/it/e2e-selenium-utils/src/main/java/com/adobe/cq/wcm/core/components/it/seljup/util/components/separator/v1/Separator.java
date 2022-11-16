/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.separator.v1;

import com.adobe.cq.testing.selenium.pagewidgets.common.BaseComponent;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.separator.SeparatorEditDialog;

public class Separator extends BaseComponent {

    private static final String SEPARATOR = ".cmp-separator";
    private static final String HORIZONTAL_RULE = ".cmp-separator__horizontal-rule";

    public Separator() {
        super(SEPARATOR);
    }

    public SeparatorEditDialog getEditDialog() {
        return new SeparatorEditDialog();
    }

    public boolean isDecorative() {
        String role = currentElement.$(HORIZONTAL_RULE).getAttribute("role");
        return "none".equals(role);
    }
}
