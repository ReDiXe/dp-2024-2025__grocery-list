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
    private final CommandLineParser parser;

    public CliParser() {
        cliOptions = new Options();
        parser = new DefaultParser();

        cliOptions.addRequiredOption("s", "source", true, "ficher avec la liste de courses");
        cliOptions.addOption("f", "format", true, "Format du fichier (json ou csv). Par défaut 'json'");
        cliOptions.addOption("c", "category", true, "Catégorie de l'article, par défaut 'default'");
    }

    /// parse les arguments de la ligne de commande
    public CommandLine parse(String[] args) {
        try {
            return parser.parse(cliOptions, args);
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