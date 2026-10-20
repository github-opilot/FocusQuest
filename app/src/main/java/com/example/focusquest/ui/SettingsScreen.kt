package com.example.focusquest.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.focusquest.ui.components.FocusQuestButton
import com.example.focusquest.ui.components.FocusQuestButtonStyle
import com.example.focusquest.ui.components.FocusQuestCard
import com.example.focusquest.ui.components.FocusQuestChoiceCard
import com.example.focusquest.ui.theme.FocusYellow

@Composable
fun SettingsScreen(
    baseDailyMinutes: Int,
    onBaseDailyMinutesSelected: (Int) -> Unit,
    appProtectionEnabled: Boolean,
    accessibilityServiceEnabled: Boolean,
    onAppProtectionEnabledChange: (Boolean) -> Unit,
    onOpenAccessibilitySettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val minuteOptions = listOf(15, 30, 45, 60)
    var showProtectionConsent by remember { mutableStateOf(false) }

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

        FocusQuestCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Protection des applications",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when {
                            !appProtectionEnabled -> "Désactivée"
                            accessibilityServiceEnabled -> "Protection activée"
                            else -> "Active le service Android pour commencer."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = appProtectionEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled) showProtectionConsent = true
                        else onAppProtectionEnabledChange(false)
                    }
                )
            }

            Spacer(Modifier.height(10.dp))
            Text(
                text = "Quand le temps est épuisé, un écran FocusQuest recouvre uniquement les applications sélectionnées. FocusQuest ne lit pas le contenu de leurs écrans.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (appProtectionEnabled && !accessibilityServiceEnabled) {
                Spacer(Modifier.height(12.dp))
                FocusQuestButton(
                    text = "Activer dans les paramètres Android",
                    onClick = { showProtectionConsent = true },
                    modifier = Modifier.fillMaxWidth(),
                    style = FocusQuestButtonStyle.Tonal
                )
            }
        }
    }

    if (showProtectionConsent) {
        AlertDialog(
            onDismissRequest = { showProtectionConsent = false },
            title = { Text("Protection des applications") },
            text = {
                Text(
                    "FocusQuest utilise le service d'accessibilité Android pour détecter l'application actuellement ouverte et afficher un écran de blocage lorsque votre temps est épuisé. Cette fonction est facultative et peut être désactivée à tout moment."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showProtectionConsent = false
                        onAppProtectionEnabledChange(true)
                        if (!accessibilityServiceEnabled) onOpenAccessibilitySettings()
                    }
                ) {
                    Text(
                        if (accessibilityServiceEnabled) "Activer la protection"
                        else "Activer dans les paramètres Android"
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showProtectionConsent = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}
