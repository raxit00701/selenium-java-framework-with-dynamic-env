package reports;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;
import java.util.stream.Stream;

public final class AllureMeta {
    private AllureMeta() {}

    private static final Path PROJECT = Paths.get("").toAbsolutePath();
    private static final Path RESULTS = PROJECT.resolve("allure-results");
    private static final Path LAST_REPORT_HISTORY = PROJECT.resolve("target/site/allure-maven-plugin/history");
    private static final Path RESULTS_HISTORY = RESULTS.resolve("history");

    /** Writes environment.properties (shows in Allure “Environment”). */
    public static void writeEnvironment() {
        try {
            // <-- NEW: remove prior run's JSON artifacts so report starts clean
            clearAllureJsonFiles();

            Files.createDirectories(RESULTS);
            Properties p = new Properties();
            p.setProperty("OS", System.getProperty("os.name"));
            p.setProperty("Java", System.getProperty("java.version"));
            p.setProperty("User", System.getProperty("user.name"));
            try (OutputStream os = Files.newOutputStream(RESULTS.resolve("environment.properties"))) {
                p.store(os, "Allure environment");
            }
        } catch (IOException ignored) {}
    }

    /** Writes executor.json (adds nice “Build/Executor” block in report). */
    public static void writeExecutor() {
        try {
            Files.createDirectories(RESULTS);
            String json = "{\n" +
                    "  \"name\": \"Local Maven\",\n" +
                    "  \"type\": \"maven\",\n" +
                    "  \"buildOrder\": 1,\n" +
                    "  \"buildName\": \"Local Run\",\n" +
                    "  \"reportName\": \"Pet Store UI Tests\",\n" +
                    "  \"url\": \"\",\n" +
                    "  \"buildUrl\": \"\"\n" +
                    "}";
            Files.writeString(RESULTS.resolve("executor.json"), json);
        } catch (IOException ignored) {}
    }

    /** Copies previous report’s history into current allure-results so Trends work. */
    public static void copyHistoryIfPresent() {
        try {
            if (Files.isDirectory(LAST_REPORT_HISTORY)) {
                Files.createDirectories(RESULTS_HISTORY);
                try (var walk = Files.walk(LAST_REPORT_HISTORY)) {
                    walk.forEach(src -> {
                        try {
                            Path dest = RESULTS_HISTORY.resolve(LAST_REPORT_HISTORY.relativize(src));
                            if (Files.isDirectory(src)) {
                                Files.createDirectories(dest);
                            } else {
                                Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                            }
                        } catch (IOException ignored) {}
                    });
                }
            }
        } catch (IOException ignored) {}
    }

    /**
     * Delete any existing JSON files in allure-results folder.
     * This keeps leftover JSON metadata from previous runs from polluting the new run.
     */
    private static void clearAllureJsonFiles() {
        try {
            if (!Files.exists(RESULTS)) return;
            try (Stream<Path> stream = Files.list(RESULTS)) {
                stream.filter(p -> {
                    String name = p.getFileName().toString().toLowerCase();
                    return name.endsWith(".json");
                }).forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (IOException ignored) {}
                });
            }
        } catch (IOException ignored) {}
    }
}
