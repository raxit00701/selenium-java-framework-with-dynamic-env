package utils;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ResultUtil {

    private static final String RESULT_FILE =
            "C:\\Users\\raxit\\IdeaProjects\\pet_frame\\src\\resources\\data\\test_results.csv";

    // Store results in memory
    private static final List<String[]> results = new ArrayList<>();

    // Add a single test result row
    public static void addResult(String testName, String status, String details) {
        results.add(new String[]{testName, status, details});
    }

    // Save results to CSV file
    public static void saveResults() {
        try (CSVWriter writer = new CSVWriter(new FileWriter(Paths.get(RESULT_FILE).toString()))) {
            // Write header
            writer.writeNext(new String[]{"Test Name", "Status", "Details"});

            // Write all rows
            for (String[] row : results) {
                writer.writeNext(row);
            }
        } catch (IOException e) {
            throw new RuntimeException("❌ Failed to write test results CSV", e);
        }
    }

    // Clear stored results (useful between runs)
    public static void clearResults() {
        results.clear();
    }
}
