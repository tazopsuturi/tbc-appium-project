package com.tbc.appium.driver;

import com.tbc.appium.config.ConfigReader;
import com.tbc.appium.exceptions.FrameworkException;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.SessionNotCreatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

/** Builds {@link AndroidDriver} sessions from the values in {@code config.properties}. */
public final class DriverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DriverFactory.class);

    private DriverFactory() {
    }

    public static AndroidDriver createAndroidDriver() {
        UiAutomator2Options options = buildOptions();
        try {
            AndroidDriver driver = new AndroidDriver(AppiumServerManager.getServerUrl(), options);
            LOG.info("Appium session {} created on device '{}'", driver.getSessionId(), options.getDeviceName().orElse("?"));
            return driver;
        } catch (SessionNotCreatedException e) {
            throw new FrameworkException("Could not create an Appium session. Check that an emulator/device is running "
                    + "(`adb devices`) and that the UiAutomator2 driver is installed.", e);
        }
    }

    private static UiAutomator2Options buildOptions() {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName(ConfigReader.getRequired("platform.name"))
                .setAutomationName(ConfigReader.getRequired("automation.name"))
                .setDeviceName(ConfigReader.getRequired("device.name"))
                .setAppPackage(ConfigReader.getRequired("app.package"))
                .setAppActivity(ConfigReader.getRequired("app.activity"))
                .setAppWaitActivity("*")
                .setAutoGrantPermissions(true)
                .setNoReset(false)
                .setNewCommandTimeout(Duration.ofSeconds(ConfigReader.getInt("timeout.new.command")));

        String udid = ConfigReader.get("device.udid");
        if (!udid.isEmpty()) {
            options.setUdid(udid);
        }

        String appPath = ConfigReader.get("app.path");
        if (!appPath.isEmpty()) {
            Path apk = Path.of(appPath).toAbsolutePath();
            if (Files.isRegularFile(apk)) {
                options.setApp(apk.toString());
            } else {
                LOG.warn("APK not found at '{}'; using the app already installed on the device", apk);
            }
        }
        return options;
    }
}
