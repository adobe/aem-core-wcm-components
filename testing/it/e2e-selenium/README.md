# Purpose

This is the E2E for WCM Core Components UI testing, it is mainly based on Selenide, Selenium Jupiter, and Sling Testing Client.

PageObjects (Page, Component etc..) are defined in the common library [selenium-it-base](https://github.com/adobe/aem-selenium-it-base)

# Requirement

## Test Content
Some of the tests rely on predefined test content/assets. So you have to install the [it-content](https://github.com/adobe/aem-core-wcm-components/tree/master/testing/it/it-content) package on your test environment.

## Java version
Use JDK 11 to compile

# Usage

Running all test against local instance (running on default 4502 port) including pre/post integration tests.

```
mvn clean verify -Ptest-all
```

---
NOTE: This command run using Chrome in Docker (using Selenoid image).
---

Running specific test using 'it.test' parameter

```
mvn clean verify failsafe:integration-test -Dit.test=<classname>#testName
```

Running specific test using 'groups' parameter (for Junit5 Tag selection)

```
mvn clean verify -Ptest-all -Dgroups=group1
```

Running test using your local chrome browser

```
mvn clean verify -Ptest-all -Dsel.jup.default.browser=chrome
```

Running specific test using docker

```
 mvn clean verify failsafe:integration-test -Dsel.jup.vnc=true -DuseIP=<ip address> -Dit.test=<classname>#testName
```

Running against a non local AEM (Skyline for example)

Simply use the usual Sling Test Client parameters

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

# Running released test using generated jar with dependencies
Make sure to download latest junit-platform-console-standalone-<version>.jar
```
java -jar junit-platform-console-standalone-<version>.jar -cp 'target/core.wcm.components.it.e2e-selenium-<version>-it-jar-with-dependencies.jar' --select-package "com.adobe.cq.wcm.core.components.it.seljup" --include-classname "^.*IT?$"
```
