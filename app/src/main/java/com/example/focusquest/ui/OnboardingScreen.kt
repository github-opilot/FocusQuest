package com.example.focusquest.ui

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private enum class OnboardingIllustration(val emoji: String, val label: String) {
    BOOK("📚", "Livres"),
    CLOCK("⏱️", "Chronomètre"),
    ROCKET("🚀", "Fusée")
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
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = illustration.emoji,
            modifier = Modifier.clearAndSetSemantics {
                contentDescription = illustration.label
            },
            fontSize = 112.sp,
            textAlign = TextAlign.Center
        )
    }
}
