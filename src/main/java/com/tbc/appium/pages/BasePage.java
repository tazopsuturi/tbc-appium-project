package com.tbc.appium.pages;

import com.tbc.appium.config.ConfigReader;
import com.tbc.appium.driver.DriverManager;
import com.tbc.appium.utils.SystemDialogHandler;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Base class for all page objects and components.
 */
public abstract class BasePage {

    protected final AndroidDriver driver;
    protected final WebDriverWait wait;

    protected BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("timeout.explicit")));
        // Zero lookup timeout: synchronization is done with explicit waits.
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ZERO), this);
    }

    /** Returns {@code true} once the screen's identifying element is visible. */
    public abstract boolean isDisplayed();

    protected WebElement waitForVisible(WebElement element) {
        return withDialogRetry(() -> wait.until(ExpectedConditions.visibilityOf(element)));
    }

    protected WebElement waitForVisible(By locator) {
        return withDialogRetry(() -> wait.until(ExpectedConditions.visibilityOfElementLocated(locator)));
    }

    protected boolean waitForInvisible(WebElement element) {
        return wait.until(d -> !isVisible(element));
    }

    /** Waits for the screen to open; returns {@code false} (instead of throwing) if it does not. */
    protected boolean isOpened(WebElement identifyingElement) {
        try {
            waitForVisible(identifyingElement);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected void click(WebElement element) {
        SystemDialogHandler.dismissIfPresent(driver);
        withStaleRetry(() -> {
            waitForVisible(element).click();
            return null;
        });
    }

    protected void type(WebElement element, String text) {
        SystemDialogHandler.dismissIfPresent(driver);
        WebElement field = waitForVisible(element);
        field.clear();
        if (text != null && !text.isEmpty()) {
            field.sendKeys(text);
        }
        hideKeyboard();
    }

    protected String getText(WebElement element) {
        return withStaleRetry(() -> waitForVisible(element).getText());
    }

    protected boolean isVisible(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    protected void hideKeyboard() {
        if (driver.isKeyboardShown()) {
            driver.hideKeyboard();
        }
    }

    protected WebElement scrollToText(String text) {
        return driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true).instance(0))"
                        + ".scrollIntoView(new UiSelector().text(\"" + text.replace("\"", "\\\"") + "\"))"));
    }

    private <T> T withStaleRetry(Supplier<T> action) {
        try {
            return action.get();
        } catch (StaleElementReferenceException e) {
            return action.get();
        }
    }

    private <T> T withDialogRetry(Supplier<T> action) {
        try {
            return action.get();
        } catch (TimeoutException e) {
            if (SystemDialogHandler.dismissIfPresent(driver)) {
                return action.get();
            }
            throw e;
        }
    }
}
