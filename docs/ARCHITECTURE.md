# Architecture technique constatée

**Périmètre :** état de l’espace de travail observé le 20 octobre 2026. Il contient des modifications non intégrées issues des travaux parallèles sur le suivi réel du temps d’écran et la refonte du quiz. Les éléments décrits comme étant en cours sont présents dans ces fichiers, mais restent non validés tant qu’ils ne sont pas intégrés et vérifiés. Ce document ne confirme ni compilation ni exécution sur appareil.

## Démarrage Android

MainActivity est l’activité déclarée comme point d’entrée dans AndroidManifest.xml. Elle active l’affichage bord à bord, installe le thème Compose et affiche FocusQuestApp dans un Scaffold.

## Application et navigation

FocusQuestApp crée un NavController et un NavHost. Les routes existantes correspondent à l’accueil, aux réglages, au choix d’applications à surveiller, au choix de classe, au choix de matière et au quiz. La classe et la matière sont transmises dans les routes du quiz. Le ViewModel du temps d’écran est créé avec le mécanisme viewModel() puis son état est observé par l’interface. Dans les changements en cours, l’application relance une actualisation quand elle revient au premier plan.

## Écrans Compose

- HomeScreen affiche le nom, le solde et les actions pour commencer une mission ou ouvrir les réglages.
- GradeSelectionScreen affiche toutes les valeurs de Grade, y compris les niveaux pour lesquels la banque n’a pas de questions.
- SubjectSelectionScreen affiche les matières de Subject.
- AppSelectionScreen affiche les applications lançables, permet d’en sélectionner et propose d’ouvrir les réglages spéciaux Android pour l’accès aux statistiques d’usage.
- QuizScreen reçoit les questions déjà sélectionnées, gère l’avancement, les réponses, le score et les résultats.
- SettingsScreen propose un temps quotidien de base de 15, 30, 45 ou 60 minutes.

Les écrans sont des fonctions composables et la navigation est gérée par Navigation Compose.

## Questions et moteur de quiz

Question contient un identifiant, une classe, une matière, un énoncé, quatre choix, l’index de la bonne réponse, une explication et une récompense de deux minutes par défaut.

QuestionRepository assemble les banques de 5e, 4e et 3e. getQuestions filtre par classe et matière puis mélange la liste. La route du quiz conserve les cinq premières questions de cette liste mélangée. Les réponses elles-mêmes ne sont pas mélangées par le dépôt.

Le quiz incrémente le score et appelle le ViewModel pour ajouter les minutes lorsqu’une réponse correspond à l’index correct. Chaque question ne peut être répondue qu’une fois dans l’état courant de la mission. Le récapitulatif indique le score, les minutes gagnées pendant la mission et le solde disponible.

## Solde, récompenses et DataStore

ScreenTimeRepository stocke avec Preferences DataStore le temps quotidien de base, les minutes gagnées, les minutes consommées, la date de dernière remise à zéro et l’ensemble des applications choisies. Les changements en cours ajoutent aussi une durée utilisée en millisecondes ainsi qu’une dernière lecture et une base par application. Le temps de base initial vaut 30 minutes. Le dépôt peut ajouter des minutes gagnées ou consommées, et réinitialise les minutes gagnées et consommées quand la date change ; le temps de base est conservé.

ScreenTimeState.availableMinutes calcule le solde comme base + gagné − consommé, avec un plancher à zéro. ScreenTimeViewModel expose les flux sous forme de StateFlow et fournit les actions de récompense, réglage du temps de base, sélection d’applications et actualisation. Les changements en cours comptabilisent les nouveaux incréments observés du temps au premier plan pour les applications choisies et convertissent les millisecondes en minutes entières.

## Lecture de l’usage Android en cours

UsageStatsRepository vérifie l’accès spécial avec AppOpsManager, obtient les applications lançables et interroge UsageStatsManager pour leur durée au premier plan depuis le début de la journée. Le manifeste partagé déclare PACKAGE_USAGE_STATS et une requête de visibilité sur les activités de lancement. AppSelectionScreen ouvre les réglages d’accès aux statistiques, affiche l’état d’autorisation et conserve les choix de l’utilisateur.

Lorsqu’une application est sélectionnée, le dépôt enregistre sa lecture de base si elle est disponible. Sinon, il conserve une valeur inconnue puis initialise la base à la première lecture réussie, afin de ne pas facturer le temps antérieur à l’autorisation. Les actualisations suivantes ajoutent seulement l’écart positif depuis la dernière lecture, pour éviter de recompter tout le temps de la journée. Les changements actuels déclenchent ces lectures au lancement, au retour au premier plan ou à l’actualisation manuelle ; aucun service de mesure en continu ou de blocage d’application n’est présent.

Cette intégration est du code de travail parallèle visible dans l’espace partagé. Elle n’a pas été compilée ni exercée sur un appareil durant l’audit ; la demande d’accès, la liste d’applications et les relevés restent donc non vérifiés.

## Tests et intégration en parallèle

Des fichiers de tests unitaires et instrumentés existent pour le dépôt de questions, le calcul de temps d’usage, l’état du solde, le parcours de quiz et la persistance. Leur présence ne démontre pas qu’ils passent : ils n’ont pas été lancés dans le cadre de cet audit.

La refonte visuelle du quiz est en cours dans un travail parallèle. Le suivi réel du temps d’écran dispose maintenant de code de collecte, de permission et d’interface dans l’espace partagé. Leur comportement final doit être revu après intégration ; leur présence dans l’arbre de travail ne vaut pas validation opérationnelle.
