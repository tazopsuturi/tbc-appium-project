package com.tbc.appium.driver;

import com.tbc.appium.exceptions.FrameworkException;
import io.appium.java_client.android.AndroidDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds the Appium driver per thread so page objects and listeners can reach the current session
 * without passing it around, and so the suite stays safe if run in parallel on several devices.
 */
public final class DriverManager {

    private static final Logger LOG = LoggerFactory.getLogger(DriverManager.class);
    private static final ThreadLocal<AndroidDriver> DRIVER = new ThreadLocal<>();

    public static AndroidDriver getDriver() {
        AndroidDriver driver = DRIVER.get();
        if (driver == null) {
            throw new FrameworkException("No Appium session for the current thread. Was DriverManager.startSession() called?");
        }
        return driver;
    }

    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }

    public static void startSession() {
        if (DRIVER.get() == null) {
            DRIVER.set(DriverFactory.createAndroidDriver());
        }
    }

    public static void quitSession() {
        AndroidDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (RuntimeException e) {
                LOG.warn("Error while closing Appium session: {}", e.getMessage());
            } finally {
                DRIVER.remove();
            }
        }
    }
}
