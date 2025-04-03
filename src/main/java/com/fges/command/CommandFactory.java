package com.fges.command;

import com.fges.storage.GroceryListStorage;
import org.apache.commons.cli.CommandLine;

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
     * @param cmd CommandLine pour les options
     * @return Instance de commande ou null si commande inconnue
     */
    public static Command getCommand(String commandName, GroceryListStorage storage, List<String> args, CommandLine cmd) {
        return switch (commandName) {
            case "add" -> new AddCommand(storage, args, cmd);
            case "list" -> new ListCommand(storage);
            case "remove" -> new RemoveCommand(storage, args, cmd);
            default -> null;
        };
    }
}