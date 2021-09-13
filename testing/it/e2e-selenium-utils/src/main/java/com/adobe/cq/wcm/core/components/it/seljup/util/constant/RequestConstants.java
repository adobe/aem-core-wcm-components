/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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
package com.adobe.cq.wcm.core.components.it.seljup.util.constant;

/**
 * This includes the constants used over entire module.
 */
public class RequestConstants {
    /**
     * Retry interval time for some path to exist
     */
    public static final int RETRY_TIME_INTERVAL = 500;

    /**
     * Timeout for path to exist or for uploading an asset in milliseconds
     */
    public static final int TIMEOUT_TIME_SEC = 20;

    /**
     * Timeout for path to exist or for uploading an asset in milliseconds
     */
    public static final int TIMEOUT_TIME_MS = 20000;

    /**
     * Web driver wait to reflect UI changes
     */
    public static final int WEBDRIVER_WAIT_TIME_MS = 1000;
}
