/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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
package com.adobe.cq.wcm.core.components.internal.servlets;

import com.adobe.cq.wcm.core.components.models.Image;
import com.day.cq.commons.DownloadResource;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.s7dam.utils.PublishUtils;
import com.day.cq.dam.scene7.api.constants.Scene7Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.servlets.post.Modification;
import org.apache.sling.servlets.post.ModificationType;
import org.apache.sling.servlets.post.SlingPostProcessor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This post processor monitors changes made to component's {@link DownloadResource#PN_REFERENCE} property. In case
 * modification of type {@link ModificationType#CREATE}, {@link ModificationType#MODIFY} or {@link ModificationType#DELETE} is detected the post
 * processor updates {@link Image#PN_IMAGE_SERVER_URL} property for future use on the publish node.
 */
@Component(
    service = SlingPostProcessor.class
)
public final class DMAssetPostProcessor implements SlingPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerServlet.class);

    private static final String IMAGE_SERVER_PATH = "/is/image/";

    @Reference
    private PublishUtils publishUtils;

    @Override
    public void process(SlingHttpServletRequest request, List<Modification> list) throws Exception {
        Modification lastFileReferenceModification = getLastFileReferenceModification(request, list);
        if (lastFileReferenceModification != null) {
            switch (lastFileReferenceModification.getType()) {
                case CREATE:
                case MODIFY:
                    ValueMap valueMap = request.getResource().getValueMap();
                    String fileReference = valueMap.get(DownloadResource.PN_REFERENCE, String.class);
                    if (fileReference != null) {
                        Resource assetResource = request.getResourceResolver().getResource(fileReference);
                        if (assetResource != null) {
                            Asset asset = assetResource.adaptTo(Asset.class);
                            if (asset != null) {
                                if(isDmAsset(asset)) {
                                    String[] productionAssetUrls = publishUtils.externalizeImageDeliveryAsset(assetResource);
                                    String imageServerUrl = productionAssetUrls[0] + IMAGE_SERVER_PATH;
                                    checkSetImageServerUrl(request.getResource(), imageServerUrl, list);
                                } else {
                                    checkSetImageServerUrl(request.getResource(), null, list);
                                }
                            } else {
                                LOGGER.error("Unable to adapt resource '{}' used by image '{}' to an asset.", fileReference, request.getResource().getPath());
                            }
                        } else {
                            LOGGER.error("Unable to find resource '{}' used by image '{}'.", fileReference, request.getResource().getPath());
                        }
                    } else {
                        LOGGER.warn("File reference was null despite '{}' modification type.", lastFileReferenceModification.getType());
                    }
                    break;
                case DELETE:
                    checkSetImageServerUrl(request.getResource(), null, list);
                    break;
                default:
                    //noop
            }
        }
    }

    private static void checkSetImageServerUrl(Resource resource, String imageServerUrl, List<Modification> list) {
        ModifiableValueMap map = resource.adaptTo(ModifiableValueMap.class);
        if (map != null) {
            ValueMap valueMap = resource.getValueMap();
            if (imageServerUrl != null) {
                String oldImageServerUrl = valueMap.get(Image.PN_IMAGE_SERVER_URL, String.class);
                if (!imageServerUrl.equals(oldImageServerUrl)) {
                    list.add(new Modification(oldImageServerUrl != null ? ModificationType.MODIFY : ModificationType.CREATE, getModificationSource(resource.getPath()), null));
                    map.put(Image.PN_IMAGE_SERVER_URL, imageServerUrl);
                }
            } else {
                if (valueMap.containsKey(Image.PN_IMAGE_SERVER_URL)) {
                    list.add(new Modification(ModificationType.DELETE, getModificationSource(resource.getPath()), null));
                    map.remove(Image.PN_IMAGE_SERVER_URL);
                }
            }
        } else {
            LOGGER.warn("Cannot adapt resource '{}' to ModifiableValueMap.", resource);
        }
    }

    private static boolean isDmAsset(Asset asset) {
        String dmAssetName = asset.getMetadataValue(Scene7Constants.PN_S7_FILE);
        //check DM asset - check for "dam:scene7File" metadata value
        return !StringUtils.isEmpty(dmAssetName);
    }

    private static Modification getLastFileReferenceModification(SlingHttpServletRequest request, List<Modification> list) {
        String expectedModificationSource = request.getResource().getPath() + "/" + DownloadResource.PN_REFERENCE;
        Modification lastFileReferenceModification = null;
        for (Modification modification : list) {
            if (expectedModificationSource.equals(modification.getSource())) {
                lastFileReferenceModification = modification;
            }
        }
        return lastFileReferenceModification;
    }

    private static String getModificationSource(String path) {
        return path + "/" + Image.PN_IMAGE_SERVER_URL;
    }
}
