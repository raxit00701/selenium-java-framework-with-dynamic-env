package utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class CsvUtils {

    private CsvUtils() { /* utility */ }

    /**
     * Reads a CSV from resources/data and returns parsed rows as String[].
     *
     * @param fileName name of the CSV file located under src/test/resources/data (e.g. "signup.csv")
     * @param hasHeader true to skip first row
     * @return list of rows where each row is a parsed String[] (handles quotes and commas inside fields)
     */
    public static List<String[]> readResourceCsv(String fileName, boolean hasHeader) {
        List<String[]> data = new ArrayList<>();

        String resourcePath = "data/" + fileName;
        InputStream is = CsvUtils.class.getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IllegalArgumentException("❌ CSV resource not found on classpath: " + resourcePath);
        }

        try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
             CSVReader reader = new CSVReaderBuilder(isr)
                     .withSkipLines(hasHeader ? 1 : 0)
                     .build()) {

            String[] row;
            while ((row = reader.readNext()) != null) {
                // row is already parsed by OpenCSV (handles quoted fields and commas inside fields)
                data.add(row);
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException("❌ Failed to read CSV file from resources/" + resourcePath, e);
        }
        return data;
    }
}
