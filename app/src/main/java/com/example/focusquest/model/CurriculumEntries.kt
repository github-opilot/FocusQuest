package com.example.focusquest.model

/** A subject declared in a grade's curriculum asset. */
data class SubjectEntry(
    val id: String,
    val title: String,
    val kind: String
)

/** A chapter belonging to [subjectId] in a grade's curriculum asset. */
data class ChapterEntry(
    val id: String,
    val title: String,
    val theme: String,
    val subjectId: String
)

/** A catalog question bound to one exact grade, subject, and chapter. */
data class QuizQuestionEntry(
    val id: String,
    val subjectId: String,
    val chapterId: String,
    val question: String,
    val answers: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String,
    val rewardMinutes: Int
)

/** Stable asset identifiers accepted by the catalog repositories. */
object CurriculumGradeIds {
    val all: Set<String> = setOf(
        "6e",
        "5e",
        "4e",
        "3e",
        "seconde",
        "premiere_generale",
        "terminale_generale"
    )
}
