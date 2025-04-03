package com.fges.command;

import com.fges.storage.GroceryListStorage;
import java.util.List;

/**
 * Fabrique pour créer des instances de commandes
 */
public class CommandFactory {

    /**
     * Crée l'instance de commande appropriée en fonction du nom
     * @param commandName Nom de la commande (add, list, remove)
     * @param storage Instance de stockage à utiliser
     * @param args Arguments de la ligne de commande
     * @return Instance de commande ou null si commande inconnue
     */
    public static Command getCommand(String commandName, GroceryListStorage storage, List<String> args) {
        return switch (commandName) {
            case "add" -> new AddCommand(storage, args);
            case "list" -> new ListCommand(storage);
            case "remove" -> new RemoveCommand(storage, args);
            default -> null;
        };
    }
}