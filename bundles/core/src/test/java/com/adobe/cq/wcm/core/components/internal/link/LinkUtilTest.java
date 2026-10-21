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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class LinkUtilTest {

    @Test
    void decode_whenReservedCharactersAreEncoded_thenReservedCharactersAreNotAffectedByTheDecode() throws UnsupportedEncodingException {
        String encodedPath = "https://api01-platform.stream.co.jp/apiservice/plt3/Mzc3Mg%3d%3d%23Mjkx%23280%23168%230%233FE320DBC400%23OzEwOzEwOzEw%23";
        String decodedPath = LinkUtil.decode(encodedPath);
        assertEquals(encodedPath, decodedPath);
    }

    @Test
    void decode_whenReservedCharactersAreNotEncoded_thenReservedCharactersAreNotAffectedBTheDecode() throws UnsupportedEncodingException {
        String encodedPath = "http://google.com/?q=hello";
        String decodedPath = LinkUtil.decode(encodedPath);
        assertEquals(encodedPath, decodedPath);
    }

    @Test
    void decode_whenUnreservedCharactersAreEncoded_thenUnreservedCharactersAreDecodedInTheDecodedString() throws UnsupportedEncodingException {
        String encodedPath = "http://google.com/?q=hello%7Eworld";
        String decodedPath = LinkUtil.decode(encodedPath);
        assertEquals("http://google.com/?q=hello~world", decodedPath);
    }

    @Test
    void decode_whenUnreservedCharactersAreNotEncoded_thenUnreservedCharactersAreNotAffectedByTheDecode() throws UnsupportedEncodingException {
        String encodedPath = "http://google.com/?q=hello-world";
        String decodedPath = LinkUtil.decode(encodedPath);
        assertEquals(encodedPath, decodedPath);
    }

    @Test
    void decode_whenCampaignPatternIsPresentInTheString_thenCampaignPatternIsNotAffectedByTheDecode() throws UnsupportedEncodingException {
        String encodedPath = "/content/path/to/page.html?recipient=<%= recipient.id %>";
        String decodedPath = LinkUtil.decode(encodedPath);
        assertEquals(encodedPath, decodedPath);
    }

    @Test
    void test_withQueryStringAndFragment() {
        assertEquals("https://www.adobe.come?q=hello&w=world#home", LinkUtil.escape("https://www.adobe.come", "q=hello&w=world", "home"));
        assertEquals("https://www.adobe.come/home.html?q=hello&w=world#home", LinkUtil.escape("https://www.adobe.come/home.html", "q=hello&w=world", "home"));
    }

    @Test
    void escape_whenEscapingPathWithFragment_thenFragmentForwardSlashIsNotEncoded() throws UnsupportedEncodingException {
        String path = "https://google.com";
        String fragment = "/assets/2/1529/RES176341/report";
        String escapedPAth = LinkUtil.escape(path, null, fragment);
        assertEquals(path+ "#" + fragment, escapedPAth);
    }

    @Test
    void escape_mailToLinkNotThrowException() {
        String path = "mailto:mail@example.com";
        String escapedMailTo = LinkUtil.escape(path, null, null);
        assertEquals(path, escapedMailTo);
    }

    @Test
    void escape_telLinkNotThrowException() {
        String path = "tel:1800";
        String escapedTel = LinkUtil.escape(path, null, null);
        assertEquals(path, escapedTel);
    }

    @Test
    void escape_quickviewLink_resultEqualToInputAndLogsDebug() {
        String path = "quickview:fragment=Camping in Western Australia&size=400,300&reservedVal_productPath=/content/experience-fragments/wknd/ca/en/featured/camping-western-australia";

        Logger appLogger = (Logger) LoggerFactory.getLogger(LinkUtil.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        appLogger.addAppender(appender);
        appLogger.setLevel(Level.DEBUG);

        String escapedQuickview = LinkUtil.escape(path, null, null);
        assertEquals(path, escapedQuickview);

        assertEquals(1, appender.list.stream()
            .filter(entry -> Level.DEBUG.equals(entry.getLevel()))
            .count()
        );

        assertEquals(0, appender.list.stream()
            .filter(entry -> Level.ERROR.equals(entry.getLevel()))
            .count()
        );
    }

    @Test
    void escape_nullInputs_returnsEmptyStringAndLogsError() {

        Logger appLogger = (Logger) LoggerFactory.getLogger(LinkUtil.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        appLogger.addAppender(appender);
        appLogger.setLevel(Level.DEBUG);

        String escaped = LinkUtil.escape(null, null, null);
        assertEquals("", escaped);

        assertEquals(1, appender.list.stream()
            .filter(entry -> Level.ERROR.equals(entry.getLevel()))
            .count()
        );
    }

    @Test
    void escape_customLink_resultEqualToInputAndLogsError() {

        Logger appLogger = (Logger) LoggerFactory.getLogger(LinkUtil.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        appLogger.addAppender(appender);
        appLogger.setLevel(Level.DEBUG);

        String path = "custom:abc";
        String escaped = LinkUtil.escape(path, null, null);
        assertEquals(path, escaped);

        assertEquals(1, appender.list.stream()
            .filter(entry -> Level.ERROR.equals(entry.getLevel()))
            .count()
        );
    }
}
