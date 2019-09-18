package com.adobe.cq.wcm.core.components.internal;

import org.apache.http.conn.util.InetAddressUtils;

public final class PageUtils {

    /* Hide the constructor of PageUtils class */
    private PageUtils() {
    }

    /**
     * Checks if serverName is a valid IPv4 address
     *
     * @param serverName from the current request
     * @return true if serverName is an valid IPv4 addres
     */
    public static boolean isServerNameAnIpAddress(String serverName) {
        return InetAddressUtils.isIPv4Address(serverName);
    }

    /**
     * Checks if serverName is localhost
     *
     * @param serverName from the current request
     * @return true if serverName is equal to localhost
     */
    public static boolean isServerNameLocalhost(String serverName) {
        return "localhost".equals(serverName);
    }

    /**
     * Removes selectors and suffixes from the URL
     *
     * @param url from the current request
     * @param selectors of the url from the current request
     * @param suffixes of the url from the current request
     * @return cleanerUrl without selectors and suffixes as the canonical url
     */
    public static String removeSelectorsAndSuffixesFromURL(String url, String selectors, String suffixes) {
        String cleanedUrl = url;

        if (selectors != null) {
            cleanedUrl = cleanedUrl.replace("." + selectors, "");
        }

        if (suffixes != null) {
            cleanedUrl = cleanedUrl.replace(suffixes, "");
        }
        return cleanedUrl;
    }

}
