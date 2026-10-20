package com.example.focusquest.data.questions

import com.example.focusquest.model.Grade
import com.example.focusquest.model.Question
import com.example.focusquest.model.Subject

internal val thirdGradeQuestions = listOf(
    // Mathématiques
    third(3001, Subject.MATHS, "Résous : 3x + 5 = 20", listOf("x = 3", "x = 5", "x = 8", "x = 15"), 1, "3x = 15, donc x = 5."),
    third(3002, Subject.MATHS, "Combien vaut √81 ?", listOf("7", "8", "9", "10"), 2, "9 × 9 = 81, donc √81 = 9."),
    third(3003, Subject.MATHS, "Combien font 10 % de 250 ?", listOf("10", "20", "25", "50"), 2, "10 % de 250 = 250 ÷ 10 = 25."),
    third(3004, Subject.MATHS, "Un triangle rectangle a des côtés de 3 cm et 4 cm autour de l'angle droit. Quelle est son hypoténuse ?", listOf("5 cm", "6 cm", "7 cm", "8 cm"), 0, "D'après Pythagore : 3² + 4² = 25, donc l'hypoténuse mesure 5 cm."),
    third(3005, Subject.MATHS, "Quel est le développement de 2(x + 3) ?", listOf("2x + 3", "2x + 5", "2x + 6", "x + 6"), 2, "On distribue 2 : 2 × x + 2 × 3 = 2x + 6."),
    third(3006, Subject.MATHS, "Quelle est la factorisation de 5x + 15 ?", listOf("5(x + 3)", "5(x + 15)", "x(5 + 15)", "15(x + 1)"), 0, "5 est un facteur commun : 5x + 15 = 5(x + 3)."),
    third(3007, Subject.MATHS, "Quelle est la probabilité d'obtenir un nombre pair avec un dé équilibré ?", listOf("1/6", "1/3", "1/2", "2/3"), 2, "Trois faces sur six sont paires, donc 3/6 = 1/2."),
    third(3008, Subject.MATHS, "Si f(x) = 2x - 1, combien vaut f(4) ?", listOf("5", "6", "7", "8"), 2, "f(4) = 2 × 4 - 1 = 7."),
    third(3009, Subject.MATHS, "Si 3/5 = x/10, combien vaut x ?", listOf("5", "6", "7", "8"), 1, "Pour passer de 5 à 10, on multiplie par 2 : x = 3 × 2 = 6."),
    third(3010, Subject.MATHS, "Un prix passe de 80 € à 100 €. Quel est le pourcentage d'augmentation ?", listOf("20 %", "25 %", "40 %", "80 %"), 1, "L'augmentation est de 20 €, soit 20/80 = 25 %."),

    // Français
    third(3101, Subject.FRANCAIS, "Quelle figure de style emploie un outil comme « tel » ou « comme » ?", listOf("La métaphore", "La comparaison", "L'oxymore", "L'hyperbole"), 1, "La comparaison rapproche deux éléments grâce à un outil de comparaison."),
    third(3102, Subject.FRANCAIS, "Dans « Il mange une pomme », quel est le COD ?", listOf("Il", "mange", "une pomme", "Il mange"), 2, "Il mange quoi ? Une pomme : c'est le complément d'objet direct."),
    third(3103, Subject.FRANCAIS, "Quel est le participe passé du verbe « écrire » ?", listOf("Écrivé", "Écrit", "Écrite", "Écrivant"), 1, "Le participe passé du verbe « écrire » est « écrit »."),
    third(3104, Subject.FRANCAIS, "Quel mot est un adverbe ?", listOf("Rapide", "Rapidement", "Rapidité", "Accélérer"), 1, "« Rapidement » est un adverbe formé à partir de l'adjectif « rapide »."),
    third(3105, Subject.FRANCAIS, "Quelle phrase est correctement accordée ?", listOf("Elles sont arrivé", "Elles sont arrivés", "Elles sont arrivées", "Elles sont arriver"), 2, "Avec l'auxiliaire être, le participe passé s'accorde avec le sujet féminin pluriel."),
    third(3106, Subject.FRANCAIS, "Quelle figure de style trouve-t-on dans « Cette obscure clarté » ?", listOf("Une comparaison", "Un oxymore", "Une anaphore", "Une litote"), 1, "L'oxymore rapproche deux mots de sens opposés : « obscure » et « clarté »."),
    third(3107, Subject.FRANCAIS, "Complète : « Il faut que tu ... à l'heure. »", listOf("viens", "viendras", "viennes", "venais"), 2, "Après « il faut que », on emploie le subjonctif : « tu viennes »."),
    third(3108, Subject.FRANCAIS, "Dans « Le livre qui est posé ici », quel est le pronom relatif ?", listOf("Le", "livre", "qui", "ici"), 2, "« Qui » introduit la proposition relative et reprend « le livre »."),
    third(3109, Subject.FRANCAIS, "Quel point de vue donne accès aux pensées d'un personnage ?", listOf("Le point de vue interne", "Le point de vue externe", "Le point de vue documentaire", "Le point de vue neutre"), 0, "Le point de vue interne présente les perceptions et pensées d'un personnage."),
    third(3110, Subject.FRANCAIS, "Dans un texte argumentatif, qu'est-ce que la thèse ?", listOf("Un exemple", "L'idée défendue", "Le titre", "Une citation"), 1, "La thèse est l'idée principale que l'auteur cherche à défendre."),

    // Histoire-Géographie
    third(3201, Subject.HISTOIRE_GEOGRAPHIE, "En quelle année débute la Seconde Guerre mondiale en Europe ?", listOf("1914", "1918", "1939", "1945"), 2, "La guerre débute en Europe en septembre 1939 avec l'invasion de la Pologne."),
    third(3202, Subject.HISTOIRE_GEOGRAPHIE, "En quelle année l'armistice de la Première Guerre mondiale est-il signé ?", listOf("1914", "1917", "1918", "1920"), 2, "L'armistice est signé le 11 novembre 1918."),
    third(3203, Subject.HISTOIRE_GEOGRAPHIE, "En quelle année la Cinquième République française est-elle fondée ?", listOf("1945", "1958", "1962", "1981"), 1, "La Constitution de la Cinquième République entre en vigueur en 1958."),
    third(3204, Subject.HISTOIRE_GEOGRAPHIE, "En quelle année l'Organisation des Nations unies est-elle créée ?", listOf("1919", "1939", "1945", "1957"), 2, "L'ONU est fondée en 1945 après la Seconde Guerre mondiale."),
    third(3205, Subject.HISTOIRE_GEOGRAPHIE, "Quelles puissances s'opposent principalement pendant la guerre froide ?", listOf("La France et l'Allemagne", "Les États-Unis et l'URSS", "La Chine et le Japon", "L'Italie et l'Espagne"), 1, "La guerre froide oppose principalement les blocs menés par les États-Unis et l'URSS."),
    third(3206, Subject.HISTOIRE_GEOGRAPHIE, "Quel pays devient indépendant du Royaume-Uni en 1947 ?", listOf("L'Inde", "Le Canada", "L'Australie", "L'Irlande"), 0, "L'Inde accède à l'indépendance en 1947."),
    third(3207, Subject.HISTOIRE_GEOGRAPHIE, "Quelle ville accueille notamment la Commission européenne ?", listOf("Bruxelles", "Madrid", "Rome", "Lisbonne"), 0, "La Commission européenne siège à Bruxelles."),
    third(3208, Subject.HISTOIRE_GEOGRAPHIE, "Quelle est la plus grande aire urbaine française ?", listOf("Lille", "Lyon", "Paris", "Toulouse"), 2, "L'aire urbaine de Paris est la plus peuplée de France."),
    third(3209, Subject.HISTOIRE_GEOGRAPHIE, "Lequel de ces territoires est un département français d'outre-mer ?", listOf("La Guadeloupe", "Le Québec", "La Corse-du-Sud uniquement", "Madagascar"), 0, "La Guadeloupe est un département et une région d'outre-mer français."),
    third(3210, Subject.HISTOIRE_GEOGRAPHIE, "Qu'appelle-t-on mondialisation ?", listOf("La fermeture des frontières", "L'intensification des échanges à l'échelle mondiale", "La disparition des villes", "La baisse de tous les transports"), 1, "La mondialisation correspond à l'intensification des échanges et des liens entre territoires."),

    // Sciences
    third(3301, Subject.SCIENCES, "Quelle molécule porte l'information génétique ?", listOf("L'eau", "L'ADN", "Le dioxygène", "Le glucose"), 1, "L'ADN contient l'information génétique des êtres vivants."),
    third(3302, Subject.SCIENCES, "Quelle est l'unité de l'intensité électrique ?", listOf("Le volt", "Le watt", "L'ampère", "L'ohm"), 2, "L'intensité électrique se mesure en ampères."),
    third(3303, Subject.SCIENCES, "Où se trouvent principalement les chromosomes d'une cellule humaine ?", listOf("Dans le noyau", "Dans la membrane", "Dans le sang", "Dans les poumons"), 0, "Les chromosomes sont principalement situés dans le noyau de la cellule."),
    third(3304, Subject.SCIENCES, "Quelle source d'énergie est renouvelable ?", listOf("Le charbon", "Le pétrole", "Le Soleil", "Le gaz naturel"), 2, "L'énergie solaire se renouvelle naturellement à l'échelle humaine."),
    third(3305, Subject.SCIENCES, "Quelle est l'unité d'une force ?", listOf("Le joule", "Le newton", "Le watt", "Le volt"), 1, "Une force se mesure en newtons, symbole N."),
    third(3306, Subject.SCIENCES, "Lors d'une transformation chimique, que deviennent les atomes ?", listOf("Ils disparaissent", "Ils sont conservés et se réorganisent", "Ils doublent toujours", "Ils deviennent de l'énergie"), 1, "Les atomes se conservent mais s'assemblent différemment."),
    third(3307, Subject.SCIENCES, "Quel pH correspond à une solution acide ?", listOf("Un pH inférieur à 7", "Un pH égal à 7", "Un pH supérieur à 7", "Uniquement un pH de 14"), 0, "Une solution acide a un pH inférieur à 7."),
    third(3308, Subject.SCIENCES, "Un coureur parcourt 100 m en 20 s. Quelle est sa vitesse moyenne ?", listOf("2 m/s", "5 m/s", "20 m/s", "80 m/s"), 1, "La vitesse vaut 100 ÷ 20 = 5 m/s."),
    third(3309, Subject.SCIENCES, "Lequel de ces gaz contribue à l'effet de serre ?", listOf("Le dioxyde de carbone", "L'hélium", "Le néon", "L'argon uniquement"), 0, "Le dioxyde de carbone est un gaz à effet de serre."),
    third(3310, Subject.SCIENCES, "Quelle formule relie puissance, tension et intensité électriques ?", listOf("P = U × I", "P = U + I", "P = U ÷ I", "P = I - U"), 0, "La puissance électrique vérifie P = U × I."),
)

private fun third(
    id: Int,
    subject: Subject,
    text: String,
    answers: List<String>,
    correctAnswerIndex: Int,
    explanation: String
) = Question(
    id = id,
    grade = Grade.TROISIEME,
    subject = subject,
    question = text,
    answers = answers,
    correctAnswerIndex = correctAnswerIndex,
    explanation = explanation
)
