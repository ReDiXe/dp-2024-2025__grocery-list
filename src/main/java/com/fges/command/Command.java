package com.fges.command;

/**
 * Interface pour toutes les commandes de l'application
 */
public interface Command {
    /**
     * Exécute la commande (ajout, suppression, liste)
     * @return code de retour (0 pour succès, autre pour erreur)
     * @throws Exception si une erreur se produit pendant l'exécution
     */
    int execute() throws Exception;
}