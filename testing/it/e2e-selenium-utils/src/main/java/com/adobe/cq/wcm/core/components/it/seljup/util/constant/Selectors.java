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

package com.adobe.cq.wcm.core.components.it.seljup.util.constant;

public class Selectors {
    public static String SELECTOR_BUTTON_TITLE = "input[name='./jcr:title']";
    public static String SELECTOR_BUTTON_NAME = "input[name='./name']";
    public static String SELECTOR_BUTTON_VALUE = "input[name='./value']";

    public static String SELECTOR_ICON = "input[name='./icon']";

    public static String SELECTOR_CONFIG_BUTTON = "button[data-action='CONFIGURE']";
    public static String SELECTOR_SUBMIT_BUTTON = "button[type='submit']";
    public static String SELECTOR_CANCEL_CONFIG_BUTTON = "button[is='coral-button'][title='Cancel']";
    public static String SELECTOR_DONE_CONFIG_BUTTON = "button[is='coral-button'][title='Done']";
    public static String SELECTOR_SAVE_BUTTON = "button[is='coral-button'][title='Save']";
    public static String SELECTOR_CONFIG_DIALOG = ".cq-dialog.foundation-form.foundation-layout-form";

    public static final String SELECTOR_ITEM_ELEMENT_CONTENT = "coral-selectlist-item-content";

    public static final String SELECTOR_PANEL_SELECT = ".cq-editable-action[data-action='PANEL_SELECT']";

    public static final String SELECTOR_CORAL_DIALOG_CONTENT = "coral-dialog-content";
}
