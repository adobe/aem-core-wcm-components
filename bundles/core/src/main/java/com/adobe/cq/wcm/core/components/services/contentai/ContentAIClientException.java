/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
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
package com.adobe.cq.wcm.core.components.services.contentai;

/**
 * Thrown when a call to the Content AI APIs fails, either due to a transport-level
 * error or a non-200 response.
 *
 * @since com.adobe.cq.wcm.core.components.services.contentai 1.0.0
 */
public class ContentAIClientException extends Exception {

    private static final long serialVersionUID = 1L;

    private final int statusCode;

    public ContentAIClientException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
    }

    public ContentAIClientException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * @return the HTTP status code returned by Content AI, or {@code 0} if the failure was a transport-level error
     *         (timeout, connection refused, etc.) rather than an HTTP response.
     */
    public int getStatusCode() {
        return statusCode;
    }
}
