package com.example.focusquest.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Réglages",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Temps d'écran quotidien de base",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(28.dp))

        minuteOptions.forEach { minutes ->
            if (minutes == baseDailyMinutes) {
                Button(
                    onClick = { onBaseDailyMinutesSelected(minutes) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "$minutes min")
                }
            } else {
                OutlinedButton(
                    onClick = { onBaseDailyMinutesSelected(minutes) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "$minutes min")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
