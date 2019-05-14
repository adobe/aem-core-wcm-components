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
package com.adobe.cq.wcm.core.components.internal.embed;

import org.apache.sling.api.resource.ValueMap;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.foundation.forms.FormsConstants;
import com.day.cq.wcm.foundation.forms.FormsManager.ComponentDescription;

public final class ComponentDescriptionImpl implements ComponentDescription {

    private final String resourceType;
    private final String title;
    private final String hint;
    private final int    order;

    public ComponentDescriptionImpl(final String rt, final String defaultName, final ValueMap props) {
        this.resourceType = rt;
        this.title = props.get(JcrConstants.JCR_TITLE, defaultName);
        this.order = props.get(FormsConstants.COMPONENT_PROPERTY_ORDER, 0);
        this.hint = props.get(FormsConstants.COMPONENT_PROPERTY_HINT, String.class);
    }

    /**
     * @see com.day.cq.wcm.foundation.forms.FormsManager.ComponentDescription#getResourceType()
     */
    public String getResourceType() {
        return this.resourceType;
    }

    /**
     * @see com.day.cq.wcm.foundation.forms.FormsManager.ComponentDescription#getTitle()
     */
    public String getTitle() {
        return this.title;
    }

    public int getOrder() {
        return this.order;
    }

    /**
     * @see com.day.cq.wcm.foundation.forms.FormsManager.ComponentDescription#getHint()
     */
    public String getHint() {
        return this.hint;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(ComponentDescription o) {
    	if (o == null)
    	    return 0;

    	  if (this.getClass() != o.getClass())
    	    return 0;
        final ComponentDescriptionImpl obj = (ComponentDescriptionImpl)o;
        if ( this.order < obj.order ) {
            return -1;
        } else if ( this.order == obj.order ) {
            return this.title.compareTo(obj.title);
        }
        return 1;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (obj == null)
    	    return false;

    	  if (this.getClass() != obj.getClass())
    	    return false;

		return compareTo((ComponentDescriptionImpl)obj) == 0; 
    }
    
    @Override
    public int hashCode() {
      return this.title.hashCode()+this.title.hashCode();
    }
    
}
