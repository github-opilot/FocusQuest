package com.example.focusquest.ui.catalog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun CatalogTopBar(
    step: Int,
    stepTitle: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(48.dp)
                .semantics { contentDescription = "Retour" }
        ) {
            Text(
                text = "👈",
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.size(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Ton parcours",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Étape $step sur 3 · $stepTitle",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            (1..3).forEach { index ->
                Surface(
                    modifier = Modifier
                        .size(width = 24.dp, height = 7.dp)
                        .semantics { if (index == step) contentDescription = "Étape $step sur 3" },
                    shape = CircleShape,
                    color = if (index <= step) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {}
            }
        }
    }
}

@Composable
internal fun CatalogPageHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
internal fun CatalogSectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 2.dp),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
internal fun SubjectCatalogCard(
    subject: SubjectCardUi,
    categoryLabel: String,
    icon: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val available = subject.availableQuizCount > 0
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 174.dp)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics {
                if (!available) contentDescription = "${subject.title}, catalogue à explorer"
            },
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(
            width = 1.dp,
            color = if (available) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.surfaceVariant
        ),
        shadowElevation = if (available) 2.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconBubble(symbol = icon, accent = accent, size = 48.dp)
                AvailabilityPill(
                    text = if (available) "${subject.availableQuizCount} QCM" else "Explorer",
                    available = available
                )
            }
            Text(
                text = subject.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = categoryLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
internal fun ChapterCatalogCard(
    chapter: ChapterCardUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val available = chapter.availableQuizCount > 0
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 88.dp)
            .clickable(enabled = available, role = Role.Button, onClick = onClick)
            .semantics {
                if (!available) contentDescription = "${chapter.title}, bientôt disponible"
            },
        shape = RoundedCornerShape(20.dp),
        color = if (available) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.58f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBubble(
                symbol = if (available) "📘" else "⏳",
                accent = if (available) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                size = 42.dp
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp)
            ) {
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = if (available) {
                        "${chapter.availableQuizCount} QCM disponible${if (chapter.availableQuizCount == 1) "" else "s"}"
                    } else {
                        "Bientôt disponible"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (available) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline
                )
            }
            if (available) {
                Text(
                    text = "👉",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clearAndSetSemantics { }
                )
            }
        }
    }
}

@Composable
internal fun IconBubble(
    symbol: String,
    accent: Color,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = accent.copy(alpha = 0.13f),
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = symbol,
                fontSize = (size.value * 0.62f).sp,
                modifier = Modifier.clearAndSetSemantics { }
            )
        }
    }
}

@Composable
internal fun AvailabilityPill(text: String, available: Boolean) {
    Surface(
        shape = CircleShape,
        color = if (available) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (available) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

internal fun subjectIcon(title: String): String {
    val normalized = title.lowercase()
    return when {
        "math" in normalized -> "🧮"
        "fran" in normalized || "lettre" in normalized -> "📖"
        "histoire" in normalized || "géographie" in normalized || "geographie" in normalized -> "🌍"
        "physique" in normalized || "chimie" in normalized -> "⚗️"
        "svt" in normalized || "science de la vie" in normalized || "biologie" in normalized -> "🌱"
        "anglais" in normalized || "langue" in normalized || "espagnol" in normalized || "allemand" in normalized -> "💬"
        "ses" in normalized || "économ" in normalized || "econom" in normalized -> "📊"
        "numérique" in normalized || "numerique" in normalized || "informatique" in normalized || "nsi" in normalized -> "💻"
        "technologie" in normalized -> "⚙️"
        "philo" in normalized -> "💡"
        "musique" in normalized -> "🎵"
        "art" in normalized -> "🎨"
        "eps" in normalized || "sport" in normalized -> "⚽"
        else -> "🌟"
    }
}

@Composable
internal fun subjectAccent(index: Int): Color = when (index % 4) {
    0 -> MaterialTheme.colorScheme.primary
    1 -> MaterialTheme.colorScheme.tertiary
    2 -> MaterialTheme.colorScheme.secondary
    else -> Color(0xFFB97800)
}

internal fun subjectSection(kind: String): String {
    val value = kind.trim().lowercase()
    return when {
        value.isBlank() || "commun" in value || "tronc" in value || "obligatoire" in value -> "Enseignements communs"
        "spécial" in value || "special" in value -> "Spécialités"
        "option" in value || "facultatif" in value -> "Options"
        else -> kind.trim()
    }
}

internal fun sectionOrder(section: String): Int = when (section) {
    "Enseignements communs" -> 0
    "Spécialités" -> 1
    "Options" -> 2
    else -> 3
}
