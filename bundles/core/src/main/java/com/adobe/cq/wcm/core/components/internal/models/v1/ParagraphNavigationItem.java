/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2019 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/
package com.adobe.cq.wcm.core.components.internal.models.v1;

import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.wcm.core.components.models.ListItem;

/**
 * Defines the {@code Navigation} Sling Model used for the {@code /apps/core/wcm/components/pagenavigation} component.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.2.0
 */
@ConsumerType
public interface ParagraphNavigationItem extends ListItem {

    /**
     * Returns the depth level of this {@code NavigationItem}.
     *
     * @return the depth level
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    default int getLevel() {
        throw new UnsupportedOperationException();
    }

}
