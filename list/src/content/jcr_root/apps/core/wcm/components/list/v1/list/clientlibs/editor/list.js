/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/*global Granite */
(function (document, $) {
    'use strict';

    const LIST_COMPONENT_V1_CLASS = 'core-wcm-list-v1',
          LIST_ITEM_TYPE_NAME     = './itemType',
          LIST_DISPLAY_AS_NAME    = './displayAs';

    function toggleDialogField($field, shouldDisable) {
        if ($field) {
            if (shouldDisable) {
                $field.parent().parent().hide();
                /**
                 * delete the property through the Sling POST Servlet
                 */
                $field.attr('name', LIST_DISPLAY_AS_NAME + '@Delete');
            } else {
                $field.parent().parent().show();
                $field.attr('name', LIST_DISPLAY_AS_NAME);
            }
        }

    }

    function getDialogFieldSelector(fieldName) {
        return 'form > div.' + LIST_COMPONENT_V1_CLASS + ' [name="' + fieldName + '"]';
    }

    $(document).on('foundation-contentloaded', function () {

        var $itemTypeField  = $(getDialogFieldSelector(LIST_ITEM_TYPE_NAME)),
            $displayAsField = $(getDialogFieldSelector(LIST_DISPLAY_AS_NAME));

        if ($itemTypeField.length > 0 && $displayAsField.length > 0) {
            toggleDialogField($displayAsField, $itemTypeField.val() !== '');

            $itemTypeField.on('change focusout', function () {
                toggleDialogField($displayAsField, $itemTypeField.val() !== '');
            });
        }
    });



})(document, Granite.$);
