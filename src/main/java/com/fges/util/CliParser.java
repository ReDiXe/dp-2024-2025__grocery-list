package com.fges.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * analyse les arguments de la ligne de commande
 */
public class CliParser {
    private final Options cliOptions;
    private final Options infoOptions;
    private final CommandLineParser parser;

    public CliParser() {
        cliOptions = new Options();
        infoOptions = new Options();
        parser = new DefaultParser();

        cliOptions.addRequiredOption("s", "source", true, "ficher avec la liste de courses");
        cliOptions.addOption("f", "format", true, "Format du fichier (json ou csv). Par défaut 'json'");
        cliOptions.addOption("c", "category", true, "Catégorie de l'article, par défaut 'default'");

        infoOptions.addOption("s", "source", true, "ficher avec la liste de courses");
        infoOptions.addOption("f", "format", true, "Format du fichier (json ou csv). Par défaut 'json'");
        infoOptions.addOption("c", "category", true, "Catégorie de l'article, par défaut 'default'");
    }

    /// parse les arguments de la ligne de commande
    public CommandLine parse(String[] args) {
        try {
            boolean isInfoCommand = false;
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("info")) {
                    isInfoCommand = true;
                    break;
                }
            }

            if (isInfoCommand) {
                return parser.parse(infoOptions, args);
            } else {
                return parser.parse(cliOptions, args);
            }
        } catch (ParseException ex) {
            System.err.println("Fail to parse arguments: " + ex.getMessage());
            return null;
        }
    }

    /// retourne les options de la ligne de commande
    public Options getOptions() {
        return cliOptions;
    }
}