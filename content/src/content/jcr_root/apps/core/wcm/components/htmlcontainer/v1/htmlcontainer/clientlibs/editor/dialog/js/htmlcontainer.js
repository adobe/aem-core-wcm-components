/*******************************************************************************
 * Copyright 2020 Adobe
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
/*console.log("Client library called");

$(document).on("foundation-contentloaded", function(e) {
    var container = e.target;
	alert("hello world!");
    console.log("foundation-contentloaded was fired.");
    console.log(container);
});

*/

/* deal with *.html */
$(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
  selector: "[data-html-should-contain]",
  validate: function(el) {

    console.log('validating text ends with ' + shouldContain);
    console.log('input should end with ' + shouldContain);

    var shouldContain = el.getAttribute("data-html-should-contain");  //.html



    var input = el.value;  //input added by author

    if (input.endsWith(shouldContain) == false ) {
      return "The filename should end with " + shouldContain + ". It's current value is " + el.value + ".";
    } 
  }
});


/* deal with *.css */
$(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
  selector: "[data-css-should-contain]",
  validate: function(el) {

    console.log('validating text ends with ' + shouldContain);
    console.log('input should end with ' + shouldContain);

    var shouldContain = el.getAttribute("data-css-should-contain");  //.css

    var input = el.value;  //input added by author

    if (input.endsWith(shouldContain) == false) {
      return "The filename(s) should end with " + shouldContain + ". It's current value is " + el.value + ".";
    } 
  }
});



/* deal with *.js */
$(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
  selector: "[data-js-should-contain]",
  validate: function(el) {

    console.log('validating text ends with ' + shouldContain);
    console.log('input should end with ' + shouldContain);

    var shouldContain = el.getAttribute("data-js-should-contain");  //.js

    var input = el.value;  //input added by author

    if (input.endsWith(shouldContain) == false) {
      return "The filename(s) should end with " + shouldContain + ". It's current value is " + el.value + ".";

    } 
  }
});

