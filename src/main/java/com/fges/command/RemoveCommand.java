package com.fges.command;

import com.fges.model.GroceryItem;
import com.fges.storage.GroceryListStorage;

import java.util.List;

/**
 * Commande pour supprimer un article de la liste de courses
 */
public class RemoveCommand implements Command {
    private final GroceryListStorage storage;
    private final List<String> args;

    public RemoveCommand(GroceryListStorage storage, List<String> args) {
        this.storage = storage;
        this.args = args;
    }

    @Override
    public int execute() throws Exception {
        // Vérifier les arguments
        if (args.size() < 2) {
            System.err.println("Missing arguments");
            return 1;
        }

        String itemName = args.get(1);

        // Charger la liste de courses
        List<GroceryItem> groceryList = storage.load();

        // Supprimer l'article dont le nom correspond exactement
        boolean removed = groceryList.removeIf(item -> item.getName().equals(itemName));

        if (!removed) {
            System.out.println("Item '" + itemName + "' not found in grocery list");
        }

        // Sauvegarder la liste mise à jour
        storage.save(groceryList);
        return 0;
    }
}