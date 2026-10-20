package com.example.focusquest

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuizFlowInstrumentedTest {
    private val thirdGradeMathsAnswers = mapOf(
        "Résous : 3x + 5 = 20" to "x = 5",
        "Combien vaut √81 ?" to "9",
        "Combien font 10 % de 250 ?" to "25",
        "Un triangle rectangle a des côtés de 3 cm et 4 cm autour de l'angle droit. Quelle est son hypoténuse ?" to "5 cm",
        "Quel est le développement de 2(x + 3) ?" to "2x + 6",
        "Quelle est la factorisation de 5x + 15 ?" to "5(x + 3)",
        "Quelle est la probabilité d'obtenir un nombre pair avec un dé équilibré ?" to "1/2",
        "Si f(x) = 2x - 1, combien vaut f(4) ?" to "7",
        "Si 3/5 = x/10, combien vaut x ?" to "6",
        "Un prix passe de 80 € à 100 €. Quel est le pourcentage d'augmentation ?" to "25 %"
    )

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun thirdGradeMathsQuizCanBeCompleted() {
        composeRule.onNodeWithText("Commencer une mission").performClick()
        composeRule.onNodeWithText("3e").performClick()
        composeRule.onNodeWithText("Mathématiques").performClick()

        composeRule.onNodeWithText("Question 1 / 5").assertIsDisplayed()
        repeat(5) { questionIndex ->
            answerCurrentQuestion(isLastQuestion = questionIndex == 4)
        }

        composeRule.onNodeWithText("Quiz terminé !").assertIsDisplayed()
        composeRule.onNodeWithText("5 / 5", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("+2 min 30 sec", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("Temps disponible :", substring = true).assertIsDisplayed()

        composeRule.onNodeWithText("Nouvelle mission").performClick()
        composeRule.onNodeWithText("Quelle est ta classe ?").assertIsDisplayed()
    }

    private fun answerCurrentQuestion(isLastQuestion: Boolean) {
        val answer = thirdGradeMathsAnswers.entries
            .first { (question, _) ->
                composeRule.onAllNodesWithText(question).fetchSemanticsNodes().isNotEmpty()
            }
            .value

        answerCorrectly(answer, isLastQuestion)
    }

    @Test
    fun androidBackReturnsToPreviousSelectionScreens() {
        composeRule.onNodeWithText("Commencer une mission").performClick()
        composeRule.onNodeWithText("3e").performClick()
        composeRule.onNodeWithText("Choisis une matière").assertIsDisplayed()

        composeRule.runOnUiThread {
            composeRule.activity.onBackPressedDispatcher.onBackPressed()
        }
        composeRule.onNodeWithText("Quelle est ta classe ?").assertIsDisplayed()

        composeRule.runOnUiThread {
            composeRule.activity.onBackPressedDispatcher.onBackPressed()
        }
        composeRule.onNodeWithText("FocusQuest").assertIsDisplayed()
    }

    private fun answerCorrectly(
        answer: String,
        isLastQuestion: Boolean = false
    ) {
        composeRule.onNodeWithText(answer).performClick()
        composeRule.onNodeWithText("Bonne réponse ! +30 sec").assertIsDisplayed()
        composeRule.onNodeWithText(
            if (isLastQuestion) "Voir mes résultats" else "Question suivante"
        ).performClick()
    }
}
