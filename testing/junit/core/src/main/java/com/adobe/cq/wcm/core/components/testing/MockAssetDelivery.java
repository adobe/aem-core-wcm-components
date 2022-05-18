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
package com.adobe.cq.wcm.core.components.testing;

import com.adobe.cq.wcm.spi.AssetDelivery;
import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MockAssetDelivery implements AssetDelivery {

    private static String FORMAT_PARAMETER = "format";
    private static String PATH_PARAMETER = "path";
    private static String SEO_PARAMETER = "seoname";
    private static List<String> params = new ArrayList<String>() {
                                            {
                                                add("width");
                                                add("quality");
                                                add("c");
                                                add("r");
                                                add("flip");
                                                add("sz");
                                                add("preferwebp");
                                            }
                                        };

    public static String BASE_URL = "/asset/delivery";

    @Nullable
    @Override
    public String getDeliveryURL(@NotNull Resource resource, @Nullable Map<String, Object> parameterMap) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(BASE_URL);
        stringBuilder.append( parameterMap.remove(PATH_PARAMETER));
        stringBuilder.append("." + parameterMap.remove(SEO_PARAMETER));
        stringBuilder.append("." + parameterMap.remove(FORMAT_PARAMETER) + "?");

        ArrayList<String> sortedKeys
            = new ArrayList<String>(parameterMap.keySet());

        Collections.sort(sortedKeys);

        for (String key : params) {
            if (parameterMap.containsKey(key)) {
                stringBuilder.append(key + "=" + parameterMap.get(key) + "&");
            }
        }

        if ((stringBuilder.charAt(stringBuilder.length() -1) == '&') || (stringBuilder.charAt(stringBuilder.length() -1) == '?') ) {
            stringBuilder.deleteCharAt(stringBuilder.length() -1);
        }

        return stringBuilder.toString();
    }
}
