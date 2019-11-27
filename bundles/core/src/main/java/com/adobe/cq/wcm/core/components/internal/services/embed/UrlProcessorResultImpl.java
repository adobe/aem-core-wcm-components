/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
package com.adobe.cq.wcm.core.components.internal.services.embed;

import java.util.Map;

import com.adobe.cq.wcm.core.components.services.embed.UrlProcessor;

public class UrlProcessorResultImpl implements UrlProcessor.Result {

    String processor;
    Map<String, Object> options;

    public UrlProcessorResultImpl(String processor, Map<String, Object> options) {
        this.processor = processor;
        this.options = options;
    }

    @Override
    public String getProcessor() {
        return processor;
    }

    @Override
    public Map<String, Object> getOptions() {
        return options;
    }
}
