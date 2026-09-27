package com.tbc.appium.reporting;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.tbc.appium.driver.DriverManager;
import org.openqa.selenium.OutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Arrays;

/**
 * TestNG listener that feeds the Extent HTML report: one entry per test (grouped by class),
 * a screenshot on failure, and a report flush when the suite finishes.
 */
public class TestListener implements ITestListener, ISuiteListener {

    private static final Logger LOG = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        String name = result.getMethod().getMethodName();
        if (result.getParameters().length > 0) {
            name += " " + Arrays.toString(result.getParameters());
        }
        ExtentTest test = ExtentReportManager.createTest(name, result.getMethod().getDescription());
        test.assignCategory(result.getTestClass().getRealClass().getSimpleName());
        LOG.info("===== START {} =====", name);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentReportManager.getCurrentTest().pass("Test passed");
        LOG.info("===== PASS {} =====", result.getMethod().getMethodName());
        ExtentReportManager.removeCurrentTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = ExtentReportManager.getCurrentTest();
        test.fail(result.getThrowable());
        attachScreenshot(test);
        LOG.error("===== FAIL {} =====", result.getMethod().getMethodName(), result.getThrowable());
        ExtentReportManager.removeCurrentTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = ExtentReportManager.getCurrentTest();
        if (test == null) {
            // Skipped before start, e.g. because a configuration method failed
            test = ExtentReportManager.createTest(result.getMethod().getMethodName(), result.getMethod().getDescription());
        }
        test.skip(result.getThrowable() != null ? result.getThrowable().toString() : "Test skipped");
        LOG.warn("===== SKIP {} =====", result.getMethod().getMethodName());
        ExtentReportManager.removeCurrentTest();
    }

    @Override
    public void onFinish(ISuite suite) {
        ExtentReportManager.flush();
    }

    private void attachScreenshot(ExtentTest test) {
        if (!DriverManager.hasDriver()) {
            return;
        }
        try {
            String base64 = DriverManager.getDriver().getScreenshotAs(OutputType.BASE64);
            test.fail("Screenshot at failure", MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
        } catch (RuntimeException e) {
            LOG.warn("Could not capture failure screenshot: {}", e.getMessage());
        }
    }
}
