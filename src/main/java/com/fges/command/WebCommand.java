// WebCommand.java
package com.fges.command;

import com.fges.storage.GroceryListStorage;
import com.fges.web.GroceryShopAdapter;
import fr.anthonyquere.GroceryShopServer;
import org.apache.commons.cli.CommandLine;

import java.util.List;

/**
 * Commande pour démarrer un serveur web pour la liste de courses
 */
public class WebCommand implements Command {

    private final GroceryListStorage storage;
    private final List<String> args;

    public WebCommand(GroceryListStorage storage, List<String> args) {
        this.storage = storage;
        this.args = args;
    }

    @Override
    public int execute() throws Exception {
        if (args.size() < 2) {
            System.err.println("Missing port argument for web command.");
            return 1;
        }

        int port;
        try {
            port = Integer.parseInt(args.get(1));
        } catch (NumberFormatException e) {
            System.err.println("Port must be a number.");
            return 1;
        }

        GroceryShopAdapter groceryShop = new GroceryShopAdapter(storage);
        GroceryShopServer server = new GroceryShopServer(groceryShop);
        server.start(port);

        System.out.println("Grocery shop server started at http://localhost:" + port);

        // Bloque le thread principal pour garder le serveur en marche
        Thread.currentThread().join();

        return 0;
    }
}
