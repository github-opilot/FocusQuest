package com.example.focusquest.ui.catalog

/** Display-only subject data supplied by the screen that hosts the catalog. */
data class SubjectCardUi(
    val id: String,
    val title: String,
    val kind: String,
    val availableQuizCount: Int
)

/** Display-only chapter data supplied by the screen that hosts the catalog. */
data class ChapterCardUi(
    val id: String,
    val title: String,
    val theme: String,
    val availableQuizCount: Int
)
