/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2023 Adobe
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
package com.adobe.cq.wcm.core.components.internal.link;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.stream.Collectors;

import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for handling links
 */
public class LinkUtil {

    private final static Logger LOG = LoggerFactory.getLogger(LinkUtil.class);

    //RFC 3986 section 2.2 Reserved Characters
    private static final String[] RESERVED_CHARACTERS_ENCODED = {
        "%20", "%21", "%22", "%23", "%24", "%25", "%26", "%27", "%28","%29",
        "%2A", "%2B", "%2C", "%2F", "%3A", "%3B", "%3D", "%3F", "%40", "%5B", "%5D",
        "%2a", "%2b", "%2c", "%2f", "%3a", "%3b", "%3d", "%3f", "%5b", "%5d"
    };
    private final static List<Pattern> PATTERNS = new ArrayList<>();

    static {
        PATTERNS.add(Pattern.compile("(<%[=@].*?%>)"));
        PATTERNS.addAll(Arrays.stream(RESERVED_CHARACTERS_ENCODED)
            .map(encoded -> Pattern.compile("(" + encoded + ")") )
            .collect(Collectors.toList()));
    }

    private static final String MAIL_TO_PATTERN = "mailto";

    private static final String TEL_PATTERN = "tel";

    //SITES-18137: imitate the exact behavior of com.google.common.net.URL_FRAGMENT_ESCAPER
    private static final BitSet URL_FRAGMENT_SAFE_CHARS = new BitSet(256);

    static {
        //alphanumeric characters
        for(int i = 97; i <= 122; i++) {
            URL_FRAGMENT_SAFE_CHARS.set(i);
        }
        for(int i = 65; i <= 90; i++) {
            URL_FRAGMENT_SAFE_CHARS.set(i);
        }
        for(int i = 48; i <= 57; i++) {
            URL_FRAGMENT_SAFE_CHARS.set(i);
        }
        // safe characters from Guava's URL_FRAGMENT_ESCAPER: -._~!$'()*,;&=@:+/?
        byte[] nonAlphaNumeric = new byte[] { 45, 46, 95, 126, 33, 36, 39, 40, 41, 42, 44, 59, 38, 61, 64, 58, 43, 47, 63 };
        for(int i = 0; i < nonAlphaNumeric.length; i++) {
            URL_FRAGMENT_SAFE_CHARS.set(nonAlphaNumeric[i]);
        }
    }


    /**
     * Decodes and encoded or escaped URL taking care to not break Adobe Campaign expressions
     * like: /content/path/to/page.html?recipient=<%= recipient.id %>
     *
     * @param url The URL to decode
     * @return The decoded URL
     * @throws UnsupportedEncodingException
     */
    public static String decode(final String url) throws UnsupportedEncodingException {
        // The link contain character sequences that are not well formatted and cannot be decoded, for example
        // Adobe Campaign expressions like: /content/path/to/page.html?recipient=<%= recipient.id %>
        final Map<String, String> placeholders = new LinkedHashMap<>();
        final String masked = LinkUtil.mask(url, placeholders);
        final String decoded = URLDecoder.decode(masked, StandardCharsets.UTF_8.name());
        final String unmasked = unmask(decoded, placeholders);
        return unmasked;
    }

    /**
     * Escapes an URI based on path, query string and fragment: path?queryString#fragment
     *
     * @param path The URI path
     * @param queryString The URI query string
     * @param fragment The URI fragment
     * @return The escaped fragment
     */
    public static String escape(final String path, final String queryString, final String fragment) {
        boolean pathContainsFragment = false;
        if (StringUtils.contains(path, fragment)) {
            pathContainsFragment = true;
        }
        final Map<String, String> placeholders = new LinkedHashMap<>();
        final String maskedQueryString = mask(queryString, placeholders);
        String escaped;
        URI parsed;
        final String maskedPath = LinkUtil.mask(path, placeholders);

        try {
            parsed = new URI(maskedPath, false);
        } catch (URIException e) {
            parsed = null;
            LOG.error(e.getMessage(), e);
        }
        try {
            if (parsed != null && !isMailToLink(parsed.getScheme()) && !isTelLink(parsed.getScheme())) {
                escaped = new URI(parsed.getScheme(), parsed.getAuthority(), parsed.getPath(), maskedQueryString, null).toString();
            } else {
                escaped = new URI(null, null, path, maskedQueryString, null).toString();
            }
            if (fragment != null) {
                StringBuilder sb = new StringBuilder(escaped);
                if (pathContainsFragment) {
                    if (parsed != null) {
                        escaped = sb.insert(parsed.toString().length(), "#" + fragment).toString();
                    } else {
                        escaped = sb.insert(path.indexOf(fragment), "#" + fragment).toString();
                    }
                } else {
                    escaped = sb.append("#")
                                .append(replaceEncodedCharactersInFragment(URLEncoder.encode(fragment, StandardCharsets.UTF_8.name())))
                                .toString();
                }
            }

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            StringBuilder sb = new StringBuilder(path);
            if (queryString != null) {
                sb.append("?").append(maskedQueryString);
            }
            if (fragment != null) {
                sb.append("#").append(fragment);
            }
            escaped = sb.toString();
        }
        final String unmasked = LinkUtil.unmask(escaped, placeholders);
        return unmasked;
    }

