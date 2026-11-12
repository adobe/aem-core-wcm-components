/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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
package com.adobe.cq.wcm.core.components.it.support;

import java.io.IOException;

import org.apache.sling.rewriter.DefaultTransformer;
import org.apache.sling.rewriter.ProcessingComponentConfiguration;
import org.apache.sling.rewriter.ProcessingContext;
import org.apache.sling.rewriter.Transformer;
import org.apache.sling.rewriter.TransformerFactory;
import org.osgi.service.component.annotations.Component;

/**
 * This {@link TransformerFactory} creates a {@link Transformer} that adds a {@code "X-CoreComponents-TestTransformer=true"} header to the
 * response. The response header can be checked in a http IT.
 */
@Component(
    service = TransformerFactory.class,
    property = {
        "pipeline.type=core-components-test-transformer"
    }
)
public class TestTransformerFactory implements TransformerFactory {

    @Override
    public Transformer createTransformer() {
        return new TestTransformer();
    }

    static class TestTransformer extends DefaultTransformer {

        @Override
        public void init(ProcessingContext context, ProcessingComponentConfiguration config) throws IOException {
            // called when the rewriter pipeline is initialised, so first time the HttpServletResponse#getWriter() is called
            // save to set a response header
            context.getResponse().addHeader("X-CoreComponents-TestTransformer", "true");
        }
    }
}
