// GroceryShopAdapter.java
package com.fges.web;

import com.fges.model.GroceryItem;
import com.fges.storage.GroceryListStorage;
import fr.anthonyquere.MyGroceryShop;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Adaptateur entre GroceryListStorage et MyGroceryShop pour le serveur web
 */
public class GroceryShopAdapter implements MyGroceryShop {

    private final GroceryListStorage storage;

    public GroceryShopAdapter(GroceryListStorage storage) {
        this.storage = storage;
    }

    @Override
    public List<WebGroceryItem> getGroceries() {
        try {
            List<GroceryItem> items = storage.load();
            List<WebGroceryItem> webItems = new ArrayList<>();

            for (GroceryItem item : items) {
                webItems.add(new WebGroceryItem(item.getName(), item.getQuantity(), item.getCategory()));
            }

            return webItems;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load groceries", e);
        }
    }

    @Override
    public void addGroceryItem(String name, int quantity, String category) {
        try {
            List<GroceryItem> items = storage.load();
            items.add(new GroceryItem(name, quantity, category));
            storage.save(items);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add grocery item", e);
        }
    }

    @Override
    public void removeGroceryItem(String name) {
        try {
            List<GroceryItem> items = storage.load();
            items.removeIf(item -> item.getName().equals(name));
            storage.save(items);
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove grocery item", e);
        }
    }

    @Override
    public Runtime getRuntime() {
        return new Runtime(
                LocalDate.now(),
                System.getProperty("java.version"),
                System.getProperty("os.name")
        );
    }
}