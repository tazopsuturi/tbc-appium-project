package com.tbc.appium.pages;

import com.tbc.appium.models.Product;
import com.tbc.appium.reporting.StepLogger;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.util.List;

/** "My Cart" screen, including its empty state. */
public class CartPage extends BaseScreen {

    @AndroidFindBy(uiAutomator = "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/productTV\").text(\"My Cart\")")
    private WebElement title;

    @AndroidFindBy(id = "titleTV")
    private List<WebElement> itemNames;

    @AndroidFindBy(id = "noTV")
    private List<WebElement> itemQuantities;

    @AndroidFindBy(accessibility = "Removes product from cart")
    private List<WebElement> removeButtons;

    @AndroidFindBy(id = "itemsTV")
    private WebElement totalItems;

    @AndroidFindBy(id = "totalPriceTV")
    private WebElement totalPrice;

    @AndroidFindBy(accessibility = "Confirms products for checkout")
    private WebElement proceedToCheckoutButton;

    // Empty-cart state
    @AndroidFindBy(id = "noItemTitleTV")
    private WebElement emptyCartTitle;

    @AndroidFindBy(id = "shoppingBt")
    private WebElement goShoppingButton;

    public CartPage() {
        super();
    }

    @Override
    public boolean isDisplayed() {
        return isOpened(title);
    }

    public boolean isEmptyStateDisplayed() {
        return isOpened(emptyCartTitle);
    }

    public String getEmptyCartTitle() {
        return getText(emptyCartTitle);
    }

    public List<String> getItemNames() {
        waitForVisible(title);
        return itemNames.stream().map(WebElement::getText).toList();
    }

    /** Quantity of the n-th (0-based) item in the cart. */
    public int getItemQuantity(int index) {
        waitForVisible(title);
        return Integer.parseInt(itemQuantities.get(index).getText().trim());
    }

    /** Total number of items as shown in the footer, e.g. "3 Items" -> 3. */
    public int getTotalItemCount() {
        return Integer.parseInt(getText(totalItems).replaceAll("\\D", ""));
    }

    public BigDecimal getTotalPrice() {
        return Product.parsePrice(getText(totalPrice));
    }

    public CartPage removeFirstItem() {
        StepLogger.step("Remove first item from cart");
        waitForVisible(title);
        click(removeButtons.get(0));
        return this;
    }

    public CatalogPage goShopping() {
        StepLogger.step("Tap 'Go Shopping'");
        click(goShoppingButton);
        return new CatalogPage();
    }

    /** Proceeds to checkout as a guest: the app asks the user to log in first. */
    public LoginPage proceedToCheckoutAsGuest() {
        StepLogger.step("Proceed to checkout (not logged in)");
        click(proceedToCheckoutButton);
        return new LoginPage();
    }
}
