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
package com.adobe.cq.wcm.core.components.internal.link;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class LinkMaskingUtil {

    private final static List<Pattern> PATTERNS = Collections.singletonList(Pattern.compile("(<%[=@].*?%>)"));

    private LinkMaskingUtil() {
        super();
    }

    /**
     * This functional interface is used to pass a callable to {@link LinkMaskingUtil#maskAndDo(String, Fn)} which has the semantics of a
     * {@link java.util.function.Function} but allows to throw a checked exception.
     *
     * @param <T>
     * @param <R>
     */
    @FunctionalInterface
    interface Fn<T, R> {
        R apply(T t) throws Exception;
    }

    /**
     * Masks the given {@link String}, calls the given {@link Fn} with it and unmasks the returned {@link String}.
     *
     * @param original a {@link String} to be masked and passed to the callable
     * @param callable the {@link Fn} to process the masked original
     * @return the unmasked return value of the given callable
     * @throws Exception when the given callable throws ans exception
     */
    static String maskAndDo(String original, Fn<String, String> callable) throws Exception {
        // The link contain character sequences that are not well formatted and cannot be decoded, for example
        // Adobe Campaign expressions like: /content/path/to/page.html?recipient=<%= recipient.id %>
        Map<String, String> placeholders = new LinkedHashMap<>();
        String masked = mask(original, placeholders);
        masked = callable.apply(masked);
        return unmask(masked, placeholders);
    }

    /**
     * Masks a given {@link String} by replacing all occurrences of {@link LinkMaskingUtil#PATTERNS} with a placeholder.
     * The generated placeholders are put into the given {@link Map} and can be used to unmask a {@link String} later on.
     * <p>
     * For example the given original {@link String} {@code /path/to/page.html?r=<%= recipient.id %>} will be transformed to
     * {@code /path/to/page.html?r=_abcd_} and the placeholder with the expression will be put into the given {@link Map}.
     *
     * @param original     the original {@link String}
     * @param placeholders a {@link Map} the generated placeholders will be put in
     * @return the masked {@link String}
     * @see LinkMaskingUtil#unmask(String, Map)
     */
    private static String mask(String original, Map<String, String> placeholders) {
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
    private static String unmask(String masked, Map<String, String> placeholders) {
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
    private static String newPlaceholder(String str) {
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
}
