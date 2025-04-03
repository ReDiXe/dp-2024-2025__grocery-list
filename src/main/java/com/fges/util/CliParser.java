package com.fges.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Utilitaire pour analyser les arguments de la ligne de commande
 */
public class CliParser {
    private final Options cliOptions;
    private final CommandLineParser parser;

    public CliParser() {
        cliOptions = new Options();
        parser = new DefaultParser();

        // Configuration des options acceptées
        cliOptions.addRequiredOption("s", "source", true, "File containing the grocery list");
        cliOptions.addOption("f", "format", true, "Format of the file (json or csv). Default is json");
    }

    /**
     * Parse les arguments de la ligne de commande
     * @param args Arguments à analyser
     * @return Objet CommandLine ou null en cas d'erreur
     */
    public CommandLine parse(String[] args) {
        try {
            return parser.parse(cliOptions, args);
        } catch (ParseException ex) {
            System.err.println("Fail to parse arguments: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Retourne les options configurées pour l'application
     * @return Options de ligne de commande
     */
    public Options getOptions() {
        return cliOptions;
    }
}