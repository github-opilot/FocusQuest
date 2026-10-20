package com.example.focusquest

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.focusquest.data.catalog.ChapterQuestionRepository
import com.example.focusquest.data.catalog.CurriculumRepository
import com.example.focusquest.data.catalog.LegacyQuestionMigration
import com.example.focusquest.data.catalog.LegacyQuestionChapterTarget
import com.example.focusquest.model.CurriculumGradeIds
import com.example.focusquest.model.Grade
import com.example.focusquest.model.Subject
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.FileNotFoundException

@RunWith(AndroidJUnit4::class)
class CatalogAssetsInstrumentedTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun catalogAndQuestionAssetsAreValidAndStrictlyRelated() {
        val curricula = CurriculumRepository(context)
        val questionRepository = ChapterQuestionRepository(context, curricula)

        CurriculumGradeIds.all.forEach { gradeId ->
            val curriculumJson = readAssetOrNull("curriculum/$gradeId.json")
            val questionsJson = readAssetOrNull("questions/$gradeId.json")

            val subjectChapterPairs = linkedSetOf<Pair<String, String>>()
            val allChapterIds = mutableSetOf<String>()
            if (curriculumJson != null) {
                assertEquals(gradeId, curriculumJson.getString("gradeId"))
                val subjects = curriculumJson.getJSONArray("subjects")
                val subjectIds = mutableSetOf<String>()
                for (subjectIndex in 0 until subjects.length()) {
                    val subject = subjects.getJSONObject(subjectIndex)
                    val subjectId = subject.getString("id")
                    assertTrue("Duplicate subject ID $gradeId/$subjectId", subjectIds.add(subjectId))
                    assertFalse(subject.getString("title").isBlank())
                    assertFalse(subject.getString("kind").isBlank())
                    val chapters = subject.getJSONArray("chapters")
                    for (chapterIndex in 0 until chapters.length()) {
                        val chapter = chapters.getJSONObject(chapterIndex)
                        val chapterId = chapter.getString("id")
                        assertTrue("Duplicate chapter ID $gradeId/$chapterId", allChapterIds.add(chapterId))
                        assertFalse(chapter.getString("title").isBlank())
                        assertFalse(chapter.getString("theme").isBlank())
                        subjectChapterPairs += subjectId to chapterId
                    }
                }
            } else {
                assertTrue("Missing curriculum must load as an empty list", curricula.getSubjects(gradeId).isEmpty())
            }

            if (questionsJson != null) {
                assertEquals(gradeId, questionsJson.getString("gradeId"))
                val questions = questionsJson.getJSONArray("questions")
                val questionIds = mutableSetOf<String>()
                for (index in 0 until questions.length()) {
                    val question = questions.getJSONObject(index)
                    val id = question.getString("id")
                    assertTrue("Duplicate question ID $gradeId/$id", questionIds.add(id))
                    val subjectId = question.getString("subjectId")
                    val chapterId = question.getString("chapterId")
                    assertFalse(question.getString("question").isBlank())
                    assertFalse(question.getString("explanation").isBlank())
                    val answers = question.getJSONArray("answers")
                    assertEquals("Question $id must have exactly four answers", 4, answers.length())
                    for (answerIndex in 0 until answers.length()) {
                        assertFalse("Question $id has a blank answer", answers.getString(answerIndex).isBlank())
                    }
                    val correctIndex = question.get("correctAnswerIndex").asExactInt()
                    assertTrue("Question $id has an invalid correct answer index", correctIndex in 0..3)
                    assertTrue("Question $id has a negative reward", question.get("rewardMinutes").asExactInt() >= 0)
                    if (curriculumJson != null) {
                        assertTrue(
                            "Question $id references another grade, subject, or chapter",
                            (subjectId to chapterId) in subjectChapterPairs
                        )
                    }
                }
            }

            curricula.getSubjects(gradeId).forEach { subject ->
                val chapters = curricula.getChapters(gradeId, subject.id)
                val countsByChapter = questionRepository.getQuestionCounts(gradeId, subject.id)
                assertEquals(chapters.map { it.id }.toSet(), countsByChapter.keys)
                chapters.forEach { chapter ->
                    val chapterQuestions = questionRepository.getQuestions(gradeId, subject.id, chapter.id)
                    assertTrue(chapterQuestions.all {
                        it.subjectId == subject.id && it.chapterId == chapter.id
                    })
                    assertEquals(chapterQuestions.size, chapterQuestions.map { it.id }.toSet().size)
                    assertEquals(
                        chapterQuestions.size,
                        questionRepository.getQuestionCount(gradeId, subject.id, chapter.id)
                    )
                    assertEquals(chapterQuestions.size, countsByChapter.getValue(chapter.id))
                    val session = questionRepository.getSessionQuestions(gradeId, subject.id, chapter.id)
                    assertTrue(session.size <= 5)
                    assertEquals(session.size, session.map { it.id }.toSet().size)
                }
            }
        }

        assertTrue(curricula.getSubjects("grade-inconnu").isEmpty())
        assertTrue(curricula.getChapters("grade-inconnu", "maths").isEmpty())
        assertTrue(questionRepository.getQuestions("grade-inconnu", "maths", "x").isEmpty())
        assertTrue(questionRepository.getQuestionCounts("grade-inconnu", "maths").isEmpty())
    }

    @Test
    fun legacyQuestionBankRemainsAvailableAndUnmappedQuestionsAreReported() {
        val legacyQuestionCount = listOf("5e", "4e", "3e").sumOf { gradeId ->
            val grade = when (gradeId) {
                "5e" -> Grade.CINQUIEME
                "4e" -> Grade.QUATRIEME
                else -> Grade.TROISIEME
            }
            Subject.entries.sumOf { subject ->
                com.example.focusquest.data.QuestionRepository.getQuestions(grade, subject).size
            }
        }
        assertEquals(120, legacyQuestionCount)

        val report = LegacyQuestionMigration.inspect(CurriculumRepository(context))
        assertEquals(120, report.unmappedQuestionIds.size)
        assertTrue(report.associations.isEmpty())
        assertTrue(report.invalidMappings.isEmpty())
        assertTrue(report.unknownQuestionIds.isEmpty())

        val curriculum = CurriculumRepository(context)
        val unrelatedSubjectChapter = curriculum.getChapters("5e", "francais").firstOrNull()
        if (unrelatedSubjectChapter != null) {
            val rejected = LegacyQuestionMigration.inspect(
                curriculum,
                mapOf(5001 to LegacyQuestionChapterTarget("francais", unrelatedSubjectChapter.id))
            )
            assertTrue(rejected.associations.none { it.question.id == 5001 })
            assertTrue(5001 in rejected.unmappedQuestionIds)
            assertTrue(5001 in rejected.invalidMappings)
        }
    }

    private fun readAssetOrNull(path: String): JSONObject? {
        val text = try {
            context.assets.open(path).bufferedReader(Charsets.UTF_8).use { it.readText() }
        } catch (_: FileNotFoundException) {
            return null
        }
        return JSONObject(text)
    }

    private fun Any.asExactInt(): Int {
        assertTrue("Expected a JSON integer but got $this", this is Number)
        val number = (this as Number).toDouble()
        assertTrue("Expected a finite whole number but got $this", number.isFinite() && number % 1.0 == 0.0)
        assertTrue("Number out of Int range: $this", number >= Int.MIN_VALUE && number <= Int.MAX_VALUE)
        return number.toInt()
    }
}
