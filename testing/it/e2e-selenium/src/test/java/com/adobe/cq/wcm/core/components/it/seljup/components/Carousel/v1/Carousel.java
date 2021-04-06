package com.adobe.cq.wcm.core.components.it.seljup.components.Carousel.v1;

import com.adobe.cq.wcm.core.components.it.seljup.components.Commons.ChildrenEditor;
import com.adobe.cq.wcm.core.components.it.seljup.components.Commons.CQOverlay;
import com.adobe.qe.selenium.pagewidgets.common.BaseComponent;
import com.adobe.qe.selenium.pagewidgets.coral.CoralCheckbox;
import com.adobe.qe.selenium.pagewidgets.cq.InsertComponentDialog;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.adobe.qe.selenium.pagewidgets.Helpers.waitForElementAnimationFinished;
import static com.codeborne.selenide.Selenide.*;

public class Carousel extends BaseComponent {

    private static String indicator = ".cmp-carousel__indicator";
    private static String activeIndicator = ".cmp-carousel__indicator--active";

    public Carousel() {
        super(".cmp-carousel");
    }

    public EditDialog getEditDialog() {
        return new EditDialog();
    }

    public CQOverlay getCQOverlay() {
        return new CQOverlay();
    }

    public ElementsCollection getIndicators() {
        return $$(indicator);
    }

    public ElementsCollection getActiveIndicator() {
        return $$(activeIndicator);
    }

    public void clickIndicator(int idx) {
        $$(indicator).get(idx).click();
    }

    public boolean isIndicatorActive(int idx) {
        return getIndicators().get(idx).getAttribute("class").contains("cmp-carousel__indicator--active");
    }

    public static final class EditDialog {

        private static String tabItems = ".cmp-carousel__editor coral-tab:eq(0)";
        private static String tabProperties = ".cmp-carousel__editor coral-tab:eq(1)";
        private static String autoplay = "[data-cmp-carousel-v1-dialog-hook='autoplay']";
        private static String autoplayGroup = "[data-cmp-carousel-v1-dialog-hook='autoplayGroup']";
        private static String delay = "[data-cmp-carousel-v1-dialog-hook='delay']";
        private static String autopauseDisabled = "[data-cmp-carousel-v1-dialog-hook='autopauseDisabled']";

        private EditDialog() {

        }

        public void openEditDialogProperties() {
              $$(".cmp-carousel__editor coral-tab").get(1).click();
        }

        public ChildrenEditor getChildrenEditor() { return new ChildrenEditor(); }
        public com.adobe.qe.selenium.pagewidgets.cq.InsertComponentDialog getInsertComponentDialog() {
            return new InsertComponentDialog();
        }

        public CoralCheckbox getAutoplay() {
            return new CoralCheckbox(autoplay);
        }

        public SelenideElement getAutoplayGroup() {
            return $(autoplayGroup);
        }

        public SelenideElement getDelay() {
            return $(delay);
        }

        public SelenideElement getAutopauseDisabled() {
            return $(autopauseDisabled);
        }

    }
}
