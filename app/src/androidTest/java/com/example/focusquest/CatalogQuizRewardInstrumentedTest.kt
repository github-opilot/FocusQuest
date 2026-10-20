package com.example.focusquest

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.focusquest.model.QuizQuestionEntry
import com.example.focusquest.ui.ChapterQuizScreen
import com.example.focusquest.ui.theme.FocusQuestTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CatalogQuizRewardInstrumentedTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun correctCatalogAnswerShowsExplanationAndAwardsThirtySecondsOnce() {
        val rewards = mutableListOf<Long>()
        val questions = (1..5).map { index ->
            QuizQuestionEntry(
                id = "reward-once-$index",
                subjectId = "maths",
                chapterId = "equations",
                question = "Question $index ?",
                answers = listOf("Faux A $index", "Juste $index", "Faux B $index", "Faux C $index"),
                correctAnswerIndex = 1,
                explanation = "Explication $index.",
                rewardSeconds = 30
            )
        }

        composeRule.setContent {
            FocusQuestTheme {
                ChapterQuizScreen(
                    questions = questions,
                    availableTimeMillis = 0L,
                    onTimeEarned = { rewards.add(it) },
                    onExitQuiz = {},
                    onNewMission = {}
                )
            }
        }

        questions.forEachIndexed { index, question ->
            composeRule.onNodeWithText("Question ${index + 1} / 5").assertIsDisplayed()
            composeRule.onNodeWithText(question.question).assertIsDisplayed()
            val answer = question.answers[question.correctAnswerIndex]
            composeRule.onNodeWithText(answer).performClick()
            composeRule.onNodeWithText("Bonne réponse !").assertIsDisplayed()
            composeRule.onNodeWithText("+30 sec").assertIsDisplayed()
            composeRule.onNodeWithText(question.explanation).assertIsDisplayed()
            composeRule.onNodeWithText(answer).assertIsNotEnabled()
            composeRule.onNodeWithText(
                if (index == questions.lastIndex) "Voir mes résultats" else "Question suivante"
            ).performClick()
        }

        composeRule.onNodeWithText("Mission terminée !").assertIsDisplayed()
        composeRule.onNodeWithText("5 / 5").assertIsDisplayed()
        composeRule.onNodeWithText("+2 min 30 sec").assertIsDisplayed()
        composeRule.runOnIdle {
            assertEquals(List(5) { 30_000L }, rewards)
        }
    }
}
