/*
 * Copyright 2021 Adobe Systems Incorporated
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.adobe.cq.wcm.core.components.it.util;

import java.time.Duration;

/**
 * Constants shared by the test content helpers.
 */
public final class ItConstants {

    private ItConstants() {
    }

    public static final String RUNMODE_AUTHOR = "author";
    public static final String RUNMODE_PUBLISH = "publish";
    public static final String DEFAULT_USER = "admin";
    public static final String DEFAULT_PASSWORD = "admin";

    public static final long DEFAULT_RETRY_DELAY = Duration.ofSeconds(1).toMillis();
    public static final long DEFAULT_TIMEOUT = Duration.ofSeconds(30).toMillis();

    public static final int DEFAULT_SMALL_SIZE = 8;

    public static final String GROUPID_CONTENT_AUTHORS = "content-authors";

    public static final String HOME_GROUPS = "/home/groups/";
    public static final String HOME_USERS = "/home/users/";
    public static final String CONTENT_ROOT = "/content";
    public static final String CONTENT_DAM = "/content/dam";
    public static final String PROP_CQ_CONF = "cq:conf";
    public static final String PROP_CQ_ALLOWED_TEMPLATES = "cq:allowedTemplates";
}
