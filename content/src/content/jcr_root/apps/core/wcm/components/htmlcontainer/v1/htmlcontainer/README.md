<!--
Copyright 2019 Adobe

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

HTML Container (v1)
====
HTML Container component written in HTL that displays the contents of the selected HTML file using the provided CSS and JS.

## Features
* Allows authors to use arbitrary HTML and required CSS and JS files.
* Requires the files to be placed into the DAM to enforce some element of code review and checkin (i.e. peer review before placing into the DAM).
* Multiple CSS and JS files are supported, but only one HTML file is supported per HTML Container Component.
* CSS files support "inline" and "reference" inclusion capability.
* JS files support "async", "inline", "defer", and "reference" inclusion capability.

### Use Object
The HTML Container component uses the `com.adobe.cq.wcm.core.components.models.HTMLContainer` Sling model as its Use-object.

### Edit dialog properties
The following JCR properties are used:

1. `./includeType` - defines how CSS or JS files are included, i.e. async (JS), defer (JS), inline (CSS, JS), reference (CSS, JS).
2. `./htmlFile` - the full path to the HTML file.
3. `./fileName` - Used in multifield elements for CSS and JS tabs, the full path to the CSS, or JS file.
4. `./id` - defines the component HTML ID attribute.

Error checking is provided to ensure only CSS files are included on the CSS tab, HTML files on the HTML tab, and JS files on the JS tab.

Files are included in CSS, HTML, JS order with appropriate script and style tags inserted.  HTML has no additional tags added.  The HTML should not have head or body tags.


## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.5
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_htmlcontainer\_v1
](https://www.adobe.com/go/aem_cmp_contentfragment_v1)
* **Component Library**: [https://www.adobe.com/go/aem\_cmp\_library\_cf](https://www.adobe.com/go/aem_cmp_library_cf)
