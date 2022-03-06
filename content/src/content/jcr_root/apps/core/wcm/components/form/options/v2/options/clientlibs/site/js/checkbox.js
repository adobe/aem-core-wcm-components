/*******************************************************************************
 * Copyright 2016 Adobe
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
$(document).ready(function () {
    $(document).on('click', 'button.cmp-form-button', function() {		  
        $('form .cmp-form-option').each(function(e){
            //$(this).find('fieldset');
            var errorMsg = $(this).attr('data-cmp-required-message');	
            //console.log(errorMsg);
            if($(this).find('fieldset').attr('required') != undefined){
                var elemName = $('input:first', this).attr('name');
                //console.log("Name -- " + elemName);
                //var fieldset = $(this).find('fieldset');
                var firstElement = $('input:first', this);
                if (($("input[name*=" + elemName + "]:checked").length)<=0) {
                    firstElement.on('invalid', function(){ firstElement.get(0).setCustomValidity(errorMsg); });
                    //fieldset.on('invalid', function(){ fieldset.setCustomValidity(errorMsg); });
                    return false;  
                }else{
                    firstElement.get(0).setCustomValidity('');
                    firstElement.removeAttr('required');	
                    return true;
                }				
            }

        });
    });	
});	    