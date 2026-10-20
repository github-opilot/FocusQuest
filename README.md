# FocusQuest

FocusQuest est une application Android de révision scolaire qui associe les bonnes réponses à du temps d’écran disponible. L’interface est en français et construite avec Kotlin, Jetpack Compose et Material 3.

## Fonctionnalités

### Parcours scolaire

Après un onboarding présenté au premier lancement, l’élève peut suivre ce parcours :

**Accueil → classe → matière → chapitre → activité → QCM → résultats**

- Le catalogue est chargé depuis les fichiers JSON de l’application pour les classes de 6e, 5e, 4e, 3e, Seconde, Première générale et Terminale générale.
- Seules les matières du niveau choisi sont affichées. En Première et Terminale, elles sont classées en enseignements communs, spécialités ou options.
- Les chapitres sont regroupés par thème. Chaque fiche indique le nombre de QCM disponibles ; les chapitres sans question restent visibles et ne peuvent pas être lancés.
- La seule activité disponible pour l’instant est le **QCM de cinq questions maximum**. Le quiz reste limité à la classe, la matière et au chapitre choisis. Il utilise moins de cinq questions si le chapitre en contient moins et ne répète pas de question dans une même mission.
- Le quiz affiche la progression, le score et les explications. Une réponse validée ne peut pas attribuer sa récompense une deuxième fois. Chaque bonne réponse ajoute actuellement **30 secondes** au temps gagné, soit jusqu’à 2 min 30 pour une mission parfaite. L’écran de résultats récapitule la mission.
- Les boutons retour permettent de revenir aux étapes précédentes ; quitter une mission en cours demande confirmation.

### Temps disponible et suivi des applications

- L’accueil affiche le temps disponible, le temps gagné et le temps utilisé aujourd’hui.
- Le temps de base est de **30 minutes par jour** par défaut. Dans les réglages, il peut être choisi parmi 15, 30, 45 ou 60 minutes.
- Le calcul est : **temps de base + temps gagné − temps utilisé**, avec un minimum de zéro.
- FocusQuest peut lister les applications lançables du téléphone, avec leurs noms et icônes système. L’utilisateur choisit celles dont l’usage doit être comptabilisé ; sa sélection est conservée.
- Avec l’autorisation Android « Accès aux données d’utilisation », FocusQuest lit dans `UsageStatsManager` le temps au premier plan des applications sélectionnées et le déduit du solde. L’écran de sélection guide vers le réglage système de cette autorisation. La première sélection d’une application dans la journée prend en compte le temps que les statistiques Android indiquent depuis minuit.
- Le relevé est actualisé au lancement, au retour de l’application au premier plan et avec le bouton **Actualiser le temps utilisé**. Ce n’est pas un suivi en continu : les applications restent ouvrables et FocusQuest **ne les bloque pas**.
- Le temps gagné et le temps utilisé sont remis à zéro au changement de journée ; le temps de base et les applications sélectionnées sont conservés localement avec Preferences DataStore.

### Onboarding et navigation

L’onboarding est affiché au premier lancement et son état de fin est sauvegardé. Les lancements suivants ouvrent directement l’accueil. Depuis l’accueil, l’élève peut démarrer une mission, régler son temps de base, gérer les applications surveillées ou actualiser le relevé d’usage.

## Contenu scolaire

Le catalogue JSON comprend actuellement :

| Niveau | Matières du catalogue | Chapitres | Chapitres avec QCM | QCM |
|---|---:|---:|---:|---:|
| 6e | 12 | 66 | 16 | 80 |
| 5e | 15 | 82 | 22 | 110 |
| 4e | 22 | 91 | 34 | 170 |
| 3e | 22 | 98 | 35 | 175 |
| Seconde | 35 | 104 | 8 | 40 |
| Première générale | 35 | 132 | 8 | 40 |
| Terminale générale | 37 | 159 | 8 | 40 |
| **Total** | **178** | **732** | **131** | **655** |

Les nombres de matières correspondent aux entrées propres à chaque niveau, et non au nombre de matières distinctes à l’échelle du catalogue. Tous les chapitres n’ont pas encore de QCM. La cohérence structurelle du JSON, les associations matière-chapitre, les choix et les réponses sont couverts par les tests instrumentés. Cela ne constitue pas une validation pédagogique : les contenus doivent encore être relus par des enseignants.

Les **120 anciennes questions Kotlin** (5e, 4e et 3e) sont conservées dans `QuestionRepository` pour compatibilité. Elles ne sont pas mélangées au nouveau catalogue et aucune n’est associée automatiquement à un chapitre. Une association éventuelle doit être explicitement vérifiée. L’audit documentaire existant dans [docs/QUESTION_AUDIT.md](docs/QUESTION_AUDIT.md) porte uniquement sur cette ancienne banque, pas sur les 655 QCM du catalogue.

## Limites connues

- FocusQuest comptabilise l’usage des applications sélectionnées, mais ne les bloque pas et ne tourne pas comme un service de surveillance permanent en arrière-plan.
- Le suivi nécessite l’autorisation spéciale d’accès aux statistiques d’utilisation dans les réglages Android. Sans cette autorisation, les relevés d’usage ne peuvent pas être lus.
- Seul le QCM est fonctionnel comme activité de révision. Les chapitres sans questions sont consultables, mais indisponibles au lancement.
- La couverture des programmes, la formulation et le niveau pédagogique des questions n’ont pas été certifiés par des enseignants.
- Les données et les catalogues sont locaux à l’application. Le projet ne requiert ni Firebase ni serveur distant.

## Architecture et données

- `MainActivity.kt` démarre l’application Compose ; `ui/FocusQuestApp.kt` relie l’onboarding, l’accueil, le parcours du catalogue, le quiz et les résultats.
- `data/catalog/CurriculumRepository.kt` charge les matières et chapitres ; `ChapterQuestionRepository.kt` charge les QCM associés à un chapitre depuis `app/src/main/assets/`.
- `data/QuestionRepository.kt` contient l’ancienne banque de 120 questions.
- `data/ScreenTimeRepository.kt` conserve le solde et la sélection d’applications avec Preferences DataStore ; `UsageStatsRepository.kt` interroge les statistiques Android.
- `data/OnboardingRepository.kt` conserve l’état de l’onboarding.

## Configuration et commandes

Prérequis : Android Studio, JDK 17 et les composants Android SDK du projet. L’application cible Android 36 et prend en charge Android 7.0 (API 24) et versions ultérieures.

Depuis la racine du dépôt :

```powershell
# Windows
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:connectedDebugAndroidTest  # nécessite un appareil ou émulateur connecté
```

```bash
# macOS / Linux
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
./gradlew :app:connectedDebugAndroidTest  # nécessite un appareil ou émulateur connecté
```

L’APK debug est généré dans `app/build/outputs/apk/debug/`. Le projet utilise Gradle 9.3.1, Android Gradle Plugin 9.1.1 et Kotlin 2.2.10.
