package com.fges.command;

import com.fges.model.GroceryItem;
import com.fges.storage.GroceryListStorage;
import org.apache.commons.cli.CommandLine;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/**
 * test unitaire de la classe RemoveCommand
 */
@ExtendWith(MockitoExtension.class)
public class RemoveCommandTest {

    @Mock
    private GroceryListStorage mockStorage;

    @Mock
    private CommandLine mockCmd;

    private List<String> args;

    // For capturing System.out
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() throws Exception {
        args = new ArrayList<>();
        args.add("remove"); // Command name

        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void shouldReturnErrorWhenArgumentsMissing() throws Exception {
        // Only "remove" command is provided, missing item name
        RemoveCommand command = new RemoveCommand(mockStorage, args, mockCmd);

        int result = command.execute();

        assertEquals(1, result);
        verify(mockStorage, never()).save(any());
    }

    @Test
    public void shouldRemoveItemWhenExistsInDefaultCategory() throws Exception {
        // Setup
        args.add("Milk"); // Item name to remove

        // Create existing list with the item in default category
        List<GroceryItem> groceryList = new ArrayList<>();
        groceryList.add(new GroceryItem("Milk", 2));
        groceryList.add(new GroceryItem("Bread", 1));

        when(mockStorage.load()).thenReturn(groceryList);
        when(mockCmd.getOptionValue("c", "default")).thenReturn("default");

        RemoveCommand command = new RemoveCommand(mockStorage, args, mockCmd);

        // Execute
        int result = command.execute();

        // Verify
        assertEquals(0, result);
        verify(mockStorage).save(groceryList);

        // Check that our item was removed
        assertEquals(1, groceryList.size());
        assertEquals("Bread", groceryList.getFirst().getName());
    }

    @Test
    public void shouldRemoveItemWhenExistsInSpecifiedCategory() throws Exception {
        // Setup
        args.add("Apple"); // Item name to remove
        String category = "Fruits";

        // Create existing list with the item in specified category
        List<GroceryItem> groceryList = new ArrayList<>();
        groceryList.add(new GroceryItem("Apple", 4, category));
        groceryList.add(new GroceryItem("Banana", 3, category));

        when(mockStorage.load()).thenReturn(groceryList);
        when(mockCmd.getOptionValue("c", "default")).thenReturn(category);

        RemoveCommand command = new RemoveCommand(mockStorage, args, mockCmd);

        // Execute
        int result = command.execute();

        // Verify
        assertEquals(0, result);
        verify(mockStorage).save(groceryList);

        // Check that our item was removed
        assertEquals(1, groceryList.size());
        assertEquals("Banana", groceryList.getFirst().getName());
    }

    @Test
    public void shouldNotRemoveItemWhenNotInSpecifiedCategory() throws Exception {
        // Setup
        args.add("Apple"); // Item name to remove
        String requestedCategory = "Snacks";
        String actualCategory = "Fruits";

        // Create existing list with the item in a different category
        List<GroceryItem> groceryList = new ArrayList<>();
        groceryList.add(new GroceryItem("Apple", 4, actualCategory));

        when(mockStorage.load()).thenReturn(groceryList);
        when(mockCmd.getOptionValue("c", "default")).thenReturn(requestedCategory);

        RemoveCommand command = new RemoveCommand(mockStorage, args, mockCmd);

        // Execute
        int result = command.execute();

        // Verify
        assertEquals(0, result); // Command still succeeds even when no matching item
        verify(mockStorage).save(groceryList);

        // Check that our item was NOT removed
        assertEquals(1, groceryList.size());

        // Verify that the correct message was printed
        String output = outContent.toString();
        assertTrue(output.contains("Item 'Apple' not found in category 'Snacks'"));
    }

    @Test
    public void shouldNotRemoveItemWhenNameDoesNotExist() throws Exception {
        // Setup
        args.add("Orange"); // Item name to remove (not in list)

        // Create existing list without the item
        List<GroceryItem> groceryList = new ArrayList<>();
        groceryList.add(new GroceryItem("Apple", 4, "Fruits"));

        when(mockStorage.load()).thenReturn(groceryList);
        when(mockCmd.getOptionValue("c", "default")).thenReturn("Fruits");

        RemoveCommand command = new RemoveCommand(mockStorage, args, mockCmd);

        // Execute
        int result = command.execute();

        // Verify
        assertEquals(0, result); // Command still succeeds even when no matching item
        verify(mockStorage).save(groceryList);

        // Check that list remains unchanged
        assertEquals(1, groceryList.size());
        assertEquals("Apple", groceryList.getFirst().getName());

        // Verify that the correct message was printed
        String output = outContent.toString();
        assertTrue(output.contains("Item 'Orange' not found in category 'Fruits'"));
    }

    @Test
    public void shouldHandleCaseSensitivityCorrectly() throws Exception {
        // Setup
        args.add("milk"); // lowercase, but item is stored with uppercase first letter

        // Create existing list with the item with different case
        List<GroceryItem> groceryList = new ArrayList<>();
        groceryList.add(new GroceryItem("Milk", 2));

        when(mockStorage.load()).thenReturn(groceryList);
        when(mockCmd.getOptionValue("c", "default")).thenReturn("default");

        RemoveCommand command = new RemoveCommand(mockStorage, args, mockCmd);

        // Execute
        int result = command.execute();

        // Verify
        assertEquals(0, result);
        verify(mockStorage).save(groceryList);

        // Check that our item was NOT removed due to case sensitivity
        assertEquals(1, groceryList.size());

        // Verify that the correct message was printed
        String output = outContent.toString();
        assertTrue(output.contains("Item 'milk' not found in category 'default'"));
    }

    @Test
    public void shouldRemoveItemWithNameContainingSpaces() throws Exception {
        // Setup
        args.add("Orange Juice"); // Item name with space

        // Create existing list with the item with spaces in name
        List<GroceryItem> groceryList = new ArrayList<>();
        groceryList.add(new GroceryItem("Orange Juice", 1, "Beverages"));

        when(mockStorage.load()).thenReturn(groceryList);
        when(mockCmd.getOptionValue("c", "default")).thenReturn("Beverages");

        RemoveCommand command = new RemoveCommand(mockStorage, args, mockCmd);

        // Execute
        int result = command.execute();

        // Verify
        assertEquals(0, result);
        verify(mockStorage).save(groceryList);

        // Check that our item was removed
        assertTrue(groceryList.isEmpty());
    }
}