# Component Checklist

**Work in progress:** This page is a tech preview and is being worked on.

## Introduction

The items of the component checklist will serve to certify [AEM Components](https://docs.adobe.com/docs/en/aem/6-2/develop/components.html), so that AEM project implementations can compare them and know which ones fit their needs and requirements. This checklist applies to AEM Components that render parts of a cq:Page, including all their dependencies (Java classes, client libraries, etc.). Exceptions to the checklist are possible, but for each there must be an explicit note in the documentation.


## The Checklist

1. [Supported](#supported)
2. [Documented](#documented)
3. [Available](#available)
4. [Production Ready](#production-ready)
    1. [Secure](#secure)
    2. [Fast](#fast)
    3. [Mobile-friendly](#mobile-friendly)
    4. [Internationalized](#internationalized)
    5. [Accessible](#accessible)
5. [Blends In](#blends-in)
    1. [Compatible](#compatible)
    2. [Lean](#lean)
    3. [Separated concerns](#separated-concerns)
    4. [Standard](#standard)
6. [Tested](#tested)

### 1. Supported

Whether the documented capabilities are supported by the vendor/author of the component for production usage.

- [ ] Link to contact support for the component.
- [ ] Description of the conditions to get or purchase support.
- [ ] Support for the documented intended use and options.

### 2. Documented

Whether the capabilities and intended use are described.

- [ ] Description of the intended use.
- [ ] Features and edit/design dialog options (possibly with screenshots).
- [ ] List of the exceptions to this checklist (possibly with some justification).
- [ ] Content structure and properties.
- [ ] Exported APIs and extension points.

### 3. Available

Whether the component's code can be accessed and added as dependency on an AEM implementation project.

- [ ] [Apache License](http://www.apache.org/licenses/LICENSE-2.0) or [similar in terms](http://www.apache.org/legal/resolved.html#category-a). 
- [ ] Publicly browseable and downloadable source code.
- [ ] Maven project that can be used as dependency.

### 4. Production Ready
Whether the component matches the basic requirements to be used in production for the intended use.

#### 4.i. Secure
- [ ] No known security vulnerability.
- [ ] No hard-coded or stored clear-text passwords.
- [ ] Systematic XSS protection and string/URL encoding (as enforced by Sightly).
- [ ] Outgoing network connections must be explicitly enabled and documented accordingly.
- [ ] Use service users for background processes (no loginAdministrative).

#### 4.ii. Fast
- [ ] Features that can have a global performance impact or performance bottlenecks must be explicitly enabled and documented accordingly.
- [ ] Cacheable output (eg. no user-specific data in the response, no query parameters).
- [ ] No JCR queries, no servlet filters, and no custom binding values providers.
- [ ] No server-side event handling without limited scope and explicit documentation.
- [ ] JavaScript and CSS placed in client libraries that can loaded as desired on the page (on top, bottom, or asynchronously).

#### 4.iii. Mobile-friendly
- [ ] Optimized loading of media sizes (images and video).
- [ ] Fluid and responsive layout using the AEM grid.
- [ ] Touch UI component dialogs for edit and design dialogs.

#### 4.iv. Internationalized
- [ ] All publish-side labels are modifiable through the edit dialog.
- [ ] All author-side labels are internationalized through a dictionary.
- [ ] Accounts for variable text lengths.

#### 4.v. Accessible
- [ ] Follows WCAG 2.0 guidelines (eg. component can be used with a keyboard).
- [ ] Extract acessibility related dialog settings to a specific tab.
- [ ] For custom UI components, follows the ARIA and ATAG guidelines.

### 5. Blends In
Whether the component will fit into a project that follows best practice and will not interfere with other elements.

#### 5.i. Compatible
- [ ] Is multi-site friendly: it won't impact other sites, and it's configuration can be site-specific.
- [ ] No assumption on the content structure, configuration or setup of the instance.
- [ ] Is compatible with the AEM page editor features (like Personalization, ContextHub, Layouting mode, Responsive preview, Launches, etc).
- [ ] No page refresh when adding the component to the paragraph system, or when modifying it.
- [ ] No usage of JCR API, of JCR observation, custom node types, or custom JCR namespaces.
- [ ] No hardcoded resource types, paths, groups, etc.
- [ ] Tolerant if the content structure is incorrect or outdated.
- [ ] Namespaced CSS and JS selectors to apply only to the one component.

#### 5.ii. Lean
- [ ] No dependencies on back-end or front-end frameworks.
- [ ] No abstraction layers that aren’t absolutely necessary.
- [ ] No functionality that isn’t deemed necessary.
- [ ] No styles or scripts that are design or site specific (keep them to the bare minimum).

#### 5.iii. Separated concerns
- [ ] No JSP, use Sightly (default choice) or Handlebars (when client & server-side rendering is needed).
- [ ] No HTML or CSS generated from Java or JavaScript code.
- [ ] No styles, scripts, or script events inlined in HTML.
- [ ] No client-side parsing of URL structures.

#### 5.iv. Standard
- [ ] No deprecated APIs.

Follows naming, formatting and coding conventions:
- [ ] Documentation
- [ ] Maven
- [ ] Bundles
- [ ] JCR
- [ ] URL
- [ ] Java
- [ ] Sightly/Handlebars (no JSP)
- [ ] HTML
- [ ] JavaScript
- [ ] CSS
- [ ] Logging

### 6. Tested
Whether the component can be verified to perform as expected once installed on a system, and won't suffer from regressions.

- [ ] Unit and/or integration tests
- [ ] Automated functional tests
- [ ] Performance tests
