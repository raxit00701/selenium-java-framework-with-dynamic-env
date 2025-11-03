package base;

import factory.DriverFactory;
import factory.BrowserOptionsFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.SkipException;
import org.testng.annotations.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

public class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    private String browser = "chrome";
    private final long timeout = 60;

    // --- Environment configuration ---
    protected static Properties config;
    protected String baseUrl;

    @Parameters("browser")
    @BeforeClass(alwaysRun = true)
    public void setUp(@Optional("chrome") String browser) {
        this.browser = (browser == null || browser.isBlank()) ? "chrome" : browser.toLowerCase();

        // --- Load environment properties before starting driver ---
        loadEnvironmentConfig();

        try {
            startDriverIfNeeded(this.browser);
            driver.manage().deleteAllCookies();
            try {
                driver.manage().window().maximize();
            } catch (Exception e) {
                System.out.println("⚠️ Window maximize skipped (headless or remote).");
            }

            // --- Always use baseUrl from env file ---
            if (baseUrl == null || baseUrl.isBlank()) {
                throw new SkipException("❌ baseUrl not defined in environment properties file.");
            }

            driver.get(baseUrl);

        } catch (Exception e) {
            throw new SkipException("❌ Driver initialization failed in @BeforeClass: " + e.getMessage());
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void resetState() {
        try {
            if (DriverFactory.getDriver() == null) {
                startDriverIfNeeded(this.browser);
            } else {
                this.driver = DriverFactory.getDriver();
                this.wait = new WebDriverWait(this.driver, Duration.ofSeconds(timeout));
            }

            driver.manage().deleteAllCookies();

            if (baseUrl == null || baseUrl.isBlank()) {
                throw new SkipException("❌ baseUrl not defined in environment properties file.");
            }

            driver.get(baseUrl);

        } catch (Exception e) {
            throw new SkipException("❌ Could not prepare driver in @BeforeMethod: " + e.getMessage());
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (DriverFactory.getDriver() != null) {
            DriverFactory.quitDriver();
            System.out.println("🔻 Browser closed after all tests in class.");
        }
        driver = null;
        wait = null;
    }

    // --- Load environment configuration from classpath: src/test/resources/config/*.properties ---
    private void loadEnvironmentConfig() {
        String env = System.getProperty("env", "qa").trim().toLowerCase();
        String resourcePath = "config/" + env + ".properties";

        config = new Properties();
        try (InputStream is = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(resourcePath)) {

            if (is == null) {
                // resource not found on classpath
                System.err.println("⚠️ Could not find environment file on classpath: src/test/resources/" + resourcePath);
                baseUrl = null;
                return;
            }

            config.load(is);
            baseUrl = config.getProperty("baseUrl");
            System.out.println("🌐 Loaded environment from classpath: " + resourcePath + " | baseUrl=" + baseUrl);

        } catch (IOException e) {
            System.err.println("⚠️ Error loading environment file from classpath: " + resourcePath + " (" + e.getMessage() + ")");
            baseUrl = null;
        }
    }

    // --- Existing driver startup helper (unchanged) ---
    private void startDriverIfNeeded(String browser) {
        if (DriverFactory.getDriver() != null) {
            this.driver = DriverFactory.getDriver();
            this.wait = new WebDriverWait(this.driver, Duration.ofSeconds(timeout));
            return;
        }

        switch (browser.toLowerCase()) {
            case "chrome":
                DriverFactory.setChromeOptions(BrowserOptionsFactory.getChromeOptions());
                break;
            case "firefox":
                DriverFactory.setFirefoxOptions(BrowserOptionsFactory.getFirefoxOptions());
                break;
            case "edge":
                DriverFactory.setEdgeOptions(BrowserOptionsFactory.getEdgeOptions());
                break;
            default:
                throw new IllegalArgumentException("❌ Browser not supported: " + browser);
        }

        this.driver = DriverFactory.init(browser);
        this.wait = new WebDriverWait(this.driver, Duration.ofSeconds(timeout));
    }

    // --- ✅ Public getter for config (for TestListener) ---
    public static Properties getConfig() {
        return config;
    }
}
