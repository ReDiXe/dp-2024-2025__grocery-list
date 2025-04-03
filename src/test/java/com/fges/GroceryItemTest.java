package com.fges.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/**
 * test unitaire de la classe GroceryItem
 */
public class GroceryItemTest {

    @Test
    public void shouldCreateDefaultGroceryItem() {
        GroceryItem item = new GroceryItem();
        assertEquals("default", item.getCategory());
        assertEquals(0, item.getQuantity());
        assertNull(item.getName());
    }

    @Test
    public void shouldCreateGroceryItemWithNameAndQuantity() {
        String name = "Milk";
        int quantity = 2;

        GroceryItem item = new GroceryItem(name, quantity);

        assertEquals(name, item.getName());
        assertEquals(quantity, item.getQuantity());
        assertEquals("default", item.getCategory());
    }

    @Test
    public void shouldCreateGroceryItemWithNameQuantityAndCategory() {
        String name = "Bread";
        int quantity = 1;
        String category = "Bakery";

        GroceryItem item = new GroceryItem(name, quantity, category);

        assertEquals(name, item.getName());
        assertEquals(quantity, item.getQuantity());
        assertEquals(category, item.getCategory());
    }

    @Test
    public void shouldUseDefaultCategoryWhenNullCategoryProvided() {
        GroceryItem item = new GroceryItem("Apple", 5, null);
        assertEquals("default", item.getCategory());
    }

    @Test
    public void shouldUseDefaultCategoryWhenEmptyCategoryProvided() {
        GroceryItem item = new GroceryItem("Banana", 3, "");
        assertEquals("default", item.getCategory());
    }

    @Test
    public void shouldIncrementQuantityCorrectly() {
        GroceryItem item = new GroceryItem("Cheese", 1);

        item.incrementQuantity(2);
        assertEquals(3, item.getQuantity());

        item.incrementQuantity(5);
        assertEquals(8, item.getQuantity());
    }

    @Test
    public void shouldFormatToStringCorrectly() {
        GroceryItem item = new GroceryItem("Eggs", 12);
        assertEquals("Eggs: 12", item.toString());
    }

    @Test
    public void shouldSetAndGetNameCorrectly() {
        GroceryItem item = new GroceryItem();
        String name = "Chocolate";

        item.setName(name);
        assertEquals(name, item.getName());
    }

    @Test
    public void shouldSetAndGetQuantityCorrectly() {
        GroceryItem item = new GroceryItem();
        int quantity = 7;

        item.setQuantity(quantity);
        assertEquals(quantity, item.getQuantity());
    }

    @Test
    public void shouldSetAndGetCategoryCorrectly() {
        GroceryItem item = new GroceryItem();
        String category = "Snacks";

        item.setCategory(category);
        assertEquals(category, item.getCategory());
    }

    @Test
    public void shouldUseDefaultCategoryWhenSettingNullCategory() {
        GroceryItem item = new GroceryItem("Yogurt", 2, "Dairy");

        item.setCategory(null);
        assertEquals("default", item.getCategory());
    }
}