/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2016 Adobe Systems Incorporated
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
package apps.core.wcm.components.page.v1.page;

import java.io.StringWriter;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.scripting.SlingScriptHelper;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.cq.wcm.core.components.commons.AuthoringUtils;
import com.day.cq.wcm.api.components.EditContext;
import com.day.cq.wcm.undo.UndoConfigService;

public class Author extends WCMUsePojo {

    private boolean isTouchAuthoring;
    private String undoConfig;
    private String dialogPath;

    @Override
    public void activate() throws Exception {
        SlingHttpServletRequest request = getRequest();
        isTouchAuthoring = AuthoringUtils.isTouch(request);
        SlingScriptHelper sling = getSlingScriptHelper();
        UndoConfigService undoConfigService = sling.getService(UndoConfigService.class);
        if (undoConfigService != null) {
            StringWriter stringWriter = new StringWriter();
            undoConfigService.writeClientConfig(stringWriter);
            undoConfig = stringWriter.toString();
        }
        EditContext editContext = get(WCMBindings.EDIT_CONTEXT, EditContext.class);
        if (editContext != null) {
            dialogPath = editContext.getComponent().getDialogPath();
        }
    }

    /**
     * Checks if the current authoring mode is using TOUCH.
     *
     * @return {@code true} if the current authoring mode is TOUCH, {@code false} otherwise
     */
    public boolean isTouchAuthoring() {
        return isTouchAuthoring;
    }

    /**
     * Returns the configuration of the {@link UndoConfigService}.
     *
     * @return the configuration of the {@link UndoConfigService} as a {@link String} if one is found, {@code null} otherwise
     */
    public String getUndoConfig() {
        return undoConfig;
    }

    /**
     * Returns the page's dialog path.
     *
     * @return the page's dialog path, if one can be found, otherwise {@code null}
     */
    public String getDialogPath() {
        return dialogPath;
    }
}
