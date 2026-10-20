package com.example.focusquest.data.catalog

import android.content.Context
import com.example.focusquest.model.ChapterEntry
import com.example.focusquest.model.CurriculumGradeIds
import com.example.focusquest.model.SubjectEntry
import org.json.JSONArray
import org.json.JSONObject

/**
 * Loads `assets/curriculum/{gradeId}.json` and exposes its subjects and chapters.
 * Missing, unsupported, or malformed assets are treated as an empty curriculum.
 * Chapter entries inherit their `subjectId` from the containing subject in JSON.
 *
 * Typical use:
 * ```kotlin
 * val curriculum = CurriculumRepository(context)
 * val subjects = curriculum.getSubjects("5e")
 * val chapters = curriculum.getChapters("5e", "maths")
 * ```
 */
class CurriculumRepository(context: Context) {
    private val appContext = context.applicationContext
    private val cache = mutableMapOf<String, CurriculumData?>()

    /** Returns the declared subjects for [gradeId], or an empty list if unavailable. */
    fun getSubjects(gradeId: String): List<SubjectEntry> = curriculum(gradeId)?.subjects.orEmpty()

    /** Returns only chapters declared under [subjectId] for [gradeId]. */
    fun getChapters(gradeId: String, subjectId: String): List<ChapterEntry> =
        curriculum(gradeId)?.chaptersBySubject?.get(subjectId).orEmpty()

    internal fun hasChapter(gradeId: String, subjectId: String, chapterId: String): Boolean =
        getChapters(gradeId, subjectId).any { it.id == chapterId }

    private fun curriculum(gradeId: String): CurriculumData? {
        if (gradeId !in CurriculumGradeIds.all) return null
        synchronized(cache) {
            if (cache.containsKey(gradeId)) return cache[gradeId]
            val loaded = loadCurriculum(gradeId)
            cache[gradeId] = loaded
            return loaded
        }
    }

    private fun loadCurriculum(gradeId: String): CurriculumData? {
        val root = readObject("curriculum/$gradeId.json") ?: return null
        if (root.requiredString("gradeId") != gradeId) return null
        val subjectsArray = root.optJSONArray("subjects") ?: return null
        val subjects = mutableListOf<SubjectEntry>()
        val chaptersBySubject = linkedMapOf<String, List<ChapterEntry>>()
        val usedSubjectIds = mutableSetOf<String>()
        val usedChapterIds = mutableSetOf<String>()

        for (subjectJson in subjectsArray.objects()) {
            val subjectId = subjectJson.requiredString("id") ?: continue
            val title = subjectJson.requiredString("title") ?: continue
            val kind = subjectJson.requiredString("kind") ?: continue
            if (!usedSubjectIds.add(subjectId)) continue

            subjects += SubjectEntry(subjectId, title, kind)
            val chapters = mutableListOf<ChapterEntry>()
            val chapterArray = subjectJson.optJSONArray("chapters") ?: JSONArray()
            for (chapterJson in chapterArray.objects()) {
                val chapterId = chapterJson.requiredString("id") ?: continue
                val chapterTitle = chapterJson.requiredString("title") ?: continue
                val theme = chapterJson.requiredString("theme") ?: continue
                if (usedChapterIds.add(chapterId)) {
                    chapters += ChapterEntry(chapterId, chapterTitle, theme, subjectId)
                }
            }
            chaptersBySubject[subjectId] = chapters
        }
        return CurriculumData(subjects, chaptersBySubject)
    }

    private fun readObject(path: String): JSONObject? = try {
        appContext.assets.open(path).bufferedReader(Charsets.UTF_8).use { reader ->
            JSONObject(reader.readText())
        }
    } catch (_: Exception) {
        null
    }

    private data class CurriculumData(
        val subjects: List<SubjectEntry>,
        val chaptersBySubject: Map<String, List<ChapterEntry>>
    )
}

private fun JSONObject.requiredString(key: String): String? =
    (opt(key) as? String)?.takeIf { it.isNotBlank() }

private fun JSONArray.objects(): Sequence<JSONObject> = sequence {
    for (index in 0 until length()) {
        optJSONObject(index)?.let { yield(it) }
    }
}
