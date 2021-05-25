<!--
Copyright 2021 Adobe

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
Video (v1)
====
Video component written in HTL that renders a video reference using the standard html5 player

## Features
* allows enabling or disabling autoplay, loop, user controls
* allows poster image to be used as placeholder when video is loading

### Use Object
The Video component uses the `com.adobe.cq.wcm.core.components.models.Video` Sling model as its Use-object.

### Edit Dialog Properties
The following properties are written to JCR for this Video component and are expected to be available as `Resource` properties:

1. `./fileReference` - property or `file` child node - will store either a reference to the video file
2. `./posterFileReference` - property or `file` child node - will store either a reference to the image file, or the image file
3. `./loopEnabled` - defines whether the video should loop from the start when playback is finished
3. `./hideControl` - defines whether video controls should be hidden for the user
3. `./autoplayEnabled` - defines whether the video should autoplay

## BEM Description
```
BLOCK cmp-video
    ELEMENT cmp-video__video
```

## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.4
* **Status**: work-in-progress
