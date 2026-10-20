package com.example.focusquest.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.focusquest.ui.theme.FocusQuestTheme

@Composable
fun ActivityPickerScreen(
    chapterTitle: String,
    questionCount: Int,
    onStartQuiz: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val safeQuestionCount = questionCount.coerceAtLeast(0)
    val quizAvailable = safeQuestionCount > 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {
        CatalogTopBar(step = 3, stepTitle = "Activité", onBack = onBack)
        CatalogPageHeader(
            title = "Que veux-tu faire ?",
            subtitle = chapterTitle
        )

        ActivityChoiceCard(
            chapterTitle = chapterTitle,
            questionCount = safeQuestionCount,
            available = quizAvailable,
            onStartQuiz = onStartQuiz,
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}

/** Reusable card pattern for adding a real activity type when one is implemented. */
@Composable
private fun ActivityChoiceCard(
    chapterTitle: String,
    questionCount: Int,
    available: Boolean,
    onStartQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconBubble(
                    symbol = "❓",
                    accent = MaterialTheme.colorScheme.primary,
                    size = 58.dp
                )
                AvailabilityPill(
                    text = if (available) "Disponible" else "Bientôt disponible",
                    available = available
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = "QCM · 5 questions max",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (available) {
                    "$questionCount question${if (questionCount == 1) "" else "s"} disponible${if (questionCount == 1) "" else "s"} pour ce chapitre"
                } else {
                    "Le QCM de « $chapterTitle » sera bientôt disponible."
                },
                modifier = Modifier.padding(top = 5.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onStartQuiz,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                enabled = available,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Commencer", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Preview(showBackground = true, name = "Activité · QCM disponible")
@Composable
private fun ActivityPickerScreenPreview() {
    FocusQuestTheme {
        ActivityPickerScreen(
            chapterTitle = "Calcul littéral",
            questionCount = 5,
            onStartQuiz = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Activité · bientôt disponible")
@Composable
private fun ActivityPickerUnavailablePreview() {
    FocusQuestTheme {
        ActivityPickerScreen(
            chapterTitle = "Thalès",
            questionCount = 0,
            onStartQuiz = {},
            onBack = {}
        )
    }
}
