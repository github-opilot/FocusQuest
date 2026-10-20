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
import com.example.focusquest.model.Subject
import com.example.focusquest.ui.components.FocusQuestCard
import com.example.focusquest.ui.components.SubjectCard
import com.example.focusquest.ui.components.subjectAccentColor

@Composable
fun SubjectSelectionScreen(
    grade: Grade,
    onSubjectSelected: (Subject) -> Unit,
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
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Text(
                text = grade.displayName,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Choisis une matière",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Une courte mission t’attend.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        Subject.entries.forEach { subject ->
            SubjectCard(
                title = subject.displayName,
                symbol = subjectSymbol(subject),
                subtitle = subjectSubtitle(subject),
                accentColor = subjectAccentColor(subject.ordinal),
                onClick = { onSubjectSelected(subject) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun subjectSymbol(subject: Subject): String = when (subject) {
    Subject.MATHS -> "🧮"
    Subject.FRANCAIS -> "📖"
    Subject.HISTOIRE_GEOGRAPHIE -> "🌍"
    Subject.SCIENCES -> "🧪"
}

private fun subjectSubtitle(subject: Subject): String = when (subject) {
    Subject.MATHS -> "Nombres et logique"
    Subject.FRANCAIS -> "Lire et s’exprimer"
    Subject.HISTOIRE_GEOGRAPHIE -> "Repères et découvertes"
    Subject.SCIENCES -> "Observer et comprendre"
}
