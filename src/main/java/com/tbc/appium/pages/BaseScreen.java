package com.tbc.appium.pages;

import com.tbc.appium.pages.components.SideMenu;
import com.tbc.appium.reporting.StepLogger;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

/**
 * Base class for full app screens. Every screen shares the same toolbar: menu button, logo and cart icon
 * with an item-count badge.
 */
public abstract class BaseScreen extends BasePage {

    @AndroidFindBy(accessibility = "View menu")
    private WebElement menuButton;

    @AndroidFindBy(id = "cartRL")
    private WebElement cartButton;

    @AndroidFindBy(id = "cartTV")
    private WebElement cartBadge;

    public SideMenu openMenu() {
        StepLogger.step("Open side menu");
        click(menuButton);
        SideMenu menu = new SideMenu();
        menu.waitUntilOpen();
        return menu;
    }

    public CartPage openCart() {
        StepLogger.step("Open cart");
        click(cartButton);
        return new CartPage();
    }

    public void pressBackButton() {
        StepLogger.step("Press system Back");
        driver.navigate().back();
    }

    public int getCartBadgeCount() {
        return isVisible(cartBadge) ? Integer.parseInt(cartBadge.getText().trim()) : 0;
    }
}
