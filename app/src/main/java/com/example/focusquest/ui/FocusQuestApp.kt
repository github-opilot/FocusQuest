package com.example.focusquest.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.app.Activity
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
import com.example.focusquest.FocusQuestAccessibilityService
import com.example.focusquest.data.OnboardingRepository
import com.example.focusquest.data.catalog.ChapterQuestionRepository
import com.example.focusquest.data.catalog.CurriculumRepository
import com.example.focusquest.ui.components.FocusQuestButton
import com.example.focusquest.ui.components.FocusQuestCard
import com.example.focusquest.model.Grade
import com.example.focusquest.ui.catalog.ActivityPickerScreen
import com.example.focusquest.ui.catalog.ChapterCardUi
import com.example.focusquest.ui.catalog.ChapterPickerScreen
import com.example.focusquest.ui.catalog.SubjectCardUi
import com.example.focusquest.ui.catalog.SubjectPickerScreen
import kotlinx.coroutines.launch

private const val HOME_ROUTE = "home"
private const val ONBOARDING_ROUTE = "onboarding"
private const val SETTINGS_ROUTE = "settings"
private const val APP_SELECTION_ROUTE = "appSelection"
private const val GRADE_ROUTE = "gradeSelection"
private const val SUBJECT_ROUTE = "subjectSelection/{grade}"
private const val CHAPTER_ROUTE = "chapterSelection/{grade}/{subject}"
private const val ACTIVITY_ROUTE = "activitySelection/{grade}/{subject}/{chapter}"
private const val QUIZ_ROUTE = "quiz/{grade}/{subject}/{chapter}"

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
    val curriculumRepository = remember(context.applicationContext) {
        CurriculumRepository(context.applicationContext)
    }
    val chapterQuestionRepository = remember(context.applicationContext) {
        ChapterQuestionRepository(context.applicationContext, curriculumRepository)
    }
    val hasSeenOnboarding by onboardingRepository.hasSeenOnboarding.collectAsState(initial = null)
    var initialStartDestination by remember { mutableStateOf<String?>(null) }
    val screenTimeState by screenTimeViewModel.screenTimeState.collectAsState()
    val availableApps by screenTimeViewModel.availableApps.collectAsState()
    val selectedPackages by screenTimeViewModel.selectedPackages.collectAsState()
    val usageAccessGranted by screenTimeViewModel.usageAccessGranted.collectAsState()
    val isRefreshing by screenTimeViewModel.isRefreshing.collectAsState()
    val appProtectionEnabled by screenTimeViewModel.appProtectionEnabled.collectAsState()
    var accessibilityServiceEnabled by remember {
        mutableStateOf(FocusQuestAccessibilityService.isEnabled(context))
    }

    DisposableEffect(lifecycleOwner, screenTimeViewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                screenTimeViewModel.refreshUsage()
                accessibilityServiceEnabled = FocusQuestAccessibilityService.isEnabled(context)
            }
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

    fun openAccessibilitySettings() {
        try {
            context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "Les réglages d'accessibilité ne sont pas disponibles sur cet appareil.",
                Toast.LENGTH_LONG
            ).show()
        } catch (_: SecurityException) {
            Toast.makeText(
                context,
                "Impossible d'ouvrir les réglages Android.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    if (!accessibilityServiceEnabled) {
        AccessibilityRequiredScreen(
            onOpenAccessibilitySettings = ::openAccessibilitySettings,
            onExit = { (context as? Activity)?.finish() }
        )
        return
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
    val hasCatalogTopBar = currentRoute == SUBJECT_ROUTE ||
        currentRoute == CHAPTER_ROUTE || currentRoute == ACTIVITY_ROUTE

    fun returnToHome() {
        showExitMissionDialog = false
        navController.navigateHomeWithoutDuplicates()
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (currentRoute != ONBOARDING_ROUTE && currentRoute != APP_SELECTION_ROUTE && !hasCatalogTopBar) {
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
                        navController.navigate(HOME_ROUTE) {
                            popUpTo(ONBOARDING_ROUTE) { inclusive = true }
                            launchSingleTop = true
                        }
                        coroutineScope.launch {
                            onboardingRepository.setHasSeenOnboarding(true)
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
                    onBaseDailyMinutesSelected = screenTimeViewModel::setBaseDailyMinutes,
                    appProtectionEnabled = appProtectionEnabled,
                    accessibilityServiceEnabled = accessibilityServiceEnabled,
                    onAppProtectionEnabledChange = screenTimeViewModel::setAppProtectionEnabled,
                    onOpenAccessibilitySettings = ::openAccessibilitySettings
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
                        navController.navigateSingleTop("subjectSelection/${grade.curriculumId()}")
                    }
                )
            }

            composable(SUBJECT_ROUTE) { entry ->
                val gradeId = entry.arguments?.getString("grade").orEmpty()
                val grade = gradeForCurriculumId(gradeId)
                if (grade != null) {
                    val subjectCards = remember(gradeId) {
                        curriculumRepository.getSubjects(gradeId).map { subject ->
                            val counts = chapterQuestionRepository.getQuestionCounts(gradeId, subject.id)
                            SubjectCardUi(
                                id = subject.id,
                                title = subject.title,
                                kind = subject.kind,
                                availableQuizCount = counts.values.sum()
                            )
                        }
                    }
                    SubjectPickerScreen(
                        gradeTitle = grade.displayName,
                        subjects = subjectCards,
                        onSubjectSelected = { subject ->
                            navController.navigateSingleTop("chapterSelection/$gradeId/${subject.id}")
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable(CHAPTER_ROUTE) { entry ->
                val gradeId = entry.arguments?.getString("grade").orEmpty()
                val subjectId = entry.arguments?.getString("subject").orEmpty()
                val grade = gradeForCurriculumId(gradeId)
                val subject = curriculumRepository.getSubjects(gradeId).firstOrNull { it.id == subjectId }

                if (grade != null && subject != null) {
                    val chapterCards = remember(gradeId, subjectId) {
                        val counts = chapterQuestionRepository.getQuestionCounts(gradeId, subjectId)
                        curriculumRepository.getChapters(gradeId, subjectId).map { chapter ->
                            ChapterCardUi(
                                id = chapter.id,
                                title = chapter.title,
                                theme = chapter.theme,
                                availableQuizCount = counts[chapter.id] ?: 0
                            )
                        }
                    }
                    ChapterPickerScreen(
                        gradeTitle = grade.displayName,
                        subjectTitle = subject.title,
                        chapters = chapterCards,
                        onChapterSelected = { chapter ->
                            if (chapter.availableQuizCount > 0) {
                                navController.navigateSingleTop(
                                    "activitySelection/$gradeId/$subjectId/${chapter.id}"
                                )
                            }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable(ACTIVITY_ROUTE) { entry ->
                val gradeId = entry.arguments?.getString("grade").orEmpty()
                val subjectId = entry.arguments?.getString("subject").orEmpty()
                val chapterId = entry.arguments?.getString("chapter").orEmpty()
                val chapter = curriculumRepository.getChapters(gradeId, subjectId)
                    .firstOrNull { it.id == chapterId }
                val questionCount = remember(gradeId, subjectId, chapterId) {
                    chapterQuestionRepository.getQuestionCount(gradeId, subjectId, chapterId)
                }

                if (chapter != null) {
                    ActivityPickerScreen(
                        chapterTitle = chapter.title,
                        questionCount = questionCount,
                        onStartQuiz = {
                            if (questionCount > 0) {
                                navController.navigateSingleTop("quiz/$gradeId/$subjectId/$chapterId")
                            }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable(QUIZ_ROUTE) { entry ->
                val gradeId = entry.arguments?.getString("grade").orEmpty()
                val subjectId = entry.arguments?.getString("subject").orEmpty()
                val chapterId = entry.arguments?.getString("chapter").orEmpty()
                val missionQuestions = remember(gradeId, subjectId, chapterId) {
                    chapterQuestionRepository.getSessionQuestions(
                        gradeId = gradeId,
                        subjectId = subjectId,
                        chapterId = chapterId,
                        limit = 5
                    ).take(5)
                }

                ChapterQuizScreen(
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
private fun AccessibilityRequiredScreen(
    onOpenAccessibilitySettings: () -> Unit,
    onExit: () -> Unit
) {
    var showConsent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🔒", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(20.dp))
        Text(
            text = "Active l’accessibilité pour continuer",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))
        FocusQuestCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Pour utiliser FocusQuest, active son service d’accessibilité dans les paramètres Android.",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Le service détecte uniquement le nom de l’application ouverte. Il ne lit pas le contenu de l’écran.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(20.dp))
        FocusQuestButton(
            text = "Continuer vers l’accessibilité",
            onClick = { showConsent = true },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        androidx.compose.material3.TextButton(onClick = onExit) {
            Text("Quitter FocusQuest")
        }
    }

    if (showConsent) {
        AlertDialog(
            onDismissRequest = { showConsent = false },
            title = { Text("Autorisation d’accessibilité") },
            text = {
                Text(
                    "FocusQuest utilise ce service pour détecter l’application ouverte et appliquer la protection du temps d’écran. Il ne lit pas le contenu des écrans. Active manuellement « Protection FocusQuest » dans Android. Si tu désactives ensuite ce service, FocusQuest restera verrouillée jusqu’à sa réactivation."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConsent = false
                        onOpenAccessibilitySettings()
                    }
                ) { Text("Ouvrir les paramètres") }
            },
            dismissButton = {
                TextButton(onClick = { showConsent = false }) { Text("Annuler") }
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
    CHAPTER_ROUTE -> "Ton chapitre"
    ACTIVITY_ROUTE -> "Ton activité"
    QUIZ_ROUTE -> "Mission"
    else -> "FocusQuest"
}

private fun Grade.curriculumId(): String = when (this) {
    Grade.SIXIEME -> "6e"
    Grade.CINQUIEME -> "5e"
    Grade.QUATRIEME -> "4e"
    Grade.TROISIEME -> "3e"
    Grade.SECONDE -> "seconde"
    Grade.PREMIERE -> "premiere_generale"
    Grade.TERMINALE -> "terminale_generale"
}

private fun gradeForCurriculumId(gradeId: String): Grade? = Grade.entries.firstOrNull { grade ->
    grade.curriculumId() == gradeId
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
