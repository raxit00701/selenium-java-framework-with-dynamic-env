package reports;

import factory.DriverFactory;
import io.qameta.allure.Allure;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TestListener implements ITestListener {

    private static final Set<String> detectedBrowsers = ConcurrentHashMap.newKeySet();

    private WebDriver getDriverSafe() {
        try { return DriverFactory.getActiveDriverOrThrow(); }
        catch (Throwable ignore) {
            try { return DriverFactory.getDriver(); }
            catch (Throwable t) { return null; }
        }
    }

    private void attachBrowserConsole(WebDriver driver) {
        try {
            if (driver == null) {
                MediaUtil.attachText("Browser Console", "(driver is null)");
                return;
            }
            LogEntries entries = driver.manage().logs().get(LogType.BROWSER);
            StringBuilder sb = new StringBuilder();
            for (LogEntry e : entries) {
                sb.append("[").append(e.getLevel()).append("] ")
                        .append(e.getTimestamp()).append(" : ")
                        .append(e.getMessage()).append("\n");
            }
            MediaUtil.attachText("Browser Console",
                    sb.length() == 0 ? "(no console logs)" : sb.toString());
        } catch (Throwable t) {
            MediaUtil.attachText("Browser Console", "Unavailable: " + t.getMessage());
        }
    }

    /** ✅ Dynamic environment based on -Denv (default: qa) */
    private synchronized void updateEnvironmentFile(WebDriver driver) {
        try {
            String projectDir = System.getProperty("user.dir");
            Path target = Paths.get(projectDir, "allure-results", "environment.properties");

            Properties props = new Properties();
            if (Files.exists(target)) {
                try (InputStream in = Files.newInputStream(target)) {
                    props.load(in);
                }
            }

            // --- NEW: dynamic env ---
            String env = System.getProperty("env", "qa").toUpperCase(Locale.ROOT);

            if (driver != null) {
                try {
                    Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
                    String browserKey = caps.getBrowserName() + "-" + caps.getBrowserVersion();
                    if (detectedBrowsers.add(browserKey)) {
                        props.setProperty("Browser." + detectedBrowsers.size(), browserKey);
                        props.setProperty("Platform." + detectedBrowsers.size(), String.valueOf(caps.getPlatformName()));
                    }
                } catch (Exception e) {
                    props.setProperty("Browser.Unknown", "Error reading capabilities");
                }
            }

            props.setProperty("Creator", "Raxit Sharma");
            props.putIfAbsent("OS.Version", System.getProperty("os.version"));
            props.putIfAbsent("Java.Version", System.getProperty("java.version"));
            props.putIfAbsent("Environment", env);

            // --- Base URL from config if loaded ---
            try {
                String baseUrl = base.BaseTest.getConfig() != null
                        ? base.BaseTest.getConfig().getProperty("baseUrl", "https://jpetstore.aspectran.com")
                        : "https://jpetstore.aspectran.com";
                props.putIfAbsent("BaseURL", baseUrl);
            } catch (Throwable t) {
                props.putIfAbsent("BaseURL", "https://jpetstore.aspectran.com");
            }

            Files.createDirectories(target.getParent());
            try (OutputStream out = Files.newOutputStream(target)) {
                props.store(out, "Allure Environment Settings (Dynamic)");
            }

            MediaUtil.attachText("Environment Info (Updated)", props.toString());

        } catch (Exception e) {
            System.err.println("⚠️ Failed to update environment.properties: " + e.getMessage());
        }
    }

    // --- TestNG lifecycle (unchanged) ---
    @Override
    public void onStart(ITestContext context) {
        MediaUtil.attachText("Suite Started", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        MediaUtil.attachText("Suite Finished", context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        String params = (result.getParameters() != null && result.getParameters().length > 0)
                ? Arrays.toString(result.getParameters()) : "(no params)";
        Allure.parameter("method", result.getMethod().getMethodName());
        MediaUtil.attachText("Test Started",
                result.getMethod().getQualifiedName() + " " + params);

        MediaUtil.startVideo(result.getMethod().getQualifiedName());

        updateEnvironmentFile(getDriverSafe());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        Allure.label("status", "passed");
        MediaUtil.attachText("Test Passed", result.getMethod().getQualifiedName());
        MediaUtil.stopVideo(false);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Allure.label("status", "failed");
        WebDriver driver = getDriverSafe();

        String err = (result.getThrowable() == null) ? "Unknown error" : result.getThrowable().toString();
        MediaUtil.attachText("Test Failed", err);
        MediaUtil.attachUrl(driver);
        MediaUtil.attachFailureScreenshot(driver, result.getMethod().getMethodName());
        MediaUtil.attachPageSource(driver);
        attachBrowserConsole(driver);
        MediaUtil.stopVideo(true);

        updateEnvironmentFile(driver);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Allure.label("status", "skipped");
        MediaUtil.attachText("Test Skipped", result.getMethod().getMethodName());
        MediaUtil.stopVideo(false);
    }
}
