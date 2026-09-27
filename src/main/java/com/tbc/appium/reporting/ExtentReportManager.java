package com.tbc.appium.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.tbc.appium.config.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ExtentReportManager {

    private static final Logger LOG = LoggerFactory.getLogger(ExtentReportManager.class);
    private static final ThreadLocal<ExtentTest> CURRENT_TEST = new ThreadLocal<>();

    private static ExtentReports extent;
    private static Path reportFile;

    private ExtentReportManager() {
    }

    public static synchronized ExtentReports getReports() {
        if (extent == null) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            reportFile = Path.of(ConfigReader.getRequired("report.dir"), "TestReport_" + timestamp + ".html").toAbsolutePath();

            ExtentSparkReporter spark = new ExtentSparkReporter(reportFile.toString());
            spark.config().setDocumentTitle(ConfigReader.get("report.title"));
            spark.config().setReportName(ConfigReader.get("report.title"));
            spark.config().setTheme(Theme.STANDARD);
            spark.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Platform", ConfigReader.get("platform.name"));
            extent.setSystemInfo("Automation", ConfigReader.get("automation.name"));
            extent.setSystemInfo("App package", ConfigReader.get("app.package"));
            extent.setSystemInfo("Java", System.getProperty("java.version"));
            extent.setSystemInfo("OS", System.getProperty("os.name"));
        }
        return extent;
    }

    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
            LOG.info("Test report written to {}", reportFile.toUri());
        }
    }

    public static ExtentTest createTest(String name, String description) {
        ExtentTest test = getReports().createTest(name, description);
        CURRENT_TEST.set(test);
        return test;
    }

    public static ExtentTest getCurrentTest() {
        return CURRENT_TEST.get();
    }

    public static void removeCurrentTest() {
        CURRENT_TEST.remove();
    }
}
