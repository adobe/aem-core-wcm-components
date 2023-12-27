/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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
package com.day.cq.wcm.foundation.forms;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This is a stub of the FormsHelper provided by AEM.
 * The only implemented methods are {@link #getContentRequestParameterNames(SlingHttpServletRequest)} and {@link #getFormId(SlingHttpServletRequest)}.
 *
 * The value {@link #values} can be set and will be returned by {@link #getValues(SlingHttpServletRequest, Resource)}.
 */
public class FormsHelper {

    public static String[] values = null;

    private FormsHelper() {}

    @Deprecated
    public static void startForm(SlingHttpServletRequest request, SlingHttpServletResponse response, JspWriter out) throws IOException, ServletException {}

    public static void startForm(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException, ServletException {}

    private static void initialize(SlingHttpServletRequest request, Resource formResource, SlingHttpServletResponse response) throws IOException, ServletException {}

    public static void runAction(String actionType, String scriptName, Resource formResource, SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException, ServletException {}

    private static void checkInit(SlingHttpServletRequest request) {}

    private static boolean checkResourceType(Resource resource, String type) {
        return false;
    }

    public static void endForm(SlingHttpServletRequest req) {}

    public static void setForwardPath(SlingHttpServletRequest req, String path) {}

    public static void setForwardPath(SlingHttpServletRequest req, String path, boolean clearFormSelector) {}

    public static String getForwardPath(SlingHttpServletRequest req) {
        return null;
    }

    public static void setForwardOptions(ServletRequest req, RequestDispatcherOptions options) {}

    public static RequestDispatcherOptions getForwardOptions(ServletRequest req) {
        return null;
    }

    public static void setForwardRedirect(ServletRequest request, String redirect) {}

    public static String getForwardRedirect(ServletRequest request) {
        return null;
    }

    public static void setActionSuffix(SlingHttpServletRequest req, String suffix) {}

    public static String getActionSuffix(SlingHttpServletRequest req) {
        return null;
    }

    public static void setFormLoadResource(SlingHttpServletRequest req, Resource rsrc) {}

    public static Resource getFormLoadResource(SlingHttpServletRequest req) {
        return null;
    }

    public static ValueMap getGlobalFormValues(SlingHttpServletRequest req) {
        return null;
    }

    public static void setFormEditResources(SlingHttpServletRequest req, List<Resource> resources) {}

    public static List<Resource> getFormEditResources(SlingHttpServletRequest req) {
        return null;
    }

    public static String getFormsPreCheckMethodName(SlingHttpServletRequest req) {
        return null;
    }

    private static void writeJavaScript(SlingHttpServletRequest req, SlingHttpServletResponse response, final Resource formResource) throws IOException, ServletException {}

    public static void includeResource(SlingHttpServletRequest request, SlingHttpServletResponse response, Resource resource, String selectorString) throws IOException, ServletException {}

    public static boolean doClientValidation(SlingHttpServletRequest req) {
        return false;
    }

    public static String getFormId(SlingHttpServletRequest req) {
        checkInit(req);
        return (String) req.getAttribute("cq.form.id");
    }

    public static String getParameterName(Resource rsrc) {
        return null;
    }

    public static String getFieldId(SlingHttpServletRequest req, Resource rsrc) {
        return null;
    }

    @Deprecated
    public static Iterator<Resource> getFormElements(Resource formResource) {
        return null;
    }

    public static Iterator<String> getContentRequestParameterNames(SlingHttpServletRequest req) {
        List<String> names = new ArrayList<>();
        Enumeration<String> paramNames = req.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            if (!name.startsWith(":") && !name.equals("_charset_")) {
                names.add(name);
            }
        }
        return names.iterator();
    }

    public static Resource getResource(SlingHttpServletRequest request, Resource elementResource, String relPath) {
        return null;
    }

    public static String getValue(SlingHttpServletRequest request, Resource elementResource) {
        return null;
    }

    public static String getValue(SlingHttpServletRequest request, Resource elementResource, String nameParam) {
        return null;
    }

    public static String[] getValues(SlingHttpServletRequest request, Resource elementResource) {
        return getValues(request, elementResource, null);
    }

    public static String[] getValues(SlingHttpServletRequest request, Resource elementResource, String nameParam) {
        return values;
    }

    public static List<String> getValuesAsList(SlingHttpServletRequest request, Resource elementResource) {
        return null;
    }

    public static String getValue(SlingHttpServletRequest request, String name, String defaultValue) {
        return null;
    }

    public static String[] getValues(SlingHttpServletRequest request, String name, String[] defaultValues) {
        return null;
    }

    public static void inlineValuesAsJson(SlingHttpServletRequest request, Writer out, String path) throws IOException, RepositoryException {}

    public static void inlineValuesAsJson(SlingHttpServletRequest request, Writer out, String path, int nodeDepth) throws IOException, RepositoryException {}

    private static void accumulateShowHideExpressions(Node node, Map<String, String> map) throws RepositoryException {}

    public static Map<String, String> getShowHideExpressions(Resource resource) throws RepositoryException {
        return null;
    }

    public static Map<String, String> getOptions(SlingHttpServletRequest request, Resource elementResource) {
        return null;
    }

    public static boolean isRequired(Resource formElement) {
        return false;
    }

    public static void setFormReadOnly(SlingHttpServletRequest request) {}

    public static Object pushFormReadOnly(SlingHttpServletRequest request) {
        return null;
    }

    public static void popFormReadOnly(SlingHttpServletRequest request, Object previousState) {}

    public static boolean isReadOnly(SlingHttpServletRequest request, Resource formElement) {
        return false;
    }

    public static boolean isReadOnly(SlingHttpServletRequest request) {
        return false;
    }

    @Deprecated
    public static boolean isReadOnly(Resource formElement) {
        return false;
    }

    public static boolean checkRule(Resource resource, SlingHttpServletRequest req, PageContext pageContext, String propName) {
        return false;
    }

    public static String getTitle(Resource formElement, String defaultTitle) {
        return null;
    }

    public static String getDescription(Resource formElement, String defaultDescription) {
        return null;
    }

    public static boolean hasMultiSelection(Resource formElement) {
        return false;
    }

    public static void redirectToReferrer(SlingHttpServletRequest req, SlingHttpServletResponse res, Map<String, String[]> params) throws IOException {}

    public static void redirectToReferrer(SlingHttpServletRequest request, SlingHttpServletResponse res) throws IOException {}

    @Deprecated
    public static Resource checkFormStructure(Resource rsrc) {
        return null;
    }

    public static String encodeValue(String value) {
        return null;
    }

    public static String decodeValue(String value) {
        return null;
    }

    public static String getCss(ValueMap props, String defaultCss) {
        return null;
    }

    public static String getReferrer(HttpServletRequest request) {
        return null;
    }

    public static void setRedirectToReferrer(ServletRequest request, boolean redirectToReferrer) {}

    public static boolean isRedirectToReferrer(ServletRequest request) {
        return false;
    }

    public static Locale getLocale(SlingHttpServletRequest request) {
        return null;
    }

    public static String getLocalizedMessage(String msg, SlingHttpServletRequest request) {
        return null;
    }

    public static String[] getWhitelistPatterns(SlingHttpServletRequest req) {
        return null;
    }

    public static boolean allowExpressions(SlingHttpServletRequest req) {
        return false;
    }
}
