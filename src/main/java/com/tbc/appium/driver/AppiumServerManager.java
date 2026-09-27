package com.tbc.appium.driver;

import com.tbc.appium.config.ConfigReader;
import com.tbc.appium.exceptions.FrameworkException;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;

/**
 * Provides the Appium server URL: either an externally started server ({@code appium.server.url})
 * or a local server started on a free port for the duration of the test run.
 */
public final class AppiumServerManager {

    private static final Logger LOG = LoggerFactory.getLogger(AppiumServerManager.class);
    private static AppiumDriverLocalService service;

    private AppiumServerManager() {
    }

    public static synchronized URL getServerUrl() {
        String externalUrl = ConfigReader.get("appium.server.url");
        if (!externalUrl.isEmpty()) {
            return toUrl(externalUrl);
        }
        if (service == null || !service.isRunning()) {
            start();
        }
        return service.getUrl();
    }

    private static void start() {
        LOG.info("Starting local Appium server...");
        try {
            service = new AppiumServiceBuilder()
                    .usingAnyFreePort()
                    .withArgument(GeneralServerFlag.LOG_LEVEL, "error")
                    .withTimeout(Duration.ofSeconds(ConfigReader.getInt("timeout.server.startup")))
                    .build();
            service.start();
        } catch (RuntimeException e) {
            throw new FrameworkException("Could not start a local Appium server. Make sure Node.js and Appium 2+/3 are installed "
                    + "(npm i -g appium && appium driver install uiautomator2), or start Appium yourself and set "
                    + "'appium.server.url'.", e);
        }
        LOG.info("Appium server started at {}", service.getUrl());
    }

    public static synchronized void stop() {
        if (service != null && service.isRunning()) {
            service.stop();
            LOG.info("Local Appium server stopped");
        }
        service = null;
    }

    private static URL toUrl(String url) {
        try {
            return URI.create(url).toURL();
        } catch (IllegalArgumentException | MalformedURLException e) {
            throw new FrameworkException("Invalid 'appium.server.url': " + url, e);
        }
    }
}
