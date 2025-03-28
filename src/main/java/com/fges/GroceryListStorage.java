package com.fges;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fges.GroceryItem;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interface et implémentations pour le stockage et le chargement de liste de courses
 */
public interface GroceryListStorage {

    /**
     * Charge la liste de courses depuis la source de données
     * @return Liste d'objets GroceryItem
     * @throws IOException Si une erreur de lecture se produit
     */
    List<GroceryItem> load() throws IOException;

    /**
     * Sauvegarde la liste de courses dans la source de données
     * @param groceryList Liste d'objets GroceryItem à sauvegarder
     * @throws IOException Si une erreur d'écriture se produit
     */
    void save(List<GroceryItem> groceryList) throws IOException;

    /**
     * Vérifie si le format spécifié est compatible avec le contenu du fichier existant
     * @param filePath Chemin du fichier à vérifier
     * @param requestedFormat Format demandé par l'utilisateur ("json" ou "csv")
     * @throws IOException Si le format est incompatible ou en cas d'erreur de lecture
     */
    default void validateFileFormat(Path filePath, String requestedFormat) throws IOException {
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

    /**
     * Implémentation du stockage en format JSON
     */
    class JsonStorage implements GroceryListStorage {
        private final String fileName;
        private final ObjectMapper objectMapper;

        public JsonStorage(String fileName) {
            this.fileName = fileName;
            this.objectMapper = new ObjectMapper();
        }

        @Override
        public List<GroceryItem> load() throws IOException {
            Path filePath = Paths.get(fileName);
            List<GroceryItem> groceryList = new ArrayList<>();

            if (!Files.exists(filePath)) {
                return groceryList; // Retourner une liste vide si le fichier n'existe pas
            }

            // Vérifier la compatibilité du format si le fichier existe déjà
            if (Files.size(filePath) > 0) {
                validateFileFormat(filePath, "json");
            }

            try {
                // Lire le fichier JSON comme un Map (clé=nom de l'article, valeur=quantité)
                Map<String, Integer> groceryMap = objectMapper.readValue(filePath.toFile(),
                        new TypeReference<Map<String, Integer>>() {});

                // Convertir le Map en liste d'objets GroceryItem
                for (Map.Entry<String, Integer> entry : groceryMap.entrySet()) {
                    groceryList.add(new GroceryItem(entry.getKey(), entry.getValue()));
                }
            } catch (Exception e) {
                // Si ça échoue, essayer d'autres formats pour la rétrocompatibilité
                try {
                    // Essayer de lire comme une liste d'objets GroceryItem (ancien format possible)
                    groceryList = objectMapper.readValue(filePath.toFile(),
                            new TypeReference<List<GroceryItem>>() {});
                } catch (Exception e2) {
                    try {
                        // Si ça échoue encore, essayer de lire comme une liste de chaînes
                        List<String> oldFormatList = objectMapper.readValue(filePath.toFile(),
                                new TypeReference<List<String>>() {});

                        // Convertir l'ancien format vers le nouveau format
                        for (String item : oldFormatList) {
                            if (item.contains(":")) {
                                String[] parts = item.split(":");
                                String name = parts[0].trim();
                                try {
                                    int quantity = Integer.parseInt(parts[1].trim());
                                    groceryList.add(new GroceryItem(name, quantity));
                                } catch (NumberFormatException nfe) {
                                    // Ignorer les entrées mal formatées
                                }
                            } else {
                                // Si l'item n'a pas de format spécifique, on le considère comme un item de quantité 1
                                groceryList.add(new GroceryItem(item.trim(), 1));
                            }
                        }
                    } catch (Exception e3) {
                        // Si tous les formats échouent, logger l'erreur et retourner une liste vide
                        System.err.println("Erreur lors de la lecture du fichier JSON: " + e.getMessage());
                    }
                }
            }

            return groceryList;
        }

        @Override
        public void save(List<GroceryItem> groceryList) throws IOException {
            // Vérifier la compatibilité du format si le fichier existe déjà
            Path filePath = Paths.get(fileName);
            if (Files.exists(filePath) && Files.size(filePath) > 0) {
                validateFileFormat(filePath, "json");
            }

            // Convertir la liste en Map pour le format JSON demandé
            Map<String, Integer> groceryMap = new HashMap<>();
            for (GroceryItem item : groceryList) {
                groceryMap.put(item.getName(), item.getQuantity());
            }

            objectMapper.writeValue(new File(fileName), groceryMap);
        }
    }

    /**
     * Implémentation du stockage en format CSV
     */
    class CsvStorage implements GroceryListStorage {
        private final String fileName;

        public CsvStorage(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public List<GroceryItem> load() throws IOException {
            Path filePath = Paths.get(fileName);
            List<GroceryItem> groceryList = new ArrayList<>();

            if (!Files.exists(filePath)) {
                return groceryList; // Retourner une liste vide si le fichier n'existe pas
            }

            // Vérifier la compatibilité du format si le fichier existe déjà
            if (Files.size(filePath) > 0) {
                validateFileFormat(filePath, "csv");
            }

            try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
                String[] header = reader.readNext(); // Lire l'en-tête
                if (header == null || header.length < 2 ||
                        !header[0].equalsIgnoreCase("name") ||
                        !header[1].equalsIgnoreCase("quantity")) {
                    // Si l'en-tête n'est pas valide ou inexistant, retourner une liste vide
                    return groceryList;
                }

                String[] line;
                while ((line = reader.readNext()) != null) {
                    if (line.length >= 2) {
                        try {
                            String name = line[0];
                            int quantity = Integer.parseInt(line[1]);
                            groceryList.add(new GroceryItem(name, quantity));
                        } catch (NumberFormatException e) {
                            // Ignorer les lignes avec une quantité non numérique
                            System.err.println("Warning: Skipping line with non-numeric quantity: " + line[0]);
                        }
                    }
                }
            } catch (CsvValidationException e) {
                throw new IOException("Error reading CSV file: " + e.getMessage(), e);
            }

            return groceryList;
        }

        @Override
        public void save(List<GroceryItem> groceryList) throws IOException {
            // Vérifier la compatibilité du format si le fichier existe déjà
            Path filePath = Paths.get(fileName);
            if (Files.exists(filePath) && Files.size(filePath) > 0) {
                validateFileFormat(filePath, "csv");
            }

            try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
                // Écrire l'en-tête
                writer.writeNext(new String[]{"name", "quantity"});

                // Écrire les données
                for (GroceryItem item : groceryList) {
                    writer.writeNext(new String[]{
                            item.getName(),
                            String.valueOf(item.getQuantity())
                    });
                }
            }
        }
    }
}