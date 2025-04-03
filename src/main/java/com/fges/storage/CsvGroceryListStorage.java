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

    @Override
    public List<GroceryItem> load() throws IOException {
        Path filePath = Paths.get(fileName);
        List<GroceryItem> groceryList = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return groceryList; // Retourner une liste vide si le fichier n'existe pas
        }

        // Vérifier la compatibilité du format si le fichier existe déjà
        if (Files.size(filePath) > 0) {
            formatValidator.validateFileFormat(filePath, "csv");
        }

        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            String[] header = reader.readNext(); // Lire l'en-tête
            if (header == null || header.length < 2 ||
                    !header[0].equalsIgnoreCase("name") ||
                    !header[1].equalsIgnoreCase("quantity")) {
                // Si l'en-tête n'est pas valide ou inexistant, retourner une liste vide
                System.err.println("Warning: CSV file has invalid header format");
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
                        System.err.println("Warning: Ignoring row with non-numeric quantity: " + line[0]);
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
            formatValidator.validateFileFormat(filePath, "csv");
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