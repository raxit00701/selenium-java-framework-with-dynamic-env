// package factory/DriverFactory.java
package factory;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class DriverFactory {

    private static final ThreadLocal<WebDriver> TL_DRIVER = new ThreadLocal<>();

    private static ChromeOptions chromeOptions;
    private static FirefoxOptions firefoxOptions;
    private static EdgeOptions edgeOptions;

    // ✅ Manual driver paths
    private static final String EDGE_DRIVER_PATH = "C:\\msedgedriver.exe";
    // You can also add these if needed:
    // private static final String CHROME_DRIVER_PATH = "C:\\chromedriver.exe";
    // private static final String FIREFOX_DRIVER_PATH = "C:\\geckodriver.exe";

    // setters for custom options (called from BaseTest prior to init)
    public static void setChromeOptions(ChromeOptions options) {
        chromeOptions = options;
    }

    public static void setFirefoxOptions(FirefoxOptions options) {
        firefoxOptions = options;
    }

    public static void setEdgeOptions(EdgeOptions options) {
        edgeOptions = options;
    }

    public static WebDriver init(String browser) {
        if (TL_DRIVER.get() == null) {
            switch (browser.toLowerCase()) {
                case "chrome":
                    // System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
                    if (chromeOptions == null) {
                        chromeOptions = new ChromeOptions();
                        chromeOptions.addArguments("--start-maximized", "--incognito");
                    }
                    TL_DRIVER.set(new ChromeDriver(chromeOptions));
                    break;

                case "firefox":
                    // System.setProperty("webdriver.gecko.driver", FIREFOX_DRIVER_PATH);
                    if (firefoxOptions == null) {
                        firefoxOptions = new FirefoxOptions();
                        firefoxOptions.addArguments("-private");
                    }
                    TL_DRIVER.set(new FirefoxDriver(firefoxOptions));
                    break;

                case "edge":
                    // ✅ Manually set Edge driver path
                    System.setProperty("webdriver.edge.driver", EDGE_DRIVER_PATH);
                    if (edgeOptions == null) {
                        edgeOptions = new EdgeOptions();
                        edgeOptions.addArguments("--start-maximized", "--inprivate");
                    }
                    TL_DRIVER.set(new EdgeDriver(edgeOptions));
                    break;

                default:
                    throw new IllegalArgumentException("❌ Unsupported browser: " + browser);
            }
        }
        return getDriver();
    }

    public static WebDriver getDriver() {
        return TL_DRIVER.get();
    }

    // ✅ Safe getter (throws helpful error if null)
    public static WebDriver getActiveDriverOrThrow() {
        WebDriver driver = TL_DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("❌ No active WebDriver found. Make sure driver is initialized in BaseTest before using it.");
        }
        return driver;
    }

    public static void quitDriver() {
        if (TL_DRIVER.get() != null) {
            try {
                TL_DRIVER.get().quit();
            } finally {
                TL_DRIVER.remove();
            }
        }
    }
}
