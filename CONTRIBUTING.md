# Contributing

Thank you for choosing to contribute to the Adobe Experience Manager Core Components project, we really appreciate your time and effort! 😃🎊

The following are a set of guidelines for contributing to the project.

#### Contents

* [Code of Conduct](#code-of-conduct)
* [Ways of Contributing](#ways-of-contributing)
  * [Joining Feature Discussions](#joining-feature-discussions-) 💭
  * [Joining Developer Discussions](#joining-developer-discussions-) 💬
  * [Reporting Bugs](#reporting-bugs-) 🐛
  * [Requesting Features](#requesting-features-) 🚀
  * [Contributing Code](#contributing-code-) 👾
  * [Reviewing Code](#reviewing-code-) 👀
  * [Documenting](#documenting-) 📜
* [Issue Report Guidelines](#issue-report-guidelines)
* [Contributor License Agreement](#contributor-license-agreement)

## Code of Conduct

This project adheres to the Adobe [Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code. Please report unacceptable behavior to the team.

## Ways of Contributing

There are many ways of contributing, from testing and reporting an issue to suggesting and coding full components or features. Below is a summary of some of the best ways to get involved. 

### Joining Feature Discussions 💭

You can add your voice to discussions around new and existing component features by commenting on an RTC. New components and features that openly invite public comment are marked by an [RTC](https://github.com/adobe/aem-core-wcm-components/labels/rtc) (Request to Comment) label. We strongly encourage users of the Core Components to bring their own project experience to these issues, as there may be alternative use-cases or requirements that haven't yet been considered.

### Joining Developer Discussions 💬

If you have thoughts on the code, the general architecture of the project, or would like some initial feedback on a feature you are thinking of coding, you can visit the [Core Components Developer Mailing List](https://groups.google.com/forum/#!forum/aem-core-components-dev) on Google Groups, or [subscribe via email](mailto:aem-core-components-dev+subscribe@googlegroups.com).

### Reporting Bugs 🐛

##### Before Reporting a Bug 

* Have a quick search through the currently open [bug reports](https://github.com/adobe/aem-core-wcm-components/labels/bug) to see if the issue has already been reported.
* Ensure that the issue is repeatable and that the actual behavior versus the expected results can be easily described.
* Check that the issue you are experiencing is related to the Core Components project. It may be that the problem derives from AEM itself, typically editor code, rather than the Core Components. If you're not sure, then feel free to report the issue anyway and the committers will clarify for you. Issues in the product can be reported via [Adobe Enterprise Support](https://helpx.adobe.com/contact/enterprise-support.ec.html).

##### Filing a Bug

1. Visit our [issue tracker on GitHub](https://github.com/adobe/aem-core-wcm-components/issues).
1. File a `New Issue` as a `Bug Report`.
1. Ensure your issue follows the [issue report guidelines](#issue-report-guidelines).
1. Thanks for the report! The committers will get back to you in a timely manner, typically within one week.

### Requesting Features 🚀

##### Before Requesting a Feature

* Have a quick search through the currently open [enhancement](https://github.com/adobe/aem-core-wcm-components/labels/enhancement) and [request to comment](https://github.com/adobe/aem-core-wcm-components/labels/rtc) issues to see if the idea has already been suggested. If it has, you may still have a slightly different requirement that isn't covered, in which case, feel free to comment on the open issue. 
* Take a look at the [Core Components Roadmap](https://github.com/adobe/aem-core-wcm-components/wiki#roadmap) to see if your feature is already on the radar. If it is and doesn't have a public issue yet, feel free to create one, listing your own requirements.
* Consider whether your requirement is generically useful rather than project-specific and would therefore benefit all users of the Core Components.

##### Requesting a Feature

1. Visit our [issue tracker on GitHub](https://github.com/adobe/aem-core-wcm-components/issues).
1. File a `New Issue` as a `Feature Request`.
1. Ensure your issue follows the [issue report guidelines](#issue-report-guidelines).
1. Thanks for the feature request! The committers will get back to you in a timely manner, typically within one week.

### Contributing Code 👾 

High quality code is important to the project, and to keep it that way, all code submissions are reviewed by committers before being accepted. Close adherence to the guidelines below can help speed up the review process and increase the likelihood of the submission being accepted.

##### Before Contributing

* Consider [joining developer discussions](#joining-developer-discussions-) to get feedback on what you are thinking of contributing. It's better to get this early feedback before going ahead and potentially having to rewrite everything later.
* Create a [bug report](#reporting-bugs-) or [feature request](#requesting-features-) issue summarizing the problem that you will be solving. This will again help with early feedback and tracking.
* Have a look at our [component checklist](Guidelines.md), for an idea of what certifies a production-ready component.
* Ensure you have [signed the Adobe Contributor License Agreement](http://opensource.adobe.com/cla.html). If you are an Adobe employee, you do not have to sign the CLA.

##### Contributing

The project accepts contributions primarily using GitHub pull requests. This process:
* Helps to maintain project quality
* Engages the community in working towards commonly accepted solutions with peer review
* Leads to a more meaningful and cleaner git history
* Ensures sustainable code management 

Creating a pull request involves creating a fork of the project in your personal space, adding your new code in a branch and triggering a pull request. Check the GitHub [Using Pull Requests](https://help.github.com/articles/using-pull-requests) article on how to perform pull requests.

Please base your pull request on the `development` branch and make sure to check you have incorporated or merged the latest changes! The maintainers will cherry-pick the change to `master` after it's successfully integrated and tested.

The title of the pull request typically matches that of the issue it fixes, see the [issue report guidelines](#issue-report-guidelines).
Have a look at our [pull request template](.github/pull_request_template.md) to see what is expected to be included in the pull request description. The same template is available when the pull request is triggered. 

##### Your first contribution
Would you like to contribute to the project but don't have an issue in mind? Or are you still fairly unfamiliar with the code? Then have a look at our [good first issues](https://github.com/adobe/aem-core-wcm-components/labels/good%20first%20issue), they are fairly simple starter issues that should only require a small amount of code and simple testing.

### Reviewing Code 👀

Reviewing others' code contributions is another great way to contribute - more eyes on the code help to improve its overall quality. To review a pull request, check the [open pull requests](https://github.com/adobe/aem-core-wcm-components/pulls) for anything you can comment on. 

### Documenting 📜

We very much welcome issue reports or pull requests that improve our documentation pages. While the best effort is made to keep them error free, useful and up-to-date there are always things that could be improved. The component documentation pages (for example the [Image Component Documentation](https://github.com/adobe/aem-core-wcm-components/blob/master/content/src/content/jcr_root/apps/core/wcm/components/image/v2/image/README.md)), this contributing guide or our [GitHub Wiki](https://github.com/adobe/aem-core-wcm-components/wiki) pages are good places to start.

## Issue Report Guidelines

A well defined issue report will help in quickly understanding and replicating the problem faced, or the feature requested. Below are some guidelines on what to include when reporting an issue. You can also see [this community reported issue](https://github.com/adobe/aem-core-wcm-components/issues/247) for an example of a well written issue report.

##### Title
* **Descriptive** - Should be specific, well described and readable at a glance.
* **Concise** - If the issue can't be easily described in a short title, then it is likely unfocused.
* **Keyword-rich** - Including keywords can help with quickly finding the issue in the backlog. Component related issues can be prefixed with a bracketed label with the component name, for example `[Image]` for the image component.

Bad title: `Search component has security problems`  
Good title: `[Search] Fulltext search of pages might lead to DDOS`

##### Description

See our [bug report template](.github/ISSUE_TEMPLATE/bug_report.md) or [feature request template](.github/ISSUE_TEMPLATE/feature_request.md) for details on what is expected to be described. The same information is available when creating a new issue on GitHub.

##### Labels

Once an issue is reported, the project committers will assign it a relevant label. You can see our [label list on GitHub](https://github.com/adobe/aem-core-wcm-components/labels) to better understand what each label means.

## Contributor License Agreement

By contributing your code to the Adobe Marketing Cloud Github Organisation you grant Adobe a non-exclusive, irrevocable, worldwide, royalty-free, sublicensable, transferable license under all of Your relevant intellectual property rights (including copyright, patent, and any other rights), to use, copy, prepare derivative works of, distribute and publicly perform and display the Contributions on any licensing terms, including without limitation: (a) open source licenses like the Apache License, Version 2.0; and (b) binary, proprietary, or commercial licenses. Except for the licenses granted herein, You reserve all right, title, and interest in and to the Contribution.

You confirm that you are able to grant us these rights. You represent that You are legally entitled to grant the above license. If Your employer has rights to intellectual property that You create, You represent that You have received permission to make the Contributions on behalf of that employer, or that Your employer has waived such rights for the Contributions.

You represent that the Contributions are Your original works of authorship, and to Your knowledge, no other person claims, or has the right to claim, any right in any invention or patent related to the Contributions. You also represent that You are not legally obligated, whether by entering into an agreement or otherwise, in any way that conflicts with the terms of this license.

YOU ARE NOT EXPECTED TO PROVIDE SUPPORT FOR YOUR SUBMISSION, UNLESS AND EXCEPT TO THE EXTENT YOU CHOOSE TO DO SO. UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN WRITING, THE SUBMISSION PROVIDED UNDER THIS AGREEMENT IS PROVIDED WITHOUT WARRANTY OF ANY KIND.
