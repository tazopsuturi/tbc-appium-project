package com.tbc.appium.pages.components;

import com.tbc.appium.pages.BasePage;
import com.tbc.appium.pages.CatalogPage;
import com.tbc.appium.pages.LoginPage;
import com.tbc.appium.reporting.StepLogger;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

/** Navigation drawer opened from the toolbar menu button. */
public class SideMenu extends BasePage {

    @AndroidFindBy(id = "menuRV")
    private WebElement menuList;

    @AndroidFindBy(uiAutomator = "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/itemTV\").text(\"Catalog\")")
    private WebElement catalogItem;

    @AndroidFindBy(accessibility = "Login Menu Item")
    private WebElement loginItem;

    @AndroidFindBy(accessibility = "Logout Menu Item")
    private WebElement logoutItem;

    // Logout confirmation dialog (app-owned AlertDialog)
    @AndroidFindBy(id = "android:id/message")
    private WebElement logoutDialogMessage;

    @AndroidFindBy(id = "android:id/button1")
    private WebElement logoutConfirmButton;

    @AndroidFindBy(id = "android:id/button2")
    private WebElement logoutCancelButton;

    @Override
    public boolean isDisplayed() {
        return isOpened(menuList);
    }

    public void waitUntilOpen() {
        waitForVisible(menuList);
    }

    public CatalogPage goToCatalog() {
        StepLogger.step("Menu: Catalog");
        click(catalogItem);
        return new CatalogPage();
    }

    public LoginPage goToLogin() {
        StepLogger.step("Menu: Log In");
        click(loginItem);
        return new LoginPage();
    }

    public boolean isLoginItemVisible() {
        return isVisible(loginItem);
    }

    public boolean isLogoutItemVisible() {
        return isVisible(logoutItem);
    }

    /** Taps "Log Out" and returns the confirmation dialog text. */
    public String startLogout() {
        StepLogger.step("Menu: Log Out");
        click(logoutItem);
        return getText(logoutDialogMessage);
    }

    /** Confirms the logout dialog; the app then shows the login screen. */
    public LoginPage confirmLogout() {
        StepLogger.step("Confirm logout");
        click(logoutConfirmButton);
        return new LoginPage();
    }

    public void cancelLogout() {
        StepLogger.step("Cancel logout");
        click(logoutCancelButton);
    }
}
