package factory;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

/**
 * Centralized browser option builder that reads environment properties
 * from src/test/resources/config/<env>.properties
 * and applies -D system property overrides.
 *
 * Example usage:
 *   mvn clean test -Denv=qa -Dheadless=true -Dbrowser=chrome
 */
public class BrowserOptionsFactory {

    private static final String DEFAULT_ENV = "qa";
    private static final Properties ENV_PROPS = loadEnvProperties();

    // -----------------------------------------------
    // Chrome Options
    // -----------------------------------------------
    public static ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();

        boolean headless = getBoolean("headless", false);
        String windowSize = ENV_PROPS.getProperty("window.size", "1920,1080");
        String extraArgs = getString("chrome.args");
        boolean acceptCerts = getBoolean("acceptInsecureCerts", true);

        options.setAcceptInsecureCerts(acceptCerts);

        options.addArguments("--incognito", "--disable-extensions",
                "--disable-popup-blocking", "--disable-notifications",
                "--disable-infobars", "--no-default-browser-check",
                "--disable-save-password-bubble");

        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=" + windowSize);
        } else {
            options.addArguments("--start-maximized");
        }

        if (!extraArgs.isBlank()) {
            Arrays.stream(extraArgs.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(options::addArguments);
        }

        logSummary("Chrome", headless, acceptCerts, windowSize, extraArgs);
        return options;
    }

    // -----------------------------------------------
    // Firefox Options
    // -----------------------------------------------
    public static FirefoxOptions getFirefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();

        boolean headless = getBoolean("headless", false);
        boolean acceptCerts = getBoolean("acceptInsecureCerts", true);
        String extraArgs = getString("firefox.args");

        options.setAcceptInsecureCerts(acceptCerts);

        // basic clean setup
        options.addArguments("-private");
        options.addPreference("dom.webnotifications.enabled", false);
        options.addPreference("dom.push.enabled", false);
        options.addPreference("signon.rememberSignons", false);
        options.addPreference("extensions.enabled", false);

        if (headless) options.addArguments("-headless");

        if (!extraArgs.isBlank()) {
            Arrays.stream(extraArgs.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(options::addArguments);
        }

        logSummary("Firefox", headless, acceptCerts, null, extraArgs);
        return options;
    }

    // -----------------------------------------------
    // Edge Options
    // -----------------------------------------------
    public static EdgeOptions getEdgeOptions() {
        EdgeOptions options = new EdgeOptions();

        boolean headless = getBoolean("headless", false);
        boolean acceptCerts = getBoolean("acceptInsecureCerts", true);
        String windowSize = ENV_PROPS.getProperty("window.size", "1920,1080");
        String extraArgs = getString("edge.args");

        options.setAcceptInsecureCerts(acceptCerts);

        options.addArguments("--inprivate", "--disable-extensions",
                "--disable-popup-blocking", "--disable-notifications",
                "--disable-infobars", "--no-default-browser-check",
                "--disable-save-password-bubble");

        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=" + windowSize);
        } else {
            options.addArguments("--start-maximized");
        }

        if (!extraArgs.isBlank()) {
            Arrays.stream(extraArgs.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(options::addArguments);
        }

        logSummary("Edge", headless, acceptCerts, windowSize, extraArgs);
        return options;
    }

    // -----------------------------------------------
    // Helpers
    // -----------------------------------------------

    private static Properties loadEnvProperties() {
        String env = System.getProperty("env", DEFAULT_ENV).trim().toLowerCase();
        String resourcePath = "config/" + env + ".properties";

        Properties p = new Properties();
        try (InputStream is = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(resourcePath)) {

            if (is != null) {
                p.load(is);
                System.out.println("[BrowserOptionsFactory] Loaded environment: " + resourcePath);
            } else {
                System.err.println("[BrowserOptionsFactory] ⚠️ Could not find env file: " + resourcePath);
            }
        } catch (IOException e) {
            System.err.println("[BrowserOptionsFactory] ⚠️ Failed to load " + resourcePath + ": " + e.getMessage());
        }

        // System property overrides
        for (String key : new String[]{
                "headless", "window.size", "acceptInsecureCerts",
                "chrome.args", "firefox.args", "edge.args", "browser"
        }) {
            if (System.getProperty(key) != null) {
                p.setProperty(key, System.getProperty(key));
                System.out.println("[BrowserOptionsFactory] Overriding from -D" + key + "=" + System.getProperty(key));
            }
        }
        return p;
    }

    private static boolean getBoolean(String key, boolean fallback) {
        String sys = System.getProperty(key);
        if (sys != null) return Boolean.parseBoolean(sys);
        String val = ENV_PROPS.getProperty(key);
        return (val != null) ? Boolean.parseBoolean(val.trim()) : fallback;
    }

    private static String getString(String key) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) return sys.trim();
        String val = ENV_PROPS.getProperty(key);
        return (val == null) ? "" : val.trim();
    }

    private static void logSummary(String browser, boolean headless, boolean certs, String size, String args) {
        System.out.println(String.format(
                "[BrowserOptionsFactory] %sOptions -> headless=%s | acceptInsecureCerts=%s%s%s",
                browser,
                headless,
                certs,
                (size != null ? " | windowSize=" + size : ""),
                (!args.isBlank() ? " | extraArgs=" + args : "")
        ));
    }
}
