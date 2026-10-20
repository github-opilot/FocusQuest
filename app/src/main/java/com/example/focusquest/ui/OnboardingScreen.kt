package com.example.focusquest.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private enum class OnboardingIllustration {
    BOOK,
    CLOCK,
    ROCKET
}

private data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val illustration: OnboardingIllustration
)

private val onboardingPages = listOf(
    OnboardingPage(
        title = "Bienvenue dans FocusQuest",
        subtitle = "Réviser devient une aventure !",
        illustration = OnboardingIllustration.BOOK
    ),
    OnboardingPage(
        title = "Apprends et gagne",
        subtitle = "Chaque bonne réponse te fait gagner du temps d'écran.",
        illustration = OnboardingIllustration.CLOCK
    ),
    OnboardingPage(
        title = "À toi de jouer !",
        subtitle = "Choisis une matière et commence ta première mission.",
        illustration = OnboardingIllustration.ROCKET
    )
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPage by rememberSaveable { mutableIntStateOf(0) }
    val page = onboardingPages[currentPage]

    BackHandler(enabled = currentPage > 0) {
        currentPage -= 1
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onFinish) {
                Text("Passer")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = page.subtitle,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))
        OnboardingIllustrationView(
            illustration = page.illustration,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            onboardingPages.indices.forEach { index ->
                Surface(
                    modifier = Modifier.size(width = if (index == currentPage) 22.dp else 8.dp, height = 8.dp),
                    shape = CircleShape,
                    color = if (index == currentPage) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    }
                ) {}
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (currentPage == onboardingPages.lastIndex) {
                    onFinish()
                } else {
                    currentPage += 1
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (currentPage == onboardingPages.lastIndex) "C'est parti" else "Suivant")
        }
    }
}

@Composable
private fun OnboardingIllustrationView(
    illustration: OnboardingIllustration,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val orbitRadius = size.minDimension * 0.36f

            drawCircle(
                color = colors.primaryContainer,
                radius = orbitRadius,
                center = center
            )
            drawCircle(
                color = colors.tertiary,
                radius = size.minDimension * 0.025f,
                center = Offset(center.x - orbitRadius * 0.88f, center.y - orbitRadius * 0.52f)
            )
            drawCircle(
                color = colors.secondary,
                radius = size.minDimension * 0.035f,
                center = Offset(center.x + orbitRadius * 0.9f, center.y + orbitRadius * 0.55f)
            )

            when (illustration) {
                OnboardingIllustration.BOOK -> {
                    val bookWidth = size.width * 0.25f
                    val bookHeight = size.height * 0.36f
                    val gap = size.width * 0.018f
                    val top = center.y - bookHeight / 2f
                    val left = center.x - bookWidth - gap / 2f
                    val corner = CornerRadius(size.minDimension * 0.035f)

                    drawRoundRect(
                        color = colors.surface,
                        topLeft = Offset(left, top),
                        size = Size(bookWidth, bookHeight),
                        cornerRadius = corner
                    )
                    drawRoundRect(
                        color = colors.surface,
                        topLeft = Offset(center.x + gap / 2f, top),
                        size = Size(bookWidth, bookHeight),
                        cornerRadius = corner
                    )
                    drawLine(
                        color = colors.primary,
                        start = Offset(center.x, top + size.height * 0.035f),
                        end = Offset(center.x, top + bookHeight),
                        strokeWidth = size.minDimension * 0.025f
                    )
                    repeat(3) { line ->
                        val y = top + bookHeight * (0.42f + line * 0.16f)
                        drawLine(colors.secondary, Offset(left + bookWidth * 0.2f, y), Offset(center.x - gap, y), size.minDimension * 0.018f)
                        drawLine(colors.secondary, Offset(center.x + gap, y), Offset(center.x + bookWidth * 0.8f, y), size.minDimension * 0.018f)
                    }
                }

                OnboardingIllustration.CLOCK -> {
                    val radius = size.minDimension * 0.22f
                    drawCircle(colors.surface, radius, center)
                    drawCircle(colors.primary, radius, center, style = Stroke(width = size.minDimension * 0.035f))
                    drawLine(
                        color = colors.primary,
                        start = center,
                        end = Offset(center.x, center.y - radius * 0.58f),
                        strokeWidth = size.minDimension * 0.035f
                    )
                    drawLine(
                        color = colors.primary,
                        start = center,
                        end = Offset(center.x + radius * 0.45f, center.y + radius * 0.24f),
                        strokeWidth = size.minDimension * 0.035f
                    )
                    drawCircle(colors.tertiary, size.minDimension * 0.035f, center)
                    drawCircle(colors.secondary, size.minDimension * 0.045f, Offset(center.x + radius * 1.15f, center.y - radius * 0.8f))
                }

                OnboardingIllustration.ROCKET -> {
                    val rocket = Path().apply {
                        moveTo(center.x, center.y - size.height * 0.24f)
                        quadraticTo(center.x + size.width * 0.18f, center.y - size.height * 0.03f, center.x + size.width * 0.1f, center.y + size.height * 0.19f)
                        lineTo(center.x - size.width * 0.1f, center.y + size.height * 0.19f)
                        quadraticTo(center.x - size.width * 0.18f, center.y - size.height * 0.03f, center.x, center.y - size.height * 0.24f)
                        close()
                    }
                    drawPath(rocket, colors.surface)
                    drawPath(rocket, colors.primary, style = Stroke(width = size.minDimension * 0.025f))
                    drawCircle(colors.secondary, size.minDimension * 0.055f, Offset(center.x, center.y - size.height * 0.04f))
                    drawPath(
                        path = Path().apply {
                            moveTo(center.x - size.width * 0.1f, center.y + size.height * 0.08f)
                            lineTo(center.x - size.width * 0.18f, center.y + size.height * 0.18f)
                            lineTo(center.x - size.width * 0.09f, center.y + size.height * 0.2f)
                            close()
                        },
                        color = colors.tertiary
                    )
                    drawPath(
                        path = Path().apply {
                            moveTo(center.x + size.width * 0.1f, center.y + size.height * 0.08f)
                            lineTo(center.x + size.width * 0.18f, center.y + size.height * 0.18f)
                            lineTo(center.x + size.width * 0.09f, center.y + size.height * 0.2f)
                            close()
                        },
                        color = colors.tertiary
                    )
                    drawRoundRect(
                        color = colors.secondary,
                        topLeft = Offset(center.x - size.width * 0.045f, center.y + size.height * 0.2f),
                        size = Size(size.width * 0.09f, size.height * 0.13f),
                        cornerRadius = CornerRadius(size.minDimension * 0.04f)
                    )
                }
            }
        }
    }
}
