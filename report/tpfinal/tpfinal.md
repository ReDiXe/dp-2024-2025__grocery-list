# Rapport Final de Reda Sebaaoui et Nathan Vandamme

## Ce qui a été difficile

La segmentation du programme, comme on l'a dit dans notre rapport TP1, a été notre principal défi. On devait trouver l'équilibre entre trop et pas assez de fichiers.

On n'a pas réussi à faire marcher les tests unitaires au début, même si l'appli fonctionnait bien manuellement. C'était frustrant, mais on a fini par comprendre comment utiliser Mockito correctement.

L'ajout du serveur web a aussi été compliqué. On a dû comprendre comment faire fonctionner notre code avec la bibliothèque externe.

## Design Patterns utilisés et pourquoi

### 1. Pattern Command
On a utilisé ce pattern pour nos commandes (add, list, remove, info, web). Ça nous permet :
- De séparer les responsabilités
- D'ajouter facilement de nouvelles commandes
- De tester chaque commande séparément

### 2. Pattern Factory
Notre `CommandFactory` s'occupe de créer les commandes. C'est utile parce que :
- Le `Main` n'a pas besoin de savoir comment créer les commandes
- C'est facile d'ajouter des nouvelles commandes
- Le code est plus propre

### 3. Pattern Strategy
L'interface `GroceryListStorage` avec ses implémentations JSON et CSV est un bon exemple de ce pattern. Avantages :
- On peut changer de format de stockage facilement
- Chaque format a son propre code
- Tout marche de la même façon pour le reste du programme

### 4. Pattern Adapter
Le `GroceryShopAdapter` fait le lien entre notre code et le serveur web. On en avait besoin pour :
- Faire marcher notre liste de courses avec le serveur
- Ne pas avoir à changer tout notre code

## Ce qu'on n'a pas eu le temps de faire

- une potentielle meilleure structure ?

## Réponses aux questions

### Comment ajouter une nouvelle commande ?
1. Créer une classe qui implémente `Command`
2. Coder la méthode `execute()`
3. L'ajouter dans le `CommandFactory`
4. Et c'est tout, le `Main` n'a pas besoin de changer

### Comment ajouter une nouvelle source de données ?
1. Créer une classe qui implémente `GroceryListStorage`
2. Coder les méthodes `load()` et `save()`
3. Ajouter un cas dans le `Main` pour le nouveau format
4. Le reste du code continue de fonctionner pareil

### Que changer pour ajouter un magasin aux courses ?
1. Ajouter un attribut `store` à `GroceryItem`
2. Ajouter une option `-o` ou `--store` dans le `CliParser`
3. Modifier les commandes pour utiliser ce paramètre
4. Adapter le stockage pour sauvegarder cette info
5. Changer l'affichage pour grouper par magasin si besoin

Notre architecture rend ces changements assez simples à faire.