package com.tbc.appium.pages;

import com.tbc.appium.models.Product;
import com.tbc.appium.reporting.StepLogger;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

/** Product details screen: price, colour selection, quantity and "Add to cart". */
public class ProductDetailsPage extends BaseScreen {

    @AndroidFindBy(id = "productTV")
    private WebElement productName;

    @AndroidFindBy(id = "priceTV")
    private WebElement price;

    @AndroidFindBy(accessibility = "Increase item quantity")
    private WebElement increaseQuantityButton;

    @AndroidFindBy(accessibility = "Decrease item quantity")
    private WebElement decreaseQuantityButton;

    @AndroidFindBy(id = "noTV")
    private WebElement quantity;

    @AndroidFindBy(accessibility = "Tap to add product to cart")
    private WebElement addToCartButton;

    public ProductDetailsPage() {
        super();
    }

    @Override
    public boolean isDisplayed() {
        return isOpened(addToCartButton);
    }

    public String getProductName() {
        // The catalog title uses the same resource id, so make sure the details screen has replaced it
        waitForVisible(addToCartButton);
        return getText(productName);
    }

    public Product getProduct() {
        String name = getProductName();
        return new Product(name, Product.parsePrice(getText(price)));
    }

    public ProductDetailsPage selectColour(String colour) {
        StepLogger.step("Select colour '%s'", colour);
        click(driver.findElement(AppiumBy.accessibilityId(colour + " color")));
        return this;
    }

    public ProductDetailsPage increaseQuantity(int times) {
        StepLogger.step("Increase quantity %d time(s)", times);
        for (int i = 0; i < times; i++) {
            click(increaseQuantityButton);
        }
        return this;
    }

    public ProductDetailsPage decreaseQuantity() {
        StepLogger.step("Decrease quantity");
        click(decreaseQuantityButton);
        return this;
    }

    public int getQuantity() {
        return Integer.parseInt(getText(quantity).trim());
    }

    public ProductDetailsPage addToCart() {
        StepLogger.step("Add '%s' to cart", getProductName());
        click(addToCartButton);
        return this;
    }
}
