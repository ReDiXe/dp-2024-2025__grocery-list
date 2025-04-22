package com.fges.command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Commande pour afficher des informations système
 * Cette commande affiche la date du jour, le système d'exploitation et la version de Java
 */
public class InfoCommand implements Command {

    @Override
    public int execute() throws Exception {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = today.format(formatter);

        String osName = System.getProperty("os.name");
        String javaVersion = System.getProperty("java.version");

        System.out.println("Today's date: " + formattedDate);
        System.out.println("Operating System: " + osName);
        System.out.println("Java version: " + javaVersion);

        return 0;
    }
}