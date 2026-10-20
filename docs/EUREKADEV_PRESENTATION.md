# Présentation de FocusQuest — EurekaDev 2026

## Proposition

FocusQuest est un prototype Android éducatif qui transforme une partie des révisions en missions scolaires courtes. L’élève choisit une classe et une matière, répond à cinq questions, puis voit les minutes ajoutées à son solde après les bonnes réponses.

## Problème observé

Les téléphones réunissent des outils utiles et des sollicitations susceptibles de détourner l’attention : notifications, messages, contenus et changements fréquents d’activité. Pour certains élèves, commencer une séance ou maintenir leur attention demande aussi un effort particulier. FocusQuest part de ce constat de conception sans prétendre décrire tous les élèves ni mesurer l’ampleur du phénomène.

## Principe de la récompense éducative

Une bonne réponse ajoute une récompense en minutes au solde local de l’application. La récompense relie le travail scolaire à un objectif immédiat et compréhensible. Dans la banque actuelle, chaque réponse correcte vaut deux minutes. Le suivi réel est en cours : le code partagé contient désormais un accès spécial Android et la sélection d’applications à mesurer, mais ce parcours n’a pas été validé sur appareil et ne doit pas encore être présenté comme opérationnel. FocusQuest ne bloque pas l’accès aux applications.

## Pourquoi des missions courtes ?

Une mission limitée à cinq questions rend l’objectif visible et permet de commencer par une tâche de petite taille. Une série courte peut aussi faciliter une démonstration et donner un retour rapide sur les réponses. Ce sont des bénéfices attendus du choix de conception, pas des résultats évalués.

## Parcours de l’utilisateur

1. L’élève ouvre l’application et consulte son solde disponible.
2. Il démarre une mission.
3. Il choisit sa classe puis une matière.
4. Il répond aux questions tirées dans cette catégorie et reçoit un retour après chaque réponse.
5. Une bonne réponse ajoute des minutes au solde.
6. L’écran de résultat récapitule le score, le gain de la mission et le solde disponible.

Le prototype propose actuellement des questions pour les classes de 5e, 4e et 3e et les matières mathématiques, français, histoire-géographie et sciences.

## Avantages attendus

- Rendre le démarrage d’une révision plus concret grâce à un objectif limité.
- Donner un retour immédiat sur une réponse.
- Encourager l’entraînement dans plusieurs matières par un mécanisme simple.
- Donner à l’élève une visibilité sur les minutes qu’il a gagnées dans l’application.

Ces points décrivent les objectifs du projet. Aucun résultat d’usage, effet mesuré ou bénéfice scientifique n’est revendiqué.

## Limites actuelles

- Le suivi réel du temps d’écran Android est en cours dans le code partagé, mais il n’est pas encore vérifié en exécution ; le solde local reste la fonction à présenter avec certitude.
- La récompense n’ouvre ni ne bloque effectivement les applications du téléphone.
- La banque doit faire l’objet d’une validation pédagogique ; des ambiguïtés et répétitions sont consignées dans l’audit.
- L’écran propose des classes qui n’ont pas encore de questions.
- Aucun essai utilisateur ni résultat clinique n’est présenté comme preuve d’efficacité.
- La compilation et le parcours complet sur un téléphone doivent être validés avant la présentation.

FocusQuest est un outil éducatif expérimental, pas un traitement médical ni un dispositif de soin.

## Développements futurs

1. Valider les questions et compléter les niveaux affichés.
2. Terminer puis tester le suivi réel du temps d’écran avec une demande de permission compréhensible et un affichage transparent.
3. Vérifier le calcul et les limites du solde lorsqu’il est relié à l’usage Android.
4. Évaluer l’ergonomie avec des élèves et des adultes encadrants, sans présumer des résultats.
5. Étudier des missions progressives, des explications pédagogiques enrichies et des outils de suivi de progression.
