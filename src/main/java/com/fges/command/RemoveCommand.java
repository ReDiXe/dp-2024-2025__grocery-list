package com.fges.command;

import com.fges.model.GroceryItem;
import com.fges.storage.GroceryListStorage;
import org.apache.commons.cli.CommandLine;

import java.util.List;

/**
 * Commande pour supprimer un article de la liste de courses
 */
public class RemoveCommand implements Command {
    private final GroceryListStorage storage;
    private final List<String> args;
    private final CommandLine cmd;

    public RemoveCommand(GroceryListStorage storage, List<String> args, CommandLine cmd) {
        this.storage = storage;
        this.args = args;
        this.cmd = cmd;
    }

    @Override
    public int execute() throws Exception {
        if (args.size() < 2) {
            System.err.println("Missing arguments");
            return 1;
        }

        String itemName = args.get(1);

        String category = cmd.getOptionValue("c", "default");

        List<GroceryItem> groceryList = storage.load();

        boolean removed = groceryList.removeIf(
                item -> item.getName().equals(itemName) && item.getCategory().equals(category)
        );

        if (!removed) {
            System.out.println("Item '" + itemName + "' not found in category '" + category + "'");
        }

        storage.save(groceryList);
        return 0;
    }
}