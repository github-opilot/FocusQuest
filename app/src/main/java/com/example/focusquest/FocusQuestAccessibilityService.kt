package com.example.focusquest

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.provider.Settings
import android.telecom.TelecomManager
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.focusquest.data.AppProtectionPolicy
import com.example.focusquest.data.ScreenTimeRepository
import com.example.focusquest.data.UsageStatsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Detects the foreground app by package name only and shows a voluntary-use blocker overlay. */
class FocusQuestAccessibilityService : AccessibilityService() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private lateinit var screenTimeRepository: ScreenTimeRepository
    private lateinit var usageStatsRepository: UsageStatsRepository
    private lateinit var windowManager: WindowManager

    @Volatile private var selectedPackages: Set<String> = emptySet()
    @Volatile private var protectionEnabled = false
    @Volatile private var availableTimeMillis = Long.MAX_VALUE
    @Volatile private var foregroundPackageName: String? = null

    private var monitoringJob: Job? = null
    private var overlayView: View? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        screenTimeRepository = ScreenTimeRepository(applicationContext)
        usageStatsRepository = UsageStatsRepository(applicationContext)
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        serviceScope.launch {
            screenTimeRepository.resetIfNewDay()
            combine(
                screenTimeRepository.selectedPackages,
                screenTimeRepository.screenTimeState,
                screenTimeRepository.appProtectionEnabled
            ) { packages, state, enabled ->
                ProtectionSnapshot(packages, state.availableTimeMillis, enabled)
            }.collect { snapshot ->
                selectedPackages = snapshot.packages
                availableTimeMillis = snapshot.availableTimeMillis
                protectionEnabled = snapshot.enabled
                reconcileProtection()
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType !in FOREGROUND_EVENTS) return

        // Deliberately read no event text, source node, or window contents.
        val eventPackage = event.packageName?.toString() ?: return

        // Accessibility overlays can emit a window event for FocusQuest itself.
        if (eventPackage == packageName && overlayView != null) return

        foregroundPackageName = eventPackage
        reconcileProtection()
    }

    override fun onInterrupt() {
        foregroundPackageName = null
        monitoringJob?.cancel()
        monitoringJob = null
        removeBlockingOverlay()
    }

    override fun onDestroy() {
        foregroundPackageName = null
        monitoringJob?.cancel()
        serviceScope.cancel()
        removeBlockingOverlay()
        super.onDestroy()
    }

    private fun reconcileProtection() {
        val currentPackage = foregroundPackageName
        if (currentPackage == null ||
            !protectionEnabled ||
            currentPackage !in selectedPackages ||
            isEssentialPackage(currentPackage)
        ) {
            monitoringJob?.cancel()
            monitoringJob = null
            removeBlockingOverlay()
            return
        }

        updateOverlayForCurrentState()
        if (monitoringJob?.isActive != true) {
            monitoringJob = serviceScope.launch {
                monitorSelectedForegroundApp(currentPackage)
            }
        }
    }

    private suspend fun monitorSelectedForegroundApp(targetPackage: String) {
        while (isMonitoringTarget(targetPackage)) {
            val latestAvailableTime = withContext(Dispatchers.IO) {
                screenTimeRepository.resetIfNewDay()
                val packagesSnapshot = selectedPackages
                if (usageStatsRepository.hasUsageAccess() && packagesSnapshot.isNotEmpty()) {
                    val readings = usageStatsRepository.getTodayForegroundTimeMillis(packagesSnapshot)
                    if (readings != null) screenTimeRepository.addUsageSnapshot(readings)
                }
                screenTimeRepository.screenTimeState.first().availableTimeMillis
            }

            withContext(Dispatchers.Main.immediate) {
                availableTimeMillis = latestAvailableTime
                if (isMonitoringTarget(targetPackage)) updateOverlayForCurrentState()
            }
            delay(POLL_INTERVAL_MILLIS)
        }
    }

    private fun isMonitoringTarget(targetPackage: String): Boolean =
        foregroundPackageName == targetPackage &&
            protectionEnabled &&
            targetPackage in selectedPackages &&
            !isEssentialPackage(targetPackage)

    private fun updateOverlayForCurrentState() {
        val currentPackage = foregroundPackageName
        val shouldBlock = AppProtectionPolicy.shouldBlock(
            protectionEnabled = protectionEnabled && currentPackage?.let(::isEssentialPackage) != true,
            selectedPackages = selectedPackages,
            foregroundPackage = currentPackage,
            availableTimeMillis = availableTimeMillis
        )
        if (shouldBlock) showBlockingOverlay() else removeBlockingOverlay()
    }

    private fun showBlockingOverlay() {
        if (overlayView != null) return

        val view = createBlockingOverlay()
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            0,
            PixelFormat.OPAQUE
        ).apply {
            gravity = Gravity.FILL
        }

        try {
            windowManager.addView(view, params)
            overlayView = view
        } catch (_: WindowManager.BadTokenException) {
            // If Android rejects the overlay, leave the underlying app usable.
        } catch (_: IllegalStateException) {
            // Service shutdown can race with a window update; never trap the user.
        }
    }

    private fun removeBlockingOverlay() {
        val currentOverlay = overlayView ?: return
        overlayView = null
        runCatching { windowManager.removeView(currentOverlay) }
    }

    private fun createBlockingOverlay(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(28), dp(28), dp(28), dp(28))
            setBackgroundColor(FOCUS_VIOLET)
            isClickable = true
            isFocusable = true
            setOnClickListener { /* Consume touches outside the action buttons. */ }
        }

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24), dp(28), dp(24), dp(24))
            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = dp(28).toFloat()
            }
        }
        root.addView(
            card,
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        )

        card.addView(textView("🔒", 48f, FOCUS_VIOLET, bold = false), centeredParams(top = 0))
        card.addView(
            textView("Temps d’écran épuisé !", 25f, Color.rgb(33, 27, 43), bold = true),
            centeredParams(top = 14)
        )
        card.addView(
            textView("Réponds à quelques questions pour continuer.", 17f, Color.rgb(70, 64, 78)),
            centeredParams(top = 12)
        )

        val missionButton = button("Faire une mission", primary = true).apply {
            setOnClickListener {
                dismissAndOpenFocusQuest()
            }
        }
        card.addView(missionButton, buttonParams(top = 24))

        val homeButton = button("Retour à l’accueil du téléphone", primary = false).apply {
            setOnClickListener {
                dismissAndOpenHome()
            }
        }
        card.addView(homeButton, buttonParams(top = 10))
        return root
    }

    private fun dismissAndOpenFocusQuest() {
        foregroundPackageName = null
        monitoringJob?.cancel()
        monitoringJob = null
        removeBlockingOverlay()
        startActivity(
            Intent(this, MainActivity::class.java).addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
        )
    }

    private fun dismissAndOpenHome() {
        foregroundPackageName = null
        monitoringJob?.cancel()
        monitoringJob = null
        removeBlockingOverlay()
        startActivity(
            Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_HOME)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    private fun isEssentialPackage(candidate: String): Boolean = candidate in essentialPackages

    private val essentialPackages: Set<String> by lazy {
        buildSet {
            add(packageName)
            add("android")
            add("com.android.systemui")
            add("com.android.settings")
            add("com.android.phone")
            add("com.android.server.telecom")
            add("com.android.emergency")

            resolvePackage(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME))?.let(::add)
            resolvePackage(Intent(Settings.ACTION_SETTINGS))?.let(::add)
            resolvePackage(Intent(Intent.ACTION_DIAL))?.let(::add)
            resolvePackage(Intent("android.intent.action.EMERGENCY_DIAL"))?.let(::add)
            resolvePackages(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)).forEach(::add)
            resolvePackages(Intent(Settings.ACTION_SETTINGS)).forEach(::add)
            resolvePackages(Intent(Intent.ACTION_DIAL)).forEach(::add)
            resolvePackages(Intent("android.intent.action.EMERGENCY_DIAL")).forEach(::add)
            (getSystemService(TELECOM_SERVICE) as? TelecomManager)?.defaultDialerPackage?.let(::add)
        }
    }

    private fun resolvePackage(intent: Intent): String? = try {
        packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            ?.activityInfo?.packageName
    } catch (_: RuntimeException) {
        null
    }

    private fun resolvePackages(intent: Intent): List<String> = try {
        packageManager.queryIntentActivities(intent, 0)
            .mapNotNull { it.activityInfo?.packageName }
            .distinct()
    } catch (_: RuntimeException) {
        emptyList()
    }

    private fun textView(text: String, sizeSp: Float, color: Int, bold: Boolean = false) =
        TextView(this).apply {
            this.text = text
            textSize = sizeSp
            setTextColor(color)
            gravity = Gravity.CENTER
            if (bold) setTypeface(typeface, Typeface.BOLD)
        }

    private fun button(text: String, primary: Boolean) = Button(this).apply {
        this.text = text
        textSize = 16f
        isAllCaps = false
        setTextColor(if (primary) FOCUS_VIOLET else Color.WHITE)
        backgroundTintList = android.content.res.ColorStateList.valueOf(
            if (primary) Color.WHITE else Color.rgb(92, 31, 177)
        )
        minHeight = dp(52)
    }

    private fun centeredParams(top: Int) = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply { topMargin = dp(top) }

    private fun buttonParams(top: Int) = centeredParams(top)

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private data class ProtectionSnapshot(
        val packages: Set<String>,
        val availableTimeMillis: Long,
        val enabled: Boolean
    )

    companion object {
        private const val POLL_INTERVAL_MILLIS = 2_000L
        private const val FOCUS_VIOLET = 0xFF7928E8.toInt()
        private val FOREGROUND_EVENTS = setOf(
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
            AccessibilityEvent.TYPE_WINDOWS_CHANGED
        )

        fun isEnabled(context: android.content.Context): Boolean {
            val enabled = Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED,
                0
            ) == 1
            if (!enabled) return false

            val expected = ComponentName(context, FocusQuestAccessibilityService::class.java)
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ).orEmpty()
            return enabledServices.split(':').any { entry ->
                ComponentName.unflattenFromString(entry) == expected
            }
        }
    }
}
