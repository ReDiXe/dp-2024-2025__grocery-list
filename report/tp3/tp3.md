# Rapport TP3 de Reda Sebaaoui et Nathan Vandamme

## Problème d'origine

Pour ce TP3, l'objectif était d'étendre l'application de gestion de liste de courses pour ajouter une nouvelle commande "info" qui affiche des informations système telles que la date du jour, le système d'exploitation et la version de Java.

## Détails des modifications

Pour implémenter cette nouvelle fonctionnalité, les modifications suivantes ont été réalisées :

1. **Création d'une nouvelle classe `InfoCommand`** - Cette classe implémente l'interface `Command` et sa méthode `execute()` affiche les informations système demandées sans nécessiter de stockage.

2. **Mise à jour de la classe `CommandFactory`** - Ajout d'un nouveau cas dans la méthode `getCommand()` pour prendre en charge la commande "info" et retourner une instance de `InfoCommand`.

3. **Modification de la classe `CliParser`** - Adaptation de la classe pour gérer le cas spécial de la commande "info" qui ne nécessite pas de fichier source. Création d'un jeu d'options spécifique où l'option "-s" (source) n'est pas obligatoire.

4. **Modification de la classe `Main`** - Ajout d'une logique spéciale pour traiter la commande "info" différemment des autres commandes, en particulier pour ne pas exiger de fichier source.

5. **Création de tests unitaires** - Implémentation de tests pour la nouvelle classe `InfoCommand` afin de vérifier que les informations système sont correctement affichées.

6. **Mise à jour du diagramme de séquence** - Ajout de la commande "info" dans le diagramme de séquence pour illustrer son intégration dans l'architecture existante.

## Difficultés rencontrées

Les principales difficultés rencontrées lors de l'implémentation de cette fonctionnalité ont été :

1. **Gestion des dépendances manquantes** - Des problèmes avec les dépendances Maven (OpenCSV et Mockito) ont dû être résolus pour assurer le bon fonctionnement du projet.

2. **Adaptation du `CliParser`** - La modification du `CliParser` pour prendre en charge des commandes qui ne nécessitent pas d'options obligatoires (comme "-s") a nécessité une refactorisation non triviale.

3. **Traitement spécial dans `Main`** - La gestion différenciée des commandes qui nécessitent ou non un fichier de stockage a requis une adaptation du flux principal.

## Principes de conception appliqués

Cette implémentation a suivi plusieurs principes de conception importants :

1. **Principe de responsabilité unique (SRP)** - Chaque classe a une responsabilité bien définie. La classe `InfoCommand` ne s'occupe que d'afficher les informations système.

2. **Ouvert/Fermé (OCP)** - L'architecture existante a permis d'ajouter une nouvelle commande sans modifier le comportement des commandes existantes.

3. **Substitution de Liskov (LSP)** - `InfoCommand` implémente l'interface `Command` et peut être utilisée de manière polymorphe comme les autres commandes.

4. **Design Pattern Command** - Ajout d'une nouvelle implémentation à la hiérarchie de commandes existante.

5. **Design Pattern Factory** - Extension de la fabrique de commandes pour prendre en charge la nouvelle commande "info".
