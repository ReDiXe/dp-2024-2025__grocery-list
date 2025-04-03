package com.fges.storage;

import com.fges.model.GroceryItem;
import com.fges.util.FormatValidator;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation du stockage en format CSV
 */
public class CsvGroceryListStorage implements GroceryListStorage {
    private final String fileName;
    private final FormatValidator formatValidator;

    public CsvGroceryListStorage(String fileName) {
        this.fileName = fileName;
        this.formatValidator = new FormatValidator();
    }

    ///charge la liste de courses à partir d'un fichier CSV
    @Override
    public List<GroceryItem> load() throws IOException {
        Path filePath = Paths.get(fileName);
        List<GroceryItem> groceryList = new ArrayList<>();

        // Si le fichier n'existe pas ou est vide, retourner une liste vide
        if (!Files.exists(filePath)) {
            return groceryList;
        }

        if (Files.size(filePath) > 0) {
            formatValidator.validateFileFormat(filePath, "csv");
        }

        // Lire le fichier CSV
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            String[] header = reader.readNext();

            boolean hasCategory = false;
            if (header != null && header.length >= 3 &&
                    header[0].equalsIgnoreCase("name") &&
                    header[1].equalsIgnoreCase("quantity") &&
                    header[2].equalsIgnoreCase("category")) {
                hasCategory = true;
            } else if (header == null || header.length < 2 ||
                    !header[0].equalsIgnoreCase("name") ||
                    !header[1].equalsIgnoreCase("quantity")) {
                System.err.println("Warning: CSV file has invalid header format");
                return groceryList;
            }

            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line.length >= 2) {
                    try {
                        String name = line[0];
                        int quantity = Integer.parseInt(line[1]);

                        String category = "default";
                        if (hasCategory && line.length >= 3 && line[2] != null && !line[2].isEmpty()) {
                            category = line[2];
                        }

                        groceryList.add(new GroceryItem(name, quantity, category));
                    } catch (NumberFormatException e) {
                        System.err.println("Warning: Ignoring row with non-numeric quantity: " + line[0]);
                    }
                }
            }
        } catch (CsvValidationException e) {
            throw new IOException("Error reading CSV file: " + e.getMessage(), e);
        }

        return groceryList;
    }

    /// sauvegarde la liste de courses dans un fichier CSV
    @Override
    public void save(List<GroceryItem> groceryList) throws IOException {
        Path filePath = Paths.get(fileName);
        if (Files.exists(filePath) && Files.size(filePath) > 0) {
            formatValidator.validateFileFormat(filePath, "csv");
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            writer.writeNext(new String[]{"name", "quantity", "category"});

            for (GroceryItem item : groceryList) {
                writer.writeNext(new String[]{
                        item.getName(),
                        String.valueOf(item.getQuantity()),
                        item.getCategory() != null ? item.getCategory() : "default"
                });
            }
        }
    }
}