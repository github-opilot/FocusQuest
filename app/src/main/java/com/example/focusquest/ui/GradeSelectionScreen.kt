package com.example.focusquest.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.focusquest.model.Grade
import com.example.focusquest.ui.components.FocusQuestChoiceCard
import com.example.focusquest.ui.components.FocusQuestCard
import com.example.focusquest.ui.components.subjectAccentColor

@Composable
fun GradeSelectionScreen(
    onGradeSelected: (Grade) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        FocusQuestCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                text = "On commence par quoi ?",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Quelle est ta classe ?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Choisis ton niveau pour trouver une mission adaptée.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Grade.entries.forEachIndexed { index, grade ->
            FocusQuestChoiceCard(
                title = grade.displayName,
                symbol = gradeSymbol(grade),
                subtitle = "Choisir ce niveau",
                accentColor = subjectAccentColor(index),
                onClick = { onGradeSelected(grade) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun gradeSymbol(grade: Grade): String = when (grade) {
    Grade.SIXIEME -> "6"
    Grade.CINQUIEME -> "5"
    Grade.QUATRIEME -> "4"
    Grade.TROISIEME -> "3"
    Grade.SECONDE -> "2"
    Grade.PREMIERE -> "1"
    Grade.TERMINALE -> "T"
}
