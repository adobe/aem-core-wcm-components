//package com.adobe.cq.wcm.core.components.it.seljup.components.video;
//
//import com.adobe.cq.wcm.core.components.it.seljup.components.commons.ChildrenEditor;
//import com.adobe.cq.wcm.core.components.it.seljup.components.tabs.TabsEditDialog;
//import com.codeborne.selenide.SelenideElement;
//import com.adobe.cq.testing.selenium.pagewidgets.coral.Dialog;
//
//import static com.codeborne.selenide.Selenide.$;
//
//public class VideoEditDialog extends Dialog {
//
//    private static SelenideElement videoTab = $("coral-tab[data-foundation-tracking-event*='video']");
//    private static SelenideElement posterTab = $("coral-tab[data-foundation-tracking-event*='poster']");
//    private static SelenideElement propertiesTab = $("coral-tab[data-foundation-tracking-event*='properties']");
//
//    public ChildrenEditor getChildrenEditor() {
//        return new ChildrenEditor();
//    }
//
//    public void openVideoTab() {
//        $(videoTab).click();
//    }
//
//    public TabsEditDialog.EditDialogProperties openPosterTab() {
//        $(posterTab).click();
//        return new TabsEditDialog.EditDialogProperties();
//    }
//
//
//
////    public void openItemsTab() {
////        $(itemsTab).click();
////    }
////
////    public TabsEditDialog.EditDialogProperties openPropertiesTab() {
////        $(propertiesTab).click();
////        return new TabsEditDialog.EditDialogProperties();
////    }
//
//}
