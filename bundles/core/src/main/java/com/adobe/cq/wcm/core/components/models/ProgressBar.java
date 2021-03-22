/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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
package com.adobe.cq.wcm.core.components.models;

import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
public interface ProgressBar extends Component {

    /**
     * Name of property that defines the completion percentage.
     * The property should provide a numeric value between 0 and 100.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.12.0
     */
    String PN_COMPLETED = "completed";

    /**
     * Get the completed percentage
     *
     * @return Completed percentage as a number between 0 and 100
     * @since com.adobe.cq.wcm.core.components.models 12.12.0
     */
    default float getCompleted() {
        return 0;
    }

    /**
     * Get the remaining percentage
     *
     * @return Remaining percentage as a number between 0 and 100
     * @since com.adobe.cq.wcm.core.components.models 12.12.0
     */
    default float getRemaining() {
        return 100;
    }

}
