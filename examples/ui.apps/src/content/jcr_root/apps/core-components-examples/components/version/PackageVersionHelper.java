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

import java.util.List;

import javax.jcr.Session;

import org.apache.jackrabbit.vault.packaging.JcrPackage;
import org.apache.jackrabbit.vault.packaging.JcrPackageManager;
import org.apache.jackrabbit.vault.packaging.PackageProperties;
import org.apache.jackrabbit.vault.packaging.Packaging;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.Version;

/**
 * Helper Use-Object for displaying the most recent version of a given package name that is installed on the AEM instance
 *
 * Usage:
 *    <sly data-sly-use.version="${'PackageVersionHelper' @ packageName='my.package.name'}">${version.packageVersion}</sly>
 */
public class PackageVersionHelper extends WCMUsePojo {

    public static final String ATTR_PACKAGE_NAME = "packageName";

    private Version packageVersion = null;

    @Override
    public void activate() throws Exception {
        String packageName = get(ATTR_PACKAGE_NAME, String.class);
        if (packageName != null) {
            Session session = getResourceResolver().adaptTo(Session.class);
            Packaging packaging = getSlingScriptHelper().getService(Packaging.class);
            if (session != null && packaging != null) {
                JcrPackageManager jcrPackageManager = packaging.getPackageManager(session);
                if (jcrPackageManager != null) {
                    List<JcrPackage> packages = jcrPackageManager.listPackages();
                    for (JcrPackage jcrPackage : packages) {
                        if (packageName.equals(jcrPackage.getPackage().getProperty(PackageProperties.NAME_NAME))) {
                            Version currentVersion = Version.create(jcrPackage.getPackage().getProperty(PackageProperties.NAME_VERSION));
                            if (packageVersion == null || packageVersion.compareTo(currentVersion) < 0) {
                                packageVersion = currentVersion;
                            }
                        }
                    }
                }
            }
        }
    }

    public String getPackageVersion() {
        if (packageVersion == null) {
            return null;
        }
        return packageVersion.toString();
    }
}