package com.adobe.cq.wcm.core.components.it.seljup.util.components.tableofcontents;

public class TableOfContentsEditDialog65 extends TableOfContentsEditDialog {

    protected static String levelSelectItemTemplate =
        "coral-overlay.is-open coral-selectlist-item[value='%s']";

    public void selectStartLevel(String startLevel) throws InterruptedException {
        selectItem(startLevelSelect, String.format(levelSelectItemTemplate, startLevel));
    }

    public void selectStopLevel(String stopLevel) throws InterruptedException {
        selectItem(stopLevelSelect, String.format(levelSelectItemTemplate, stopLevel));
    }
}
