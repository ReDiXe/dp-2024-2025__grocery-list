package com.fges.command;

import com.fges.model.GroceryItem;
import com.fges.storage.GroceryListStorage;
import org.apache.commons.cli.CommandLine;

import java.util.List;

/**
 * Commande pour ajouter un article à la liste de courses
 */
public class AddCommand implements Command {
    private final GroceryListStorage storage;
    private final List<String> args;
    private final CommandLine cmd;

    public AddCommand(GroceryListStorage storage, List<String> args, CommandLine cmd) {
        this.storage = storage;
        this.args = args;
        this.cmd = cmd;
    }

    /// Exécute la commande
    @Override
    public int execute() throws Exception {
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

        String category = cmd.getOptionValue("c", "default");

        List<GroceryItem> groceryList = storage.load();

        boolean itemExists = false;
        for (GroceryItem item : groceryList) {
            if (item.getName().equals(itemName) && item.getCategory().equals(category)) {
                item.incrementQuantity(quantity);
                itemExists = true;
                break;
            }
        }

        if (!itemExists) {
            groceryList.add(new GroceryItem(itemName, quantity, category));
        }

        storage.save(groceryList);
        return 0;
    }
}