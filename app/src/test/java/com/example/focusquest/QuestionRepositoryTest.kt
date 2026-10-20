package com.example.focusquest

import com.example.focusquest.data.QuestionRepository
import com.example.focusquest.model.Grade
import com.example.focusquest.model.Subject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuestionRepositoryTest {
    private val supportedGrades = listOf(
        Grade.CINQUIEME,
        Grade.QUATRIEME,
        Grade.TROISIEME
    )

    @Test
    fun eachSupportedCategoryContainsTenQuestions() {
        supportedGrades.forEach { grade ->
            Subject.entries.forEach { subject ->
                assertEquals(
                    "Nombre incorrect pour ${grade.displayName} / ${subject.displayName}",
                    10,
                    QuestionRepository.getQuestions(grade, subject).size
                )
            }
        }
    }

    @Test
    fun allQuestionsHaveValidUniqueContent() {
        val questions = QuestionRepository.allQuestions

        assertEquals(120, questions.size)
        assertEquals(questions.size, questions.map { it.id }.toSet().size)

        questions.forEach { question ->
            assertTrue("Question ${question.id} vide", question.question.isNotBlank())
            assertEquals("Question ${question.id} sans 4 réponses", 4, question.answers.size)
            assertEquals(
                "Question ${question.id} avec des réponses dupliquées",
                4,
                question.answers.distinct().size
            )
            assertTrue(
                "Index incorrect pour la question ${question.id}",
                question.correctAnswerIndex in 0..3
            )
            assertTrue(
                "Réponse vide pour la question ${question.id}",
                question.answers.all { it.isNotBlank() }
            )
            assertTrue("Explication vide pour la question ${question.id}", question.explanation.isNotBlank())
            assertEquals(2, question.rewardMinutes)
        }
    }

    @Test
    fun repositoryFiltersWithoutMixingCategories() {
        Grade.entries.forEach { grade ->
            Subject.entries.forEach { subject ->
                val questions = QuestionRepository.getQuestions(grade, subject)

                assertTrue(questions.all { it.grade == grade })
                assertTrue(questions.all { it.subject == subject })
            }
        }
    }

    @Test
    fun aMissionCanTakeFiveDifferentQuestions() {
        supportedGrades.forEach { grade ->
            Subject.entries.forEach { subject ->
                val mission = QuestionRepository.getQuestions(grade, subject).take(5)

                assertEquals(5, mission.size)
                assertEquals(5, mission.map { it.id }.toSet().size)
            }
        }
    }

    @Test
    fun unsupportedCategoryReturnsAnEmptyList() {
        assertTrue(
            QuestionRepository.getQuestions(Grade.SIXIEME, Subject.MATHS).isEmpty()
        )
    }
}
