package com.tbc.appium.pages;

import com.tbc.appium.models.Product;
import com.tbc.appium.reporting.StepLogger;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.util.List;

/** Checkout step 3: order review (items, delivery address, payment method, total). */
public class CheckoutReviewPage extends BaseScreen {

    @AndroidFindBy(uiAutomator = "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/enterShippingAddressTV\").text(\"Review your order\")")
    private WebElement subtitle;

    @AndroidFindBy(id = "titleTV")
    private List<WebElement> itemNames;

    @AndroidFindBy(id = "fullNameTV")
    private WebElement deliveryName;

    @AndroidFindBy(id = "addressTV")
    private WebElement deliveryAddress;

    @AndroidFindBy(id = "cardHolderTV")
    private WebElement cardHolder;

    @AndroidFindBy(id = "itemNumberTV")
    private WebElement itemCount;

    @AndroidFindBy(id = "totalAmountTV")
    private WebElement totalAmount;

    @AndroidFindBy(accessibility = "Completes the process of checkout")
    private WebElement placeOrderButton;

    public CheckoutReviewPage() {
        super();
    }

    @Override
    public boolean isDisplayed() {
        return isOpened(subtitle);
    }

    public List<String> getItemNames() {
        waitForVisible(subtitle);
        return itemNames.stream().map(WebElement::getText).toList();
    }

    public String getDeliveryName() {
        return getText(deliveryName);
    }

    public String getDeliveryAddress() {
        return getText(deliveryAddress);
    }

    public String getCardHolder() {
        return getText(cardHolder);
    }

    public int getItemCount() {
        return Integer.parseInt(getText(itemCount).replaceAll("\\D", ""));
    }

    /** Order total including delivery. */
    public BigDecimal getTotalAmount() {
        return Product.parsePrice(getText(totalAmount));
    }

    public CheckoutCompletePage placeOrder() {
        StepLogger.step("Place order");
        click(placeOrderButton);
        return new CheckoutCompletePage();
    }
}
