package com.fges.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utilitaire pour valider le format des fichiers
 */
public class FormatValidator {

    /**
     * Vérifie si le format spécifié est compatible avec le contenu du fichier existant
     * @param filePath Chemin du fichier à vérifier
     * @param requestedFormat Format demandé par l'utilisateur ("json" ou "csv")
     * @throws IOException Si le format est incompatible ou en cas d'erreur de lecture
     */
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
        if (content.contains(",") && (content.toLowerCase().startsWith("name,quantity") ||
                content.split("\n").length > 1 && content.split("\n")[0].toLowerCase().contains("name"))) {
            seemsToBeCsv = true;
        }

        // Vérification de compatibilité
        if (seemsToBeJson && !requestedFormat.equalsIgnoreCase("json")) {
            throw new IOException("Le fichier semble être au format JSON, mais vous essayez de l'utiliser avec le format "
                    + requestedFormat.toUpperCase() + ". Utilisez l'option --format json.");
        }

        if (seemsToBeCsv && !requestedFormat.equalsIgnoreCase("csv")) {
            throw new IOException("Le fichier semble être au format CSV, mais vous essayez de l'utiliser avec le format "
                    + requestedFormat.toUpperCase() + ". Utilisez l'option --format csv.");
        }
    }
}