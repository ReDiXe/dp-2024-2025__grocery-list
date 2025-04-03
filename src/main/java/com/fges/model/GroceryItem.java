package com.fges.model;

/**
 * Représente un article de liste de courses avec un nom, une quantité et une catégorie
 */
public class GroceryItem {
    private String name;
    private int quantity;
    private String category;

    public GroceryItem() {
        this.category = "default";
    }

    public GroceryItem(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
        this.category = "default";
    }

    public GroceryItem(String name, int quantity, String category) {
        this.name = name;
        this.quantity = quantity;
        this.category = (category != null && !category.isEmpty()) ? category : "default";
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = (category != null && !category.isEmpty()) ? category : "default";
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