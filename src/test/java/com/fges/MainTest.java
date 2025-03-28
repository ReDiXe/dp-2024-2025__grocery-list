package com.fges;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.fges.GroceryItem;
import com.fges.GroceryListStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class MainTest {

    @TempDir
    Path tempDir;

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();
    private PrintStream standardOut;
    private PrintStream standardErr;

    @BeforeEach
    void setUp() {
        // Capture des sorties standard et d'erreur
        standardOut = System.out;
        standardErr = System.err;
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(errorStreamCaptor));
    }

    @Test
    void testAddCommand() throws IOException {
        // Créer un fichier temporaire
        File tempFile = tempDir.resolve("groceries.json").toFile();
        String[] args = {"-s", tempFile.getAbsolutePath(), "add", "pommes", "5"};

        // Exécuter la commande d'ajout
        assertEquals(0, Main.exec(args));

        // Vérifier que le fichier a été créé
        assertTrue(tempFile.exists());

        // Exécuter une seconde commande d'ajout pour le même article
        args = new String[]{"-s", tempFile.getAbsolutePath(), "add", "pommes", "3"};
        assertEquals(0, Main.exec(args));

        // Lister pour vérifier le résultat
        outputStreamCaptor.reset();
        args = new String[]{"-s", tempFile.getAbsolutePath(), "list"};
        assertEquals(0, Main.exec(args));

        // Vérifier que la quantité a été additionnée
        assertEquals("pommes: 8", outputStreamCaptor.toString().trim());
    }

    @Test
    void testListCommand() throws IOException {
        // Créer un fichier temporaire
        File tempFile = tempDir.resolve("groceries.json").toFile();

        // Ajouter plusieurs articles
        String[] args = {"-s", tempFile.getAbsolutePath(), "add", "bananes", "3"};
        assertEquals(0, Main.exec(args));

        args = new String[]{"-s", tempFile.getAbsolutePath(), "add", "pommes", "5"};
        assertEquals(0, Main.exec(args));

        args = new String[]{"-s", tempFile.getAbsolutePath(), "add", "carottes", "10"};
        assertEquals(0, Main.exec(args));

        // Lister pour vérifier
        outputStreamCaptor.reset();
        args = new String[]{"-s", tempFile.getAbsolutePath(), "list"};
        assertEquals(0, Main.exec(args));

        // Vérifier l'ordre alphabétique et le contenu
        String output = outputStreamCaptor.toString().trim();
        String[] lines = output.split(System.lineSeparator());
        assertEquals(3, lines.length);
        assertEquals("bananes: 3", lines[0]);
        assertEquals("carottes: 10", lines[1]);
        assertEquals("pommes: 5", lines[2]);
    }

    @Test
    void testRemoveCommand() throws IOException {
        // Créer un fichier temporaire
        File tempFile = tempDir.resolve("groceries.json").toFile();

        // Ajouter plusieurs articles
        String[] args = {"-s", tempFile.getAbsolutePath(), "add", "bananes", "3"};
        assertEquals(0, Main.exec(args));

        args = new String[]{"-s", tempFile.getAbsolutePath(), "add", "pommes", "5"};
        assertEquals(0, Main.exec(args));

        // Supprimer un article
        args = new String[]{"-s", tempFile.getAbsolutePath(), "remove", "bananes"};
        assertEquals(0, Main.exec(args));

        // Lister pour vérifier
        outputStreamCaptor.reset();
        args = new String[]{"-s", tempFile.getAbsolutePath(), "list"};
        assertEquals(0, Main.exec(args));

        // Vérifier que seul l'article non supprimé est présent
        assertEquals("pommes: 5", outputStreamCaptor.toString().trim());
    }

    @Test
    void testCsvFormat() throws IOException {
        // Créer un fichier temporaire
        File tempFile = tempDir.resolve("groceries.csv").toFile();

        // Ajouter un article avec format CSV
        String[] args = {"-s", tempFile.getAbsolutePath(), "-f", "csv", "add", "oranges", "7"};
        assertEquals(0, Main.exec(args));

        // Vérifier que le fichier a été créé
        assertTrue(tempFile.exists());

        // Lister pour vérifier
        outputStreamCaptor.reset();
        args = new String[]{"-s", tempFile.getAbsolutePath(), "-f", "csv", "list"};
        assertEquals(0, Main.exec(args));

        // Vérifier le contenu
        assertEquals("oranges: 7", outputStreamCaptor.toString().trim());
    }

    @Test
    void testInvalidFormat() throws IOException {
        // Tester avec un format invalide
        String[] args = {"-s", "groceries.txt", "-f", "xml", "list"};
        assertEquals(1, Main.exec(args));

        // Vérifier le message d'erreur
        assertTrue(errorStreamCaptor.toString().contains("Format must be either 'json' or 'csv'"));
    }

    @Test
    void testMissingCommand() throws IOException {
        // Tester sans commande
        String[] args = {"-s", "groceries.json"};
        assertEquals(1, Main.exec(args));

        // Vérifier le message d'erreur
        assertTrue(errorStreamCaptor.toString().contains("Missing Command"));
    }

    @Test
    void testUnknownCommand() throws IOException {
        // Tester avec une commande inconnue
        String[] args = {"-s", "groceries.json", "unknown"};
        assertEquals(1, Main.exec(args));

        // Vérifier le message d'erreur
        assertTrue(errorStreamCaptor.toString().contains("Unknown command: unknown"));
    }

    @Test
    void testInvalidQuantity() throws IOException {
        // Tester avec une quantité non numérique
        String[] args = {"-s", "groceries.json", "add", "pommes", "abc"};
        assertEquals(1, Main.exec(args));

        // Vérifier le message d'erreur
        assertTrue(errorStreamCaptor.toString().contains("Quantity must be a number"));

        // Tester avec une quantité négative
        errorStreamCaptor.reset();
        args = new String[]{"-s", "groceries.json", "add", "pommes", "-5"};
        assertEquals(1, Main.exec(args));

        // Vérifier le message d'erreur
        assertTrue(errorStreamCaptor.toString().contains("Quantity must be positive"));
    }
}