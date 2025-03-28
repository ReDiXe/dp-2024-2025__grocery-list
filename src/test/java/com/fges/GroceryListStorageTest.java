package com.fges;

import static org.junit.jupiter.api.Assertions.*;

import com.fges.GroceryItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class GroceryListStorageTest {

    @TempDir
    Path tempDir;

    @Test
    void testJsonSaveAndLoad() throws IOException {
        // Créer un fichier temporaire
        File jsonFile = tempDir.resolve("test.json").toFile();
        GroceryListStorage jsonStorage = new GroceryListStorage.JsonStorage(jsonFile.getAbsolutePath());

        // Créer une liste de test
        List<GroceryItem> testList = Arrays.asList(
                new GroceryItem("pommes", 5),
                new GroceryItem("bananes", 3),
                new GroceryItem("carottes", 10)
        );

        // Sauvegarder la liste
        jsonStorage.save(testList);

        // Vérifier que le fichier existe
        assertTrue(jsonFile.exists());

        // Charger la liste
        List<GroceryItem> loadedList = jsonStorage.load();

        // Vérifier le contenu
        assertEquals(3, loadedList.size());

        // Vérifier que tous les éléments sont présents avec les bonnes quantités
        boolean foundPommes = false;
        boolean foundBananes = false;
        boolean foundCarottes = false;

        for (GroceryItem item : loadedList) {
            switch (item.getName()) {
                case "pommes":
                    assertEquals(5, item.getQuantity());
                    foundPommes = true;
                    break;
                case "bananes":
                    assertEquals(3, item.getQuantity());
                    foundBananes = true;
                    break;
                case "carottes":
                    assertEquals(10, item.getQuantity());
                    foundCarottes = true;
                    break;
            }
        }

        assertTrue(foundPommes && foundBananes && foundCarottes);
    }

    @Test
    void testCsvSaveAndLoad() throws IOException {
        // Créer un fichier temporaire
        File csvFile = tempDir.resolve("test.csv").toFile();
        GroceryListStorage csvStorage = new GroceryListStorage.CsvStorage(csvFile.getAbsolutePath());

        // Créer une liste de test
        List<GroceryItem> testList = Arrays.asList(
                new GroceryItem("pommes", 5),
                new GroceryItem("bananes", 3),
                new GroceryItem("carottes", 10)
        );

        // Sauvegarder la liste
        csvStorage.save(testList);

        // Vérifier que le fichier existe
        assertTrue(csvFile.exists());

        // Charger la liste
        List<GroceryItem> loadedList = csvStorage.load();

        // Vérifier le contenu
        assertEquals(3, loadedList.size());

        // Vérifier que tous les éléments sont présents avec les bonnes quantités
        boolean foundPommes = false;
        boolean foundBananes = false;
        boolean foundCarottes = false;

        for (GroceryItem item : loadedList) {
            switch (item.getName()) {
                case "pommes":
                    assertEquals(5, item.getQuantity());
                    foundPommes = true;
                    break;
                case "bananes":
                    assertEquals(3, item.getQuantity());
                    foundBananes = true;
                    break;
                case "carottes":
                    assertEquals(10, item.getQuantity());
                    foundCarottes = true;
                    break;
            }
        }

        assertTrue(foundPommes && foundBananes && foundCarottes);
    }

    @Test
    void testEmptyFile() throws IOException {
        // Créer un fichier vide
        File emptyFile = tempDir.resolve("empty.json").toFile();
        emptyFile.createNewFile();

        GroceryListStorage jsonStorage = new GroceryListStorage.JsonStorage(emptyFile.getAbsolutePath());

        // Charger le fichier vide
        List<GroceryItem> loadedList = jsonStorage.load();

        // Vérifier que la liste est vide
        assertTrue(loadedList.isEmpty());
    }

    @Test
    void testNonExistentFile() throws IOException {
        // Créer un chemin vers un fichier qui n'existe pas
        File nonExistentFile = tempDir.resolve("nonexistent.json").toFile();

        GroceryListStorage jsonStorage = new GroceryListStorage.JsonStorage(nonExistentFile.getAbsolutePath());

        // Charger le fichier inexistant
        List<GroceryItem> loadedList = jsonStorage.load();

        // Vérifier que la liste est vide
        assertTrue(loadedList.isEmpty());
    }

    @Test
    void testFormatValidation() throws IOException {
        // Créer un fichier JSON
        File jsonFile = tempDir.resolve("test.json").toFile();
        GroceryListStorage jsonStorage = new GroceryListStorage.JsonStorage(jsonFile.getAbsolutePath());

        // Sauvegarder des données au format JSON
        List<GroceryItem> testList = Arrays.asList(new GroceryItem("pommes", 5));
        jsonStorage.save(testList);

        // Essayer de charger comme CSV devrait échouer
        GroceryListStorage csvStorage = new GroceryListStorage.CsvStorage(jsonFile.getAbsolutePath());

        // Vérifier l'exception
        IOException exception = assertThrows(IOException.class, () -> csvStorage.load());
        assertTrue(exception.getMessage().contains("Le fichier semble être au format JSON"));
    }

    @Test
    void testJsonBackwardCompatibility() throws IOException {
        // Créer un fichier au format ancien (liste d'objets)
        File jsonFile = tempDir.resolve("old-format.json").toFile();
        String oldFormat = "[{\"name\":\"pommes\",\"quantity\":5},{\"name\":\"bananes\",\"quantity\":3}]";
        Files.writeString(jsonFile.toPath(), oldFormat);

        // Charger avec le stockage actuel
        GroceryListStorage jsonStorage = new GroceryListStorage.JsonStorage(jsonFile.getAbsolutePath());
        List<GroceryItem> loadedList = jsonStorage.load();

        // Vérifier le contenu
        assertEquals(2, loadedList.size());

        // Vérifier les éléments
        boolean foundPommes = false;
        boolean foundBananes = false;

        for (GroceryItem item : loadedList) {
            switch (item.getName()) {
                case "pommes":
                    assertEquals(5, item.getQuantity());
                    foundPommes = true;
                    break;
                case "bananes":
                    assertEquals(3, item.getQuantity());
                    foundBananes = true;
                    break;
            }
        }

        assertTrue(foundPommes && foundBananes);

        // Tester aussi un format très ancien (liste de chaînes)
        File veryOldFile = tempDir.resolve("very-old-format.json").toFile();
        String veryOldFormat = "[\"pommes: 5\", \"bananes: 3\"]";
        Files.writeString(veryOldFile.toPath(), veryOldFormat);

        // Charger avec le stockage actuel
        jsonStorage = new GroceryListStorage.JsonStorage(veryOldFile.getAbsolutePath());
        loadedList = jsonStorage.load();

        // Vérifier le contenu
        assertEquals(2, loadedList.size());

        // Vérifier les éléments
        foundPommes = false;
        foundBananes = false;

        for (GroceryItem item : loadedList) {
            switch (item.getName()) {
                case "pommes":
                    assertEquals(5, item.getQuantity());
                    foundPommes = true;
                    break;
                case "bananes":
                    assertEquals(3, item.getQuantity());
                    foundBananes = true;
                    break;
            }
        }

        assertTrue(foundPommes && foundBananes);
    }
}