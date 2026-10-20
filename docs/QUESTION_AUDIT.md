# Audit pédagogique de la banque de questions

**Date de l’inspection : 20 octobre 2026**

L’audit porte sur les trois fichiers de questions et sur QuestionRepository. Les entrées ont été comptées statiquement et relues pour vérifier les consignes, les choix, les index et les explications. Aucun fichier Kotlin n’a été modifié et aucun test ni build n’a été lancé.

## Inventaire

La banque contient **120 questions** : 40 par classe, 30 par matière et 10 dans chaque combinaison classe-matière.

| Classe | Mathématiques | Français | Histoire-Géographie | Sciences | Total |
|---|---:|---:|---:|---:|---:|
| 5e | 10 | 10 | 10 | 10 | 40 |
| 4e | 10 | 10 | 10 | 10 | 40 |
| 3e | 10 | 10 | 10 | 10 | 40 |
| **Total** | **30** | **30** | **30** | **30** | **120** |

Les identifiants sont uniques. Chaque entrée inspectée a quatre choix distincts, un index de réponse dans l’intervalle 0–3, une question et une explication non vides. Je n’ai pas relevé de calcul faux, de clé manifestement incorrecte, ni d’erreur factuelle nette dans les réponses attendues. Les points ci-dessous concernent surtout la précision des formulations, quelques explications et des répétitions. Ils sont à corriger dans la banque puis à faire valider par un enseignant.

## Problèmes et améliorations proposées

| ID | Classe | Matière | Problème constaté | Correction proposée |
|---|---|---|---|---|
| 5104 | 5e | Français | « Au présent » ne précise pas le mode ; « nous finissions » est également le présent du subjonctif. La réponse attendue est le présent de l’indicatif. | Demander explicitement « à l’indicatif présent ». |
| 5107 | 5e | Français | « Ancien » a plusieurs sens. « Moderne » est l’antonyme dans le sens d’un objet vieux ou d’une époque passée, mais la consigne ne donne pas de contexte. | Préciser le sens, par exemple « Quel est le contraire de “ancien” pour parler d’un bâtiment ? » |
| 5103 | 5e | Français | L’explication dit que « dort » exprime une action. Dormir décrit plutôt un état ou une activité ; cela peut brouiller la définition du verbe. | Écrire : « “Dort” est le verbe conjugué de la phrase. » |
| 5208 | 5e | Histoire-Géographie | La question porte sur « la France » sans préciser le territoire ; l’explication répond seulement pour la France métropolitaine. | Demander explicitement « Sur quel continent se trouve la France métropolitaine ? » |
| 5306 | 5e | Sciences | La photosynthèse nécessite notamment de la lumière, de l’eau et du dioxyde de carbone. La formulation générale suggère qu’une seule ressource suffit ; seule la lumière figure parmi les choix. | Demander quelle source d’énergie est nécessaire, ou proposer un choix qui associe les conditions attendues. |
| 5308 | 5e | Sciences | Un circuit fermé ne suffit pas, à lui seul, à allumer une lampe : il faut aussi un générateur et des composants correctement reliés. | Préciser « Dans un circuit simple comportant une pile et une lampe, quelle condition permet au courant de circuler ? » |
| 4306 | 4e | Sciences | « Rejette-t-il davantage » ne dit pas clairement si l’on compare l’air expiré à l’air inspiré ou les quantités des différents gaz. | Demander quel gaz est produit par la respiration cellulaire et rejeté dans l’air expiré. |
| 3208 | 3e | Histoire-Géographie | « Aire urbaine » est une ancienne nomenclature statistique française. L’Insee indique que le zonage des aires d’attraction des villes, introduit en 2020, s’est substitué au zonage des aires urbaines. | Employer « aire d’attraction des villes » et préciser le critère, par exemple la plus peuplée. [Définition Insee](https://www.insee.fr/fr/metadonnees/definition/c2070). |

## Répétitions à arbitrer

Je n’ai relevé **aucun doublon intégral d’énoncé, de choix et d’explication**. Les répétitions de formulation ou de notion ci-dessous ne rendent pas les réponses fausses, mais peuvent réduire la variété pédagogique.

| IDs | Classes et matière | Répétition | Proposition |
|---|---|---|---|
| 3105 et 4109 | 3e et 4e, Français | Même consigne « Quelle phrase est correctement accordée ? », avec deux exemples d’accord du participe passé. | Garder les deux comme révision spiralaire si c’est intentionnel ; sinon varier l’objectif ou la consigne. |
| 3302 et 4302 | 3e et 4e, Sciences | Même question sur l’unité de l’intensité électrique. | Vérifier que la répétition est voulue ou remplacer une question par une application/calcul. |
| 3307 et 4310 | 3e et 4e, Sciences | Même vérification de la règle « pH inférieur à 7 » pour une solution acide. | Garder une seule définition et faire de l’autre une question d’application. |

## Conclusion pédagogique

Les clés de réponse et les calculs relus sont cohérents. La banque couvre de façon uniforme les quatre matières et les trois niveaux annoncés. La conformité détaillée à la progression officielle, l’adéquation du vocabulaire aux élèves et le niveau de difficulté n’ont pas été certifiés par un enseignant. Une relecture pédagogique avant diffusion reste nécessaire, en plus des améliorations formulées ci-dessus.
