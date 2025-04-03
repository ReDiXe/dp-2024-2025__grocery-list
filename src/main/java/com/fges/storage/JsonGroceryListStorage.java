package com.fges.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fges.model.GroceryItem;
import com.fges.util.FormatValidator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implémentation du stockage en format JSON
 */
public class JsonGroceryListStorage implements GroceryListStorage {
    private final String fileName;
    private final ObjectMapper objectMapper;
    private final FormatValidator formatValidator;

    public JsonGroceryListStorage(String fileName) {
        this.fileName = fileName;
        this.objectMapper = new ObjectMapper();
        this.formatValidator = new FormatValidator();
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
            formatValidator.validateFileFormat(filePath, "json");
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
                                System.err.println("Warning: Ignoring malformed entry: " + item);
                            }
                        } else {
                            // Si l'item n'a pas de format spécifique, on le considère comme un item de quantité 1
                            groceryList.add(new GroceryItem(item.trim(), 1));
                        }
                    }
                } catch (Exception e3) {
                    // Si tous les formats échouent, logger l'erreur et retourner une liste vide
                    System.err.println("Error reading JSON file: " + e.getMessage());
                    throw new IOException("Failed to parse JSON file in any supported format", e);
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
            formatValidator.validateFileFormat(filePath, "json");
        }

        // Convertir la liste en Map pour le format JSON demandé
        Map<String, Integer> groceryMap = new HashMap<>();
        for (GroceryItem item : groceryList) {
            groceryMap.put(item.getName(), item.getQuantity());
        }

        objectMapper.writeValue(new File(fileName), groceryMap);
    }
}