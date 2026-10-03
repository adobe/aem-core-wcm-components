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
package com.adobe.cq.wcm.core.components.it.pw.download;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_DOWNLOAD_V2;

@Tag("playwright-group2")
public class DownloadV2PwIT extends DownloadV1PwIT {

    @Override
    protected String resourceType() {
        return RT_DOWNLOAD_V2;
    }

    @Test
    public void downloadWordFile() throws Exception {
        assertDownloadHeaders("core-comp-test-word.doc",
            "/content/dam/core-components/core-comp-test-word.doc");
    }

    @Test
    public void downloadXlsxFile() throws Exception {
        assertDownloadHeaders("core-comp-test-xlsx.xlsx",
            "/content/dam/core-components/core-comp-test-xlsx.xlsx");
    }

    @Test
    public void downloadPptxFile() throws Exception {
        assertDownloadHeaders("core-comp-test-pptx.pptx",
            "/content/dam/core-components/core-comp-test-pptx.pptx");
    }
}
