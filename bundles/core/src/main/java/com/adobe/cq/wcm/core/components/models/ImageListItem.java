/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe Systems Incorporated
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

import java.util.Calendar;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * Interface for a generic list item, used by the {@link List} and {@link Search} models.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.2.0
 */
@Model(adaptables = Resource.class)
public class ImageListItem {

	@Inject
	private String fileReference;
	
	@Inject
	private String linkText;
	
	@Inject
	private String linkURL;
	
	private Resource imageResource;
	
    public Resource getImageResource() {
		return imageResource;
	}

	public void setImageResource(Resource imageResource) {
		this.imageResource = imageResource;
	}

	/**
     * Returns the ImagePath of this {@code ImageListItem}.
     *
     * @return the ImagePath of this image list item or {@code null}
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
	/*@Inject
    @Nullable
    default String getImagePath() {
        throw new UnsupportedOperationException();
    }*/
	public String getImagePath() {
		return fileReference;
	}

	public void setImagePath(String fileReference) {
		this.fileReference = fileReference;
	}

	public String getLinkText() {
		return linkText;
	}

	public void setLinkText(String linkText) {
		this.linkText = linkText;
	}

	public String getLinkURL() {
		return linkURL;
	}

	public void setLinkURL(String linkURL) {
		this.linkURL = linkURL;
	}

    /**
     * Returns the LinkText of this {@code ImageListItem}.
     *
     * @return the LinkText of this image list item or {@code null}
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
	/*@Inject
    @Nullable
    default String getLinkText() {
        throw new UnsupportedOperationException();
    }*/

    /**
     * Returns the LinkUrl of this {@code ImageListItem}.
     *
     * @return the image list item LinkUrl or {@code null}
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
	/*@Inject
    @Nullable
    default String getLinkUrl() {
        throw new UnsupportedOperationException();
    }*/
}
