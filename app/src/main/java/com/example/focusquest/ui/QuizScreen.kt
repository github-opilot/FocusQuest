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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.focusquest.model.Question

@Composable
fun QuizScreen(
    questions: List<Question>,
    availableMinutes: Int,
    onMinutesEarned: (Int) -> Unit,
    onExitQuiz: () -> Unit,
    onNewMission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quizQuestions = questions.take(5)
    var currentQuestionIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedAnswerIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    var score by rememberSaveable { mutableIntStateOf(0) }
    var earnedMinutes by rememberSaveable { mutableIntStateOf(0) }
    var quizFinished by rememberSaveable { mutableStateOf(false) }

    if (quizQuestions.isEmpty()) {
        NoQuestionsScreen(
            onExitQuiz = onExitQuiz,
            modifier = modifier
        )
        return
    }

    if (quizFinished) {
        QuizResult(
            score = score,
            questionCount = quizQuestions.size,
            earnedMinutes = earnedMinutes,
            availableMinutes = availableMinutes,
            onNewMission = onNewMission,
            modifier = modifier
        )
        return
    }

    val question = quizQuestions[currentQuestionIndex]

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Question ${currentQuestionIndex + 1} / ${quizQuestions.size}",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = { (currentQuestionIndex + 1).toFloat() / quizQuestions.size },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Temps gagné : $earnedMinutes min",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Score : $score",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = question.question,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        question.answers.forEachIndexed { index, answer ->
            Button(
                onClick = {
                    if (selectedAnswerIndex == null) {
                        selectedAnswerIndex = index

                        if (index == question.correctAnswerIndex) {
                            score += 1
                            earnedMinutes += question.rewardMinutes
                            onMinutesEarned(question.rewardMinutes)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedAnswerIndex == null
            ) {
                Text(text = answer)
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        selectedAnswerIndex?.let { selectedIndex ->
            Spacer(modifier = Modifier.height(12.dp))

            if (selectedIndex == question.correctAnswerIndex) {
                Text(
                    text = "Bonne réponse ! +${question.rewardMinutes} minutes",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                Text(
                    text = "Mauvaise réponse",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = question.explanation)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (currentQuestionIndex < quizQuestions.lastIndex) {
                        currentQuestionIndex += 1
                        selectedAnswerIndex = null
                    } else {
                        quizFinished = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (currentQuestionIndex == quizQuestions.lastIndex) {
                        "Voir mes résultats"
                    } else {
                        "Question suivante"
                    }
                )
            }
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
        Text(
            text = "Pas encore de questions disponibles pour cette classe et cette matière.",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(onClick = onExitQuiz) {
            Text(text = "Choisir une autre matière")
        }
    }
}

@Composable
private fun QuizResult(
    score: Int,
    questionCount: Int,
    earnedMinutes: Int,
    availableMinutes: Int,
    onNewMission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Quiz terminé !",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Score :\n$score / $questionCount",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Gagné pendant cette mission :\n+$earnedMinutes min",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Temps disponible :\n$availableMinutes min",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onNewMission) {
            Text(text = "Nouvelle mission")
        }
    }
}
