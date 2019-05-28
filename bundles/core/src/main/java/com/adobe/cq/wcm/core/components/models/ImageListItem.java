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

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * Interface for a generic image list item, used by the {@item ImageList} models.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.8.0
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
	
	/**
     * Returns the Image Resource of this {@code ImageListItem}.
     *
     * @return the Image Resource of this image list item or {@code null}
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    public Resource getImageResource() {
		return imageResource;
	}
    
    /**
     * Sets the Image Resource of this {@code ImageListItem}.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
	public void setImageResource(Resource imageResource) {
		this.imageResource = imageResource;
	}

	/**
     * Returns the ImagePath of this {@code ImageListItem}.
     *
     * @return the ImagePath of this image list item or {@code null}
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
	public String getImagePath() {
		return fileReference;
	}
	
	/**
     * Sets the ImagePath of this {@code ImageListItem}.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
	public void setImagePath(String fileReference) {
		this.fileReference = fileReference;
	}
	
	/**
	 * Returns the LinkText of this {@code ImageListItem}.
	 *
	 * @return the LinkText of this image list item or {@code null}
	 * @since com.adobe.cq.wcm.core.components.models 12.8.0
	 */
	public String getLinkText() {
		return linkText;
	}
	
	/**
	 * Sets the LinkText of this {@code ImageListItem}.
	 *
	 * @since com.adobe.cq.wcm.core.components.models 12.8.0
	 */
	public void setLinkText(String linkText) {
		this.linkText = linkText;
	}

    /**
     * Returns the LinkUrl of this {@code ImageListItem}.
     *
     * @return the image list item LinkUrl or {@code null}
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
	public String getLinkURL() {
		return linkURL;
	}

	/**
     * Sets the LinkUrl of this {@code ImageListItem}.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
	public void setLinkURL(String linkURL) {
		this.linkURL = linkURL;
	}

}
