package com.example.focusquest.data.questions

import com.example.focusquest.model.Grade
import com.example.focusquest.model.Question
import com.example.focusquest.model.Subject

internal val fourthGradeQuestions = listOf(
    // Mathématiques
    fourth(4001, Subject.MATHS, "Combien vaut 2³ ?", listOf("6", "8", "9", "12"), 1, "2³ = 2 × 2 × 2 = 8."),
    fourth(4002, Subject.MATHS, "Résous : 2x + 3 = 11", listOf("x = 3", "x = 4", "x = 5", "x = 7"), 1, "2x = 8, donc x = 4."),
    fourth(4003, Subject.MATHS, "Combien font 15 % de 200 ?", listOf("15", "20", "30", "40"), 2, "15 % de 200 = 0,15 × 200 = 30."),
    fourth(4004, Subject.MATHS, "Un triangle rectangle a des côtés de 6 cm et 8 cm autour de l'angle droit. Quelle est son hypoténuse ?", listOf("9 cm", "10 cm", "12 cm", "14 cm"), 1, "6² + 8² = 100, donc l'hypoténuse mesure 10 cm."),
    fourth(4005, Subject.MATHS, "Combien font (-4) × 3 ?", listOf("-12", "-7", "7", "12"), 0, "Le produit d'un nombre négatif par un nombre positif est négatif."),
    fourth(4006, Subject.MATHS, "Combien font 3/5 + 1/5 ?", listOf("3/10", "4/10", "4/5", "1"), 2, "Les dénominateurs sont identiques : 3/5 + 1/5 = 4/5."),
    fourth(4007, Subject.MATHS, "Quel est le développement de 3(x + 2) ?", listOf("3x + 2", "3x + 5", "3x + 6", "x + 6"), 2, "On distribue 3 : 3 × x + 3 × 2 = 3x + 6."),
    fourth(4008, Subject.MATHS, "Quatre kilogrammes de pommes coûtent 12 €. Combien coûtent six kilogrammes ?", listOf("15 €", "16 €", "18 €", "20 €"), 2, "Un kilogramme coûte 3 €, donc six coûtent 18 €."),
    fourth(4009, Subject.MATHS, "Combien vaut 10³ ?", listOf("30", "100", "1 000", "10 000"), 2, "10³ = 10 × 10 × 10 = 1 000."),
    fourth(4010, Subject.MATHS, "Quelle est l'aire d'un triangle de base 8 cm et de hauteur 5 cm ?", listOf("13 cm²", "20 cm²", "40 cm²", "80 cm²"), 1, "L'aire vaut (8 × 5) ÷ 2 = 20 cm²."),

    // Français
    fourth(4101, Subject.FRANCAIS, "Dans « Il marche lentement », quel est l'adverbe ?", listOf("Il", "marche", "lentement", "aucun"), 2, "« Lentement » précise la manière dont l'action est réalisée."),
    fourth(4102, Subject.FRANCAIS, "Quelle forme du verbe « prendre » est au passé simple ?", listOf("Il prend", "Il prenait", "Il prit", "Il prendra"), 2, "« Il prit » est la forme du passé simple."),
    fourth(4103, Subject.FRANCAIS, "Dans « Je reste parce qu'il pleut », quel groupe introduit la cause ?", listOf("Je reste", "parce que", "il", "pleut"), 1, "« Parce que » introduit la proposition qui exprime la cause."),
    fourth(4104, Subject.FRANCAIS, "Quelle figure de style trouve-t-on dans « Cet homme est un lion » ?", listOf("Une comparaison", "Une métaphore", "Une personnification", "Une répétition"), 1, "L'homme est assimilé à un lion sans outil de comparaison : c'est une métaphore."),
    fourth(4105, Subject.FRANCAIS, "Dans « Léa prépare un gâteau », quel est le COD ?", listOf("Léa", "prépare", "un gâteau", "Léa prépare"), 2, "Léa prépare quoi ? Un gâteau : c'est le COD."),
    fourth(4106, Subject.FRANCAIS, "À quel mode et temps est « je viendrais » ?", listOf("Futur simple", "Conditionnel présent", "Imparfait", "Présent"), 1, "« Je viendrais » est au conditionnel présent."),
    fourth(4107, Subject.FRANCAIS, "Quel est l'antonyme de « généreux » ?", listOf("Aimable", "Avare", "Courageux", "Sincère"), 1, "Une personne avare est le contraire d'une personne généreuse."),
    fourth(4108, Subject.FRANCAIS, "Quelle phrase est à la voix passive ?", listOf("Paul lance le ballon.", "Le ballon est lancé par Paul.", "Paul va lancer le ballon.", "Paul lançait le ballon."), 1, "Le sujet « le ballon » subit l'action : la phrase est passive."),
    fourth(4109, Subject.FRANCAIS, "Quelle phrase est correctement accordée ?", listOf("Les fleurs sont fané.", "Les fleurs sont fanés.", "Les fleurs sont fanées.", "Les fleurs sont faner."), 2, "Avec être, « fanées » s'accorde avec « les fleurs », féminin pluriel."),
    fourth(4110, Subject.FRANCAIS, "Dans « Hugo arrive. Il sourit. », que remplace « Il » ?", listOf("arrive", "Hugo", "sourit", "rien"), 1, "Le pronom « Il » reprend le nom propre « Hugo »."),

    // Histoire-Géographie
    fourth(4201, Subject.HISTOIRE_GEOGRAPHIE, "En quelle année débute la Révolution française ?", listOf("1685", "1750", "1789", "1815"), 2, "La Révolution française débute en 1789."),
    fourth(4202, Subject.HISTOIRE_GEOGRAPHIE, "Quel événement a lieu le 14 juillet 1789 ?", listOf("Le sacre de Napoléon", "La prise de la Bastille", "La bataille de Waterloo", "La proclamation de la République"), 1, "La Bastille est prise par les Parisiens le 14 juillet 1789."),
    fourth(4203, Subject.HISTOIRE_GEOGRAPHIE, "En quelle année Napoléon Ier est-il sacré empereur ?", listOf("1789", "1792", "1804", "1815"), 2, "Napoléon est sacré empereur en 1804."),
    fourth(4204, Subject.HISTOIRE_GEOGRAPHIE, "Quelle invention symbolise la première révolution industrielle ?", listOf("La machine à vapeur", "Internet", "Le moteur électrique", "L'avion"), 0, "La machine à vapeur joue un rôle majeur dans la première industrialisation."),
    fourth(4205, Subject.HISTOIRE_GEOGRAPHIE, "Quel texte proclame des droits fondamentaux en août 1789 ?", listOf("Le Code civil", "La Déclaration des droits de l'homme et du citoyen", "La Constitution de 1958", "L'Édit de Nantes"), 1, "La Déclaration des droits de l'homme et du citoyen est adoptée en août 1789."),
    fourth(4206, Subject.HISTOIRE_GEOGRAPHIE, "Quel océan est au centre du commerce triangulaire ?", listOf("L'océan Pacifique", "L'océan Atlantique", "L'océan Indien", "L'océan Arctique"), 1, "Le commerce triangulaire relie des ports situés autour de l'Atlantique."),
    fourth(4207, Subject.HISTOIRE_GEOGRAPHIE, "Qu'appelle-t-on urbanisation ?", listOf("La baisse du nombre de villes", "L'augmentation de la population vivant en ville", "La disparition des routes", "Le retour massif à la campagne"), 1, "L'urbanisation est la croissance de la population urbaine et des villes."),
    fourth(4208, Subject.HISTOIRE_GEOGRAPHIE, "Quelle est la capitale du Royaume-Uni ?", listOf("Dublin", "Londres", "Manchester", "Édimbourg"), 1, "Londres est la capitale du Royaume-Uni."),
    fourth(4209, Subject.HISTOIRE_GEOGRAPHIE, "Quel parallèle traverse le nord de l'Afrique ?", listOf("Le tropique du Cancer", "Le cercle polaire antarctique", "Le méridien de Greenwich", "Le tropique du Capricorne"), 0, "Le tropique du Cancer traverse le nord du continent africain."),
    fourth(4210, Subject.HISTOIRE_GEOGRAPHIE, "Quel grand fleuve européen traverse plusieurs capitales, dont Vienne et Budapest ?", listOf("La Seine", "Le Danube", "La Tamise", "Le Tage"), 1, "Le Danube traverse notamment Vienne, Bratislava, Budapest et Belgrade."),

    // Sciences
    fourth(4301, Subject.SCIENCES, "Quelle est la formule chimique de l'eau ?", listOf("CO₂", "O₂", "H₂O", "NaCl"), 2, "Une molécule d'eau contient deux atomes d'hydrogène et un atome d'oxygène."),
    fourth(4302, Subject.SCIENCES, "Quelle est l'unité de l'intensité électrique ?", listOf("Le volt", "L'ampère", "Le watt", "Le mètre"), 1, "L'intensité électrique se mesure en ampères."),
    fourth(4303, Subject.SCIENCES, "Quelle est l'unité de la tension électrique ?", listOf("Le volt", "L'ampère", "Le joule", "Le kilogramme"), 0, "La tension électrique se mesure en volts."),
    fourth(4304, Subject.SCIENCES, "Dans un circuit en série, que se passe-t-il si une lampe est retirée ?", listOf("Les autres brillent davantage", "Toutes les lampes s'éteignent", "Rien ne change", "La pile se recharge"), 1, "Retirer une lampe ouvre le circuit en série et interrompt le courant."),
    fourth(4305, Subject.SCIENCES, "Quel gaz est nécessaire à une combustion ?", listOf("Le dioxygène", "Le diazote", "Le dioxyde de carbone", "La vapeur d'eau"), 0, "Le dioxygène est un réactif nécessaire à la combustion."),
    fourth(4306, Subject.SCIENCES, "Quel gaz notre corps rejette-t-il davantage lors de l'expiration ?", listOf("Du dioxygène", "Du dioxyde de carbone", "De l'hydrogène", "De l'hélium"), 1, "La respiration cellulaire produit du dioxyde de carbone rejeté à l'expiration."),
    fourth(4307, Subject.SCIENCES, "Comment calcule-t-on une vitesse moyenne ?", listOf("distance × durée", "distance ÷ durée", "durée ÷ distance", "distance + durée"), 1, "La vitesse moyenne est la distance parcourue divisée par la durée."),
    fourth(4308, Subject.SCIENCES, "Combien de temps environ la Terre met-elle pour faire le tour du Soleil ?", listOf("24 heures", "30 jours", "365 jours", "10 ans"), 2, "La révolution de la Terre autour du Soleil dure environ 365 jours."),
    fourth(4309, Subject.SCIENCES, "Quelle est l'unité de base du vivant ?", listOf("L'organe", "La cellule", "Le muscle", "Le squelette"), 1, "La cellule est l'unité structurale de base des êtres vivants."),
    fourth(4310, Subject.SCIENCES, "Quel pH indique une solution acide ?", listOf("Un pH inférieur à 7", "Un pH égal à 7", "Un pH supérieur à 7", "Toujours un pH de 14"), 0, "Une solution acide possède un pH inférieur à 7."),
)

private fun fourth(
    id: Int,
    subject: Subject,
    text: String,
    answers: List<String>,
    correctAnswerIndex: Int,
    explanation: String
) = Question(
    id = id,
    grade = Grade.QUATRIEME,
    subject = subject,
    question = text,
    answers = answers,
    correctAnswerIndex = correctAnswerIndex,
    explanation = explanation
)
