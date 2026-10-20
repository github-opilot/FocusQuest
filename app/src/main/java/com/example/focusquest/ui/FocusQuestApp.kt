package com.example.focusquest.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.focusquest.data.QuestionRepository
import com.example.focusquest.model.Grade
import com.example.focusquest.model.Subject

private const val HOME_ROUTE = "home"
private const val SETTINGS_ROUTE = "settings"
private const val APP_SELECTION_ROUTE = "appSelection"
private const val GRADE_ROUTE = "gradeSelection"
private const val SUBJECT_ROUTE = "subjectSelection/{grade}"
private const val QUIZ_ROUTE = "quiz/{grade}/{subject}"

@Composable
fun FocusQuestApp(
    modifier: Modifier = Modifier,
    screenTimeViewModel: ScreenTimeViewModel = viewModel()
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val screenTimeState by screenTimeViewModel.screenTimeState.collectAsState()
    val availableApps by screenTimeViewModel.availableApps.collectAsState()
    val selectedPackages by screenTimeViewModel.selectedPackages.collectAsState()
    val usageAccessGranted by screenTimeViewModel.usageAccessGranted.collectAsState()
    val isRefreshing by screenTimeViewModel.isRefreshing.collectAsState()

    DisposableEffect(lifecycleOwner, screenTimeViewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) screenTimeViewModel.refreshUsage()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val appNames = remember(availableApps, selectedPackages) {
        val labels = availableApps.associate { it.packageName to it.label }
        selectedPackages.map { packageName -> labels[packageName] ?: packageName }
    }

    NavHost(
        navController = navController,
        startDestination = HOME_ROUTE,
        modifier = modifier
    ) {
        composable(HOME_ROUTE) {
            HomeScreen(
                availableMinutes = screenTimeState.availableMinutes,
                earnedMinutes = screenTimeState.earnedMinutes,
                usedMinutes = screenTimeState.usedMinutes,
                monitoredApps = appNames,
                usageAccessGranted = usageAccessGranted,
                isRefreshing = isRefreshing,
                onStartMission = { navController.navigate(GRADE_ROUTE) },
                onOpenSettings = { navController.navigate(SETTINGS_ROUTE) },
                onSelectApps = { navController.navigate(APP_SELECTION_ROUTE) },
                onRefreshUsage = screenTimeViewModel::refreshUsage
            )
        }

        composable(SETTINGS_ROUTE) {
            SettingsScreen(
                baseDailyMinutes = screenTimeState.baseDailyMinutes,
                onBaseDailyMinutesSelected = screenTimeViewModel::setBaseDailyMinutes
            )
        }

        composable(APP_SELECTION_ROUTE) {
            AppSelectionScreen(
                apps = availableApps,
                selectedPackages = selectedPackages,
                usageAccessGranted = usageAccessGranted,
                isRefreshing = isRefreshing,
                onRequestUsageAccess = {
                    try {
                        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                    } catch (_: ActivityNotFoundException) {
                        Toast.makeText(
                            context,
                            "Les réglages d'accès aux statistiques ne sont pas disponibles sur cet appareil.",
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (_: SecurityException) {
                        Toast.makeText(
                            context,
                            "Impossible d'ouvrir les réglages Android.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                onSaveSelection = { packages ->
                    screenTimeViewModel.saveSelectedPackages(packages)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
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
