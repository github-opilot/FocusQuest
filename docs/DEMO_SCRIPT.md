# Scénario vidéo de démonstration — 4 minutes maximum

## Préparation avant l’enregistrement

- Construire l’application et faire le parcours complet sur un téléphone Android.
- Vérifier les valeurs affichées, le gain après une bonne réponse et la persistance du solde.
- Préparer un appareil et des données de démonstration sans information personnelle.
- Ne montrer que les écrans réellement observés pendant cette vérification.
- Si le suivi système n’est pas intégré et vérifié, utiliser la variante de narration indiquée à 2:30. Ne pas simuler ce suivi.

## 0:00 – 0:30 | Le problème

**Image :** un élève s’installe pour réviser ; son téléphone est posé à proximité. Ne pas afficher de données personnelles ou de fausses statistiques.

**Voix :** « Les outils numériques donnent accès à beaucoup de contenus, mais leurs sollicitations peuvent interrompre une séance. Et parfois, le plus difficile est simplement de commencer. FocusQuest explore une manière de rendre les révisions plus courtes et leur objectif plus visible. »

## 0:30 – 1:00 | FocusQuest

**Image :** ouverture de l’application et écran d’accueil.

**Voix :** « FocusQuest est un prototype éducatif Android. L’élève choisit une classe et une matière, répond à cinq questions et gagne des minutes dans le solde de l’application lorsqu’il répond juste. Le solde récompense le travail scolaire. Le suivi des applications Android est en cours de vérification et ne sera présenté que s’il est validé sur l’appareil filmé. »

## 1:00 – 2:30 | Démonstration du parcours

**Image et actions :**

- À 1:00, toucher « Commencer une mission ».
- Choisir une classe disposant d’une banque de questions, puis une matière.
- Lire les questions tirées au hasard ; sélectionner une réponse correcte sur un exemple visible.
- Montrer le retour de bonne réponse et l’ajout des minutes affiché par l’application.
- Continuer la mission et montrer le score, le gain et le solde sur l’écran de résultat.

**Voix :** « Une mission contient cinq questions. Une bonne réponse ajoute la récompense définie pour cette question ; dans la banque actuelle, elle vaut deux minutes. L’élève avance à son rythme, voit son score et retrouve le total gagné à la fin. »

Ne pas prétendre que toutes les réponses d’une session sont correctes si la vidéo ne les montre pas. Si une réponse est choisie par erreur pendant la prise, recommencer la séquence ou conserver le retour réel affiché.

## 2:30 – 3:15 | Fonctions Android présentes dans le code

**Image :** montrer l’écran des réglages, modifier le temps quotidien de base, revenir à l’accueil et, après vérification, montrer que le solde reste affiché.

**Voix :** « L’interface est construite avec Jetpack Compose. La navigation relie l’accueil, les choix de classe et de matière, le quiz et les réglages. Le solde et le temps de base sont conservés localement avec DataStore. Le prototype remet les minutes gagnées et consommées à zéro au changement de jour, tout en conservant le réglage de base. »

**Variante si le suivi réel du temps d’écran n’est pas terminé ou n’a pas été validé sur l’appareil filmé :** « Cette version conserve un solde de minutes et les récompenses des quiz. Le suivi du temps d’écran Android est en cours de vérification ; je ne le présente pas comme opérationnel. » Montrer alors uniquement les réglages et le solde local. Ne pas montrer de compteur système fictif.

**Variante si le suivi est intégré et validé avant le tournage :** montrer l’écran de sélection des applications, l’accès spécial Android réellement accordé, les applications choisies et une actualisation réelle du temps utilisé. Ne garder cette séquence que si le relevé observé correspond au comportement expliqué ; sinon, revenir à la variante précédente.

## 3:15 – 3:45 | Architecture et intérêt

**Image :** bref aperçu du dépôt ou d’un schéma préparé à partir de docs/ARCHITECTURE.md.

**Voix :** « Les banques de questions et leur filtrage sont séparés de l’interface. Les écrans Compose affichent le parcours, et un dépôt local conserve le solde. Cette structure permet de faire évoluer le contenu et d’examiner ensuite comment relier une récompense éducative à un usage Android réel, avec les validations nécessaires. »

## 3:45 – 4:00 | Conclusion

**Image :** retour à l’écran d’accueil, puis nom FocusQuest.

**Voix :** « FocusQuest propose une piste à tester : commencer par une courte mission scolaire et rendre la récompense visible. La prochaine étape est de valider le contenu et les fonctions Android avec les utilisateurs concernés. »

## Règles de montage

- Durée finale inférieure ou égale à quatre minutes.
- Ne pas présenter une animation, un solde ou un compteur comme une mesure système s’il s’agit seulement d’un affichage local.
- Ne pas annoncer de résultat scientifique ou clinique.
- Refaire l’enregistrement si le comportement filmé diffère du comportement réellement vérifié.
