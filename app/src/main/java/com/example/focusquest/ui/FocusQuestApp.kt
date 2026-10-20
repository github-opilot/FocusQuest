package com.example.focusquest.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.focusquest.data.QuestionRepository
import com.example.focusquest.model.Grade
import com.example.focusquest.model.Subject

private const val HOME_ROUTE = "home"
private const val SETTINGS_ROUTE = "settings"
private const val GRADE_ROUTE = "gradeSelection"
private const val SUBJECT_ROUTE = "subjectSelection/{grade}"
private const val QUIZ_ROUTE = "quiz/{grade}/{subject}"

@Composable
fun FocusQuestApp(
    modifier: Modifier = Modifier,
    screenTimeViewModel: ScreenTimeViewModel = viewModel()
) {
    val navController = rememberNavController()
    val screenTimeState by screenTimeViewModel.screenTimeState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = HOME_ROUTE,
        modifier = modifier
    ) {
        composable(HOME_ROUTE) {
            HomeScreen(
                availableMinutes = screenTimeState.availableMinutes,
                onStartMission = { navController.navigate(GRADE_ROUTE) },
                onOpenSettings = { navController.navigate(SETTINGS_ROUTE) }
            )
        }

        composable(SETTINGS_ROUTE) {
            SettingsScreen(
                baseDailyMinutes = screenTimeState.baseDailyMinutes,
                onBaseDailyMinutesSelected = screenTimeViewModel::setBaseDailyMinutes
            )
        }

        composable(GRADE_ROUTE) {
            GradeSelectionScreen(
                onGradeSelected = { grade ->
                    navController.navigate("subjectSelection/${grade.name}")
                }
            )
        }

        composable(SUBJECT_ROUTE) { backStackEntry ->
            val gradeName = backStackEntry.arguments?.getString("grade")
            val grade = Grade.entries.firstOrNull { it.name == gradeName }

            if (grade != null) {
                SubjectSelectionScreen(
                    grade = grade,
                    onSubjectSelected = { subject ->
                        navController.navigate("quiz/${grade.name}/${subject.name}")
                    }
                )
            }
        }

        composable(QUIZ_ROUTE) { backStackEntry ->
            val gradeName = backStackEntry.arguments?.getString("grade")
            val subjectName = backStackEntry.arguments?.getString("subject")
            val grade = Grade.entries.firstOrNull { it.name == gradeName }
            val subject = Subject.entries.firstOrNull { it.name == subjectName }

            if (grade != null && subject != null) {
                val missionQuestions = remember(grade, subject) {
                    QuestionRepository.getQuestions(grade, subject).take(5)
                }

                QuizScreen(
                    questions = missionQuestions,
                    availableMinutes = screenTimeState.availableMinutes,
                    onMinutesEarned = screenTimeViewModel::addEarnedMinutes,
                    onExitQuiz = { navController.popBackStack() },
                    onNewMission = {
                        navController.navigate(GRADE_ROUTE) {
                            popUpTo(HOME_ROUTE)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
