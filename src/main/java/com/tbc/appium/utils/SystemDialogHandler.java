package com.tbc.appium.utils;

import com.tbc.appium.config.ConfigReader;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class SystemDialogHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SystemDialogHandler.class);

    private static final By COMPAT_DIALOG_TITLE = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"android:id/alertTitle\").textContains(\"App Compatibility\")");
    private static final By DONT_SHOW_AGAIN_BUTTON = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"android:id/button1\").text(\"Don't Show Again\")");
    private static final By OK_BUTTON = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"android:id/button2\").text(\"OK\")");

    private SystemDialogHandler() {
    }

    /**
     * Dismisses the compatibility dialog if it is currently shown. Never throws: a failure here must not
     * mask the real outcome of a test step.
     */
    public static boolean dismissIfPresent(AndroidDriver driver) {
        try {
            if (driver.findElements(COMPAT_DIALOG_TITLE).isEmpty()) {
                return false;
            }
            By button = ConfigReader.getBoolean("compat.dialog.suppress") ? DONT_SHOW_AGAIN_BUTTON : OK_BUTTON;
            List<WebElement> buttons = driver.findElements(button);
            if (buttons.isEmpty()) {
                buttons = driver.findElements(OK_BUTTON);
            }
            if (!buttons.isEmpty()) {
                buttons.get(0).click();
                LOG.info("Dismissed 'Android App Compatibility' system dialog");
                return true;
            }
        } catch (WebDriverException e) {
            LOG.debug("Could not check/dismiss system dialog: {}", e.getMessage());
        }
        return false;
    }
}
