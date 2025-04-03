package com.fges.command;

import com.fges.model.GroceryItem;
import com.fges.storage.GroceryListStorage;
import org.apache.commons.cli.CommandLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * test unitaire de la classe AddCommand
 */

//un objet mock est une imitation d'un objet réel, utilisé pour simuler le comportement d'un objet dans un environnement de test.
@ExtendWith(MockitoExtension.class)
public class AddCommandTest {

    @Mock
    private GroceryListStorage mockStorage;

    @Mock
    private CommandLine mockCmd;

    private List<String> args;

    @BeforeEach
    public void setUp() throws Exception {
        args = new ArrayList<>();
        args.add("add"); // Command name
    }

    @Test
    public void shouldReturnErrorWhenArgumentsMissing() throws Exception {
        // Only "add" command is provided, missing item name and quantity
        AddCommand command = new AddCommand(mockStorage, args, mockCmd);

        int result = command.execute();

        assertEquals(1, result);
        verify(mockStorage, never()).save(any());
    }

    @Test
    public void shouldReturnErrorWhenQuantityIsNotNumeric() throws Exception {
        args.add("Milk"); // Item name
        args.add("abc"); // Invalid quantity

        AddCommand command = new AddCommand(mockStorage, args, mockCmd);

        int result = command.execute();

        assertEquals(1, result);
        verify(mockStorage, never()).save(any());
    }

    @Test
    public void shouldReturnErrorWhenQuantityIsZero() throws Exception {
        args.add("Milk"); // Item name
        args.add("0"); // Zero quantity

        AddCommand command = new AddCommand(mockStorage, args, mockCmd);

        int result = command.execute();

        assertEquals(1, result);
        verify(mockStorage, never()).save(any());
    }

    @Test
    public void shouldReturnErrorWhenQuantityIsNegative() throws Exception {
        args.add("Milk"); // Item name
        args.add("-5"); // Negative quantity

        AddCommand command = new AddCommand(mockStorage, args, mockCmd);

        int result = command.execute();

        assertEquals(1, result);
        verify(mockStorage, never()).save(any());
    }

    @Test
    public void shouldAddNewItemWhenNotExisting() throws Exception {
        // Setup
        args.add("Milk"); // Item name
        args.add("3");   // Quantity
        String category = "Dairy";

        List<GroceryItem> groceryList = new ArrayList<>();
        when(mockStorage.load()).thenReturn(groceryList);
        when(mockCmd.getOptionValue("c", "default")).thenReturn(category);

        AddCommand command = new AddCommand(mockStorage, args, mockCmd);

        // Execute
        int result = command.execute();

        // Verify
        assertEquals(0, result);
        verify(mockStorage).save(any());

        // Check that our item was added to the list
        assertEquals(1, groceryList.size());
        GroceryItem addedItem = groceryList.getFirst();
        assertEquals("Milk", addedItem.getName());
        assertEquals(3, addedItem.getQuantity());
        assertEquals(category, addedItem.getCategory());
    }

    @Test
    public void shouldIncrementQuantityWhenItemExists() throws Exception {
        // Setup
        args.add("Bread"); // Item name
        args.add("2");    // Quantity to add
        String category = "Bakery";

        // Create existing list with the item already in it
        List<GroceryItem> groceryList = new ArrayList<>();
        GroceryItem existingItem = new GroceryItem("Bread", 1, category);
        groceryList.add(existingItem);

        when(mockStorage.load()).thenReturn(groceryList);
        when(mockCmd.getOptionValue("c", "default")).thenReturn(category);

        AddCommand command = new AddCommand(mockStorage, args, mockCmd);

        // Execute
        int result = command.execute();

        // Verify
        assertEquals(0, result);
        verify(mockStorage).save(groceryList);

        // Check that our item quantity was updated
        assertEquals(1, groceryList.size());
        assertEquals(3, existingItem.getQuantity()); // 1 (existing) + 2 (added)
    }

    @Test
    public void shouldAddNewItemWhenCategoryDiffers() throws Exception {
        // Setup
        args.add("Bread"); // Item name
        args.add("2");    // Quantity
        String newCategory = "Organic";

        // Create existing list with the item in a different category
        List<GroceryItem> groceryList = new ArrayList<>();
        groceryList.add(new GroceryItem("Bread", 1, "Bakery"));

        when(mockStorage.load()).thenReturn(groceryList);
        when(mockCmd.getOptionValue("c", "default")).thenReturn(newCategory);

        AddCommand command = new AddCommand(mockStorage, args, mockCmd);

        // Execute
        int result = command.execute();

        // Verify
        assertEquals(0, result);
        verify(mockStorage).save(groceryList);

        // Check that a new item was added (not incrementing the existing one)
        assertEquals(2, groceryList.size());

        boolean foundNewItem = false;
        for (GroceryItem item : groceryList) {
            if (item.getName().equals("Bread") && item.getCategory().equals(newCategory)) {
                assertEquals(2, item.getQuantity());
                foundNewItem = true;
            }
        }
        assertTrue(foundNewItem);
    }

    @Test
    public void shouldUseDefaultCategoryWhenNotSpecified() throws Exception {
        // Setup
        args.add("Eggs"); // Item name
        args.add("6");    // Quantity

        List<GroceryItem> groceryList = new ArrayList<>();
        when(mockStorage.load()).thenReturn(groceryList);
        when(mockCmd.getOptionValue("c", "default")).thenReturn("default"); // Default behavior

        AddCommand command = new AddCommand(mockStorage, args, mockCmd);

        // Execute
        int result = command.execute();

        // Verify
        assertEquals(0, result);
        verify(mockStorage).save(groceryList);

        // Check that our item was added with default category
        assertEquals(1, groceryList.size());
        GroceryItem addedItem = groceryList.getFirst();
        assertEquals("Eggs", addedItem.getName());
        assertEquals(6, addedItem.getQuantity());
        assertEquals("default", addedItem.getCategory());
    }
}