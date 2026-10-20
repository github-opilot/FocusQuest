package com.example.focusquest.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.focusquest.ui.theme.FocusQuestTheme

@Composable
fun ChapterPickerScreen(
    gradeTitle: String,
    subjectTitle: String,
    chapters: List<ChapterCardUi>,
    onChapterSelected: (ChapterCardUi) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themes = remember(chapters) {
        chapters.groupBy { it.theme.trim().ifBlank { "Autres chapitres" } }
    }
    val hasAvailableQuiz = chapters.any { it.availableQuizCount > 0 }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {
        CatalogTopBar(step = 2, stepTitle = "Chapitre", onBack = onBack)
        CatalogPageHeader(
            title = subjectTitle,
            subtitle = "$gradeTitle · Choisis un thème puis un chapitre."
        )
        if (chapters.isNotEmpty() && !hasAvailableQuiz) {
            Text(
                text = "Les QCM arrivent bientôt pour cette matière.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (chapters.isEmpty()) {
            EmptyCatalogMessage(
                title = "Aucun chapitre pour le moment",
                message = "Les chapitres de cette matière apparaîtront ici."
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                themes.forEach { (theme, themeChapters) ->
                    item(key = "theme-$theme") {
                        CatalogSectionTitle(title = theme)
                    }
                    items(
                        items = themeChapters,
                        key = { "${theme}-${it.id}" }
                    ) { chapter ->
                        ChapterCatalogCard(
                            chapter = chapter,
                            onClick = { onChapterSelected(chapter) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Chapitres · mathématiques")
@Composable
private fun ChapterPickerScreenPreview() {
    FocusQuestTheme {
        ChapterPickerScreen(
            gradeTitle = "3e",
            subjectTitle = "Mathématiques",
            chapters = listOf(
                ChapterCardUi("litteral", "Calcul littéral", "Nombres et calculs", 3),
                ChapterCardUi("equations", "Équations", "Nombres et calculs", 2),
                ChapterCardUi("pythagore", "Pythagore", "Géométrie", 1),
                ChapterCardUi("thales", "Thalès", "Géométrie", 0)
            ),
            onChapterSelected = {},
            onBack = {}
        )
    }
}
