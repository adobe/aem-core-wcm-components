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
package com.adobe.cq.wcm.core.components.internal;

import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utilities for localization.
 */
public final class LocalizationUtils {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private LocalizationUtils() {
        // NOOP
    }

    /**
     * Same as {@link #getLocalPage(Page, Page, ResourceResolver, LanguageManager, LiveRelationshipManager)}, but will
     * also return empty if the referenced page path does not reference a page.
     *
     * @param referencePagePath The path of the referenced page.
     * @param currentPage The current page.
     * @param resourceResolver A resource resolver.
     * @param languageManager The language manager service.
     * @param relationshipManager The live relationship manager service.
     * @return A page, that belongs to the same language or live copy as the current page, and can be used as the local
     * alternative to the referenced page, or empty if no such page exist, or the referenced page path does not point to
     * an existing page.
     */
    public static Optional<Page> getLocalPage(@Nullable final String referencePagePath,
                                         @NotNull final Page currentPage,
                                         @NotNull final ResourceResolver resourceResolver,
                                         @NotNull final LanguageManager languageManager,
                                         @NotNull final LiveRelationshipManager relationshipManager) {
        return Optional.ofNullable(currentPage.getPageManager().getPage(referencePagePath))
            .flatMap(referencePage -> getLocalPage(referencePage, currentPage, resourceResolver, languageManager, relationshipManager));
    }

    /**
     * Given the current requested page and a reference page, this method will determine a page belonging to the
     * current site and locale that can be used instead of the reference site.
     *
     * Specifically, if the reference page and the current page are both found under a language root, and that language
     * root is not the same, then the returned page is the page located under the current page's language root at the
     * same relative path as the reference page is located under it's own language root; or empty if that page does not
     * exist.
     *
     * If either the reference page or the current page are not located under a language root, or if they share the
     * same language root, and if the reference page has a live relationship where the target is the current page or
     * an ancestor of the current page, then the target of that live relationship is returned; or empty if that page
     * does not exist.
     *
     * All other conditions return empty.
     *
     * @param referencePage The referenced page.
     * @param currentPage The current page.
     * @param resourceResolver A resource resolver.
     * @param languageManager The language manager service.
     * @param relationshipManager The live relationship manager service.
     * @return A page, that belongs to the same language or live copy as the current page, and can be used as the local
     * alternative to the referenced page, or empty if no such page exists.
     */
    public static Optional<Page> getLocalPage(@NotNull final Page referencePage,
                                              @NotNull final Page currentPage,
                                              @NotNull final ResourceResolver resourceResolver,
                                              @NotNull final LanguageManager languageManager,
                                              @NotNull final LiveRelationshipManager relationshipManager) {
        Page referencePageLanguageRoot = Optional.ofNullable(referencePage.getPath())
            .map(resourceResolver::getResource)
            .map(languageManager::getLanguageRoot)
            .orElse(null);

        Page currentPageLanguageRoot = languageManager.getLanguageRoot(currentPage.getContentResource());
        if (referencePageLanguageRoot != null && currentPageLanguageRoot != null && !referencePageLanguageRoot.equals
            (currentPageLanguageRoot)) {
            // check if there's a language copy of the navigation root
            return Optional.ofNullable(
                referencePage.getPageManager().getPage(
                    ResourceUtil.normalize(
                        String.join("/",
                            currentPageLanguageRoot.getPath(),
                            getRelativePath(referencePageLanguageRoot, referencePage)))));
        } else {
            try {
                String currentPagePath = currentPage.getPath() + "/";
                return Optional.of(
                    Optional.ofNullable((Iterator<LiveRelationship>) relationshipManager.getLiveRelationships(referencePage.adaptTo(Resource.class), null, null))
                    .map(liveRelationshipIterator -> StreamSupport.stream(((Iterable<LiveRelationship>) () -> liveRelationshipIterator).spliterator(), false))
                    .orElseGet(Stream::empty)
                    .map(LiveRelationship::getTargetPath)
                    .filter(target -> currentPagePath.startsWith(target + "/"))
                    .map(referencePage.getPageManager()::getPage)
                    .findFirst()
                    .orElse(referencePage));
            } catch (WCMException e) {
                // ignore it
            }
        }
        return Optional.empty();
    }

    /**
     * Get the relative path between the two pages.
     *
     * @param root The root page.
     * @param child The child page.
     * @return The relative path between root and child page, null if child is not a child of root.
     */
    @Nullable
    private static String getRelativePath(@NotNull final Page root, @NotNull final Page child) {
        if (child.equals(root)) {
            return ".";
        } else if ((child.getPath() + "/").startsWith(root.getPath())) {
            return child.getPath().substring(root.getPath().length() + 1);
        }
        return null;
    }

}
