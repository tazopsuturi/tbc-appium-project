package com.tbc.appium.tests;

import com.tbc.appium.data.TestData;
import com.tbc.appium.models.User;
import com.tbc.appium.pages.CatalogPage;
import com.tbc.appium.pages.LoginPage;
import com.tbc.appium.pages.components.SideMenu;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {

    @Test(description = "A valid user can log in and the menu then offers 'Log Out' instead of 'Log In'")
    public void validUserCanLogIn() {
        CatalogPage catalogAfterLogin = catalog.openMenu().goToLogin().loginAs(TestData.STANDARD_USER);

        Assert.assertTrue(catalogAfterLogin.isDisplayed(), "Catalog should be shown after a successful login");
        SideMenu menu = catalogAfterLogin.openMenu();
        Assert.assertTrue(menu.isLogoutItemVisible(), "'Log Out' should be offered to a logged-in user");
        Assert.assertFalse(menu.isLoginItemVisible(), "'Log In' should not be offered to a logged-in user");
    }

    @Test(description = "A logged-in user can log out after confirming the dialog")
    public void userCanLogOut() {
        SideMenu menu = catalog.openMenu().goToLogin().loginAs(TestData.STANDARD_USER).openMenu();

        Assert.assertEquals(menu.startLogout(), TestData.LOGOUT_CONFIRMATION, "Logout confirmation message");
        LoginPage loginPage = menu.confirmLogout();

        Assert.assertTrue(loginPage.isDisplayed(), "Login screen should be shown after logout");
        Assert.assertTrue(loginPage.openMenu().isLoginItemVisible(), "'Log In' should be offered again after logout");
    }

    @Test(description = "Cancelling the logout dialog keeps the user logged in")
    public void cancellingLogoutKeepsUserLoggedIn() {
        SideMenu menu = catalog.openMenu().goToLogin().loginAs(TestData.STANDARD_USER).openMenu();

        menu.startLogout();
        menu.cancelLogout();

        Assert.assertTrue(catalog.openMenu().isLogoutItemVisible(), "User should still be logged in after cancelling logout");
    }

    @Test(description = "Tapping a demo account fills in its username")
    public void demoAccountAutofillsUsername() {
        LoginPage loginPage = catalog.openMenu().goToLogin().selectFirstDemoAccount();

        Assert.assertEquals(loginPage.getUsernameFieldText(), TestData.STANDARD_USER.username(), "Username field value");
    }

    @DataProvider(name = "invalidLogins")
    public Object[][] invalidLogins() {
        return new Object[][]{
                // user, expected username error, expected password error
                {new User("", ""), TestData.USERNAME_REQUIRED, null},
                {new User(TestData.STANDARD_USER.username(), ""), null, TestData.PASSWORD_REQUIRED},
                {TestData.LOCKED_OUT_USER, null, TestData.USER_LOCKED_OUT},
        };
    }

    @Test(dataProvider = "invalidLogins", description = "Invalid logins are rejected with a validation message")
    public void invalidLoginShowsValidationMessage(User user, String expectedUsernameError, String expectedPasswordError) {
        LoginPage loginPage = catalog.openMenu().goToLogin();

        loginPage.submitCredentials(user);

        if (expectedUsernameError != null) {
            Assert.assertEquals(loginPage.getUsernameError(), expectedUsernameError, "Username validation message");
        }
        if (expectedPasswordError != null) {
            Assert.assertFalse(loginPage.isUsernameErrorDisplayed(), "No username error expected");
            Assert.assertEquals(loginPage.getPasswordError(), expectedPasswordError, "Password validation message");
        }
        Assert.assertTrue(loginPage.isDisplayed(), "User should stay on the login screen");
    }
}
