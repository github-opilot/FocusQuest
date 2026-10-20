package com.example.focusquest.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.focusquest.data.LaunchableApp
import com.example.focusquest.ui.components.FocusQuestButton
import com.example.focusquest.ui.components.FocusQuestButtonStyle
import com.example.focusquest.ui.components.FocusQuestCard
import com.example.focusquest.ui.components.SymbolBadge
import com.example.focusquest.ui.theme.FocusBlue

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
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(top = 4.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    FocusQuestButton(
                        text = "← Retour",
                        onClick = onBack,
                        style = FocusQuestButtonStyle.Outlined
                    )

                    FocusQuestCard(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Applications surveillées",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "Choisis les applications dont le temps d’utilisation sera déduit de ton temps disponible.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            SymbolBadge(
                                symbol = "◉",
                                accentColor = FocusBlue,
                                modifier = Modifier.padding(start = 8.dp),
                                size = 42.dp
                            )
                        }
                    }

                    FocusQuestCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Pourquoi cet accès ?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Android utilise les statistiques d’utilisation pour mesurer le temps passé dans les applications choisies. FocusQuest ne bloque aucune application. Tu peux retirer cet accès dans les réglages Android.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(10.dp))
                        if (usageAccessGranted) {
                            Text(
                                text = "Accès aux statistiques autorisé.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            FocusQuestButton(
                                text = "Autoriser l’accès",
                                onClick = onRequestUsageAccess,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Applications lançables",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = apps.size.toString(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (apps.isEmpty()) {
                item {
                    FocusQuestCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = if (isRefreshing) {
                                "Chargement des applications…"
                            } else {
                                "Aucune application lançable trouvée."
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                items(apps, key = LaunchableApp::packageName) { app ->
                    val checked = app.packageName in pendingSelection
                    val iconBitmap = remember(app.packageName) {
                        runCatching { app.icon.toBitmap(96, 96).asImageBitmap() }.getOrNull()
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .toggleable(
                                value = checked,
                                role = Role.Checkbox,
                                onValueChange = { isChecked ->
                                    pendingSelection = if (isChecked) {
                                        pendingSelection + app.packageName
                                    } else {
                                        pendingSelection - app.packageName
                                    }
                                }
                            ),
                        shape = RoundedCornerShape(20.dp),
                        color = if (checked) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        contentColor = if (checked) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (iconBitmap != null) {
                                Image(
                                    bitmap = iconBitmap,
                                    contentDescription = null,
                                    modifier = Modifier.size(44.dp)
                                )
                            } else {
                                SymbolBadge(
                                    symbol = "•",
                                    accentColor = FocusBlue,
                                    size = 44.dp
                                )
                            }
                            Text(
                                text = app.label,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Normal
                            )
                            Checkbox(checked = checked, onCheckedChange = null)
                        }
                    }
                }
            }
        }

        FocusQuestButton(
            text = if (isRefreshing) "Enregistrement…" else "Enregistrer la sélection",
            onClick = { onSaveSelection(pendingSelection) },
            enabled = !isRefreshing,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp)
        )
    }
}
