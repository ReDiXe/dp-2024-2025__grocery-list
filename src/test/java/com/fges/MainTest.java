package com.fges;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    private static final String TEST_JSON_FILE = "test_grocery.json";
    private static final String TEST_CSV_FILE = "test_grocery.csv";

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;

    @BeforeEach
    public void setUp() throws Exception {
        // Make sure test files don't exist at start
        Files.deleteIfExists(Paths.get(TEST_JSON_FILE));
        Files.deleteIfExists(Paths.get(TEST_CSV_FILE));

        // Capture error output
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Clean up test files
        Files.deleteIfExists(Paths.get(TEST_JSON_FILE));
        Files.deleteIfExists(Paths.get(TEST_CSV_FILE));

        // Restore original error stream
        System.setErr(originalErr);
    }

    @Test
    public void shouldReturnErrorCodeWhenNoArgs() throws Exception {
        int exitCode = Main.exec(new String[]{});
        assertEquals(1, exitCode);
        assertTrue(errContent.toString().contains("Fail to parse arguments"));
    }

    @Test
    public void shouldReturnErrorCodeWhenMissingSourceOption() throws Exception {
        int exitCode = Main.exec(new String[]{"list"});
        assertEquals(1, exitCode);
        assertTrue(errContent.toString().contains("Fail to parse arguments"));
    }

    @Test
    public void shouldReturnErrorCodeWhenInvalidFormat() throws Exception {
        int exitCode = Main.exec(new String[]{"-s", TEST_JSON_FILE, "-f", "xml", "list"});
        assertEquals(1, exitCode);
        assertTrue(errContent.toString().contains("Format must be either 'json' or 'csv'"));
    }

    @Test
    public void shouldReturnErrorCodeWhenMissingCommand() throws Exception {
        int exitCode = Main.exec(new String[]{"-s", TEST_JSON_FILE});
        assertEquals(1, exitCode);
        assertTrue(errContent.toString().contains("Missing Command"));
    }

    @Test
    public void shouldReturnErrorCodeWhenUnknownCommand() throws Exception {
        int exitCode = Main.exec(new String[]{"-s", TEST_JSON_FILE, "unknown"});
        assertEquals(1, exitCode);
        assertTrue(errContent.toString().contains("Unknown command: unknown"));
    }

    @Test
    public void shouldAddItemSuccessfully() throws Exception {
        int exitCode = Main.exec(new String[]{"-s", TEST_JSON_FILE, "add", "Milk", "2"});
        assertEquals(0, exitCode);

        // Verify file was created
        File file = new File(TEST_JSON_FILE);
        assertTrue(file.exists());

        // Verify item was added with default category
        exitCode = Main.exec(new String[]{"-s", TEST_JSON_FILE, "list"});
        assertEquals(0, exitCode);
    }

    @Test
    public void shouldAddItemWithCategorySuccessfully() throws Exception {
        int exitCode = Main.exec(new String[]{"-s", TEST_JSON_FILE, "-c", "Dairy", "add", "Milk", "2"});
        assertEquals(0, exitCode);

        // Verify file was created with category
        File file = new File(TEST_JSON_FILE);
        assertTrue(file.exists());

        // Check if we can add another item to same category
        exitCode = Main.exec(new String[]{"-s", TEST_JSON_FILE, "-c", "Dairy", "add", "Yogurt", "3"});
        assertEquals(0, exitCode);
    }

    @Test
    public void shouldRemoveItemSuccessfully() throws Exception {
        // First add an item
        Main.exec(new String[]{"-s", TEST_JSON_FILE, "add", "Bread", "1"});

        // Then remove it
        int exitCode = Main.exec(new String[]{"-s", TEST_JSON_FILE, "remove", "Bread"});
        assertEquals(0, exitCode);

        // Verify it was removed (list should be empty)
        File file = new File(TEST_JSON_FILE);
        assertTrue(file.exists());
        String content = Files.readString(file.toPath());
        assertTrue(content.contains("{}") || content.contains("\"default\":{}"));
    }

    @Test
    public void shouldRemoveItemWithCategorySuccessfully() throws Exception {
        // First add items in different categories
        Main.exec(new String[]{"-s", TEST_JSON_FILE, "-c", "Dairy", "add", "Milk", "2"});
        Main.exec(new String[]{"-s", TEST_JSON_FILE, "-c", "Bakery", "add", "Bread", "1"});

        // Remove item from specific category
        int exitCode = Main.exec(new String[]{"-s", TEST_JSON_FILE, "-c", "Dairy", "remove", "Milk"});
        assertEquals(0, exitCode);

        // Verify it was removed but other category remains
        String content = Files.readString(Paths.get(TEST_JSON_FILE));
        assertFalse(content.contains("\"Milk\""));
        assertTrue(content.contains("\"Bread\""));
    }

    @Test
    public void shouldHandleCsvFormatCorrectly() throws Exception {
        // Add item with CSV format
        int exitCode = Main.exec(new String[]{"-s", TEST_CSV_FILE, "-f", "csv", "add", "Salt", "1"});
        assertEquals(0, exitCode);

        // Verify file was created
        File file = new File(TEST_CSV_FILE);
        assertTrue(file.exists());

        // Check content format
        String content = Files.readString(file.toPath());
        assertTrue(content.contains("name") && content.contains("quantity") && content.contains("category"));
        assertTrue(content.contains("Salt") && content.contains("1") && content.contains("default"));
    }

    @Test
    public void shouldListItemsCorrectly() throws Exception {
        // Add multiple items
        Main.exec(new String[]{"-s", TEST_JSON_FILE, "-c", "Fruits", "add", "Apple", "5"});
        Main.exec(new String[]{"-s", TEST_JSON_FILE, "-c", "Vegetables", "add", "Carrot", "3"});

        // Capture standard output for list command
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            // Execute list command
            int exitCode = Main.exec(new String[]{"-s", TEST_JSON_FILE, "list"});
            assertEquals(0, exitCode);

            // Verify output contains categories and items
            String output = outContent.toString();
            assertTrue(output.contains("#Fruits:"));
            assertTrue(output.contains("Apple: 5"));
            assertTrue(output.contains("#Vegetables:"));
            assertTrue(output.contains("Carrot: 3"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void shouldHandleErrorsInCommands() throws Exception {
        // Test add command with invalid quantity
        int exitCode = Main.exec(new String[]{"-s", TEST_JSON_FILE, "add", "Salt", "abc"});
        assertEquals(1, exitCode);
        assertTrue(errContent.toString().contains("Quantity must be a number"));

    }
}