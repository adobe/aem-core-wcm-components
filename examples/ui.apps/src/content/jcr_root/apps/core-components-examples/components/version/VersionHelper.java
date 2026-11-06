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

import javax.jcr.Session;

import org.apache.jackrabbit.vault.packaging.JcrPackage;
import org.apache.jackrabbit.vault.packaging.JcrPackageManager;
import org.apache.jackrabbit.vault.packaging.PackageId;
import org.apache.jackrabbit.vault.packaging.Packaging;
import org.osgi.framework.FrameworkUtil;

import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.cq.wcm.core.components.models.Page;

/**
 * Helper Use-Object for displaying the Core Components bundle version and the
 * Core Components Examples content package version.
 *
 * Usage:
 *    <sly data-sly-use.bundle="CoreComponentsBundle">${bundle.version} - ${bundle.examplesVersion}</sly>
 */
public class VersionHelper extends WCMUsePojo {

    /**
     * Name (artifactId) of the Core Components Examples content package.
     */
    private static final String EXAMPLES_PACKAGE_NAME = "core.wcm.components.examples.ui.apps";

    private String examplesVersion;

    @Override
    public void activate() throws Exception {
        examplesVersion = resolveExamplesVersion();
    }

    /**
     * @return the version of the {@code core.wcm.components.core} OSGi bundle.
     */
    public String getBundleVersion() {
        return FrameworkUtil.getBundle(Page.class).getVersion().toString();
    }

    /**
     * @return the version of the installed Core Components Examples content package,
     *         or {@code null} if it cannot be determined.
     */
    public String getExamplesVersion() {
        return examplesVersion;
    }

    private String resolveExamplesVersion() throws Exception {
        Packaging packaging = getSlingScriptHelper().getService(Packaging.class);
        Session session = getResourceResolver().adaptTo(Session.class);
        if (packaging == null || session == null) {
            return null;
        }
        JcrPackageManager packageManager = packaging.getPackageManager(session);
        for (JcrPackage jcrPackage : packageManager.listPackages()) {
            try {
                PackageId id = jcrPackage.getDefinition().getId();
                if (EXAMPLES_PACKAGE_NAME.equals(id.getName())) {
                    return id.getVersionString();
                }
            } finally {
                jcrPackage.close();
            }
        }
        return null;
    }

}
