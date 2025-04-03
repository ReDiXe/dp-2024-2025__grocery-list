package com.fges.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Valide le format des fichiers
 */
public class FormatValidator {

    /// Vérifie si le fichier est au format JSON ou CSV
    public void validateFileFormat(Path filePath, String requestedFormat) throws IOException {
        String content = Files.readString(filePath);
        content = content.trim();

        boolean seemsToBeJson = false;
        boolean seemsToBeCsv = false;

        // Détection simple du format JSON
        if ((content.startsWith("{") && content.endsWith("}")) ||
                (content.startsWith("[") && content.endsWith("]"))) {
            seemsToBeJson = true;
        }

        // Détection simple du format CSV
        if (content.contains(",") && (content.toLowerCase().startsWith("name,quantity,category") ||
                content.split("\n").length > 1 && content.split("\n")[0].toLowerCase().contains("name"))) {
            seemsToBeCsv = true;
        }

        // Vérification de compatibilité
        if (seemsToBeJson && !requestedFormat.equalsIgnoreCase("json")) {
            throw new IOException("this file seems to be in JSON format, but you are trying to use it with the format "
                    + requestedFormat.toUpperCase() + ". use option --format json.");
        }
        if (seemsToBeCsv && !requestedFormat.equalsIgnoreCase("csv")) {
            throw new IOException("this file seems to be in CSV format, but you are trying to use it with the format "
                    + requestedFormat.toUpperCase() + ". use option --format csv.");
        }
    }
}