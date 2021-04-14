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
import com.day.cq.dam.scene7.api.constants.Scene7AssetType;
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
 * Additionally this post processor monitors drag and drop actions by checking if fileReference update is accompanied by file@Delete update. In these cases it resets
 * the value of smartCropRendition.
 */
@Component(
    service = SlingPostProcessor.class
)
public final class DMAssetPostProcessor implements SlingPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerServlet.class);

    public static final String IMAGE_SERVER_PATH = "/is/image/";
    public static final String CONTENT_SERVER_PATH = "/is/content/";

    @Reference
    private PublishUtils publishUtils;

    @Override
    public void process(SlingHttpServletRequest request, List<Modification> list) throws Exception {

        Modification lastFileReferenceModification = getLastPropertyModification(list, DownloadResource.PN_REFERENCE);
        if (lastFileReferenceModification != null) {
            String pathToComponent = lastFileReferenceModification.getSource().substring(0, lastFileReferenceModification.getSource().indexOf("/" + DownloadResource.PN_REFERENCE));
            if (!pathToComponent.isEmpty()) {
                Resource componentResource = request.getResourceResolver().getResource(pathToComponent);
                if (componentResource != null) {
                    switch (lastFileReferenceModification.getType()) {
                        case CREATE:
                        case MODIFY:
                            ValueMap valueMap = componentResource.getValueMap();
                            String fileReference = valueMap.get(DownloadResource.PN_REFERENCE, String.class);
                            if (fileReference != null) {
                                Resource assetResource = request.getResourceResolver().getResource(fileReference);
                                if (assetResource != null) {
                                    Asset asset = assetResource.adaptTo(Asset.class);
                                    if (asset != null) {
                                        if (isDmAsset(asset)) {
                                            String[] productionAssetUrls = publishUtils.externalizeImageDeliveryAsset(assetResource);
                                            String imageServerUrl = productionAssetUrls[0];
                                            if (asset.getMetadataValue(Scene7Constants.PN_S7_TYPE).equals(Scene7AssetType.ANIMATED_GIF.getValue())) {
                                                imageServerUrl += CONTENT_SERVER_PATH;
                                            } else {
                                                imageServerUrl += IMAGE_SERVER_PATH;
                                            }

                                            checkSetProperty(componentResource, Image.PN_IMAGE_SERVER_URL, imageServerUrl, list);
                                        } else {
                                            checkSetProperty(componentResource, Image.PN_IMAGE_SERVER_URL, null, list);
                                        }

                                        Modification lastFileModification = getLastPropertyModification(request, list, "file");
                                        if ((lastFileModification != null) && (lastFileModification.getType() == ModificationType.DELETE)) {
                                            checkSetProperty(componentResource, "smartCropRendition", null, list);
                                        }
                                    } else {
                                        LOGGER.error("Unable to adapt resource '{}' used by image '{}' to an asset.", fileReference, componentResource.getPath());
                                    }
                                } else {
                                    LOGGER.error("Unable to find resource '{}' used by image '{}'.", fileReference, componentResource.getPath());
                                }
                            } else {
                                LOGGER.warn("File reference was null despite '{}' modification type.", lastFileReferenceModification.getType());
                            }
                            break;
                        case DELETE:
                            checkSetProperty(componentResource, Image.PN_IMAGE_SERVER_URL, null, list);
                            checkSetProperty(componentResource, "smartCropRendition", null, list);
                            break;
                        default:
                            //noop
                    }
                }
            } else {
                LOGGER.error("Unable to find path to component used by modification '{}'", lastFileReferenceModification.getSource());
            }
        } else {
            LOGGER.warn("Last file reference modification was null for '{}' property name.", DownloadResource.PN_REFERENCE);
        }
    }

    private static void checkSetProperty(Resource resource, String name, String value, List<Modification> list) {
        ModifiableValueMap map = resource.adaptTo(ModifiableValueMap.class);
        if (map != null) {
            ValueMap valueMap = resource.getValueMap();
            if (value != null) {
                String oldValue = valueMap.get(name, String.class);
                if (!value.equals(oldValue)) {
                    list.add(new Modification(oldValue != null ? ModificationType.MODIFY : ModificationType.CREATE, getModificationSource(resource.getPath(), name), null));
                    map.put(name, value);
                }
            } else {
                if (valueMap.containsKey(name)) {
                    list.add(new Modification(ModificationType.DELETE, getModificationSource(resource.getPath(), name), null));
                    map.remove(name);
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

    private static Modification getLastPropertyModification(SlingHttpServletRequest request, List<Modification> list, String propertyName) {
        String expectedModificationSource = request.getResource().getPath() + "/" + propertyName;
        Modification lastPropertyModification = null;
        for (Modification modification : list) {
            if (expectedModificationSource.equals(modification.getSource())) {
                lastPropertyModification = modification;
            }
        }
        return lastPropertyModification;
    }

    private static Modification getLastPropertyModification(List<Modification> list, String propertyName) {
        String expectedModificationSource = "/" + propertyName;
        Modification lastPropertyModification = null;
        for (Modification modification : list) {
            if (modification.getSource().endsWith(expectedModificationSource)) {
                lastPropertyModification = modification;
            }
        }
        return lastPropertyModification;
    }

    private static String getModificationSource(String path, String name) {
        return path + "/" + name;
    }
}
