/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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
package com.adobe.cq.wcm.core.components.internal.servlets;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.adobe.cq.wcm.core.components.internal.form.FormConstants;
import com.day.cq.wcm.foundation.forms.FormStructureHelperFactory;
import com.day.cq.wcm.foundation.forms.FormsHandlingServletHelper;
import com.day.cq.wcm.foundation.security.SaferSlingPostValidator;

/**
 * This form handling servlet accepts POSTs to a core form container
 * but only if the selector "form" and the extension "html" is used.
 */
@SuppressWarnings("serial")
@Component(
        service = {Servlet.class, Filter.class},
        configurationPid = "com.adobe.cq.wcm.core.components.commons.forms.impl.CoreFormsHandlingServlet",
        property = {
                "sling.servlet.resourceTypes=" + FormConstants.RT_CORE_FORM_CONTAINER_V1,
                "sling.servlet.resourceTypes=" + FormConstants.RT_CORE_FORM_CONTAINER_V2,
                "sling.servlet.methods=POST",
                "sling.servlet.selectors=" + CoreFormHandlingServlet.SELECTOR,
                "sling.servlet.extensions=" + CoreFormHandlingServlet.EXTENSION,
                "sling.filter.scope=request",
                "service.ranking:Integer=610",
        }
)
@Designate(
        ocd = CoreFormHandlingServlet.Configuration.class
)
public class CoreFormHandlingServlet
        extends SlingAllMethodsServlet
        implements Filter {

    @ObjectClassDefinition(
            name = "Core Form Handling Servlet",
            description = "Accepts posting to a core form container component and performs validations"
    )
    @interface Configuration {
        @AttributeDefinition(
                name = "Parameter Name Whitelist",
                description = "List of name expressions that will pass request validation. A validation error will occur " +
                        "if any posted parameters are not in the whitelist and not defined on the form."
        ) String[] name_whitelist() default {};

        @AttributeDefinition(
                name = "Allow Expressoins",
                description = "Evaluate expressions on form submissions."
        ) boolean allow_expressions() default true;
    }

    static final String SELECTOR = "form";
    static final String EXTENSION = "html";
    private static final Boolean PROP_ALLOW_EXPRESSION_DEFAULT = true;

    private transient FormsHandlingServletHelper formsHandlingServletHelper;

    @Reference
    private transient SaferSlingPostValidator validator;

    @Reference
    private transient FormStructureHelperFactory formStructureHelperFactory;

    @Activate
    protected void activate(Configuration configuration) {

        String[] dataNameWhitelist = PropertiesUtil.toStringArray(configuration.name_whitelist());
        boolean allowExpressions = PropertiesUtil.toBoolean(configuration.allow_expressions(), PROP_ALLOW_EXPRESSION_DEFAULT);
        formsHandlingServletHelper = new FormsHandlingServletHelper(dataNameWhitelist, validator, FormConstants.RT_ALL_CORE_FORM_CONTAINER,
            allowExpressions, formStructureHelperFactory);
    }

    /**
     * @see org.apache.sling.api.servlets.SlingAllMethodsServlet#doPost(org.apache.sling.api.SlingHttpServletRequest, org.apache.sling.api.SlingHttpServletResponse)
     */
    @Override
    protected void doPost(@NotNull SlingHttpServletRequest request, @NotNull final SlingHttpServletResponse response)
            throws ServletException, IOException {
        formsHandlingServletHelper.doPost(request, response);
    }

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(final ServletRequest request,
                         final ServletResponse response,
                         final FilterChain chain)
            throws IOException, ServletException {
        formsHandlingServletHelper.handleFilter(request, response, chain, EXTENSION, SELECTOR);
    }

    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(final FilterConfig config) {
        // nothing to do!
    }
}
