package com.fges.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class FormatValidatorTest {

    private static final String JSON_TEST_FILE = "test_json_format.json";
    private static final String CSV_TEST_FILE = "test_csv_format.csv";
    private FormatValidator validator;

    @BeforeEach
    public void setUp() throws Exception {
        validator = new FormatValidator();

        // Make sure test files don't exist at start
        Files.deleteIfExists(Paths.get(JSON_TEST_FILE));
        Files.deleteIfExists(Paths.get(CSV_TEST_FILE));
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Clean up test files
        Files.deleteIfExists(Paths.get(JSON_TEST_FILE));
        Files.deleteIfExists(Paths.get(CSV_TEST_FILE));
    }

    @Test
    public void shouldAcceptJsonFormatForJsonFile() throws IOException {
        // Create a JSON file
        try (FileWriter writer = new FileWriter(JSON_TEST_FILE)) {
            writer.write("{\"default\": {\"Salt\": 1, \"Pepper\": 2}}");
        }

        Path filePath = Paths.get(JSON_TEST_FILE);

        // This should not throw an exception
        validator.validateFileFormat(filePath, "json");
    }

    @Test
    public void shouldAcceptCsvFormatForCsvFile() throws IOException {
        // Create a CSV file
        try (FileWriter writer = new FileWriter(CSV_TEST_FILE)) {
            writer.write("name,quantity,category\n");
            writer.write("Salt,1,Spices\n");
            writer.write("Pepper,2,Spices\n");
        }

        Path filePath = Paths.get(CSV_TEST_FILE);

        // This should not throw an exception
        validator.validateFileFormat(filePath, "csv");
    }

    @Test
    public void shouldRejectJsonFormatForCsvFile() {
        // Create a CSV file
        assertThrows(IOException.class, () -> {
            try (FileWriter writer = new FileWriter(CSV_TEST_FILE)) {
                writer.write("name,quantity,category\n");
                writer.write("Salt,1,Spices\n");
                writer.write("Pepper,2,Spices\n");
            }

            Path filePath = Paths.get(CSV_TEST_FILE);

            // This should throw an exception
            validator.validateFileFormat(filePath, "json");
        });
    }

    @Test
    public void shouldRejectCsvFormatForJsonFile() {
        // Create a JSON file
        assertThrows(IOException.class, () -> {
            try (FileWriter writer = new FileWriter(JSON_TEST_FILE)) {
                writer.write("{\"default\": {\"Salt\": 1, \"Pepper\": 2}}");
            }

            Path filePath = Paths.get(JSON_TEST_FILE);

            // This should throw an exception
            validator.validateFileFormat(filePath, "csv");
        });
    }

    @Test
    public void shouldAcceptJsonFormatForArrayStyleJsonFile() throws IOException {
        // Create a JSON array file
        try (FileWriter writer = new FileWriter(JSON_TEST_FILE)) {
            writer.write("[{\"name\":\"Salt\",\"quantity\":1,\"category\":\"Spices\"}," +
                    "{\"name\":\"Pepper\",\"quantity\":2,\"category\":\"Spices\"}]");
        }

        Path filePath = Paths.get(JSON_TEST_FILE);

        // This should not throw an exception
        validator.validateFileFormat(filePath, "json");
    }

    @Test
    public void shouldHandleEmptyFile() throws IOException {
        // Create an empty file
        try (FileWriter writer = new FileWriter(JSON_TEST_FILE)) {
            writer.write("");
        }

        Path filePath = Paths.get(JSON_TEST_FILE);

        // This should not throw an exception for any format since the file is empty
        validator.validateFileFormat(filePath, "json");
        validator.validateFileFormat(filePath, "csv");
    }
}