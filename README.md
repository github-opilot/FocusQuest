# FocusQuest

FocusQuest est un prototype d’application Android éducative. Il transforme de courtes révisions scolaires en missions à choix multiples et ajoute des minutes à un solde de temps disponible lorsque l’élève répond correctement.

## Problème et concept

Les notifications, les applications et les sollicitations numériques peuvent interrompre une activité scolaire. Commencer une séance et y rester concentré peut aussi être difficile. FocusQuest explore une approche simple : proposer une mission courte, claire et liée aux apprentissages, puis associer les bonnes réponses à une récompense en minutes.

Cette approche est une intention de conception. Le projet ne constitue pas un traitement médical et aucune efficacité clinique n’a été démontrée.

## Fonctionnement

1. L’élève démarre une mission et choisit une classe puis une matière.
2. L’application sélectionne cinq questions dans la banque disponible pour cette catégorie.
3. Une bonne réponse ajoute la récompense configurée pour la question. Dans la banque actuelle, chaque question vaut deux minutes, soit jusqu’à dix minutes pour cinq bonnes réponses.
4. L’application affiche le score et le solde disponible.

Le solde est calculé ainsi : minutes de base + minutes gagnées − minutes consommées, avec un minimum de zéro. Les minutes de base sont réglables dans l’application et le solde est conservé localement avec DataStore. Des changements en cours dans l’espace de travail ajoutent le choix d’applications et la lecture des statistiques d’usage Android pour comptabiliser les minutes utilisées. Ce suivi n’a pas encore été validé sur appareil ; il ne faut pas le présenter comme opérationnel avant intégration et vérification. FocusQuest ne bloque pas les applications Android.

## Public cible et contenu

La banque contient 120 questions en français pour les élèves de 5e, 4e et 3e, réparties entre mathématiques, français, histoire-géographie et sciences.

L’écran de choix de classe affiche également la 6e, la Seconde, la Première et la Terminale, mais aucune question n’est disponible pour ces niveaux. Les banques correspondantes restent à créer ou les choix à restreindre.

## Technologies

- Kotlin et Android
- Jetpack Compose et Material 3
- Navigation Compose
- AndroidX Lifecycle et ViewModel
- Preferences DataStore
- Gradle 9.3.1, Android Gradle Plugin 9.1.1 et Kotlin 2.2.10
- JDK 17

## Architecture

- MainActivity démarre l’interface Compose et le thème.
- ui/FocusQuestApp.kt contient les routes et relie les écrans, les questions et le solde.
- ui/ regroupe l’accueil, le choix de classe et de matière, le quiz, les résultats et les réglages.
- data/QuestionRepository.kt filtre et mélange les questions.
- data/questions/ contient les banques par niveau.
- data/ScreenTimeRepository.kt conserve le solde local avec DataStore.
- model/ contient les modèles de question, de classe, de matière et de solde.

La description détaillée est dans [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## Installation et lancement

1. Installer Android Studio, JDK 17 et les composants Android SDK demandés par le projet.
2. Cloner le dépôt et l’ouvrir dans Android Studio.
3. Laisser Gradle synchroniser les dépendances.
4. Construire et installer la variante debug :

       Windows : .\gradlew.bat :app:assembleDebug
       macOS/Linux : ./gradlew :app:assembleDebug

L’APK debug est généré dans app/build/outputs/apk/debug/. Un appareil ou émulateur compatible avec le minimum configuré dans Gradle (API 24) est nécessaire pour l’exécuter.

Les tests unitaires et instrumentés existent dans app/src/test/ et app/src/androidTest/. Ils n’ont pas été exécutés pendant l’audit documentaire.

## Fonctionnalités terminées dans le périmètre du prototype

- Accueil avec solde disponible et accès aux missions et réglages.
- Parcours de sélection classe, matière et quiz.
- Missions de cinq questions tirées au hasard dans une catégorie.
- Score, retour bonne/mauvaise réponse et ajout de minutes après une bonne réponse.
- Réglage local du temps quotidien de base.
- Conservation du solde avec DataStore et remise à zéro quotidienne des minutes gagnées et consommées lors de l’actualisation du dépôt.

Cette liste décrit le code inspecté ; elle ne vaut pas validation sur téléphone. L’audit des questions et les limites relevées sont dans [docs/QUESTION_AUDIT.md](docs/QUESTION_AUDIT.md).

## En développement ou à vérifier

- Le suivi réel du temps d’écran est développé en parallèle. Le code de l’espace de travail contient maintenant une demande d’accès aux statistiques d’usage, un sélecteur d’applications et des lectures de leur temps au premier plan. Ce changement n’a pas été vérifié sur appareil et reste non confirmé comme intégré.
- Une refonte visuelle du quiz est en cours en parallèle et reste à valider après intégration.
- La banque doit être relue par des enseignants, notamment pour les ambiguïtés et les répétitions relevées dans l’audit.
- Les classes proposées sans banque doivent être traitées.
- La compilation, les tests, les permissions, les relevés d’usage et le parcours sur appareil doivent être vérifiés avant toute démonstration publique.

## Perspectives

Intégrer et tester le suivi Android avec une explication claire des permissions, aligner les niveaux proposés sur les contenus disponibles, enrichir et faire valider les questions, puis examiner des missions progressives, des statistiques de progression et une configuration familiale. Ces pistes ne sont pas des fonctions actuelles.

La présentation d’EurekaDev est dans [docs/EUREKADEV_PRESENTATION.md](docs/EUREKADEV_PRESENTATION.md), le scénario vidéo dans [docs/DEMO_SCRIPT.md](docs/DEMO_SCRIPT.md), et les étapes de préparation dans [docs/RELEASE_CHECKLIST.md](docs/RELEASE_CHECKLIST.md).
