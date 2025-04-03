package com.fges.command;

import com.fges.model.GroceryItem;
import com.fges.storage.GroceryListStorage;

import java.util.Comparator;
import java.util.List;

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
        // Charger la liste de courses
        List<GroceryItem> groceryList = storage.load();

        // Trier la liste par ordre alphabétique
        groceryList.sort(Comparator.comparing(GroceryItem::getName));

        // Afficher chaque article
        for (GroceryItem item : groceryList) {
            System.out.println(item);
        }

        return 0;
    }
}