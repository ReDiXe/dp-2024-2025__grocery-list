# Rapport de Reda Sebaaoui et Nathan Vandamme

## Problème d'origine

Après la remarque de notre professeur, nous avons séparé le code en plusieurs fichiers traitant un seul type de tâche. Nous avons donc reconstruit entièrement l'arborescence de notre projet.
De plus, nous devions ajouter une fonctionnalité de category pour pouvoir "ranger" nos objets.
## Détails
 
Chaque fichier classe que nous avons créé à une responsabilité unique et claire.

Nous avons l'introduction de controller et de factory dans notre projet pour faciliter l'ajout de nouvelle commande et de nouveau format de stockage. Nous avons séparé les differentes classes dans des dossiers différents pour une meilleure lisibilité.
Nous avons aussi repris depuis le debut nos tests unitaires pour les rendre fonctionnels. Chaque class a son fichier de tests unitaires

Nous avons ajouter la fonctionnalité de category gérable en JSON et CSV. pour la fonctionnalité de suppression : nous devons forcement precisé la category sinon la suppression se fera seulement dans la liste de category default.

Pour finir, nous avons ajouter le diagramme de sequence du projet
## Difficultés rencontrées

Comprendre comment fonctionnent les factory et controller.
régler les problèmes de dépendance entre les classes.
régler les problèmes de librairy (beaucoup de problème maven)



