package com.example.focusquest.data.catalog

import com.example.focusquest.data.QuestionRepository
import com.example.focusquest.model.Grade
import com.example.focusquest.model.Question
import com.example.focusquest.model.Subject

/** An explicit, reviewed chapter destination for one legacy numeric question ID. */
data class LegacyQuestionChapterTarget(
    val subjectId: String,
    val chapterId: String
)

/** A legacy question whose supplied target exists in the same grade and subject. */
data class LegacyQuestionAssociation(
    val question: Question,
    val subjectId: String,
    val chapterId: String
)

/**
 * Result of checking explicit legacy question-to-chapter associations.
 * [unmappedQuestionIds] includes questions with no mapping and mappings rejected because their
 * chapter does not belong to the question's grade and subject. [invalidMappings] explains rejects.
 */
data class LegacyQuestionMigrationReport(
    val associations: List<LegacyQuestionAssociation>,
    val unmappedQuestionIds: Set<Int>,
    val invalidMappings: Map<Int, String>,
    val unknownQuestionIds: Set<Int>
)

/**
 * Compatibility helper for the original 120-question bank. The current bank stores no chapter
 * IDs, so callers must provide a reviewed map keyed by its stable numeric question IDs. Calling
 * [inspect] with no mapping is safe and reports the legacy questions as unresolved; it never
 * guesses a chapter. The existing [QuestionRepository] remains the quiz source until integration
 * explicitly supplies and adopts verified associations.
 */
object LegacyQuestionMigration {
    fun inspect(
        curriculumRepository: CurriculumRepository,
        mappings: Map<Int, LegacyQuestionChapterTarget> = emptyMap()
    ): LegacyQuestionMigrationReport {
        val legacyQuestions = QuestionRepository.allQuestions
        val questionsById = legacyQuestions.associateBy(Question::id)
        val associations = mutableListOf<LegacyQuestionAssociation>()
        val unmapped = mutableSetOf<Int>()
        val invalid = linkedMapOf<Int, String>()

        legacyQuestions.forEach { question ->
            val target = mappings[question.id]
            if (target == null) {
                unmapped += question.id
                return@forEach
            }
            val gradeId = question.grade.curriculumId()
            val subjectCompatible = question.subject.compatibleCurriculumSubjectIds().contains(target.subjectId)
            val targetExists = subjectCompatible && curriculumRepository.getChapters(gradeId, target.subjectId)
                .any { it.id == target.chapterId }
            if (targetExists) {
                associations += LegacyQuestionAssociation(question, target.subjectId, target.chapterId)
            } else {
                unmapped += question.id
                invalid[question.id] =
                    "La matière ou le chapitre ${target.subjectId}/${target.chapterId} ne correspond pas à " +
                        "la question ${question.subject} du niveau $gradeId."
            }
        }

        return LegacyQuestionMigrationReport(
            associations = associations,
            unmappedQuestionIds = unmapped,
            invalidMappings = invalid,
            unknownQuestionIds = mappings.keys - questionsById.keys
        )
    }
}

private fun Grade.curriculumId(): String = when (this) {
    Grade.SIXIEME -> "6e"
    Grade.CINQUIEME -> "5e"
    Grade.QUATRIEME -> "4e"
    Grade.TROISIEME -> "3e"
    Grade.SECONDE -> "seconde"
    Grade.PREMIERE -> "premiere_generale"
    Grade.TERMINALE -> "terminale_generale"
}

private fun Subject.compatibleCurriculumSubjectIds(): Set<String> = when (this) {
    Subject.MATHS -> setOf("maths")
    Subject.FRANCAIS -> setOf("francais")
    Subject.HISTOIRE_GEOGRAPHIE -> setOf("histoire", "geographie", "histoire-geographie")
    Subject.SCIENCES -> setOf("sciences", "sciences-technologie", "physique-chimie", "svt", "technologie")
}
