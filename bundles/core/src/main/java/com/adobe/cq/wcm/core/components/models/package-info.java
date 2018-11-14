/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
/**
 * <p>
 *      This package defines the Sling Models exposed by the Adobe Experience Manager Core WCM Components Bundle.
 * </p>
 * <p>
 *      Implementors of this API can choose which API level they support, by implementing all the methods up to a specified version of the
 *      API. All the provided interfaces document in which API version they were added. Furthermore, all methods also specify the API
 *      version in which they were introduced and deprecated.
 * </p>
 * <p>
 *      All the interfaces from this package provide {@code default} methods that throw {@link java.lang.UnsupportedOperationException}s.
 *      The reasoning behind this pattern is that implementors can upgrade the bundle without being forced to implement all the
 *      new methods provided by a newer API version, since the interfaces from this package are
 *      {@link org.osgi.annotation.versioning.ConsumerType}s. An {@link java.lang.UnsupportedOperationException} could be thrown when a
 *      component script would start using the newer API, without the actual implementation to support it. This can happen when an
 *      implementor migrates a
 *      <a href="https://helpx.adobe.com/experience-manager/core-components-v1/using/guidelines.html#ProxyComponentPattern">proxy component
 *      </a> to a newer version of the core component it proxies and a custom Sling Model implementation, supporting an older API
 *      version, is bound to this proxy component resource type.
 * </p>
 */
@Version("13.0.0")
package com.adobe.cq.wcm.core.components.models;

import org.osgi.annotation.versioning.Version;
