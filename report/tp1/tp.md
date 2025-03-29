# Rapport de Reda Sebaaoui et Nathan Vandamme

## Problème d'origine

La classe 'Main' dans le code original était surchargée. 
Celle-ci était seule et par conséquent gérait tout : le parsing des arguments, la logique métier, la persistance des données et l'affichage. 
 À cause de cela, le code était complexe à comprendre et maintenir.

**Ce que nous avons fait :**
Nous avons restructuré le code de l'application permettant la gestion de liste de courses pour améliorer sa maintenabilité mais en conservant les fonctionnalités qui existaient déjà. Nous avons ainsi découpé la classe monolithique qu'était 'Main' en 3 fichiers distincts, plus petits et spécialisés.

Ainsi, nous avons au final les 3 fichiers principaux suivants : 

1. `Main.java`, qui est la classe principale gérant l'exécution du programme. 
    On y retrouve trois commandes principales avec add, list et remove. 

2.  Il y a ensuite `GroceryItem.java`. Celle-ci représente un article de la liste de courses.
    Chaque article est composé d'un nom et d'une quantité. 
    Il y a des getters, setters, constructeurs mais aussi la méthode toString, permettant l'affichage dudit article sous forme textuelle.

3. `GroceryListStorage.java` est la 3ième classe ici-présente. 
    Ce fichier permet de définir l'interface et gère le stockage et le chargement de la liste de courses.
    Il y a deux méthodes principales, load et save.
    Cette classe comporte en plus deux implémentations de stockage, sous format JSON avec JsonStorage et sous format CSV avec CsvStorage.

## Détails
 
Chaque fichier classe que nous avons créé à une responsabilité unique et claire.

L'introduction d'une interface 'GroceryListStorage' permet de facilement ajouter de nouveaux formats de stockage à l'avenir.

La séparation claire des responsabilités permet d'ajouter de nouvelles fonctionnalités avec facilité. 
Que ce soit l'ajout de nouvelles commandes dans la classe 'Main' ou l'implémentation de nouveaux formats de stockage dans l'interface 'GroceryListStorage', le code facilite l'intégration de ces évolutions.

Malgré la restructuration totale du code, toutes les fonctionnalités de rétrocompatibilité ont été conservées.

## Difficultés rencontrées

La segmentation du programme en général.

En effet, en faisant cela, il fallait premièrement faire attention à ne pas trop en faire afin d'éviter les fichiers inutiles, mais en en faisant trop peu, cela n'aurait pas servi à grand-chose. 
La division du code en 3 fichiers avec une utilité définie est ce qui nous est apparu comme la meilleure option. 

Nous n'avons malheureusement pas réussi à mettre en place les tests unitaires liés à ce TP, nous ne comprenons pas pourquoi. 
Nous avons bien testé notre programme à la main, et cela est fonctionnel, mais pour autant nos tests unitaires n'ont pas fonctionné.



