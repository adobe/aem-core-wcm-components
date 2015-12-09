/*******************************************************************************
 * Copyright 2015 Adobe Systems Incorporated
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.adobe.cq.wcm.core.components.text;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Via;

import com.adobe.cq.wcm.core.components.commons.AuthoringUtils;
import com.adobe.cq.sightly.SightlyWCMMode;
import com.day.cq.i18n.I18n;

/**
 * The {@code Text} model represents the Sightly Use-API object that will be used by the {@code /apps/core/wcm/components/text} component
 * for its rendering.
 */
@Model(adaptables = SlingHttpServletRequest.class)
public final class Text {

    public static final String PROP_TEXT = "text";
    public static final String PROP_RICH_FORMAT = "textIsRich";
    public static final String CONTEXT_TEXT = "text";
    public static final String CONTEXT_HTML = "html";
    public static final String CSS_CLASS_TOUCH = "cq-placeholder";
    public static final String CSS_CLASS_CLASSIC = "cq-text-placeholder-ipe";

    @Inject
    @Via("resource")
    @Named(PROP_TEXT)
    @Optional
    private String text;

    @Inject
    @Via("resource")
    @Named(PROP_RICH_FORMAT)
    @Default(booleanValues = false)
    @Optional
    private Boolean textIsRich;

    @Inject
    @Named("wcmmode")
    private SightlyWCMMode wcmMode;

    private String cssClass;
    private SlingHttpServletRequest request;
    private String xssContext;
    private boolean hasContent;

    public Text(SlingHttpServletRequest request) {
        this.request = request;
    }

    /**
     * Retrieves the value of the {@link #PROP_TEXT} resource property.
     *
     * @return the value of the {@link #PROP_TEXT} property, or {@code null} if this doesn't exist
     */
    public String getText() {
        return text;
    }

    /**
     * Retrieves the value of the {@link #PROP_RICH_FORMAT} resource property.
     *
     * @return the value of the {@link #PROP_RICH_FORMAT} property, or {@code null} if this doesn't exist
     */
    public Boolean getTextIsRich() {
        return textIsRich;
    }

    /**
     * Returns the CSS class that will be used for this component in edit mode, when the {@link #PROP_TEXT} property is empty or missing.
     *
     * @return the CSS class that will be used for this component in edit mode, when the {@link #PROP_TEXT} property is empty or missing
     */
    public String getCssClass() {
        return cssClass;
    }

    /**
     * Returns the Sightly XSS context that will be applied, depending on the value of the {@link #PROP_RICH_FORMAT} property: <ul>
     * <li>{@code true}: {@link #CONTEXT_HTML}</li> <li>{@code false}: {@link #CONTEXT_TEXT}</li> </ul>
     *
     * @return {@link #CONTEXT_HTML} if the value of {@link #PROP_RICH_FORMAT} is {@code true}, {@link #CONTEXT_TEXT} otherwise
     */
    public String getXssContext() {
        return xssContext;
    }

    /**
     * Checks if the current {@code Text} component has a non-empty {@link #PROP_TEXT} property or not.
     *
     * @return {@code true} if the {@link #PROP_TEXT} is not empty, {@code false} otherwise
     */
    public boolean hasContent() {
        return hasContent;
    }

    @PostConstruct
    protected void process() {

        xssContext = textIsRich ? CONTEXT_HTML : CONTEXT_TEXT;
        hasContent = true;
        if (StringUtils.isEmpty(text)) {
            hasContent = false;
            boolean isTouch = AuthoringUtils.isTouch(request);
            cssClass = isTouch ? CSS_CLASS_TOUCH : CSS_CLASS_CLASSIC;
            xssContext = CONTEXT_TEXT;
            if (wcmMode != null && wcmMode.isEdit()) {
                text = isTouch ? "" : I18n.get(request, "Edit text");
            } else {
                text = "";
            }
        }
    }


}
