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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.focusquest.ui.components.FocusQuestButton
import com.example.focusquest.ui.components.FocusQuestButtonStyle
import com.example.focusquest.ui.components.FocusQuestCard
import com.example.focusquest.ui.components.MissionCard
import com.example.focusquest.ui.components.StatCard
import com.example.focusquest.ui.components.SymbolBadge
import com.example.focusquest.ui.theme.FocusBlue
import com.example.focusquest.ui.theme.FocusPink
import com.example.focusquest.ui.theme.FocusViolet
import com.example.focusquest.ui.theme.FocusYellow

@Composable
fun HomeScreen(
    availableTimeMillis: Long,
    earnedTimeMillis: Long,
    usedTimeMillis: Long,
    monitoredApps: List<String>,
    usageAccessGranted: Boolean,
    isRefreshing: Boolean,
    onStartMission: () -> Unit,
    onOpenSettings: () -> Unit,
    onSelectApps: () -> Unit,
    onRefreshUsage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "FocusQuest",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Chaque réponse te fait avancer.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            SymbolBadge(symbol = "✦", accentColor = FocusYellow)
        }

        FocusQuestCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Text(
                text = "Ton temps disponible",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = formatScreenTime(availableTimeMillis),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Aujourd’hui",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                label = "Temps gagné",
                value = formatScreenTime(earnedTimeMillis),
                symbol = "✦",
                accentColor = FocusPink,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Temps utilisé",
                value = formatScreenTime(usedTimeMillis),
                symbol = "◷",
                accentColor = FocusBlue,
                modifier = Modifier.weight(1f)
            )
        }

        MissionCard(
            onStartMission = onStartMission,
            modifier = Modifier.fillMaxWidth()
        )

        FocusQuestCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Applications surveillées",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (monitoredApps.isEmpty()) {
                            "Aucune application sélectionnée"
                        } else {
                            monitoredApps.joinToString(
                                separator = " · ",
                                limit = 3,
                                truncated = " +${(monitoredApps.size - 3).coerceAtLeast(0)}"
                            )
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                SymbolBadge(symbol = "◉", accentColor = FocusBlue, size = 42.dp)
            }

            Spacer(Modifier.height(10.dp))
            Text(
                text = if (usageAccessGranted) {
                    "Suivi activé"
                } else {
                    "Autorise l’accès aux statistiques pour suivre l’utilisation."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = if (usageAccessGranted) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
            Spacer(Modifier.height(12.dp))
            FocusQuestButton(
                text = "Gérer les applications",
                onClick = onSelectApps,
                modifier = Modifier.fillMaxWidth(),
                style = FocusQuestButtonStyle.Tonal
            )
        }

        FocusQuestButton(
            text = if (isRefreshing) "Actualisation…" else "Actualiser le temps utilisé",
            onClick = onRefreshUsage,
            enabled = !isRefreshing,
            modifier = Modifier.fillMaxWidth(),
            style = FocusQuestButtonStyle.Outlined
        )
        FocusQuestButton(
            text = "Réglages",
            onClick = onOpenSettings,
            modifier = Modifier.fillMaxWidth(),
            style = FocusQuestButtonStyle.Outlined
        )
    }
}
