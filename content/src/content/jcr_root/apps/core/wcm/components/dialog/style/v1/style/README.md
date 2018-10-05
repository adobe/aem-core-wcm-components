<!--
Copyright 2017 Adobe Systems Incorporated

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
Dialog Style Selector (v1)
====
Component for TouchUI authoring dialogs written in HTL that adds the style system options from a component's policy to the component dialog.

## Features
* Consolidates authoring options
* Checkboxes for multiple-selection styles
* Radio buttons for single-selection styles

### Use Object
The Dialog Style Selector component uses the `com.adobe.cq.editor.model.StyleSelector` Sling Model as its Use-object.

## Dialog Usage
The component can be included in in a dialog by referencing its resource type. An example dialog:
```
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" 
          xmlns:jcr="http://www.jcp.org/jcr/1.0" 
          xmlns:nt="http://www.jcp.org/jcr/nt/1.0"
          jcr:primaryType="nt:unstructured"
          jcr:title="Some Component"
          sling:resourceType="cq/gui/components/authoring/dialog">
    <content
        jcr:primaryType="nt:unstructured"
        sling:resourceType="granite/ui/components/coral/foundation/container">
        <items jcr:primaryType="nt:unstructured">
            <tabs
                jcr:primaryType="nt:unstructured"
                sling:resourceType="granite/ui/components/coral/foundation/tabs">
                <items jcr:primaryType="nt:unstructured">
                    <styles
                        jcr:primaryType="nt:unstructured"
                        jcr:title="Styles"
                        sling:resourceType="granite/ui/components/coral/foundation/container"
                        margin="{Boolean}true">
                        <items jcr:primaryType="nt:unstructured">
                            <columns
                                jcr:primaryType="nt:unstructured"
                                sling:resourceType="granite/ui/components/coral/foundation/fixedcolumns"
                                margin="{Boolean}true">
                                <items jcr:primaryType="nt:unstructured">
                                    <column
                                        jcr:primaryType="nt:unstructured"
                                        sling:resourceType="granite/ui/components/coral/foundation/container">
                                        <items jcr:primaryType="nt:unstructured">
                                            <style
                                                jcr:primaryType="nt:unstructured"
                                                sling:resourceType="core/wcm/components/dialog/style/v1/style"/>
                                        </items>
                                    </column>
                                </items>
                            </columns>
                        </items>
                    </styles>
                </items>
            </tabs>
        </items>
    </content>
</jcr:root>
```

## Client Libraries
The component extends the `cq.authoring.dialog` client library category. 
