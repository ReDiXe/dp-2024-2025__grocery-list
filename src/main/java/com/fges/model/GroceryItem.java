package com.fges.model;

/**
 * Représente un article de liste de courses avec un nom et une quantité
 */
public class GroceryItem {
    private String name;
    private int quantity;

    public GroceryItem() {
        // Constructeur vide pour Jackson
    }

    public GroceryItem(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Incrémente la quantité de l'article par le montant spécifié
     * @param amount La quantité à ajouter
     */
    public void incrementQuantity(int amount) {
        this.quantity += amount;
    }

    @Override
    public String toString() {
        return name + ": " + quantity;
    }
}