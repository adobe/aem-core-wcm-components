/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2025 Adobe
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
package com.adobe.cq.wcm.core.components.util.ngdm;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.AbstractMap;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to build preview token for Dynamic Media with OpenAPI Asset.
 */
public class PreviewTokenBuilderUtils {

    private static final Logger LOG = LoggerFactory.getLogger(PreviewTokenBuilderUtils.class);

    private final static String HMAC_SHA256 = "HmacSHA256";
    private final static String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private final static String PREVIEW_KEY = "ASSET_DELIVERY_PREVIEW_KEY";
    private final static String UTC = "UTC";

    private static final ThreadLocal<SimpleDateFormat> dateFormat = ThreadLocal.withInitial(() -> {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        dateFormat.setTimeZone(TimeZone.getTimeZone(UTC));
        return dateFormat;
    });

    /**
     * Builds the preview token for the given assetId.
     * @param assetId - the assetId for which the preview token is to be generated.
     * @return a Map.Entry containing the token and the expiry time as a string.
     */
    public static Map.Entry<String, String> buildPreviewToken(String assetId) {
        try {
            String secretKey = readKeyFromEnvVar(PREVIEW_KEY);
            if (assetId == null || StringUtils.isBlank(secretKey)) {
                return null;
            }
            LocalDateTime nowPlusThirty = LocalDateTime.now().plusMinutes(30);
            Date expirationTime = Date.from(nowPlusThirty.atZone(ZoneId.systemDefault()).toInstant());
            final String expiryTimeStr = dateFormat.get().format(expirationTime);
            return new AbstractMap.SimpleEntry<>(computeSignature(assetId, expiryTimeStr, secretKey), expiryTimeStr);
        } catch (Exception e) {
            LOG.error("Could not generate preview token for asset {}. Exception : {}", assetId, e.getMessage());
        }
        return null;
    }

    private static String computeSignature(String assetId, String expiryTime, String secretKey) throws Exception {
        try {
            String stringToSign = expiryTime + ":" + assetId;
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            return Hex.encodeHexString(mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | InvalidKeyException nsae) {
            throw new Exception("Failed to compute signature", nsae);
        }
    }

    private static String readKeyFromEnvVar(String key) {
        return System.getProperty(key, System.getenv(key));
    }
}
