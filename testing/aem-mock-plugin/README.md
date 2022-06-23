# AEM WCM Core Components Plugin for AEM Mocks

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.adobe.cq/core.wcm.components.testing.aem-mock-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.adobe.cq/core.wcm.components.testing.aem-mock-plugin)
[![javadoc](https://javadoc.io/badge2/com.adobe.cq/core.wcm.components.testing.aem-mock-plugin/javadoc.svg)](https://javadoc.io/doc/com.adobe.cq/core.wcm.components.testing.aem-mock-plugin)

Context Plugin for [AEM Mocks][aem-mock].

Helps setting up mock environment for unit testing custom code based on Core Components.


## Purpose

When you are writing unit tests for your own Sling Models that are extending Core Component Sling Models using the [Delegate Pattern][delegate-pattern] you need to make sure the actual Core Component model implementation can be executed in your unit tests.

Some of the core component model implementations rely on OSGi services provided by the Core Component bundle, which need to be present in the unit test as well. To ease this setup and avoid to having look up all internal implementation details a "Context Plugin" for AEM Mocks is provided, which makes sure all required OSGi services are present in your unit test.

For successfully writing tests for models extending Core Component models involves some more aspects, covered in detail in this [adaptTo() talk][aem-mock-talk-2021].


## Usage

```java
import static com.adobe.cq.wcm.core.components.testing.mock.ContextPlugins.CORE_COMPONENTS;

@ExtendWith(AemContextExtension.class)
class MyUnitTest {

  private final AemContext context = new AemContextBuilder()
      .plugin(CORE_COMPONENTS)
      .build();

  // ...

}
```


[aem-mock]: https://wcm.io/testing/aem-mock/
[delegate-pattern]: https://github.com/adobe/aem-core-wcm-components/wiki/Delegation-Pattern-for-Sling-Models
[aem-mock-talk-2021]: https://adapt.to/2021/en/schedule/whats-new-in-aem-mocks.html
