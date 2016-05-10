**Work in progress:** This page is a tech preview and being worked on.

# Component Guidelines

The below guidelines should serve as checklist and quality measure for components released for the Experience Manager community, allowing an easy overview of the status of components and comparison among them.

**Table of Content**
- [Supported](#supported)
- [Documented](#documented)
- [Production Ready](#production-ready)
    - [Secure](#secure)
    - [Fast](#fast)
    - [Mobile-friendly](#mobile-friendly)
    - [Internationalized](#internationalized)
    - [Accessible](#accessible)
- [Blends In](#blends-in)
    - [Compatible](#compatible)
    - [Lean](#lean)
    - [Separated concerns](#separated-concerns)
    - [Standard](#standard)
- [Tested](#tested)

## Supported
Whether the documented capabilities of the component are supported by the vendor/creator of the component for production usage.
- [ ] Link to contact support for the component.
- [ ] Description of the conditions to get or purchase support.
- [ ] Support for the documented intended use and options.

## Documented
Whether the capabilities and intended use are described.
- [ ] Description of the intended use.
- [ ] Features and edit/design dialog options.
- [ ] Content structure and properties.
- [ ] Exported APIs and extension points.

## Production Ready
Whether the component matches the basic requirements to be used in production for the intended use.

#### Secure
- [ ] Uses the request session only to resolve and manipulate resources.
- [ ] Systematic XSS protection and string/URL encoding (as enforced by Sightly).
- [ ] No hard-coded or stored clear-text passwords.
- [ ] No outgoing network connections that aren’t explicitly documented and configured.
- [ ] No known security vulnerability.

#### Fast
- [ ] Cacheable output (eg. no user-specific data in the response, no GET attributes).
- [ ] No JCR queries, servlet filters or binding values providers.
- [ ] No server-side event handling without limited scope and clear documentation.
- [ ] JavaScript and CSS placed in client libraries that can loaded as desired on the page.
- [ ] No known significant performance bottleneck that isn't documented.

#### Mobile-friendly
- [ ] Optimized loading of media sizes (images and video).
- [ ] Fluid and responsive layout using the AEM grid.
- [ ] Touch UI component dialogs for edit and design dialogs.

#### Internationalized
- [ ] All publish-side labels are modifiable through the edit dialog.
- [ ] All author-side labels are internationalized through a dictionary.
- [ ] Accounts for variable text lengths.

#### Accessible
- [ ] Follows Accessible Rich Internet Applications (WAI-ARIA 1.0).
- [ ] Follows Web Content Accessibility Guidelines (WCAG 2.0).
- [ ] Follows Authoring Tool Accessibility Guidelines (ATAG 1.0).

## Blends In
Whether the component will fit into a project that follows best practice and will not interfere with other elements.

#### Compatible
- [ ] Works with the features of the AEM page editor, like Personalization, ContextHub, Layouting mode, Responsive preview, etc.
- [ ] No usage of JCR API, of JCR observation, or of custom node types.
- [ ] No page refresh when adding the component to the paragraph system, or when modifying it.
- [ ] No assumption on the content structure, configuration or setup of the instance.
- [ ] No hardcoded resource types, paths, groups, etc.
- [ ] Tolerant if the content structure is incorrect or outdated.
- [ ] Namespaced CSS and JS selectors to apply only to the one component.

#### Lean
- [ ] No dependencies on back-end or front-end frameworks.
- [ ] No abstraction layers that aren’t absolutely necessary.
- [ ] No functionality that isn’t deemed necessary.
- [ ] No styles or scripts that are design or site specific (keep them to the bare minimum).

#### Separated concerns
- [ ] No JSP, use Sightly (default choice) or Handlebars (when client & server-side rendering is needed).
- [ ] No HTML or CSS generated from Java or JavaScript code.
- [ ] No styles, scripts, or script events inlined in HTML.
- [ ] No client-side parsing of URL structures.

#### Standard
- [ ] No deprecated APIs.

Follows coding, naming and formatting conventions:
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

## Tested
Whether the component can be verified to perform as expected once installed on a system, and won't suffer from regressions.

- [ ] Unit and/or integration tests
- [ ] Automated functional tests
- [ ] Performance tests
