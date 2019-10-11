/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
package com.adobe.cq.wcm.core.components.internal.services.amp;

import static com.adobe.cq.wcm.core.components.internal.services.amp.AmpHelperUtil.AMP_ONLY;
import static com.adobe.cq.wcm.core.components.internal.services.amp.AmpHelperUtil.AMP_SELECTOR;
import static com.adobe.cq.wcm.core.components.internal.services.amp.AmpHelperUtil.DOT;
import static com.adobe.cq.wcm.core.components.internal.services.amp.AmpHelperUtil.NO_AMP;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;

/**
 * Forwards page requests based on the request's 'amp' selector and the page's AMP mode value.
 */
@Component(
        service = {Filter.class},
        configurationPid = "com.adobe.cq.wcm.core.components.internal.services.amp.AmpModeForwardFilter",
        property = {
                "sling.servlet.methods=GET",
                "sling.servlet.extensions=amp.html",
                "sling.filter.scope=request",
                "sling.filter.pattern=/content/.*",
                "service.ranking:Integer=1000",
        }
)
public class AmpModeForwardFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(AmpModeForwardFilter.class);

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {

        SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;

        // Read the full selector string from the request.
        String selectors = slingRequest.getRequestPathInfo().getSelectorString();
        if (selectors == null) {
            selectors = "";
        }

        boolean hasAmpSelector = selectors.contains(AMP_SELECTOR);

        String ampMode = AmpHelperUtil.getAmpMode(slingRequest);

        if (hasAmpSelector && (ampMode.equals(NO_AMP) || ampMode.isEmpty())) {

            // Remove the amp selector.
            String newSelectors;
            if (selectors.contains(DOT + AMP_SELECTOR)) {
                newSelectors = selectors.replace(DOT + AMP_SELECTOR, "");
            } else if (selectors.contains(AMP_SELECTOR + DOT)) {
                newSelectors = selectors.replace(AMP_SELECTOR + DOT, "");
            } else {
                newSelectors = selectors.replace(AMP_SELECTOR, "");
            }

            // Apply the updated selectors.
            RequestDispatcherOptions options = new RequestDispatcherOptions();
            options.setReplaceSelectors(newSelectors);

            if (forward(slingRequest, response, options)) {
                return;
            }
        } else if (!hasAmpSelector && ampMode.equals(AMP_ONLY)) {

            // Add the 'amp' selector to the dispatcher. Making sure to put it at the beginning of the selector list.
            RequestDispatcherOptions options = new RequestDispatcherOptions();
            if (!selectors.isEmpty()) {
                options.setReplaceSelectors(AMP_SELECTOR + DOT + selectors);
            } else {
                options.setAddSelectors(AMP_SELECTOR);
            }

            if (forward(slingRequest, response, options)) {
                return;
            }
        } else if (selectors.contains(DOT + AMP_SELECTOR)) {

            // Move the amp selector to the front of the list of selectors.
            RequestDispatcherOptions options = new RequestDispatcherOptions();
            String newSelectors = selectors.replace(DOT + AMP_SELECTOR, "");
            options.setReplaceSelectors(AMP_SELECTOR + DOT + newSelectors);

            if (forward(slingRequest, response, options)) {
                return;
            }
        }

        chain.doFilter(request, response);
    }

    /**
     * Forwards the request using the provided dispatcher options.
     * @param slingRequest The request to forward.
     * @param response The response to forward.
     * @param options The options to apply to the forward.
     * @return If request forwarded successfully.
     */
    private boolean forward(SlingHttpServletRequest slingRequest, ServletResponse response,
                            RequestDispatcherOptions options) throws ServletException, IOException {

        RequestDispatcher dispatcher = slingRequest.getRequestDispatcher(slingRequest.getResource(), options);
        if (dispatcher != null) {
            dispatcher.forward(slingRequest, response);
            return true;
        }

        LOG.debug("Request dispatcher is null. AMP mode forwarding aborted.");

        return false;
    }

    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(final FilterConfig config) throws ServletException {}

    /**
     * @see Filter#destroy()
     */
    public void destroy() {}
}
