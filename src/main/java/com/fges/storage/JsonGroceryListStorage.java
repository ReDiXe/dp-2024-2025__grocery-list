package com.fges.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.Iterator;

/**
 * Implémentation du stockage en format JSON avec support des catégories
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

    /// charge la liste de courses à partir d'un fichier JSON
    @Override
    public List<GroceryItem> load() throws IOException {
        Path filePath = Paths.get(fileName);
        List<GroceryItem> groceryList = new ArrayList<>();

        // Si le fichier n'existe pas ou est vide, retourner une liste vide
        if (!Files.exists(filePath) || Files.size(filePath) == 0) {
            return groceryList;
        }

        // Valider le format du fichier
        formatValidator.validateFileFormat(filePath, "json");

        try {
            JsonNode rootNode = objectMapper.readTree(filePath.toFile());

            if (rootNode.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> categoryFields = rootNode.fields();
                while (categoryFields.hasNext()) {
                    Map.Entry<String, JsonNode> categoryEntry = categoryFields.next();
                    String category = categoryEntry.getKey();
                    JsonNode categoryItems = categoryEntry.getValue();

                    if (categoryItems.isObject()) {
                        Iterator<Map.Entry<String, JsonNode>> itemFields = categoryItems.fields();
                        while (itemFields.hasNext()) {
                            Map.Entry<String, JsonNode> itemEntry = itemFields.next();
                            String itemName = itemEntry.getKey();
                            int quantity = itemEntry.getValue().asInt();
                            groceryList.add(new GroceryItem(itemName, quantity, category));
                        }
                    }
                }

                if (!groceryList.isEmpty()) {
                    return groceryList;
                }
            }

            try {
                Map<String, Integer> groceryMap = objectMapper.readValue(filePath.toFile(),
                        new TypeReference<Map<String, Integer>>() {});

                for (Map.Entry<String, Integer> entry : groceryMap.entrySet()) {
                    groceryList.add(new GroceryItem(entry.getKey(), entry.getValue(), "default"));
                }
                return groceryList;
            } catch (Exception e) {
                try {
                    List<GroceryItem> oldItems = objectMapper.readValue(filePath.toFile(),
                            new TypeReference<List<GroceryItem>>() {});

                    for (GroceryItem item : oldItems) {
                        if (item.getCategory() == null) {
                            item.setCategory("default");
                        }
                        groceryList.add(item);
                    }
                    return groceryList;
                } catch (Exception e2) {
                    try {
                        List<String> oldFormatList = objectMapper.readValue(filePath.toFile(),
                                new TypeReference<List<String>>() {});

                        for (String item : oldFormatList) {
                            if (item.contains(":")) {
                                String[] parts = item.split(":");
                                String name = parts[0].trim();
                                try {
                                    int quantity = Integer.parseInt(parts[1].trim());
                                    groceryList.add(new GroceryItem(name, quantity, "default"));
                                } catch (NumberFormatException nfe) {
                                    System.err.println("Warning: Ignoring malformed entry: " + item);
                                }
                            } else {
                                groceryList.add(new GroceryItem(item.trim(), 1, "default"));
                            }
                        }
                        return groceryList;
                    } catch (Exception e3) {
                        System.err.println("Error reading JSON file: " + e.getMessage());
                        throw new IOException("Failed to parse JSON file in any supported format", e);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
            throw new IOException("Failed to parse JSON file", e);
        }
    }

    /// saugegarde la liste de courses dans un fichier JSON
    @Override
    public void save(List<GroceryItem> groceryList) throws IOException {
        Path filePath = Paths.get(fileName);
        if (Files.exists(filePath) && Files.size(filePath) > 0) {
            formatValidator.validateFileFormat(filePath, "json");
        }

        Map<String, Map<String, Integer>> categorizedItems = new HashMap<>();

        for (GroceryItem item : groceryList) {
            String category = item.getCategory();
            if (category == null) {
                category = "default";
            }

            categorizedItems.putIfAbsent(category, new HashMap<>());
            categorizedItems.get(category).put(item.getName(), item.getQuantity());
        }

        objectMapper.writeValue(new File(fileName), categorizedItems);
    }
}