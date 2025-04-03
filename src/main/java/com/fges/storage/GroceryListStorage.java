package com.fges.storage;

import com.fges.model.GroceryItem;

import java.io.IOException;
import java.util.List;

/**
 * Interface pour le stockage et le chargement de liste de courses
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
}