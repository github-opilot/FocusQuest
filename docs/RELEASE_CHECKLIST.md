# Checklist de préparation à EurekaDev 2026

Cocher chaque point après vérification et noter la date, l’appareil ou la source officielle quand cela s’applique.

## Application et compilation

- [ ] Synchroniser le projet avec la version Android Studio et le JDK compatibles avec la configuration Gradle.
- [ ] Construire la variante debug et vérifier que l’APK est généré.
- [ ] Installer et démarrer l’application sur au moins un téléphone Android.
- [ ] Parcourir accueil, sélection de classe, sélection de matière, quiz, résultats, réglages et retour arrière.
- [ ] Refaire ce parcours après intégration des changements réalisés en parallèle.

## Questions et contenu

- [ ] Confirmer le nombre de questions par classe et matière.
- [ ] Vérifier les identifiants uniques, quatre choix distincts, index corrects et contenus non vides.
- [ ] Corriger ou arbitrer les points consignés dans docs/QUESTION_AUDIT.md.
- [ ] Faire relire les réponses, explications, niveaux et formulations par une personne compétente en pédagogie.
- [ ] Décider quoi faire des niveaux affichés sans questions.
- [ ] Exécuter les tests de questions et enregistrer le résultat.

## Récompenses et temps disponible

- [ ] Vérifier qu’une bonne réponse donne la récompense attendue une seule fois.
- [ ] Vérifier qu’une mauvaise réponse ne donne pas de minutes.
- [ ] Vérifier le score, le total gagné dans la mission et le solde.
- [ ] Vérifier le calcul base + gagné − consommé et le plancher à zéro.
- [ ] Vérifier le réglage du temps de base, la conservation après fermeture et la remise à zéro au changement de jour.
- [ ] Déterminer et vérifier comment les minutes consommées sont réellement saisies ou mesurées.
- [ ] Vérifier l’accès spécial aux statistiques Android accordé, refusé puis révoqué.
- [ ] Vérifier la sélection d’applications, la lecture du temps au premier plan et les mises à jour après actualisation et retour au premier plan.
- [ ] Vérifier les valeurs de base, l’absence de double comptage et le comportement au changement de jour et après désinstallation d’une application surveillée.
- [ ] Ne revendiquer le suivi réel Android qu’après intégration et vérification sur appareil.

## Permissions et confidentialité

- [ ] Comparer les permissions du manifeste aux fonctions effectivement présentées.
- [ ] Si le suivi Android est ajouté, vérifier le consentement, l’écran de réglage spécial Android, le refus, la révocation et l’explication destinée à l’élève et à son responsable.
- [ ] Vérifier quelles données restent sur l’appareil et éviter d’inclure des informations personnelles dans les captures.

## Présentation et dépôt

- [ ] Relire le README et les documents d’audit, d’architecture et de présentation après intégration des travaux en cours.
- [ ] Vérifier le dépôt GitHub, sa branche de présentation et les fichiers effectivement inclus.
- [ ] Capturer des écrans réels de l’accueil, des sélections, d’une question, du résultat et des réglages.
- [ ] Filmer le scénario de docs/DEMO_SCRIPT.md en moins de quatre minutes.
- [ ] Utiliser la variante de narration prévue si le suivi réel du temps d’écran n’est pas terminé.
- [ ] Vérifier que la vidéo ne montre ni simulation présentée comme réelle, ni résultat clinique ou scientifique non établi.

## Règles et échéance du concours

- [ ] Consulter le règlement EurekaDev 2026 sur une source officielle de l’organisateur.
- [ ] Confirmer les critères d’admissibilité, le format des livrables, les règles de propriété intellectuelle et les modalités de dépôt.
- [ ] Confirmer la date limite, le fuseau horaire et l’heure de clôture sur la source officielle ; noter le lien et la date de vérification.
- [ ] Prévoir une marge pour le dépôt et vérifier que les liens et fichiers remis sont accessibles.
