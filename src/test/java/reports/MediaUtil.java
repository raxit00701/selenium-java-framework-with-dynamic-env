package reports;

import io.qameta.allure.Allure;
import org.monte.media.Format;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public final class MediaUtil {

    private MediaUtil() {}

    // ---------- Paths ----------
    private static final Path SCREEN_DIR = Paths.get("screenshots");  // project-root/screenshots
    private static final Path VIDEO_DIR  = Paths.get("videos");       // project-root/videos

    // ---------- Video (Monte) ----------
    private static final ThreadLocal<ScreenRecorder> TL_RECORDER   = new ThreadLocal<>();
    private static final ThreadLocal<File>           TL_VIDEO_FILE = new ThreadLocal<>();

    /** Call at @Test start. Always starts recording, but you decide later to attach or delete. */
    public static void startVideo(String testQualifiedName) {
        try {
            Files.createDirectories(VIDEO_DIR);

            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            String safeName = sanitize(testQualifiedName == null ? "test" : testQualifiedName);
            File outFile = VIDEO_DIR.resolve(safeName + "__" + ts + ".mov").toFile();
            TL_VIDEO_FILE.set(outFile);

            GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().getDefaultConfiguration();

            ScreenRecorder recorder = new ScreenRecorder(
                    gc,
                    gc.getBounds(),
                    new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_QUICKTIME),
                    new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_QUICKTIME_ANIMATION,
                            CompressorNameKey, ENCODING_QUICKTIME_ANIMATION,
                            DepthKey, 24, FrameRateKey, Rational.valueOf(15),
                            QualityKey, 0.7f, KeyFrameIntervalKey, 15 * 60),
                    new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black",
                            FrameRateKey, Rational.valueOf(30)),
                    null,
                    outFile.getParentFile()) {
                @Override
                protected File createMovieFile(Format fileFormat) {
                    // Force Monte to use our deterministic file name/path
                    return outFile;
                }
            };

            recorder.start();
            TL_RECORDER.set(recorder);
            attachText("Video", "Recording started: " + outFile.getAbsolutePath());

        } catch (Exception e) {
            attachText("Video error", "Start failed: " + e.getMessage());
        }
    }

    /**
     * Call at @Test end.
     * @param attach If true (i.e., on failure) attach the recording to Allure; else delete the file.
     */
    public static void stopVideo(boolean attach) {
        try {
            ScreenRecorder r = TL_RECORDER.get();
            if (r != null) r.stop();
        } catch (Exception e) {
            attachText("Video error", "Stop failed: " + e.getMessage());
        }

        try {
            File f = TL_VIDEO_FILE.get();
            if (f != null && f.exists()) {
                if (attach) {
                    try (FileInputStream fis = new FileInputStream(f)) {
                        byte[] bytes = fis.readAllBytes();
                        Allure.addAttachment("Video | " + f.getName(), "video/quicktime",
                                new ByteArrayInputStream(bytes), "mov");
                    }
                } else {
                    // Pass/Skipped: delete the recording to keep repo clean
                    try {
                        Files.deleteIfExists(f.toPath());
                        attachText("Video", "Recording discarded for pass/skip: " + f.getName());
                    } catch (Exception deleteErr) {
                        attachText("Video", "Could not delete non-failure recording: " + f.getName());
                    }
                }
            } else if (attach) {
                attachText("Video", "No video file found to attach.");
            }
        } catch (Exception e) {
            attachText("Video error", "Post-process failed: " + e.getMessage());
        } finally {
            TL_RECORDER.remove();
            TL_VIDEO_FILE.remove();
        }
    }

    // ---------- Screenshots (failure-only) ----------
    /** Capture screenshot, attach to Allure, and save PNG under /screenshots (used on failure). */
    public static void attachFailureScreenshot(WebDriver driver, String testMethodName) {
        if (driver == null) {
            attachText("Screenshot skipped", "WebDriver is null.");
            return;
        }
        try {
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

            String browser = "unknown";
            String version = "";
            try {
                Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
                browser = String.valueOf(caps.getBrowserName());
                Object v = caps.getCapability("browserVersion");
                version = v == null ? "" : String.valueOf(v);
            } catch (Throwable ignored) {}

            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            String method = (testMethodName == null || testMethodName.isBlank()) ? "test" : testMethodName;

            // Save to disk
            Files.createDirectories(SCREEN_DIR);
            String fileName = sanitize(method) + "__" + sanitize(browser + "_" + version) + "__" + ts + ".png";
            Path out = SCREEN_DIR.resolve(fileName);
            Files.write(out, png, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            attachText("Screenshot saved to", out.toAbsolutePath().toString());

            // Attach to Allure
            String title = "Screenshot | " + method + " | " + browser + " " + version + " | " + ts;
            Allure.addAttachment(title, "image/png", new ByteArrayInputStream(png), "png");

        } catch (Throwable t) {
            attachText("Screenshot failed", "Error: " + t.getMessage());
        }
    }

    // ---------- Helpful Allure attachments ----------
    public static void attachUrl(WebDriver driver) {
        try {
            String url = (driver == null) ? "(driver null)" : driver.getCurrentUrl();
            Allure.addAttachment("URL", "text/plain",
                    new ByteArrayInputStream(url.getBytes(StandardCharsets.UTF_8)), "txt");
        } catch (Throwable t) {
            attachText("URL", "Failed: " + t.getMessage());
        }
    }

    public static void attachPageSource(WebDriver driver) {
        try {
            String html = (driver == null) ? "(driver null)" : driver.getPageSource();
            Allure.addAttachment("Page Source", "text/html",
                    new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8)), "html");
        } catch (Throwable t) {
            attachText("Page Source", "Failed: " + t.getMessage());
        }
    }

    public static void attachText(String name, String content) {
        try {
            Allure.addAttachment(
                    (name == null || name.isBlank()) ? "Log" : name,
                    "text/plain",
                    new ByteArrayInputStream((content == null ? "" : content).getBytes(StandardCharsets.UTF_8)),
                    "txt"
            );
        } catch (Throwable ignored) {}
    }

    private static String sanitize(String s) {
        return s.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
