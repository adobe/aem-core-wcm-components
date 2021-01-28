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
package com.adobe.cq.wcm.core.components.util;

import java.util.Optional;

import javax.jcr.RangeIterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveCopy;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;

public class LocalizationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalizationUtils.class);

    /**
     * Returns the localization root of the given resource.
     * <pre>
     * Use case                                  | Resource Path                        | Root
     * ------------------------------------------|--------------------------------------|------------------
     * 1. No localization                        | /content/mysite/mypage               | null
     * 2. Language localization                  | /content/mysite/en/mypage            | /content/mysite/en
     * 3. Country-language localization          | /content/mysite/us/en/mypage         | /content/mysite/us/en
     * 4. Country-language localization (variant)| /content/us/mysite/en/mypage         | /content/us/mysite/en
     * 5. Blueprint                              | /content/mysite/blueprint/mypage     | /content/mysite/blueprint
     * 6. Live Copy                              | /content/mysite/livecopy/mypage      | /content/mysite/livecopy
     * </pre>
     *
     * @param resource the resource for which we want to find the localization root
     * @param resolver the resource resolver
     * @param languageManager the language manager service
     * @param relationshipManager the live relationship manager service
     * @return the localization root of the resource at the given path if it exists, {@code null} otherwise
     */
    @Nullable
    public static String getLocalizationRoot(@NotNull Resource resource, @NotNull ResourceResolver resolver,
        @NotNull LanguageManager languageManager, @NotNull LiveRelationshipManager relationshipManager) {
        String root = getLanguageRoot(resource, languageManager);
        if (StringUtils.isEmpty(root)) {
            root = getBlueprintPath(resource, relationshipManager);
        }
        if (StringUtils.isEmpty(root)) {
            root = getLiveCopyPath(resource, relationshipManager);
        }
        return root;
    }

    /**
     * Returns the language root of the resource.
     *
     * @param resource the resource
     * @param languageManager the language manager service
     * @return the language root of the resource if it exists, {@code null} otherwise
     */
    @Nullable
    public static String getLanguageRoot(@NotNull Resource resource, @NotNull LanguageManager languageManager) {
        return Optional.ofNullable(languageManager.getLanguageRoot(resource))
            .map(Page::getPath)
            .orElse(null);
    }

    /**
     * Returns the path of the blueprint of the resource.
     *
     * @param resource the resource
     * @param relationshipManager the live relationship manager service
     * @return the path of the blueprint of the resource if it exists, {@code null} otherwise
     */
    @Nullable
    public static String getBlueprintPath(@NotNull Resource resource, @NotNull LiveRelationshipManager relationshipManager) {
        try {
            if (relationshipManager.isSource(resource)) {
                // the resource is a blueprint
                RangeIterator liveCopiesIterator = relationshipManager.getLiveRelationships(resource, null, null);
                if (liveCopiesIterator != null) {
                    LiveRelationship relationship = (LiveRelationship) liveCopiesIterator.next();
                    LiveCopy liveCopy = relationship.getLiveCopy();
                    if (liveCopy != null) {
                        return liveCopy.getBlueprintPath();
                    }
                }
            }
        } catch (WCMException e) {
            LOGGER.error("Unable to get the blueprint: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Returns the path of the live copy of the resource.
     *
     * @param resource the resource
     * @param relationshipManager the live relationship manager service
     * @return the path of the live copy of the resource if it exists, {@code null} otherwise
     */
    @Nullable
    public static String getLiveCopyPath(@NotNull Resource resource, @NotNull LiveRelationshipManager relationshipManager) {
        try {
            if (relationshipManager.hasLiveRelationship(resource)) {
                // the resource is a live copy
                LiveRelationship liveRelationship = relationshipManager.getLiveRelationship(resource, false);
                if (liveRelationship != null) {
                    LiveCopy liveCopy = liveRelationship.getLiveCopy();
                    if (liveCopy != null) {
                        return liveCopy.getPath();
                    }
                }
            }
        } catch (WCMException e) {
            LOGGER.error("Unable to get the live copy: {}", e.getMessage());
        }
        return null;
    }
}
