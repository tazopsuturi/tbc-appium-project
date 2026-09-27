package com.tbc.appium.pages;

import com.tbc.appium.models.User;
import com.tbc.appium.reporting.StepLogger;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

/** Login screen with username/password fields and a list of demo accounts. */
public class LoginPage extends BaseScreen {

    @AndroidFindBy(id = "loginTV")
    private WebElement title;

    @AndroidFindBy(id = "nameET")
    private WebElement usernameField;

    @AndroidFindBy(id = "passwordET")
    private WebElement passwordField;

    @AndroidFindBy(accessibility = "Tap to login with given credentials")
    private WebElement loginButton;

    @AndroidFindBy(id = "nameErrorTV")
    private WebElement usernameError;

    @AndroidFindBy(id = "passwordErrorTV")
    private WebElement passwordError;

    /** First demo account in the list ("bod@example.com"); tapping it fills both fields. */
    @AndroidFindBy(id = "username1TV")
    private WebElement firstDemoUsername;

    public LoginPage() {
        super();
    }

    @Override
    public boolean isDisplayed() {
        return isOpened(loginButton);
    }

    public String getTitle() {
        return getText(title);
    }

    public LoginPage enterUsername(String username) {
        StepLogger.step("Enter username '%s'", username);
        type(usernameField, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        StepLogger.step("Enter password");
        type(passwordField, password);
        return this;
    }

    public void tapLogin() {
        StepLogger.step("Tap Login");
        click(loginButton);
    }

    /** Submits credentials. The caller decides which screen to expect next (catalog, checkout or an error). */
    public void submitCredentials(User user) {
        enterUsername(user.username());
        enterPassword(user.password());
        tapLogin();
    }

    /** Logs in from the menu entry point; the app returns to the catalog. */
    public CatalogPage loginAs(User user) {
        submitCredentials(user);
        return new CatalogPage();
    }

    /** Logs in after being redirected from the cart; the app continues to the shipping address step. */
    public CheckoutAddressPage loginAndContinueToCheckout(User user) {
        submitCredentials(user);
        return new CheckoutAddressPage();
    }

    public LoginPage selectFirstDemoAccount() {
        StepLogger.step("Select first demo account from the list");
        click(firstDemoUsername);
        return this;
    }

    public String getUsernameFieldText() {
        return getText(usernameField);
    }

    public String getUsernameError() {
        return getText(usernameError);
    }

    public String getPasswordError() {
        return getText(passwordError);
    }

    public boolean isUsernameErrorDisplayed() {
        return isVisible(usernameError);
    }
}
