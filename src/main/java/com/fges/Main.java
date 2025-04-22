package com.fges;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fges.command.Command;
import com.fges.command.CommandFactory;
import com.fges.storage.GroceryListStorage;
import com.fges.storage.JsonGroceryListStorage;
import com.fges.storage.CsvGroceryListStorage;
import com.fges.util.CliParser;
import org.apache.commons.cli.CommandLine;

import java.io.IOException;

/**
 * Point d'entrée principal de l'application de gestion de liste de courses
 */
public class Main {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        System.exit(exec(args));
    }

    /// Exécute la commande principale
    public static int exec(String[] args) throws IOException {
        try {
            /// etape 1 : parsing des arguments de la ligne de commande
            CliParser cliParser = new CliParser();
            CommandLine cmd = cliParser.parse(args);

            if (cmd == null) {
                return 1;
            }

            /// etape 2: récupération des arguments de ligne de commande
            java.util.List<String> positionalArgs = cmd.getArgList();
            if (positionalArgs.isEmpty()) {
                System.err.println("Missing Command");
                return 1;
            }

            String commandName = positionalArgs.getFirst();

            // La commande info est spéciale et n'a pas besoin de stockage
            if (commandName.equals("info")) {
                Command command = CommandFactory.getCommand(commandName, null, positionalArgs, cmd);
                if (command == null) {
                    System.err.println("Error initializing info command");
                    return 1;
                }
                return command.execute();
            }

            // Pour les autres commandes, vérifier si le fichier source est spécifié
            String fileName = cmd.getOptionValue("s");
            if (fileName == null) {
                System.err.println("Source file required for command: " + commandName);
                return 1;
            }

            String format = cmd.getOptionValue("f", "json");

            /// etape 3: création de l'instance de stockage (json ou csv)
            GroceryListStorage storage;
            if (format.equalsIgnoreCase("json")) {
                storage = new JsonGroceryListStorage(fileName);
            } else if (format.equalsIgnoreCase("csv")) {
                storage = new CsvGroceryListStorage(fileName);
            } else {
                System.err.println("Format must be either 'json' or 'csv'");
                return 1;
            }

            /// etape 4: exécution de la commande
            Command command = CommandFactory.getCommand(commandName, storage, positionalArgs, cmd);
            if (command == null) {
                System.err.println("Unknown command: " + commandName);
                return 1;
            }

            return command.execute();

        } catch (Exception e) {
            System.err.println("Error executing command: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }
}