package com.tbc.appium.tests;

import com.tbc.appium.driver.AppManager;
import com.tbc.appium.driver.AppiumServerManager;
import com.tbc.appium.driver.DriverManager;
import com.tbc.appium.pages.CatalogPage;
import com.tbc.appium.reporting.TestListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

@Listeners(TestListener.class)
public abstract class BaseTest {

    /** Catalog screen the app starts on; the entry point for every test. */
    protected CatalogPage catalog;

    @BeforeClass(alwaysRun = true)
    public void startSession() {
        DriverManager.startSession();
    }

    @BeforeMethod(alwaysRun = true)
    public void resetApp() {
        AppManager.resetApp();
        catalog = new CatalogPage();
    }

    @AfterClass(alwaysRun = true)
    public void quitSession() {
        DriverManager.quitSession();
    }

    @AfterSuite(alwaysRun = true)
    public void stopServer() {
        AppiumServerManager.stop();
    }
}
