package com.fges.storage;

import com.fges.model.GroceryItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CsvGroceryListStorageTest {

    private static final String TEST_FILE = "test_grocery_list.csv";
    private CsvGroceryListStorage storage;

    @BeforeEach
    public void setUp() throws Exception {
        storage = new CsvGroceryListStorage(TEST_FILE);

        // Make sure test file doesn't exist at start
        Files.deleteIfExists(Paths.get(TEST_FILE));
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Clean up test file
        Files.deleteIfExists(Paths.get(TEST_FILE));
    }

    @Test
    public void shouldLoadEmptyListWhenFileDoesNotExist() throws IOException {
        List<GroceryItem> result = storage.load();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldLoadEmptyListWhenFileIsEmpty() throws IOException {
        // Create empty file
        new File(TEST_FILE).createNewFile();

        List<GroceryItem> result = storage.load();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldSaveAndLoadItemsWithCategories() throws IOException {
        // Setup test data
        List<GroceryItem> groceryList = new ArrayList<>();
        groceryList.add(new GroceryItem("Milk", 2, "Dairy"));
        groceryList.add(new GroceryItem("Bread", 1, "Bakery"));
        groceryList.add(new GroceryItem("Apple", 5, "Fruits"));

        // Save the list
        storage.save(groceryList);

        // Load the list
        List<GroceryItem> loadedList = storage.load();

        // Verify
        assertEquals(3, loadedList.size());

        boolean foundMilk = false;
        boolean foundBread = false;
        boolean foundApple = false;

        for (GroceryItem item : loadedList) {
            if (item.getName().equals("Milk") && item.getQuantity() == 2 && item.getCategory().equals("Dairy")) {
                foundMilk = true;
            } else if (item.getName().equals("Bread") && item.getQuantity() == 1 && item.getCategory().equals("Bakery")) {
                foundBread = true;
            } else if (item.getName().equals("Apple") && item.getQuantity() == 5 && item.getCategory().equals("Fruits")) {
                foundApple = true;
            }
        }

        assertTrue(foundMilk);
        assertTrue(foundBread);
        assertTrue(foundApple);
    }

    @Test
    public void shouldLoadItemsWithDefaultCategoryWhenCategoryMissing() throws IOException {
        // Create CSV file with no category column
        try (FileWriter writer = new FileWriter(TEST_FILE)) {
            writer.write("name,quantity\n");
            writer.write("Salt,1\n");
            writer.write("Pepper,2\n");
        }

        // Load the list
        List<GroceryItem> loadedList = storage.load();

        // Verify
        assertEquals(2, loadedList.size());

        boolean foundSalt = false;
        boolean foundPepper = false;

        for (GroceryItem item : loadedList) {
            if (item.getName().equals("Salt") && item.getQuantity() == 1 && item.getCategory().equals("default")) {
                foundSalt = true;
            } else if (item.getName().equals("Pepper") && item.getQuantity() == 2 && item.getCategory().equals("default")) {
                foundPepper = true;
            }
        }

        assertTrue(foundSalt);
        assertTrue(foundPepper);
    }

    @Test
    public void shouldIgnoreRowsWithNonNumericQuantity() throws IOException {
        // Create CSV file with invalid quantity
        try (FileWriter writer = new FileWriter(TEST_FILE)) {
            writer.write("name,quantity,category\n");
            writer.write("Salt,1,Spices\n");
            writer.write("Pepper,abc,Spices\n"); // Invalid quantity
            writer.write("Sugar,3,Baking\n");
        }

        // Load the list
        List<GroceryItem> loadedList = storage.load();

        // Verify
        assertEquals(2, loadedList.size());

        // The "Pepper" entry should be ignored
        for (GroceryItem item : loadedList) {
            assertNotEquals("Pepper", item.getName());
        }
    }

    @Test
    public void shouldCreateFileWhenSavingToNonExistentFile() throws IOException {
        List<GroceryItem> groceryList = new ArrayList<>();
        groceryList.add(new GroceryItem("Test Item", 1));

        // Save to non-existent file
        storage.save(groceryList);

        // Check file exists
        Path filePath = Paths.get(TEST_FILE);
        assertTrue(Files.exists(filePath));

        // Verify content
        List<String> lines = Files.readAllLines(filePath);
        assertTrue(lines.size() >= 2); // Header + at least one data row
        assertTrue(lines.get(0).contains("name") && lines.get(0).contains("quantity") && lines.get(0).contains("category"));
        assertTrue(lines.get(1).contains("Test Item") && lines.get(1).contains("1") && lines.get(1).contains("default"));
    }
}