    /**
     * Escapes an URI fragment, imitating the exact behavior of com.google.common.net.URL_FRAGMENT_ESCAPER.
     * (SITES-18137)
     *
     * @param fragment The URI fragment
     * @return The escaped fragment
     */
    public static String escapeFragment(final String fragment) {
        if (fragment == null) {
            return null;
        }
        return new String(URLCodec.encodeUrl(URL_FRAGMENT_SAFE_CHARS, fragment.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    /**
     * Masks a given {@link String} by replacing all occurrences of {@link LinkUtil#PATTERNS} with a placeholder.
     * The generated placeholders are put into the given {@link Map} and can be used to unmask a {@link String} later on.
     * <p>
     * For example the given original {@link String} {@code /path/to/page.html?r=<%= recipient.id %>} will be transformed to
     * {@code /path/to/page.html?r=_abcd_} and the placeholder with the expression will be put into the given {@link Map}.
     *
     * @param original     the original {@link String}
     * @param placeholders a {@link Map} the generated placeholders will be put in
     * @return the masked {@link String}
     * @see LinkUtil#unmask(String, Map)
     */
    static String mask(final String original, final Map<String, String> placeholders) {
        if (original == null) {
            return null;
        }
        String masked = original;
        for (Pattern pattern : PATTERNS) {
            Matcher matcher = pattern.matcher(masked);
            while (matcher.find()) {
                String expression = matcher.group(1);
                String placeholder = newPlaceholder(masked);
                masked = masked.replaceFirst(Pattern.quote(expression), placeholder);
                placeholders.put(placeholder, expression);
            }
        }
        return masked;
    }

    /**
     * Unmasks the given {@link String} by replacing the given placeholders with their original value.
     * <p>
     * For example the given masked {@link String} {@code /path/to/page.html?r=_abcd_} will be transformed to
     * {@code /path/to/page.html?r=<%= recipient.id %>} by replacing each of the given {@link Map}s keys with the corresponding value.
     *
     * @param masked       the masked {@link String}
     * @param placeholders the {@link Map} of placeholders to replace
     * @return the unmasked {@link String}
     */
    static String unmask(final String masked, final Map<String, String> placeholders) {
        if (masked == null) {
            return null;
        }
        String unmasked = masked;
        for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
            unmasked = unmasked.replaceFirst(placeholder.getKey(), placeholder.getValue());
        }
        return unmasked;
    }

    /**
     * Generate a new random placeholder that is not conflicting with any character sequence in the given {@link String}.
     * <p>
     * For example the given {@link String} {@code "foo"} a new random {@link String} will be returned that is not contained in the
     * given {@link String}. In this example the following {@link String}s will never be returned "f", "fo", "foo", "o", "oo".
     *
     * @param str the given {@link String}
     * @return the placeholder name
     */
    private static String newPlaceholder(final String str) {
        SecureRandom random = new SecureRandom();
        StringBuilder placeholderBuilder = new StringBuilder(5);

        do {
            placeholderBuilder.setLength(0);
            placeholderBuilder
                    .append("_")
                    .append(new BigInteger(16, random).toString(16))
                    .append("_");
        } while (str.contains(placeholderBuilder));

        return placeholderBuilder.toString();
    }

    private static String replaceEncodedCharactersInFragment(final String str) {
        return str.replace("%2B", "+")
            .replace("%3D", "=")
            .replace("%7E", "~")
            .replace("%24", "$")
            .replace("%26", "&")
            .replace("%3B", ";")
            .replace("%3A", ":")
            .replace("%40", "@")
            .replace("%21", "!")
            .replace("%27", "'")
            .replace("%28", "(")
            .replace("%29", ")")
            .replace("%2C", ",")
            .replace("%2F", "/")
            .replace("%3F", "?");
    }

    private static boolean isMailToLink(String link) {
        if (link != null) {
            return link.startsWith(MAIL_TO_PATTERN);
        } else {
            return false;
        }
    }

    private static boolean isTelLink(String link) {
        if (link != null) {
            return link.startsWith(TEL_PATTERN);
        } else {
            return false;
        }
    }
}
