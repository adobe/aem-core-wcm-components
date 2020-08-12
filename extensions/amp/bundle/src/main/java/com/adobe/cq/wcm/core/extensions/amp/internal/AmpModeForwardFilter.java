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
package com.adobe.cq.wcm.core.extensions.amp.internal;

import java.io.IOException;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.engine.EngineConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.adobe.cq.wcm.core.extensions.amp.internal.AmpUtil.*;

/**
 * Forwards page requests based on the request's 'amp' selector and the page's AMP mode value.
 */
@Component(
        service = {Filter.class},
        configurationPid = "com.adobe.cq.wcm.core.components.internal.services.amp.AmpModeForwardFilter",
        property = {
                "sling.filter.methods=" + HttpConstants.METHOD_GET,
                EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
                EngineConstants.SLING_FILTER_PATTERN + "=/content/.*",
                "sling.filter.extensions=html",
                Constants.SERVICE_RANKING + "Integer=1000"
        }
)
public class AmpModeForwardFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(AmpModeForwardFilter.class);

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {

        SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
        AMP_MODE ampMode = getAmpMode(slingRequest);

        Supplier<Stream<String>> selectors = () -> Stream.of(slingRequest.getRequestPathInfo().getSelectors());

        if (selectors.get().anyMatch(a -> a.equals(AMP_SELECTOR)) || ampMode == AMP_MODE.AMP_ONLY) {
            RequestDispatcherOptions options = new RequestDispatcherOptions();
            Stream<String> newSelectors = selectors.get().filter(e -> !e.equals(AMP_SELECTOR));

            if (ampMode != AMP_MODE.NO_AMP) {
                newSelectors = Stream.concat(Stream.of(AMP_SELECTOR), newSelectors);
            }

            options.setReplaceSelectors(newSelectors.collect(Collectors.joining(DOT)));
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
    @Override
    public void init(final FilterConfig config) {}

    /**
     * @see Filter#destroy()
     */
    @Override
    public void destroy() {}
}
