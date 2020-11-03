<!--
Copyright 2020 Adobe

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
Post form data - Form Action
====
Forwards form data as JSON to a remote service via a post request. 

## Features
* Display error message if post request to remote service fails
* Redirect to internal (thank you) page if request was successful  

### OSGi Configuration
The HTTP Client which is responsible for the remote post request can be configured in the OSGi configuration `Core Components Form API 
Client` (PID
 com.adobe.cq.wcm
.core.components.internal.form.FormHandlerImpl)

1. `connectionTimeout` - defines the timeout in milliseconds until a connection is established. A timeout value of zero is interpreted as
 an infinite timeout. Default is 6000ms
2. `socketTimeout` - defines the timeout in milliseconds for waiting for data or a maximum period of inactivity between two consecutive 
data packets. Default is 6000ms

## Information
* **Vendor**: Adobe
* **Compatibility**: AEM 6.4
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_form\_action\_forward](https://www.adobe.com/go/aem_cmp_form_action_forward)
* **Author**: [Burkhard Pauli](https://github.com/bpauli)

