package com.fges;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GroceryItemTest {

    @Test
    void testConstructor() {
        GroceryItem item = new GroceryItem("pommes", 5);
        assertEquals("pommes", item.getName());
        assertEquals(5, item.getQuantity());
    }

    @Test
    void testDefaultConstructor() {
        GroceryItem item = new GroceryItem();
        assertNull(item.getName());
        assertEquals(0, item.getQuantity());
    }

    @Test
    void testSetters() {
        GroceryItem item = new GroceryItem("pommes", 5);

        item.setName("bananes");
        assertEquals("bananes", item.getName());

        item.setQuantity(10);
        assertEquals(10, item.getQuantity());
    }

    @Test
    void testToString() {
        GroceryItem item = new GroceryItem("pommes", 5);
        assertEquals("pommes: 5", item.toString());

        item.setName("bananes");
        item.setQuantity(3);
        assertEquals("bananes: 3", item.toString());
    }
}