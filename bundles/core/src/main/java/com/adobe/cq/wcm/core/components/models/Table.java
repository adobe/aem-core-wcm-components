/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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

import java.io.IOException;
import java.util.List;

public interface Table extends Component {

    /**
     * Returns formatted property names by removing jcr: from the property name.
     * @return
     */
    default List<String> getFormattedHeaderNames() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the table items(Rows) associated to the selected source path.
     *
      * @return
     */
    default List<List<String>> getItems() throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns Table Description
     *
     * @return
     */
    default String getDescription() {
        throw new UnsupportedOperationException();
    }
}
