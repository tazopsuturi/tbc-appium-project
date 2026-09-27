package com.tbc.appium.reporting;

import com.aventstack.extentreports.ExtentTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Logs a test step to the console and to the current test's entry in the HTML report. */
public final class StepLogger {

    private static final Logger LOG = LoggerFactory.getLogger("STEP");

    private StepLogger() {
    }

    public static void step(String message, Object... args) {
        String text = String.format(message, args);
        LOG.info(text);
        ExtentTest test = ExtentReportManager.getCurrentTest();
        if (test != null) {
            test.info(text);
        }
    }
}
