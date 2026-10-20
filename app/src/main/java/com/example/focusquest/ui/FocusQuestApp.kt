package com.example.focusquest.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.focusquest.data.OnboardingRepository
import com.example.focusquest.data.QuestionRepository
import com.example.focusquest.model.Grade
import com.example.focusquest.model.Subject
import kotlinx.coroutines.launch

private const val HOME_ROUTE = "home"
private const val ONBOARDING_ROUTE = "onboarding"
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
    val coroutineScope = rememberCoroutineScope()
    val onboardingRepository = remember(context.applicationContext) {
        OnboardingRepository(context.applicationContext)
    }
    val hasSeenOnboarding by onboardingRepository.hasSeenOnboarding.collectAsState(initial = null)
    var initialStartDestination by remember { mutableStateOf<String?>(null) }
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

    LaunchedEffect(hasSeenOnboarding) {
        if (initialStartDestination == null && hasSeenOnboarding != null) {
            initialStartDestination = if (hasSeenOnboarding == true) HOME_ROUTE else ONBOARDING_ROUTE
        }
    }

    if (initialStartDestination == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val startDestination = initialStartDestination ?: ONBOARDING_ROUTE
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: startDestination
    var showExitMissionDialog by remember(currentRoute) { mutableStateOf(false) }
    val isQuizRoute = currentRoute == QUIZ_ROUTE

    fun returnToHome() {
        showExitMissionDialog = false
        navController.navigateHomeWithoutDuplicates()
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (currentRoute != ONBOARDING_ROUTE && currentRoute != APP_SELECTION_ROUTE) {
            FocusQuestTopBar(
                title = topBarTitle(currentRoute),
                backLabel = if (isQuizRoute) null else if (currentRoute == HOME_ROUTE) null else "Retour",
                actionLabel = if (isQuizRoute) "Retour à l'accueil" else null,
                onBack = {
                    if (!navController.popBackStack()) returnToHome()
                },
                onAction = {
                    if (isQuizRoute) showExitMissionDialog = true
                }
            )
        }

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.weight(1f)
        ) {
            composable(ONBOARDING_ROUTE) {
                OnboardingScreen(
                    onFinish = {
                        coroutineScope.launch {
                            onboardingRepository.setHasSeenOnboarding(true)
                            navController.navigate(HOME_ROUTE) {
                                popUpTo(ONBOARDING_ROUTE) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }

            composable(HOME_ROUTE) {
                HomeScreen(
                    availableTimeMillis = screenTimeState.availableTimeMillis,
                    earnedTimeMillis = screenTimeState.earnedTimeMillis,
                    usedTimeMillis = screenTimeState.usedTimeMillis,
                    monitoredApps = appNames,
                    usageAccessGranted = usageAccessGranted,
                    isRefreshing = isRefreshing,
                    onStartMission = { navController.navigateSingleTop(GRADE_ROUTE) },
                    onOpenSettings = { navController.navigateSingleTop(SETTINGS_ROUTE) },
                    onSelectApps = { navController.navigateSingleTop(APP_SELECTION_ROUTE) },
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
                        navController.navigateSingleTop("subjectSelection/${grade.name}")
                    }
                )
            }

            composable(SUBJECT_ROUTE) { entry ->
                val gradeName = entry.arguments?.getString("grade")
                val grade = Grade.entries.firstOrNull { it.name == gradeName }

                if (grade != null) {
                    SubjectSelectionScreen(
                        grade = grade,
                        onSubjectSelected = { subject ->
                            navController.navigateSingleTop("quiz/${grade.name}/${subject.name}")
                        }
                    )
                }
            }

            composable(QUIZ_ROUTE) { entry ->
                val gradeName = entry.arguments?.getString("grade")
                val subjectName = entry.arguments?.getString("subject")
                val grade = Grade.entries.firstOrNull { it.name == gradeName }
                val subject = Subject.entries.firstOrNull { it.name == subjectName }

                if (grade != null && subject != null) {
                    val missionQuestions = remember(grade, subject) {
                        QuestionRepository.getQuestions(grade, subject).take(5)
                    }

                    QuizScreen(
                        questions = missionQuestions,
                        availableTimeMillis = screenTimeState.availableTimeMillis,
                        onTimeEarned = screenTimeViewModel::addEarnedTimeMillis,
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

        BackHandler(enabled = isQuizRoute && !showExitMissionDialog) {
            showExitMissionDialog = true
        }
    }

    if (showExitMissionDialog) {
        AlertDialog(
            onDismissRequest = { showExitMissionDialog = false },
            title = { Text("Retourner à l'accueil ?") },
            text = { Text("Tu quitteras cette mission et ses résultats.") },
            confirmButton = {
                TextButton(onClick = ::returnToHome) {
                    Text("Retour à l'accueil")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitMissionDialog = false }) {
                    Text("Continuer la mission")
                }
            }
        )
    }
}

@Composable
private fun FocusQuestTopBar(
    title: String,
    backLabel: String?,
    actionLabel: String?,
    onBack: () -> Unit,
    onAction: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (backLabel != null) {
                TextButton(onClick = onBack) { Text(backLabel) }
            }
            Text(
                text = title,
                modifier = Modifier.padding(start = if (backLabel == null) 12.dp else 4.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.weight(1f))
            if (actionLabel != null) {
                TextButton(onClick = onAction) { Text(actionLabel) }
            }
        }
    }
}

private fun topBarTitle(route: String): String = when (route) {
    HOME_ROUTE -> "Accueil"
    SETTINGS_ROUTE -> "Réglages"
    GRADE_ROUTE -> "Ta classe"
    SUBJECT_ROUTE -> "Ta matière"
    QUIZ_ROUTE -> "Mission"
    else -> "FocusQuest"
}

private fun NavHostController.navigateSingleTop(route: String) {
    navigate(route) { launchSingleTop = true }
}

private fun NavHostController.navigateHomeWithoutDuplicates() {
    navigate(HOME_ROUTE) {
        popUpTo(HOME_ROUTE) { inclusive = false }
        launchSingleTop = true
    }
}
