package com.fges;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fges.GroceryItem;
import com.fges.GroceryListStorage;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

/**
 * Classe principale de l'application de gestion de liste de courses
 */
public class Main {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        System.exit(exec(args));
    }

    public static int exec(String[] args) throws IOException {
        // Setup CLI interface
        Options cliOptions = new Options();
        CommandLineParser parser = new DefaultParser();

        cliOptions.addRequiredOption("s", "source", true, "File containing the grocery list");
        cliOptions.addOption("f", "format", true, "Format of the file (json or csv). Default is json");

        CommandLine cmd;
        try {
            cmd = parser.parse(cliOptions, args);
        } catch (ParseException ex) {
            System.err.println("Fail to parse arguments: " + ex.getMessage());
            return 1;
        }

        String fileName = cmd.getOptionValue("s");
        String format = cmd.getOptionValue("f", "json"); // Par défaut JSON si non spécifié

        if (!format.equalsIgnoreCase("json") && !format.equalsIgnoreCase("csv")) {
            System.err.println("Format must be either 'json' or 'csv'");
            return 1;
        }

        List<String> positionalArgs = cmd.getArgList();
        if (positionalArgs.isEmpty()) {
            System.err.println("Missing Command");
            return 1;
        }

        String command = positionalArgs.get(0);

        // Créer le stockage approprié
        GroceryListStorage storage = format.equalsIgnoreCase("json") ?
                new GroceryListStorage.JsonStorage(fileName) :
                new GroceryListStorage.CsvStorage(fileName);

        // Exécuter la commande appropriée
        try {
            switch (command) {
                case "add" -> {
                    if (positionalArgs.size() < 3) {
                        System.err.println("Missing arguments");
                        return 1;
                    }

                    String itemName = positionalArgs.get(1);
                    int quantity;

                    try {
                        quantity = Integer.parseInt(positionalArgs.get(2));
                    } catch (NumberFormatException e) {
                        System.err.println("Quantity must be a number");
                        return 1;
                    }

                    if (quantity <= 0) {
                        System.err.println("Quantity must be positive");
                        return 1;
                    }

                    // Charger la liste de courses actuelle
                    List<GroceryItem> groceryList = storage.load();

                    // Vérifier si l'article existe déjà
                    boolean itemExists = false;
                    for (GroceryItem item : groceryList) {
                        if (item.getName().equals(itemName)) {
                            // Ajouter à la quantité existante
                            item.setQuantity(item.getQuantity() + quantity);
                            itemExists = true;
                            break;
                        }
                    }

                    // Si l'article n'existe pas, l'ajouter
                    if (!itemExists) {
                        groceryList.add(new GroceryItem(itemName, quantity));
                    }

                    storage.save(groceryList);
                    return 0;
                }
                case "list" -> {
                    // Charger et trier la liste par ordre alphabétique
                    List<GroceryItem> groceryList = storage.load();
                    groceryList.sort(Comparator.comparing(GroceryItem::getName));

                    for (GroceryItem item : groceryList) {
                        System.out.println(item);
                    }
                    return 0;
                }
                case "remove" -> {
                    if (positionalArgs.size() < 2) {
                        System.err.println("Missing arguments");
                        return 1;
                    }

                    String itemName = positionalArgs.get(1);
                    List<GroceryItem> groceryList = storage.load();

                    // Ne supprimer que les articles dont le nom correspond exactement
                    groceryList.removeIf(item -> item.getName().equals(itemName));

                    storage.save(groceryList);
                    return 0;
                }
                default -> {
                    System.err.println("Unknown command: " + command);
                    return 1;
                }
            }
        } catch (Exception e) {
            System.err.println("Error executing command: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }
}