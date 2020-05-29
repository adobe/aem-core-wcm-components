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

# Data Layer Integration with the Core Components

The Core Components provide an out-of-the-box integration with the [Adobe Client Data Layer](https://github.com/adobe/adobe-client-data-layer), which for convenience is called data layer in this page.

## Enabling the Data Layer

The data layer is disabled by default. 

To enable the data layer for your site:
1. Create the following structure below the `/conf` node:
    `/conf/<my-site>/sling:configs/com.adobe.cq.wcm.core.components.internal.DataLayerConfig`
1. Add the `enabled` boolean property and set it to `true`.
1. Add a `sling:configRef` property to the `jcr:content` node of your site below `/content` (e.g. `/content/<my-site>/jcr:content`) and set it to `/conf/<my-site>`

## Data Layer State Structure

When the data layer is enabled, the javascript `adobeDataLayer` object is available on the page and is populated with the components and their properties that are used on the page.

The data layer state (returned by calling `adobeDataLayer.getState()`) is an object with two objects (`page` and `component`). All the components are stored below the `component` object as a flat structure. The structure looks as follows:
```
{
  "page": {
    "page-id": {
      "key1": "value1,
      "key2": "value2,
    }
  },
  "component": {
    "component-id1": {
      "key1": "value1,
      "key2": "value2,
    },
    "component-id2": {
      "key1": "value1,
      "key2": "value2,
    },
    ...
  }
}
```

Calling `adobeDataLayer.getState()` in the browser console will return e.g.:

```
{
  "page": {
    "page-df1699a779": {
      "xdm:tags": [],
      "xdm:language": "en-GB",
      "xdm:template": "/conf/core-components-examples/settings/wcm/templates/content-page",
      "repo:path": "/content/core-components-examples/library/page-authoring/title.html",
      "dc:title": "Title",
      "@type": "core-components-examples/components/page",
      "repo:modifyDate": "2020-05-28T08:46:44Z",
      "dc:description": "Display a page heading"
    }
  },
  "component": {
    "image-1bd0710a59": {
      "image": {
        "xdm:tags": [],
        "repo:id": "401835a1-832c-427f-a508-c7f55495a4df",
        "repo:modifyDate": "2020-05-28T08:46:49Z",
        "@type": "image/svg+xml",
        "repo:path": "/content/dam/core-components-examples/library/aem-corecomponents-logo.svg"
      },
      "dc:title": "AEM Core Components",
      "xdm:linkURL": "/content/core-components-examples/library.html",
      "@type": "core-components-examples/components/image",
      "repo:modifyDate": "2019-01-09T16:58:39Z",
      "parentId": "page-df1699a779"
    },
    "accordion-3b90a50076": {
      "shownItems": [
        "accordion-3b90a50076-item-6b9d62c47d",
        "accordion-3b90a50076-item-f05b6c6615",
        "accordion-3b90a50076-item-f3fcd996f5",
        "accordion-3b90a50076-item-cd527eac50"
      ],
      "@type": "core-components-examples/components/accordion",
      "repo:modifyDate": "2020-01-14T16:59:24Z",
      "parentId": "page-df1699a779"
    },
    "title-13869e3afb": {
      "dc:title": "Standard",
      "@type": "core/wcm/components/title/v2/title",
      "repo:modifyDate": "2018-12-07T12:49:20Z",
      "parentId": "page-df1699a779"
    },
    "text-c36e0b2cf9": {
      "@type": "core/wcm/components/text/v2/text",
      "repo:modifyDate": "2018-12-07T12:49:23Z",
      "xdm:text": "<p>Default title without any configuration. The title is taken from the current page.</p>\n",
      "parentId": "page-df1699a779"
    }
  }
}
```

## Components supporting the Data Layer

The following table shows the components supporting the data layer:

Components | Data Layer Support
---------- | -------------------
Accordion | x
Breadcrumb | x
Button | x
Carousel | x
Container | 
Content Fragment | 
Content Fragment List | 
Download | 
Embed | 
Experience Fragment | 
Form button | 
Form container | 
Form hidden field | 
Form options field | 
Form text field | 
Image | x
Language Navigation | x
List | x
Navigation | x
Page | x
Progress Bar | 
Quick Search | 
Separator | 
Sharing | 
Tabs | x
Teaser | x
Text | x
Title | x

## Core Component Schemas

#### Component and Container Item

Schema used for all the components that are not listed below.

```
id: {                   // component ID
    @type               // resource type
    repo:modifyDate     // last modified date
    dc:title            // title
    dc:description      // description
    xdm:text            // text
    xdm:linkURL         // link URL
    parentId            // parent component ID
}
```

#### Container Components

Schema used for the Accordion, the Carousel and the Tabs components.

```
id: {
    @type
    repo:modifyDate
    dc:title
    dc:description
    xdm:text
    xdm:linkURL
    parentId
    shownItems          // array of the displayed item IDs
}
```

#### Page

Schema used for the Page component:

```
id: {
    @type
    repo:modifyDate
    dc:title
    dc:description
    xdm:text
    xdm:linkURL
    parentId
    xdm:tags            // page tags
    repo:path           // page path
    xdm:template        // page template
    xdm:language        // page language
}
```

#### Image

Schema used for the Image component:

```
id: {
    @type
    repo:modifyDate
    dc:title
    dc:description
    xdm:text
    xdm:linkURL
    parentId
    image               // asset detail (see below section)
}
```

#### Asset

Schema used inside the Image component schema:

```
id: {
    repo:id             // asset UUID
    repo:path           // asset path
    @type               // asset resource type
    xdm:tags            // asset tags
    repo:modifyDate
}
```

## HTML attributes

When the data layer is enabled, the body element has a `data-cmp-data-layer-enabled` attribute.

The Core Components supporting the data layer have a `data-cmp-data-layer` attribute populated with the component properties as defined by the component model.

Clickable elements of the Core Components (e.g. links, buttons) have a `data-cmp-clickable` attribute (see below for more details about the events).

## Events

Clicking a clickable element (an element that has a `data-cmp-clickable` attribute) makes the data layer trigger a `cmp:click` event.

Manipulating the accordion (expand/collapse), the carousel (next/previous buttons) and the tabs (tab select) components makes the data layer trigger respectively a `cmp:show` and a `cmp:hide` event.

As soon as the data layer is populated with the core components available on the page, the data layer triggers a `cmp:loaded` event.

## JSON Rendering

The JSON rendering of a Core Component exposes a `dataLayer` property that is populated with the data layer specific properties defined by the component model. E.g.:

```
  "dataLayer": {
    "title-b358cbbf54": {
      "dc:title": "Lorem Ipsum",
      "@type": "core/wcm/components/title/v2/title",
      "repo:modifyDate": "2018-12-07T12:53:27Z"
    }
  }

```

## Enabling the Data Layer for Custom Components

To automatically add a custom component to the data layer:
1. Define the properties of the custom component model that needs to be tracked.
1. Add a component ID to the the custom component HTL.
1. Add the `data-cmp-data-layer` attribute to the custom component HTL. E.g. `data-cmp-data-layer="${mycomponent.data.json}"`.

To automatically make the data layer trigger a `cmp:click` event each time a specific element of the custom component is clicked:
1. In the custom component HTL add the `data-cmp-clickable` attribute to the element to be tracked.

The `data-cmp-data-layer-enabled` attribute can be queried client side to check if the data layer is enabled.



