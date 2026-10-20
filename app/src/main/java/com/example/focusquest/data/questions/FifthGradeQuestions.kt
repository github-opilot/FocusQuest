package com.example.focusquest.data.questions

import com.example.focusquest.model.Grade
import com.example.focusquest.model.Question
import com.example.focusquest.model.Subject

internal val fifthGradeQuestions = listOf(
    // Mathématiques
    fifth(5001, Subject.MATHS, "Combien vaut 3/4 de 20 ?", listOf("5", "10", "15", "18"), 2, "20 ÷ 4 = 5, puis 5 × 3 = 15."),
    fifth(5002, Subject.MATHS, "Combien font -3 + 7 ?", listOf("-10", "-4", "4", "10"), 2, "Sur une droite graduée, avancer de 7 depuis -3 conduit à 4."),
    fifth(5003, Subject.MATHS, "Quel est le périmètre d'un rectangle de 6 cm sur 4 cm ?", listOf("10 cm", "18 cm", "20 cm", "24 cm"), 2, "Le périmètre vaut 2 × (6 + 4) = 20 cm."),
    fifth(5004, Subject.MATHS, "Quelle est l'aire d'un rectangle de 7 cm sur 3 cm ?", listOf("10 cm²", "21 cm²", "20 cm²", "28 cm²"), 1, "L'aire vaut 7 × 3 = 21 cm²."),
    fifth(5005, Subject.MATHS, "Combien font 25 % de 40 ?", listOf("5", "10", "15", "20"), 1, "25 % représente un quart, et 40 ÷ 4 = 10."),
    fifth(5006, Subject.MATHS, "Combien font 2,5 + 1,75 ?", listOf("3,25", "4,15", "4,25", "4,75"), 2, "2,50 + 1,75 = 4,25."),
    fifth(5007, Subject.MATHS, "Combien font 84 ÷ 7 ?", listOf("10", "11", "12", "14"), 2, "7 × 12 = 84, donc 84 ÷ 7 = 12."),
    fifth(5008, Subject.MATHS, "Deux angles d'un triangle mesurent 50° et 60°. Combien mesure le troisième ?", listOf("60°", "65°", "70°", "80°"), 2, "La somme vaut 180° : 180 - 50 - 60 = 70°."),
    fifth(5009, Subject.MATHS, "Quelle fraction est égale à 6/8 ?", listOf("2/3", "3/4", "4/5", "5/6"), 1, "En divisant 6 et 8 par 2, on obtient 3/4."),
    fifth(5010, Subject.MATHS, "Trois cahiers coûtent 6 €. Combien coûtent cinq cahiers au même prix ?", listOf("8 €", "9 €", "10 €", "12 €"), 2, "Un cahier coûte 2 €, donc cinq coûtent 10 €."),

    // Français
    fifth(5101, Subject.FRANCAIS, "Quel est le pluriel de « cheval » ?", listOf("Chevals", "Chevaux", "Chevaus", "Cheval"), 1, "Le pluriel de « cheval » est « chevaux »."),
    fifth(5102, Subject.FRANCAIS, "Dans « Elle parle doucement », quel est l'adverbe ?", listOf("Elle", "parle", "doucement", "aucun"), 2, "« Doucement » précise la manière de parler."),
    fifth(5103, Subject.FRANCAIS, "Dans « Le chat dort », quel est le verbe ?", listOf("Le", "chat", "dort", "Le chat"), 2, "« Dort » exprime l'action du sujet."),
    fifth(5104, Subject.FRANCAIS, "Comment conjugue-t-on « finir » avec « nous » au présent ?", listOf("Nous finons", "Nous finissons", "Nous finirons", "Nous finissions"), 1, "Au présent, on écrit « nous finissons »."),
    fifth(5105, Subject.FRANCAIS, "Dans « Elle porte une robe bleue », quel mot est un adjectif ?", listOf("Elle", "porte", "bleue", "une"), 2, "« Bleue » précise une caractéristique de la robe."),
    fifth(5106, Subject.FRANCAIS, "Quel est un synonyme de « joyeux » ?", listOf("Heureux", "Triste", "Lent", "Sombre"), 0, "« Heureux » et « joyeux » ont un sens proche."),
    fifth(5107, Subject.FRANCAIS, "Quel est le contraire de « ancien » ?", listOf("Vieux", "Moderne", "Passé", "Usé"), 1, "« Moderne » est l'antonyme d'« ancien »."),
    fifth(5108, Subject.FRANCAIS, "Dans « Les élèves lisent », quel est le sujet ?", listOf("Les élèves", "lisent", "Les", "élèves lisent"), 0, "Ce sont les élèves qui accomplissent l'action de lire."),
    fifth(5109, Subject.FRANCAIS, "Quelle forme est à l'imparfait ?", listOf("Ils jouent", "Ils jouaient", "Ils joueront", "Ils ont joué"), 1, "« Ils jouaient » est conjugué à l'imparfait."),
    fifth(5110, Subject.FRANCAIS, "Complète : « Il ... faim. »", listOf("a", "à", "as", "ah"), 0, "On utilise le verbe avoir : « Il a faim. »"),

    // Histoire-Géographie
    fifth(5201, Subject.HISTOIRE_GEOGRAPHIE, "Quelle date marque traditionnellement le début du Moyen Âge ?", listOf("52 av. J.-C.", "476", "800", "1492"), 1, "La chute de l'Empire romain d'Occident en 476 marque traditionnellement ce début."),
    fifth(5202, Subject.HISTOIRE_GEOGRAPHIE, "En quelle année Charlemagne est-il couronné empereur ?", listOf("476", "622", "800", "987"), 2, "Charlemagne est couronné empereur à Rome en l'an 800."),
    fifth(5203, Subject.HISTOIRE_GEOGRAPHIE, "Où vit généralement un seigneur au Moyen Âge ?", listOf("Dans une usine", "Dans un château", "Dans une pyramide", "Dans un forum"), 1, "Le château est la résidence et le centre du pouvoir du seigneur."),
    fifth(5204, Subject.HISTOIRE_GEOGRAPHIE, "Quelle activité occupe la majorité des paysans au Moyen Âge ?", listOf("L'agriculture", "La navigation", "L'imprimerie", "La banque"), 0, "La majorité des paysans cultivent les terres."),
    fifth(5205, Subject.HISTOIRE_GEOGRAPHIE, "Quelle religion domine l'Europe occidentale au Moyen Âge ?", listOf("Le christianisme", "L'hindouisme", "Le bouddhisme", "Le shintoïsme"), 0, "Le christianisme structure largement la société médiévale occidentale."),
    fifth(5206, Subject.HISTOIRE_GEOGRAPHIE, "Quelle est la capitale de l'Empire byzantin ?", listOf("Rome", "Athènes", "Constantinople", "Alexandrie"), 2, "Constantinople est la capitale de l'Empire byzantin."),
    fifth(5207, Subject.HISTOIRE_GEOGRAPHIE, "Quel est le livre sacré de l'islam ?", listOf("La Bible", "La Torah", "Le Coran", "L'Iliade"), 2, "Le Coran est le livre sacré de l'islam."),
    fifth(5208, Subject.HISTOIRE_GEOGRAPHIE, "Sur quel continent se trouve la France ?", listOf("L'Afrique", "L'Europe", "L'Asie", "L'Amérique"), 1, "La France métropolitaine se situe en Europe."),
    fifth(5209, Subject.HISTOIRE_GEOGRAPHIE, "Quel est le plus vaste océan du monde ?", listOf("L'océan Atlantique", "L'océan Indien", "L'océan Pacifique", "L'océan Arctique"), 2, "L'océan Pacifique est le plus vaste."),
    fifth(5210, Subject.HISTOIRE_GEOGRAPHIE, "Quelle ligne imaginaire sépare la Terre en deux hémisphères nord et sud ?", listOf("Le tropique du Cancer", "L'équateur", "Le méridien de Greenwich", "Le cercle polaire"), 1, "L'équateur sépare les hémisphères nord et sud."),

    // Sciences
    fifth(5301, Subject.SCIENCES, "Quel organe permet principalement de respirer ?", listOf("Le cœur", "Les poumons", "L'estomac", "Les reins"), 1, "Les poumons assurent les échanges gazeux entre l'air et le sang."),
    fifth(5302, Subject.SCIENCES, "À quelle température l'eau pure gèle-t-elle habituellement ?", listOf("0 °C", "10 °C", "50 °C", "100 °C"), 0, "L'eau pure gèle à 0 °C dans les conditions habituelles."),
    fifth(5303, Subject.SCIENCES, "Quels sont les trois états courants de la matière ?", listOf("Chaud, froid, tiède", "Solide, liquide, gaz", "Lourd, léger, moyen", "Dur, mou, fragile"), 1, "La matière existe couramment sous les états solide, liquide et gazeux."),
    fifth(5304, Subject.SCIENCES, "Quelle étoile éclaire la Terre ?", listOf("La Lune", "Mars", "Le Soleil", "Vénus"), 2, "Le Soleil est l'étoile autour de laquelle la Terre tourne."),
    fifth(5305, Subject.SCIENCES, "Quelle planète est la plus proche du Soleil ?", listOf("Vénus", "Mercure", "La Terre", "Mars"), 1, "Mercure est la planète la plus proche du Soleil."),
    fifth(5306, Subject.SCIENCES, "De quoi une plante verte a-t-elle besoin pour réaliser la photosynthèse ?", listOf("De lumière", "De sel", "De sable", "De métal"), 0, "La lumière fournit l'énergie nécessaire à la photosynthèse."),
    fifth(5307, Subject.SCIENCES, "Quelle unité utilise-t-on couramment pour mesurer une température ?", listOf("Le mètre", "Le litre", "Le degré Celsius", "Le kilogramme"), 2, "La température se mesure couramment en degrés Celsius."),
    fifth(5308, Subject.SCIENCES, "Dans quel type de circuit une lampe peut-elle s'allumer ?", listOf("Un circuit ouvert", "Un circuit fermé", "Un circuit cassé", "Un circuit sans générateur"), 1, "Le courant circule lorsque le circuit est fermé."),
    fifth(5309, Subject.SCIENCES, "Que forme du sel complètement dissous dans l'eau ?", listOf("Un mélange homogène", "Un corps pur", "Un solide", "Un gaz"), 0, "Le sel dissous est réparti uniformément : le mélange est homogène."),
    fifth(5310, Subject.SCIENCES, "Quel est le rôle principal du cœur ?", listOf("Digérer les aliments", "Pomper le sang", "Produire l'air", "Filtrer la lumière"), 1, "Le cœur propulse le sang dans l'organisme."),
)

private fun fifth(
    id: Int,
    subject: Subject,
    text: String,
    answers: List<String>,
    correctAnswerIndex: Int,
    explanation: String
) = Question(
    id = id,
    grade = Grade.CINQUIEME,
    subject = subject,
    question = text,
    answers = answers,
    correctAnswerIndex = correctAnswerIndex,
    explanation = explanation
)
