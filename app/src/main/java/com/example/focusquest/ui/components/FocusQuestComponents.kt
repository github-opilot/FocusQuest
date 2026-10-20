package com.example.focusquest.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focusquest.ui.theme.FocusBlue
import com.example.focusquest.ui.theme.FocusPink
import com.example.focusquest.ui.theme.FocusViolet

enum class FocusQuestButtonStyle {
    Filled,
    Tonal,
    Outlined
}

@Composable
fun FocusQuestButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: FocusQuestButtonStyle = FocusQuestButtonStyle.Filled
) {
    val buttonModifier = modifier.heightIn(min = 56.dp)
    val buttonShape = RoundedCornerShape(18.dp)

    when (style) {
        FocusQuestButtonStyle.Filled -> Button(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled,
            shape = buttonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) { Text(text, style = MaterialTheme.typography.titleMedium) }

        FocusQuestButtonStyle.Tonal -> FilledTonalButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled,
            shape = buttonShape
        ) { Text(text, style = MaterialTheme.typography.titleMedium) }

        FocusQuestButtonStyle.Outlined -> OutlinedButton(
            onClick = onClick,
            modifier = buttonModifier,
            enabled = enabled,
            shape = buttonShape
        ) { Text(text, style = MaterialTheme.typography.titleMedium) }
    }
}

@Composable
fun FocusQuestCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    contentPadding: Dp = 20.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = 1.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}

@Composable
fun MissionCard(
    onStartMission: () -> Unit,
    modifier: Modifier = Modifier
) {
    FocusQuestCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "À toi de jouer",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Une mission à la fois.",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            SymbolBadge(
                symbol = "🌟",
                accentColor = FocusViolet,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(Modifier.height(18.dp))
        FocusQuestButton(
            text = "Commencer une mission",
            onClick = onStartMission,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SubjectCard(
    title: String,
    symbol: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false
) {
    FocusQuestChoiceCard(
        title = title,
        symbol = symbol,
        subtitle = subtitle,
        accentColor = accentColor,
        onClick = onClick,
        modifier = modifier,
        selected = selected
    )
}

@Composable
fun FocusQuestChoiceCard(
    title: String,
    symbol: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 84.dp)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics {
                if (selected) {
                    this.selected = true
                    stateDescription = "Sélectionné"
                }
            },
        shape = RoundedCornerShape(22.dp),
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        contentColor = if (selected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SymbolBadge(symbol = symbol, accentColor = accentColor)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = if (selected) "✅" else "👉",
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .clearAndSetSemantics { }
            )
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    symbol: String,
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    FocusQuestCard(
        modifier = modifier,
        contentPadding = 14.dp,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SymbolBadge(
                symbol = symbol,
                accentColor = accentColor,
                size = 38.dp
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
            ) {
                Text(text = label, style = MaterialTheme.typography.labelLarge)
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RewardBadge(
    amount: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🌟",
                modifier = Modifier.clearAndSetSemantics { },
                fontSize = 23.sp
            )
            Text(
                text = "+$amount",
                modifier = Modifier.padding(start = 6.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ProgressIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    val safeTotal = totalSteps.coerceAtLeast(1)
    val progress by animateFloatAsState(
        targetValue = (currentStep.coerceIn(0, safeTotal)).toFloat() / safeTotal,
        animationSpec = tween(durationMillis = 280),
        label = "focusQuestProgress"
    )

    Column(modifier = modifier) {
        Text(
            text = "Question $currentStep / $totalSteps",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 8.dp)
                .semantics { contentDescription = "Progression de la mission" },
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.secondaryContainer
        )
    }
}

@Composable
fun SymbolBadge(
    symbol: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp
) {
    Surface(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = accentColor.copy(alpha = 0.14f),
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = symbol,
                modifier = Modifier.clearAndSetSemantics { },
                fontSize = (size.value * 0.62f).sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

fun subjectAccentColor(index: Int): Color = when (index % 4) {
    0 -> FocusViolet
    1 -> FocusPink
    2 -> FocusBlue
    else -> Color(0xFF9A6500)
}
