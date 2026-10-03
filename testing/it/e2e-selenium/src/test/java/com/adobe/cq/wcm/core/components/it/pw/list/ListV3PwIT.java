/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.wcm.core.components.it.pw.list;

import org.junit.jupiter.api.Tag;

@Tag("playwright-group4")
public class ListV3PwIT extends ListV2PwIT {
    @Override
    protected String listResourceType() {
        return com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_LIST_V3;
    }
}
