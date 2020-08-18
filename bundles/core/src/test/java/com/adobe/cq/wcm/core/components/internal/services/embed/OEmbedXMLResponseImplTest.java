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
package com.adobe.cq.wcm.core.components.internal.services.embed;

import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.services.embed.OEmbedResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OEmbedXMLResponseImplTest {

    @Test
    void testXMLDeserialization() throws JAXBException {
        OEmbedResponse embedResponse = getResponseFromXMLFile();
        assertEquals("photo", embedResponse.getType());
        assertEquals("ZB8T0193", embedResponse.getTitle());
        assertEquals("1.0", embedResponse.getVersion());
        assertEquals("\u202E\u202D\u202Cbees\u202C", embedResponse.getAuthorName());
        assertEquals("https://www.flickr.com/photos/bees/", embedResponse.getAuthorUrl());
        assertEquals("Flickr", embedResponse.getProviderName());
        assertEquals("https://www.flickr.com/", embedResponse.getProviderUrl());
        assertEquals(Long.valueOf(3600), embedResponse.getCacheAge());
        assertEquals("https://live.staticflickr.com/3123/2341623661_7c99f48bbf_q.jpg", embedResponse.getThumbnailUrl());
        assertEquals("150", embedResponse.getThumbnailHeight());
        assertEquals("150", embedResponse.getThumbnailWidth());
        assertEquals("1024", embedResponse.getWidth());
        assertEquals("683", embedResponse.getHeight());
        assertEquals("<a data-flickr-embed=\"true\" href=\"https://www.flickr.com/photos/bees/2341623661/\" title=\"ZB8T0193 by \u202E\u202D\u202Cbees\u202C, on Flickr\"><img src=\"https://live.staticflickr.com/3123/2341623661_7c99f48bbf_b.jpg\" width=\"1024\" height=\"683\" alt=\"ZB8T0193\"></a><script async src=\"https://embedr.flickr.com/assets/client-code.js\" charset=\"utf-8\"></script>", embedResponse.getHtml());
        assertEquals("https://live.staticflickr.com/3123/2341623661_7c99f48bbf_b.jpg", embedResponse.getUrl());
    }

    private OEmbedResponse getResponseFromXMLFile() throws JAXBException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("embed/oembed/response.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(OEmbedXMLResponseImpl.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (OEmbedResponse)jaxbUnmarshaller.unmarshal(is);
    }
}
