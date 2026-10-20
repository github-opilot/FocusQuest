package com.example.focusquest.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.focusquest.model.Question
import com.example.focusquest.ui.components.FocusQuestButton
import com.example.focusquest.ui.components.FocusQuestCard
import com.example.focusquest.ui.components.ProgressIndicator
import com.example.focusquest.ui.components.RewardBadge
import com.example.focusquest.ui.components.StatCard

private val AnswerShape = RoundedCornerShape(18.dp)

@Composable
fun QuizScreen(
    questions: List<Question>,
    availableTimeMillis: Long,
    onTimeEarned: (Long) -> Unit,
    onExitQuiz: () -> Unit,
    onNewMission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quizQuestions = questions.take(5)
    var currentQuestionIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedAnswerIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    var score by rememberSaveable { mutableIntStateOf(0) }
    var earnedTimeMillis by rememberSaveable { mutableLongStateOf(0L) }
    var quizFinished by rememberSaveable { mutableStateOf(false) }

    if (quizQuestions.isEmpty()) {
        NoQuestionsScreen(onExitQuiz = onExitQuiz, modifier = modifier)
        return
    }

    AnimatedContent(
        targetState = quizFinished,
        modifier = modifier.fillMaxSize(),
        transitionSpec = {
            (fadeIn(tween(180)) + slideInVertically(tween(180)) { it / 24 }) togetherWith
                (fadeOut(tween(120)) + slideOutVertically(tween(120)) { -it / 24 })
        },
        label = "quizToResults"
    ) { isFinished ->
        if (isFinished) {
            QuizResult(
                score = score,
                questionCount = quizQuestions.size,
                earnedTimeMillis = earnedTimeMillis,
                availableTimeMillis = availableTimeMillis,
                onNewMission = onNewMission,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            QuizContent(
                questions = quizQuestions,
                currentQuestionIndex = currentQuestionIndex,
                selectedAnswerIndex = selectedAnswerIndex,
                score = score,
                earnedTimeMillis = earnedTimeMillis,
                onAnswerSelected = { answerIndex ->
                    // The selection lock makes the reward idempotent for this question.
                    if (selectedAnswerIndex == null) {
                        selectedAnswerIndex = answerIndex
                        if (answerIndex == quizQuestions[currentQuestionIndex].correctAnswerIndex) {
                            score += 1
                            val rewardMillis = quizQuestions[currentQuestionIndex].rewardTimeMillis
                            earnedTimeMillis += rewardMillis
                            onTimeEarned(rewardMillis)
                        }
                    }
                },
                onNext = {
                    if (currentQuestionIndex < quizQuestions.lastIndex) {
                        currentQuestionIndex += 1
                        selectedAnswerIndex = null
                    } else {
                        quizFinished = true
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun QuizContent(
    questions: List<Question>,
    currentQuestionIndex: Int,
    selectedAnswerIndex: Int?,
    score: Int,
    earnedTimeMillis: Long,
    onAnswerSelected: (Int) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        ProgressIndicator(
            currentStep = currentQuestionIndex + 1,
            totalSteps = questions.size,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                label = "Score",
                value = "$score / ${questions.size}",
                symbol = "★",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Temps gagné",
                value = formatScreenTime(earnedTimeMillis),
                symbol = "✦",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))

        AnimatedContent(
            targetState = currentQuestionIndex,
            transitionSpec = {
                (fadeIn(tween(180)) + slideInHorizontally(tween(180)) { it / 18 }) togetherWith
                    (fadeOut(tween(120)) + slideOutHorizontally(tween(120)) { -it / 18 })
            },
            label = "questionTransition"
        ) { questionIndex ->
            val question = questions[questionIndex]
            Column {
                FocusQuestCard(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    Text(
                        text = question.question,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(20.dp))

                question.answers.forEachIndexed { answerIndex, answer ->
                    AnswerButton(
                        text = answer,
                        answerIndex = answerIndex,
                        correctAnswerIndex = question.correctAnswerIndex,
                        selectedAnswerIndex = selectedAnswerIndex,
                        enabled = selectedAnswerIndex == null,
                        onClick = { onAnswerSelected(answerIndex) }
                    )
                    if (answerIndex != question.answers.lastIndex) {
                        Spacer(Modifier.height(12.dp))
                    }
                }

                AnimatedVisibility(
                    visible = selectedAnswerIndex != null,
                    enter = fadeIn(tween(180)) + slideInVertically(tween(180)) { it / 8 },
                    exit = fadeOut(tween(100))
                ) {
                    selectedAnswerIndex?.let { selectedIndex ->
                        Column {
                            Spacer(Modifier.height(18.dp))
                            AnswerFeedback(
                                question = question,
                                isCorrect = selectedIndex == question.correctAnswerIndex
                            )
                            Spacer(Modifier.height(18.dp))
                            FocusQuestButton(
                                text = if (questionIndex == questions.lastIndex) {
                                    "Voir mes résultats"
                                } else {
                                    "Question suivante"
                                },
                                onClick = onNext,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnswerButton(
    text: String,
    answerIndex: Int,
    correctAnswerIndex: Int,
    selectedAnswerIndex: Int?,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val isSelected = selectedAnswerIndex == answerIndex
    val isCorrect = answerIndex == correctAnswerIndex
    val revealedCorrectAnswer = selectedAnswerIndex != null && isCorrect
    val containerColor = when {
        isSelected && isCorrect -> MaterialTheme.colorScheme.primaryContainer
        isSelected -> MaterialTheme.colorScheme.errorContainer
        revealedCorrectAnswer -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    val contentColor = when {
        isSelected && isCorrect -> MaterialTheme.colorScheme.onPrimaryContainer
        isSelected -> MaterialTheme.colorScheme.onErrorContainer
        revealedCorrectAnswer -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }
    val marker = when {
        revealedCorrectAnswer -> "✓"
        isSelected -> "×"
        else -> ('A'.code + answerIndex).toChar().toString()
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        shape = AnswerShape,
        color = containerColor,
        contentColor = contentColor,
        border = BorderStroke(
            width = if (isSelected || revealedCorrectAnswer) 2.dp else 1.dp,
            color = when {
                isSelected && !isCorrect -> MaterialTheme.colorScheme.error
                isSelected || revealedCorrectAnswer -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.outlineVariant
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = marker,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.size(28.dp),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.size(12.dp))
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected || revealedCorrectAnswer) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun AnswerFeedback(
    question: Question,
    isCorrect: Boolean
) {
    if (isCorrect) {
        FocusQuestCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Text(
                text = "Bonne réponse !",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(10.dp))
            RewardBadge(amount = formatScreenTime(question.rewardTimeMillis))
        }
    } else {
        FocusQuestCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            Text(
                text = "Pas tout à fait, continue !",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "La bonne réponse : ${question.answers[question.correctAnswerIndex]}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = question.explanation,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun NoQuestionsScreen(
    onExitQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        FocusQuestCard(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            Text(
                text = "Pas encore de questions disponibles pour cette classe et cette matière.",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(24.dp))

        FocusQuestButton(text = "Choisir une autre matière", onClick = onExitQuiz)
    }
}

@Composable
private fun QuizResult(
    score: Int,
    questionCount: Int,
    earnedTimeMillis: Long,
    availableTimeMillis: Long,
    onNewMission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scoreProgress by animateFloatAsState(
        targetValue = score.toFloat() / questionCount,
        animationSpec = tween(durationMillis = 500),
        label = "resultScore"
    )

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Mission terminée !",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        ScoreGraphic(
            score = score,
            questionCount = questionCount,
            progress = scoreProgress
        )

        Spacer(Modifier.height(24.dp))

        FocusQuestCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Temps gagné",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "+${formatScreenTime(earnedTimeMillis)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Temps disponible : ${formatScreenTime(availableTimeMillis)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        FocusQuestButton(
            text = "Recommencer une mission",
            onClick = onNewMission,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ScoreGraphic(
    score: Int,
    questionCount: Int,
    progress: Float
) {
    Box(
        modifier = Modifier.size(156.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxSize()
                .semantics {
                    contentDescription = "$score bonnes réponses sur $questionCount"
                },
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.secondaryContainer,
            strokeWidth = 12.dp
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score / $questionCount",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Score",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
