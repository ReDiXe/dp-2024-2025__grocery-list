package com.fges.command;

import com.fges.model.GroceryItem;
import com.fges.storage.GroceryListStorage;

import java.util.List;

/**
 * Commande pour ajouter un article à la liste de courses
 */
public class AddCommand implements Command {
    private final GroceryListStorage storage;
    private final List<String> args;

    public AddCommand(GroceryListStorage storage, List<String> args) {
        this.storage = storage;
        this.args = args;
    }

    @Override
    public int execute() throws Exception {
        // Vérifier les arguments
        if (args.size() < 3) {
            System.err.println("Missing arguments");
            return 1;
        }

        String itemName = args.get(1);
        int quantity;

        try {
            quantity = Integer.parseInt(args.get(2));
        } catch (NumberFormatException e) {
            System.err.println("Quantity must be a number");
            return 1;
        }

        if (quantity <= 0) {
            System.err.println("Quantity must be positive");
            return 1;
        }

        // Charger la liste de courses
        List<GroceryItem> groceryList = storage.load();

        // Vérifier si l'article existe déjà
        boolean itemExists = false;
        for (GroceryItem item : groceryList) {
            if (item.getName().equals(itemName)) {
                // Ajouter à la quantité existante
                item.incrementQuantity(quantity);
                itemExists = true;
                break;
            }
        }

        // Si l'article n'existe pas, l'ajouter
        if (!itemExists) {
            groceryList.add(new GroceryItem(itemName, quantity));
        }

        // Sauvegarder la liste mise à jour
        storage.save(groceryList);
        return 0;
    }
}