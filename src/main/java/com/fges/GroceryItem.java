package com.fges;

/**
 * Représente un article de liste de courses avec un nom et une quantité
 */
public class GroceryItem {
    private String name;
    private int quantity;

    /**
     * Constructeur vide pour Jackson
     */
    public GroceryItem() {
    }

    /**
     * Constructeur avec nom et quantité
     * @param name Nom de l'article
     * @param quantity Quantité de l'article
     */
    public GroceryItem(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    /**
     * Getter pour le nom
     * @return Le nom de l'article
     */
    public String getName() {
        return name;
    }

    /**
     * Setter pour le nom
     * @param name Le nouveau nom de l'article
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter pour la quantité
     * @return La quantité de l'article
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Setter pour la quantité
     * @param quantity La nouvelle quantité de l'article
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Représentation textuelle de l'article
     * @return Une chaîne au format "nom: quantité"
     */
    @Override
    public String toString() {
        return name + ": " + quantity;
    }
}