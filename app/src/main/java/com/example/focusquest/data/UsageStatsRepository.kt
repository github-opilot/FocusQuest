package com.example.focusquest.data

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Process
import java.util.Calendar

data class LaunchableApp(
    val packageName: String,
    val label: String,
    val icon: Drawable
)

/** Reads today's foreground time for apps visible to FocusQuest. */
class UsageStatsRepository(context: Context) {
    private val appContext = context.applicationContext
    private val packageManager = appContext.packageManager

    fun hasUsageAccess(): Boolean {
        return try {
            val appOps = appContext.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                appContext.packageName
            ) == AppOpsManager.MODE_ALLOWED
        } catch (_: RuntimeException) {
            false
        }
    }

    fun getLaunchableApps(): List<LaunchableApp> {
        val launchIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return try {
            packageManager.queryIntentActivities(launchIntent, 0)
                .asSequence()
                .mapNotNull { resolveInfo ->
                    val activityInfo = resolveInfo.activityInfo ?: return@mapNotNull null
                    val packageName = activityInfo.packageName
                    if (packageName == appContext.packageName) return@mapNotNull null
                    val appInfo = activityInfo.applicationInfo ?: return@mapNotNull null
                    runCatching {
                        LaunchableApp(
                            packageName = packageName,
                            label = packageManager.getApplicationLabel(appInfo).toString(),
                            icon = packageManager.getApplicationIcon(appInfo)
                        )
                    }.getOrNull()
                }
                .distinctBy(LaunchableApp::packageName)
                .sortedBy { it.label.lowercase() }
                .toList()
        } catch (_: RuntimeException) {
            emptyList()
        }
    }

    /** Returns null only when access is unavailable or Android rejects the query. */
    fun getTodayForegroundTimeMillis(
        packageNames: Set<String>,
        nowMillis: Long = System.currentTimeMillis()
    ): Map<String, Long>? {
        if (!hasUsageAccess()) return null

        return try {
            val startOfDay = Calendar.getInstance().apply {
                timeInMillis = nowMillis
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val usageStatsManager = appContext.getSystemService(Context.USAGE_STATS_SERVICE)
                as? UsageStatsManager ?: return null
            val usageByPackage = usageStatsManager
                .queryAndAggregateUsageStats(startOfDay, nowMillis)
                .orEmpty()
            packageNames.associateWith { packageName ->
                usageByPackage[packageName]?.totalTimeInForeground?.coerceAtLeast(0L) ?: 0L
            }
        } catch (_: SecurityException) {
            null
        } catch (_: RuntimeException) {
            null
        }
    }
}

/** Pure calculation helpers used by repository tests and the refresh workflow. */
object UsageTimeCalculator {
    fun newlyObservedMillis(previousMillis: Long, currentMillis: Long): Long {
        val previous = previousMillis.coerceAtLeast(0L)
        val current = currentMillis.coerceAtLeast(0L)
        return (current - previous).coerceAtLeast(0L)
    }

    fun totalNewlyObservedMillis(
        packageNames: Set<String>,
        previousReadings: Map<String, Long>,
        currentReadings: Map<String, Long>
    ): Long = packageNames.fold(0L) { total, packageName ->
        val previous = previousReadings[packageName] ?: return@fold total
        val current = currentReadings[packageName] ?: return@fold total
        val increment = newlyObservedMillis(previous, current)
        if (Long.MAX_VALUE - total < increment) Long.MAX_VALUE else total + increment
    }
}
