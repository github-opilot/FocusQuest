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
import com.example.focusquest.ui.components.FocusQuestCard
import com.example.focusquest.ui.components.FocusQuestChoiceCard
import com.example.focusquest.ui.theme.FocusYellow

@Composable
fun SettingsScreen(
    baseDailyMinutes: Int,
    onBaseDailyMinutesSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val minuteOptions = listOf(15, 30, 45, 60)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        FocusQuestCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ) {
            Text(
                text = "Réglages",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Choisis ton temps d’écran quotidien de base.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }

        minuteOptions.forEach { minutes ->
            FocusQuestChoiceCard(
                title = "$minutes minutes",
                symbol = "⏱️",
                subtitle = if (minutes == baseDailyMinutes) {
                    "Durée actuelle"
                } else {
                    "Choisir cette durée"
                },
                accentColor = FocusYellow,
                onClick = { onBaseDailyMinutesSelected(minutes) },
                modifier = Modifier.fillMaxWidth(),
                selected = minutes == baseDailyMinutes
            )
        }
    }
}
