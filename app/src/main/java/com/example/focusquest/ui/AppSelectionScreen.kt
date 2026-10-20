package com.example.focusquest.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.example.focusquest.data.LaunchableApp

@Composable
fun AppSelectionScreen(
    apps: List<LaunchableApp>,
    selectedPackages: Set<String>,
    usageAccessGranted: Boolean,
    isRefreshing: Boolean,
    onRequestUsageAccess: () -> Unit,
    onSaveSelection: (Set<String>) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pendingSelection by remember { mutableStateOf(selectedPackages) }
    LaunchedEffect(selectedPackages) {
        pendingSelection = selectedPackages
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        OutlinedButton(onClick = onBack) { Text("← Retour") }
        Text(
            text = "Applications surveillées",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Choisis les applications dont le temps au premier plan sera déduit de ton temps disponible.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Pourquoi cet accès ?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "Android demande l'accès aux statistiques d'utilisation pour mesurer le temps passé dans les applications choisies. FocusQuest ne bloque pas les applications. Tu peux retirer cet accès dans les réglages Android.",
            style = MaterialTheme.typography.bodySmall
        )
        if (usageAccessGranted) {
            Text(
                text = "Accès aux statistiques autorisé.",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Button(onClick = onRequestUsageAccess, modifier = Modifier.fillMaxWidth()) {
                Text("Autoriser l'accès")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Applications lançables", style = MaterialTheme.typography.titleMedium)

        if (apps.isEmpty()) {
            Text(
                text = if (isRefreshing) "Chargement des applications…" else "Aucune application lançable trouvée.",
                modifier = Modifier.padding(vertical = 16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(apps, key = LaunchableApp::packageName) { app ->
                    val checked = app.packageName in pendingSelection
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                pendingSelection = if (checked) {
                                    pendingSelection - app.packageName
                                } else {
                                    pendingSelection + app.packageName
                                }
                            }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val iconBitmap = remember(app.packageName) {
                            runCatching { app.icon.toBitmap(96, 96).asImageBitmap() }.getOrNull()
                        }
                        if (iconBitmap != null) {
                            Image(
                                bitmap = iconBitmap,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Text(
                            text = app.label,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { isChecked ->
                                pendingSelection = if (isChecked) {
                                    pendingSelection + app.packageName
                                } else {
                                    pendingSelection - app.packageName
                                }
                            }
                        )
                    }
                }
            }
        }

        Button(
            onClick = { onSaveSelection(pendingSelection) },
            enabled = !isRefreshing,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp)
        ) {
            Text(if (isRefreshing) "Enregistrement…" else "Enregistrer la sélection")
        }
    }
}
