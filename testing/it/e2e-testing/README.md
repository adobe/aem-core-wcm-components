# Purpose

End-to-end browser tests for the WCM Core Components UI (authoring and rendering). The tests are written with
[Playwright for Java](https://playwright.dev/java/), JUnit 5 and the Sling Testing Clients (used for server-side
fixtures through HTTP).

# Requirements

## Test Content
Some of the tests rely on predefined test content/assets. Install the `it.ui.content` package
(`testing/it/it.ui.content`) on your test environment.

## Java version
Use JDK 11 to compile.

## Browser
The tests drive an installed Google Chrome (Playwright channel `chrome`) and need a display, or run headless with
`-Dpw.headless=true`. Use `-Dpw.channel=<channel>` to pick another browser channel and `-Dpw.trace=false` to
disable the Playwright traces (kept in `target/playwright-traces` for failing tests).

# Usage

This module is not part of the default reactor build (so a regular build does not download the Playwright driver)
and is not published to Maven Central. Build it from the repository root with the `e2e-testing` profile
(`mvn clean install -Pe2e-testing`), or run the commands below from this directory.

Running all tests against a local instance (running on the default 4502 port):

```
mvn clean verify -Ptest-all
```

Running a specific test using the `it.test` parameter:

```
mvn clean verify -Ptest-all -Dit.test=<classname>#testName
```

Running a group of tests using the `groups` parameter (JUnit 5 tag selection, `playwright-group1` ... `playwright-groupN`):

```
mvn clean verify -Ptest-all -Dgroups=playwright-group1
```

Running against a non local AEM (Skyline for example), use the usual Sling Testing Client parameters:

```
   -Dsling.it.instances=2 \
   -Dsling.it.instance.url.1="${CM_AUTHOR_URL}" \
   -Dsling.it.instance.url.2="${CM_PUBLISH_URL}" \
   -Dsling.it.instance.runmode.1=author \
   -Dsling.it.instance.adminUser.1=${AEM_USER} \
   -Dsling.it.instance.adminPassword.1=${AEM_PASSWORD} \
   -Dsling.it.instance.runmode.2=publish \
   -Dsling.it.instance.adminUser.2=${AEM_USER} \
   -Dsling.it.instance.adminPassword.2=${AEM_PASSWORD}
```

To run the tests against a Dockerized AEM, see [`testing/it/docker`](../docker/README.md).

# Running the tests from the jar with dependencies

`mvn package` in this module produces `target/core.wcm.components.it.e2e-testing-<version>-it-jar-with-dependencies.jar`
(the jar is not published). Download the latest `junit-platform-console-standalone-<version>.jar`, then:

```
java -jar junit-platform-console-standalone-<version>.jar -cp 'target/core.wcm.components.it.e2e-testing-<version>-it-jar-with-dependencies.jar' --select-package "com.adobe.cq.wcm.core.components.it.pw" --include-classname "^.*IT?$"
```
