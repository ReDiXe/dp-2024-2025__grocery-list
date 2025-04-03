package com.fges.storage;

import com.fges.model.GroceryItem;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JsonGroceryListStorageTest {

    private static final String TEST_FILE = "test_grocery_list.json";
    private JsonGroceryListStorage storage;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() throws Exception {
        storage = new JsonGroceryListStorage(TEST_FILE);
        objectMapper = new ObjectMapper();

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
    public void shouldLoadLegacyFormatWithDefaultCategory() throws IOException {
        // Create legacy format JSON (simple map)
        Map<String, Integer> legacyMap = new HashMap<>();
        legacyMap.put("Salt", 1);
        legacyMap.put("Pepper", 2);

        objectMapper.writeValue(new File(TEST_FILE), legacyMap);

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
    public void shouldLoadLegacyGroceryItemListFormat() throws IOException {
        // Create legacy format JSON (list of GroceryItems without categories)
        List<Map<String, Object>> legacyList = new ArrayList<>();

        Map<String, Object> item1 = new HashMap<>();
        item1.put("name", "Salt");
        item1.put("quantity", 1);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("name", "Pepper");
        item2.put("quantity", 2);

        legacyList.add(item1);
        legacyList.add(item2);

        objectMapper.writeValue(new File(TEST_FILE), legacyList);

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
    public void shouldSaveInCategorizedFormat() throws IOException {
        // Setup test data
        List<GroceryItem> groceryList = new ArrayList<>();
        groceryList.add(new GroceryItem("Milk", 2, "Dairy"));
        groceryList.add(new GroceryItem("Yogurt", 3, "Dairy"));
        groceryList.add(new GroceryItem("Bread", 1, "Bakery"));

        // Save the list
        storage.save(groceryList);

        // Verify file structure (should be categorized)
        String jsonContent = Files.readString(Paths.get(TEST_FILE));

        assertTrue(jsonContent.contains("\"Dairy\""));
        assertTrue(jsonContent.contains("\"Bakery\""));
        assertTrue(jsonContent.contains("\"Milk\""));
        assertTrue(jsonContent.contains("\"Yogurt\""));
        assertTrue(jsonContent.contains("\"Bread\""));
    }

    @Test
    public void shouldHandleItemsWithNullCategory() throws IOException {
        // Setup test data with null category
        List<GroceryItem> groceryList = new ArrayList<>();
        GroceryItem item = new GroceryItem("Salt", 1);
        item.setCategory(null); // Explicitly set null category
        groceryList.add(item);

        // Save the list
        storage.save(groceryList);

        // Load the list
        List<GroceryItem> loadedList = storage.load();

        // Verify
        assertEquals(1, loadedList.size());
        assertEquals("Salt", loadedList.get(0).getName());
        assertEquals(1, loadedList.get(0).getQuantity());
        assertEquals("default", loadedList.get(0).getCategory()); // Should use default category
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
        String content = Files.readString(filePath);
        assertTrue(content.contains("default"));
        assertTrue(content.contains("Test Item"));
        assertTrue(content.contains("1"));
    }
}