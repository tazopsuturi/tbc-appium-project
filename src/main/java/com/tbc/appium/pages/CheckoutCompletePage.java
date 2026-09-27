package com.tbc.appium.pages;

import com.tbc.appium.reporting.StepLogger;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

/** Order confirmation screen shown after placing an order. */
public class CheckoutCompletePage extends BaseScreen {

    @AndroidFindBy(id = "completeTV")
    private WebElement title;

    @AndroidFindBy(id = "thankYouTV")
    private WebElement thankYouMessage;

    @AndroidFindBy(id = "orderTV")
    private WebElement orderMessage;

    @AndroidFindBy(accessibility = "Tap to open catalog")
    private WebElement continueShoppingButton;

    public CheckoutCompletePage() {
        super();
    }

    @Override
    public boolean isDisplayed() {
        return isOpened(title);
    }

    public String getTitle() {
        return getText(title);
    }

    public String getThankYouMessage() {
        return getText(thankYouMessage);
    }

    public CatalogPage continueShopping() {
        StepLogger.step("Tap 'Continue Shopping'");
        click(continueShoppingButton);
        return new CatalogPage();
    }
}
