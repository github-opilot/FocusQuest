package com.example.focusquest

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.focusquest.data.catalog.ChapterQuestionRepository
import com.example.focusquest.data.catalog.CurriculumRepository
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuizFlowInstrumentedTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun thirdGradeMathsEquationsChapterOpensItsQuiz() {
        val questions = openQuiz(
            gradeTitle = "3e",
            gradeId = "3e",
            subjectId = "maths",
            chapterId = "3e-maths-equations"
        )

        assertEqualsQuestionCount(questions.size)
        assertCurrentQuestionBelongsTo(questions)
    }

    @Test
    fun fifthGradeFrenchChapterOpensItsQuiz() {
        val questions = openQuiz(
            gradeTitle = "5e",
            gradeId = "5e",
            subjectId = "francais",
            chapterId = "5e-francais-heroines-heros"
        )

        assertEqualsQuestionCount(questions.size)
        assertCurrentQuestionBelongsTo(questions)
    }

    @Test
    fun secondeMathsChapterOpensItsQuiz() {
        val questions = openQuiz(
            gradeTitle = "Seconde",
            gradeId = "seconde",
            subjectId = "maths",
            chapterId = "seconde-maths-arithmetique"
        )

        assertEqualsQuestionCount(questions.size)
        assertCurrentQuestionBelongsTo(questions)
    }

    @Test
    fun premiereSpecialtyChapterOpensItsQuiz() {
        val questions = openQuiz(
            gradeTitle = "Première",
            gradeId = "premiere_generale",
            subjectId = "maths-spe",
            chapterId = "premiere-maths-second-degre"
        )

        assertEqualsQuestionCount(questions.size)
        assertCurrentQuestionBelongsTo(questions)
    }

    @Test
    fun backButtonsReturnThroughTheSelectionFlow() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val curriculum = CurriculumRepository(context)
        val chapterQuestions = ChapterQuestionRepository(context, curriculum)
        val subject = curriculum.getSubjects("3e").first { it.id == "maths" }
        val firstAvailableChapter = curriculum.getChapters("3e", subject.id)
            .first { chapterQuestions.getQuestionCount("3e", subject.id, it.id) > 0 }

        ensureHome()
        composeRule.onNodeWithText("Commencer une mission").performClick()
        composeRule.onNodeWithText("3e").performClick()
        composeRule.onNodeWithText("Choisis une matière").assertIsDisplayed()

        scrollToText(subject.title)
        composeRule.onNodeWithText(subject.title).performClick()
        scrollToText(firstAvailableChapter.title)
        composeRule.onNodeWithText(firstAvailableChapter.title).performClick()
        composeRule.onNodeWithText("QCM · 5 questions max").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Retour").performClick()
        composeRule.onNodeWithText(subject.title).assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Retour").performClick()
        composeRule.onNodeWithText("Choisis une matière").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Retour").performClick()
        composeRule.onNodeWithText("Quelle est ta classe ?").assertIsDisplayed()

        composeRule.onNodeWithText("Retour").performClick()
        composeRule.onNodeWithText("FocusQuest").assertIsDisplayed()
    }

    @Test
    fun chaptersWithoutQuestionsRemainVisibleAndCannotBeOpened() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val curriculum = CurriculumRepository(context)
        val chapterQuestions = ChapterQuestionRepository(context, curriculum)
        val emptyChapter = curriculum.getChapters("3e", "maths")
            .first { chapterQuestions.getQuestionCount("3e", "maths", it.id) == 0 }

        ensureHome()
        composeRule.onNodeWithText("Commencer une mission").performClick()
        composeRule.onNodeWithText("3e").performClick()
        val subject = curriculum.getSubjects("3e").first { it.id == "maths" }
        scrollToText(subject.title)
        composeRule.onNodeWithText(subject.title).performClick()
        scrollToText(emptyChapter.title)
        composeRule.onNodeWithText(emptyChapter.title).assertIsDisplayed().assertIsNotEnabled()
    }

    private fun openQuiz(
        gradeTitle: String,
        gradeId: String,
        subjectId: String,
        chapterId: String
    ): List<com.example.focusquest.model.QuizQuestionEntry> {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val curriculum = CurriculumRepository(context)
        val questions = ChapterQuestionRepository(context, curriculum)
            .getQuestions(gradeId, subjectId, chapterId)
        assertTrue("Expected QCM data for $gradeId/$subjectId/$chapterId", questions.isNotEmpty())

        val subject = curriculum.getSubjects(gradeId).first { it.id == subjectId }
        val chapter = curriculum.getChapters(gradeId, subjectId).first { it.id == chapterId }

        ensureHome()
        composeRule.onNodeWithText("Commencer une mission").performClick()
        composeRule.onNodeWithText(gradeTitle).performClick()
        scrollToText(subject.title)
        composeRule.onNodeWithText(subject.title).performClick()
        scrollToText(chapter.title)
        composeRule.onNodeWithText(chapter.title).performClick()
        composeRule.onNodeWithText("QCM · 5 questions max").assertIsDisplayed()
        composeRule.onNodeWithText("Commencer").performClick()
        composeRule.onNodeWithText("Question 1 / ${minOf(questions.size, 5)}").assertIsDisplayed()
        return questions
    }

    private fun assertEqualsQuestionCount(count: Int) {
        assertTrue("Expected a five-question mission, got $count questions", count >= 5)
    }

    private fun assertCurrentQuestionBelongsTo(questions: List<com.example.focusquest.model.QuizQuestionEntry>) {
        val visibleQuestion = questions.firstOrNull { question ->
            composeRule.onAllNodesWithText(question.question).fetchSemanticsNodes().isNotEmpty()
        }
        assertNotNull("The quiz showed no question from the selected chapter", visibleQuestion)
    }

    private fun scrollToText(text: String) {
        if (composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()) return

        if (composeRule.onAllNodesWithTag("subject-picker-grid").fetchSemanticsNodes().isNotEmpty()) {
            composeRule.onNodeWithTag("subject-picker-grid").performScrollToNode(hasText(text))
            return
        }
        if (composeRule.onAllNodesWithTag("chapter-picker-list").fetchSemanticsNodes().isNotEmpty()) {
            composeRule.onNodeWithTag("chapter-picker-list").performScrollToNode(hasText(text))
            return
        }

        repeat(12) {
            if (composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()) {
                return
            }
            composeRule.onRoot().performTouchInput { swipeUp() }
        }
        composeRule.onNodeWithText(text).assertIsDisplayed()
    }

    private fun ensureHome() {
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithText("Commencer une mission")
                .fetchSemanticsNodes().isNotEmpty() ||
                composeRule.onAllNodesWithText("Passer").fetchSemanticsNodes().isNotEmpty()
        }
        if (composeRule.onAllNodesWithText("Commencer une mission").fetchSemanticsNodes().isEmpty()) {
            composeRule.onNodeWithText("Passer").performClick()
        }
        composeRule.onNodeWithText("Commencer une mission").assertIsDisplayed()
    }
}
