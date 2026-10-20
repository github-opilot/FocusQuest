package com.example.focusquest.data.catalog

import android.content.Context
import com.example.focusquest.model.CurriculumGradeIds
import com.example.focusquest.model.QuizQuestionEntry
import org.json.JSONArray
import org.json.JSONObject

/**
 * Loads `assets/questions/{gradeId}.json` and returns questions bound to an
 * existing curriculum chapter. Every query is filtered by grade, subject, and chapter.
 */
class ChapterQuestionRepository(
    context: Context,
    private val curriculumRepository: CurriculumRepository = CurriculumRepository(context)
) {
    private val appContext = context.applicationContext
    private val cache = mutableMapOf<String, List<QuizQuestionEntry>>()

    /** Returns questions only for the exact grade/subject/chapter tuple. */
    fun getQuestions(gradeId: String, subjectId: String, chapterId: String): List<QuizQuestionEntry> {
        if (gradeId !in CurriculumGradeIds.all ||
            !curriculumRepository.hasChapter(gradeId, subjectId, chapterId)
        ) {
            return emptyList()
        }
        return questionsForGrade(gradeId).filter { question ->
            question.subjectId == subjectId && question.chapterId == chapterId
        }
    }

    /** Returns the available question count for a chapter, including zero for an empty chapter. */
    fun getQuestionCount(gradeId: String, subjectId: String, chapterId: String): Int =
        getQuestions(gradeId, subjectId, chapterId).size

    /** Returns a count for every chapter in the subject, including chapters with no questions. */
    fun getQuestionCounts(gradeId: String, subjectId: String): Map<String, Int> =
        curriculumRepository.getChapters(gradeId, subjectId).associate { chapter ->
            chapter.id to getQuestionCount(gradeId, subjectId, chapter.id)
        }

    /**
     * Builds a shuffled session without repeated question IDs. The result may be shorter than
     * [limit] when the chapter has fewer available questions.
     */
    fun getSessionQuestions(
        gradeId: String,
        subjectId: String,
        chapterId: String,
        limit: Int = 5
    ): List<QuizQuestionEntry> = getQuestions(gradeId, subjectId, chapterId)
        .distinctBy { it.id }
        .shuffled()
        .take(limit.coerceAtLeast(0))

    private fun questionsForGrade(gradeId: String): List<QuizQuestionEntry> {
        synchronized(cache) {
            if (cache.containsKey(gradeId)) return cache.getValue(gradeId)
            val loaded = loadQuestions(gradeId)
            cache[gradeId] = loaded
            return loaded
        }
    }

    private fun loadQuestions(gradeId: String): List<QuizQuestionEntry> {
        val root = readObject("questions/$gradeId.json") ?: return emptyList()
        if (root.requiredString("gradeId") != gradeId) return emptyList()
        val questionsArray = root.optJSONArray("questions") ?: return emptyList()
        val result = mutableListOf<QuizQuestionEntry>()
        val usedIds = mutableSetOf<String>()

        for (questionJson in questionsArray.objects()) {
            val id = questionJson.requiredString("id") ?: continue
            if (id in usedIds) continue
            val subjectId = questionJson.requiredString("subjectId") ?: continue
            val chapterId = questionJson.requiredString("chapterId") ?: continue
            if (!curriculumRepository.hasChapter(gradeId, subjectId, chapterId)) continue
            val question = questionJson.requiredString("question") ?: continue
            val answers = questionJson.optJSONArray("answers")?.strings() ?: continue
            if (answers.size != 4 || answers.any(String::isBlank)) continue
            val correctAnswerIndex = questionJson.exactInt("correctAnswerIndex") ?: continue
            if (correctAnswerIndex !in answers.indices) continue
            val explanation = questionJson.requiredString("explanation") ?: continue
            val rewardSeconds = questionJson.exactInt("rewardSeconds") ?: continue
            if (rewardSeconds < 0) continue

            usedIds += id
            result += QuizQuestionEntry(
                id = id,
                subjectId = subjectId,
                chapterId = chapterId,
                question = question,
                answers = answers,
                correctAnswerIndex = correctAnswerIndex,
                explanation = explanation,
                rewardSeconds = rewardSeconds
            )
        }
        return result
    }

    private fun readObject(path: String): JSONObject? = try {
        appContext.assets.open(path).bufferedReader(Charsets.UTF_8).use { reader ->
            JSONObject(reader.readText())
        }
    } catch (_: Exception) {
        null
    }
}

private fun JSONObject.requiredString(key: String): String? =
    (opt(key) as? String)?.takeIf { it.isNotBlank() }

private fun JSONObject.exactInt(key: String): Int? {
    val value = opt(key) as? Number ?: return null
    val asDouble = value.toDouble()
    if (!asDouble.isFinite() || asDouble % 1.0 != 0.0 ||
        asDouble < Int.MIN_VALUE || asDouble > Int.MAX_VALUE
    ) return null
    return asDouble.toInt()
}

private fun JSONArray.objects(): Sequence<JSONObject> = sequence {
    for (index in 0 until length()) {
        optJSONObject(index)?.let { yield(it) }
    }
}

private fun JSONArray.strings(): List<String>? {
    val result = mutableListOf<String>()
    for (index in 0 until length()) {
        result += opt(index) as? String ?: return null
    }
    return result
}
