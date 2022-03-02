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
YouTube Embeddable (v1)
====
YouTube component written in HTL that includes the YouTube player via an iFrame. It is used from the [Embed component](../..).

## Features
* Allows to configure player parameters
* Each player parameter can be enabled and given a default value by a template author.

### Component Policy Configuration Properties
The following configuration properties are used and exposed on the embed component's design dialog:

1. `./youtubeMuteEnabled` - enables the ability of content authors to configure the [YouTube mute parameter][yt-parameters].
1. `./youtubeMuteDefaultValue` - the default value of the [YouTube mute parameter][yt-parameters].
1. `./youtubeAutoPlayEnabled` - enables the ability of content authors to configure the [YouTube Autoplay parameter][yt-parameters]. 
1. `./youtubeAutoPlayDefaultValue` - the default value of the [YouTube Autoplay parameter][yt-parameters]. Be aware that modern browsers restrict the possibilities for autoplay. Further information: [Chrome Autoplay Policy](https://developers.google.com/web/updates/2017/09/autoplay-policy-changes), [Firefox Autoplay Policy](https://support.mozilla.org/en-US/kb/block-autoplay), [MS Edge Autoplay Policy](https://docs.microsoft.com/en-us/microsoft-edge/dev-guide/browser-features/autoplay-policies). In general it is much more likely that autoplay works if combined with `mute`.
1. `./youtubeLoopEnabled` - enables the ability of content authors to configure the [YouTube loop parameter][yt-parameters].
1. `./youtubeLoopDefaultValue` - the default value of the [YouTube loop parameter][yt-parameters].
1. `./youtubePlaysInlineEnabled` - enables the ability of content authors to configure the [YouTube playsinline parameter][yt-parameters].
1. `./youtubePlaysInlineDefaultValue` - the default value of the [YouTube playsinline parameter][yt-parameters].
1. `./youtubeRelatedVideosEnabled` - enables the ability of content authors to configure the [YouTube rel parameter][yt-parameters].
1. `./youtubeRelatedVideosDefaultValue` - the default value of the [YouTube rel parameter][yt-parameters].


### Edit Dialog Properties
The following JCR properties are used:

1. `./youtubeVideoId` - defines the YouTube video ID.
1. `./youtubeWidth` - defines the YouTube video player width.
1. `./youtubeHeight` - defines the YouTube video player height.
1. `./youtubeAspectRatio` - defines the YouTube video player aspect ratio.
1. `./youtubeMute` - the value of the [YouTube mute parameter][yt-parameters].
1. `./youtubeAutoPlay` - the value of the [YouTube autoplay parameter][yt-parameters]. Be aware that modern browsers restrict the possibilities for autoplay. Further information: [Chrome Autoplay Policy](https://developers.google.com/web/updates/2017/09/autoplay-policy-changes), [Firefox Autoplay Policy](https://support.mozilla.org/en-US/kb/block-autoplay), [MS Edge Autoplay Policy](https://docs.microsoft.com/en-us/microsoft-edge/dev-guide/browser-features/autoplay-policies). In general it is much more likely that autoplay works if combined with `mute`.
1. `./youtubeLoop` - the value of the [YouTube loop parameter][yt-parameters].
1. `./youtubePlaysInline` - the value of the [YouTube playsinline parameter][yt-parameters].
1. `./youtubeRel` - the default value of the [YouTube rel parameter][yt-parameters].


## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_embed\_v1](https://www.adobe.com/go/aem_cmp_embed_v1)
* **Component Library**: [https://www.adobe.com/go/aem\_cmp\_library\_embed](https://www.adobe.com/go/aem_cmp_library_embed)
* **Author**: [Konrad Windszus](https://github.com/kwin)
* **Co-authors**: [Jean-Christophe Kautzmann](https://github.com/jckautzmann), [Richard Hand](https://github.com/richardhand), [Vlad Bailescu](https://github.com/vladbailescu)

_If you were involved in the authoring of this component and are not credited above, please reach out to us on [GitHub](https://github.com/adobe/aem-core-wcm-components)._

[yt-parameters]: https://developers.google.com/youtube/player_parameters