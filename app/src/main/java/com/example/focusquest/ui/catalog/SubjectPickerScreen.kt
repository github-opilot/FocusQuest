package com.example.focusquest.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.focusquest.ui.theme.FocusQuestTheme

@Composable
fun SubjectPickerScreen(
    gradeTitle: String,
    subjects: List<SubjectCardUi>,
    onSubjectSelected: (SubjectCardUi) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sections = remember(subjects) {
        subjects.groupBy { subjectSection(it.kind) }
            .toList()
            .sortedWith(compareBy<Pair<String, List<SubjectCardUi>>> { sectionOrder(it.first) }.thenBy { it.first })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {
        CatalogTopBar(step = 1, stepTitle = "Matière", onBack = onBack)
        CatalogPageHeader(
            title = "Choisis une matière",
            subtitle = "${gradeTitle.ifBlank { "Ta classe" }} · À toi de choisir ton prochain défi."
        )
        if (subjects.isEmpty()) {
            EmptyCatalogMessage(
                title = "Aucune matière pour le moment",
                message = "Les matières de cette classe apparaîtront ici."
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 148.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                sections.forEach { (section, sectionSubjects) ->
                    item(key = "section-$section", span = { GridItemSpan(maxLineSpan) }) {
                        CatalogSectionTitle(title = section)
                    }
                    items(
                        items = sectionSubjects,
                        key = { "${section}-${it.id}" }
                    ) { subject ->
                        SubjectCatalogCard(
                            subject = subject,
                            categoryLabel = section,
                            icon = subjectIcon(subject.title),
                            accent = subjectAccent(subjects.indexOf(subject)),
                            onClick = { onSubjectSelected(subject) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun EmptyCatalogMessage(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 28.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.extraLarge
            )
            .padding(22.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(
            message,
            modifier = Modifier.padding(top = 6.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, name = "Matières · lycée")
@Composable
private fun SubjectPickerScreenPreview() {
    FocusQuestTheme {
        SubjectPickerScreen(
            gradeTitle = "Première",
            subjects = listOf(
                SubjectCardUi("math", "Mathématiques", "Spécialité", 12),
                SubjectCardUi("fr", "Français", "Enseignement commun", 5),
                SubjectCardUi("ses", "Sciences économiques et sociales", "Spécialité", 0),
                SubjectCardUi("lv", "LV3 italien", "Option", 2)
            ),
            onSubjectSelected = {},
            onBack = {}
        )
    }
}
