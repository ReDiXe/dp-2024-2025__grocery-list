package com.fges.command;

import com.fges.model.GroceryItem;
import com.fges.storage.GroceryListStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


/**
 * test unitaire de la classe ListCommand
 */
@ExtendWith(MockitoExtension.class)
public class ListCommandTest {

    @Mock
    private GroceryListStorage mockStorage;

    // For capturing System.out
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void shouldReturnSuccessWithEmptyList() throws Exception {
        // Setup
        List<GroceryItem> emptyList = new ArrayList<>();
        when(mockStorage.load()).thenReturn(emptyList);

        ListCommand command = new ListCommand(mockStorage);

        // Execute
        int result = command.execute();

        // Verify
        assertEquals(0, result);
        assertEquals("", outContent.toString().trim());
    }

    @Test
    public void shouldDisplayItemsCorrectly() throws Exception {
        // Setup
        List<GroceryItem> groceryList = new ArrayList<>();
        groceryList.add(new GroceryItem("Milk", 2, "Dairy"));
        groceryList.add(new GroceryItem("Eggs", 12, "Dairy"));

        when(mockStorage.load()).thenReturn(groceryList);

        ListCommand command = new ListCommand(mockStorage);

        // Execute
        int result = command.execute();

        // Verify
        assertEquals(0, result);
        String output = outContent.toString();
        assertTrue(output.contains("#Dairy:"));
        assertTrue(output.contains("Eggs: 12"));
        assertTrue(output.contains("Milk: 2"));
    }

    @Test
    public void shouldSortItemsAlphabeticallyWithinCategory() throws Exception {
        // Setup - items added out of alphabetical order
        List<GroceryItem> groceryList = new ArrayList<>();
        groceryList.add(new GroceryItem("Zucchini", 3, "Vegetables"));
        groceryList.add(new GroceryItem("Carrot", 5, "Vegetables"));
        groceryList.add(new GroceryItem("Broccoli", 2, "Vegetables"));

        when(mockStorage.load()).thenReturn(groceryList);

        ListCommand command = new ListCommand(mockStorage);

        // Execute
        int result = command.execute();

        // Verify
        assertEquals(0, result);
        String output = outContent.toString();

        // Check alphabetical ordering within category
        int posBroccoli = output.indexOf("Broccoli: 2");
        int posCarrot = output.indexOf("Carrot: 5");
        int posZucchini = output.indexOf("Zucchini: 3");

        assertTrue(posBroccoli != -1);
        assertTrue(posCarrot != -1);
        assertTrue(posZucchini != -1);
        assertTrue(posBroccoli < posCarrot);
        assertTrue(posCarrot < posZucchini);
    }

    @Test
    public void shouldDisplayMultipleCategoriesCorrectly() throws Exception {
        // Setup
        List<GroceryItem> groceryList = new ArrayList<>();
        groceryList.add(new GroceryItem("Bread", 1, "Bakery"));
        groceryList.add(new GroceryItem("Milk", 2, "Dairy"));
        groceryList.add(new GroceryItem("Apple", 4, "Fruits"));

        when(mockStorage.load()).thenReturn(groceryList);

        ListCommand command = new ListCommand(mockStorage);

        // Execute
        int result = command.execute();

        // Verify
        assertEquals(0, result);
        String output = outContent.toString();

        assertTrue(output.contains("#Bakery:"));
        assertTrue(output.contains("#Dairy:"));
        assertTrue(output.contains("#Fruits:"));
        assertTrue(output.contains("Bread: 1"));
        assertTrue(output.contains("Milk: 2"));
        assertTrue(output.contains("Apple: 4"));
    }

    @Test
    public void shouldSortCategoriesAlphabetically() throws Exception {
        // Setup - categories added out of alphabetical order
        List<GroceryItem> groceryList = new ArrayList<>();
        groceryList.add(new GroceryItem("Zucchini", 3, "Vegetables"));
        groceryList.add(new GroceryItem("Bread", 1, "Bakery"));
        groceryList.add(new GroceryItem("Milk", 2, "Dairy"));

        when(mockStorage.load()).thenReturn(groceryList);

        ListCommand command = new ListCommand(mockStorage);

        // Execute
        int result = command.execute();

        // Verify
        assertEquals(0, result);
        String output = outContent.toString();

        // Check alphabetical ordering of categories
        int posBakery = output.indexOf("#Bakery:");
        int posDairy = output.indexOf("#Dairy:");
        int posVegetables = output.indexOf("#Vegetables:");

        assertTrue(posBakery != -1);
        assertTrue(posDairy != -1);
        assertTrue(posVegetables != -1);
        assertTrue(posBakery < posDairy);
        assertTrue(posDairy < posVegetables);
    }

    @Test
    public void shouldHandleDefaultCategoryCorrectly() throws Exception {
        // Setup
        List<GroceryItem> groceryList = new ArrayList<>();
        groceryList.add(new GroceryItem("Salt", 1)); // default category
        groceryList.add(new GroceryItem("Pepper", 1, "default")); // explicit default category

        when(mockStorage.load()).thenReturn(groceryList);

        ListCommand command = new ListCommand(mockStorage);

        // Execute
        int result = command.execute();

        // Verify
        assertEquals(0, result);
        String output = outContent.toString();

        assertTrue(output.contains("#default:"));
        assertTrue(output.contains("Salt: 1"));
        assertTrue(output.contains("Pepper: 1"));
    }
}