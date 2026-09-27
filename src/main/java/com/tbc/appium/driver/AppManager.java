package com.tbc.appium.driver;

import com.tbc.appium.config.ConfigReader;
import com.tbc.appium.exceptions.FrameworkException;
import com.tbc.appium.utils.SystemDialogHandler;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;

/** App life-cycle operations used to give every test a clean, predictable starting point. */
public final class AppManager {

    private static final Logger LOG = LoggerFactory.getLogger(AppManager.class);
    private static final By CATALOG_TITLE = By.id("productTV");

    private AppManager() {
    }

    /**
     * Stops the app, wipes its data (logged-in user, cart contents, sort order...) and starts it again
     * on the catalog screen.
     */
    public static void resetApp() {
        AndroidDriver driver = DriverManager.getDriver();
        String appPackage = ConfigReader.getRequired("app.package");
        LOG.info("Resetting app state for {}", appPackage);
        driver.terminateApp(appPackage);
        driver.executeScript("mobile: clearApp", Map.of("appId", appPackage));
        driver.activateApp(appPackage);
        waitForAppReady(driver);
    }

    /** Waits until the catalog is shown, dismissing the OS compatibility dialog if it pops up meanwhile. */
    public static void waitForAppReady(AndroidDriver driver) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("timeout.explicit") * 2L))
                    .pollingEvery(Duration.ofMillis(500))
                    .until(d -> {
                        SystemDialogHandler.dismissIfPresent(driver);
                        return !driver.findElements(CATALOG_TITLE).isEmpty();
                    });
        } catch (TimeoutException e) {
            throw new FrameworkException("The app did not reach the catalog screen after launch", e);
        }
    }
}
