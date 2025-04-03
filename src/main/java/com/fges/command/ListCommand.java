package com.fges.command;

import com.fges.model.GroceryItem;
import com.fges.storage.GroceryListStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;

/**
 * Commande pour afficher la liste de courses
 */
public class ListCommand implements Command {
    private final GroceryListStorage storage;

    public ListCommand(GroceryListStorage storage) {
        this.storage = storage;
    }

    @Override
    public int execute() throws Exception {
        List<GroceryItem> groceryList = storage.load();

        Map<String, List<GroceryItem>> categorizedItems = new TreeMap<>();

        for (GroceryItem item : groceryList) {
            String category = item.getCategory();
            if (!categorizedItems.containsKey(category)) {
                categorizedItems.put(category, new ArrayList<>());
            }
            categorizedItems.get(category).add(item);
        }

        for (Map.Entry<String, List<GroceryItem>> entry : categorizedItems.entrySet()) {
            String category = entry.getKey();
            List<GroceryItem> items = entry.getValue();

            System.out.println("#" + category + ":");

            items.sort(Comparator.comparing(GroceryItem::getName));

            for (GroceryItem item : items) {
                System.out.println(item);
            }

            if (categorizedItems.size() > 1) {
                System.out.println();
            }
        }

        return 0;
    }
}
