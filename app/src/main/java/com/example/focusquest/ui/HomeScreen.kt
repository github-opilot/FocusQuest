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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    availableMinutes: Int,
    earnedMinutes: Int,
    usedMinutes: Int,
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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "FocusQuest",
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Transforme tes révisions en temps d'écran",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Temps disponible aujourd'hui", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "$availableMinutes min",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(text = "Temps gagné : $earnedMinutes min", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Temps utilisé : $usedMinutes min", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (monitoredApps.isEmpty()) {
                "Aucune application surveillée"
            } else {
                "Applications surveillées : ${monitoredApps.joinToString(", ")}"
            },
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        if (!usageAccessGranted) {
            Text(
                text = "L'accès aux statistiques d'utilisation n'est pas autorisé.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onStartMission, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Commencer une mission")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(onClick = onSelectApps, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Sélectionner les applications")
        }

        OutlinedButton(
            onClick = onRefreshUsage,
            enabled = !isRefreshing,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isRefreshing) "Actualisation…" else "Actualiser")
        }

        OutlinedButton(onClick = onOpenSettings, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Réglages")
        }
    }
}
