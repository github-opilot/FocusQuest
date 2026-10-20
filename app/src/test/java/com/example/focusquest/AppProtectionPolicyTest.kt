package com.example.focusquest

import com.example.focusquest.data.AppProtectionPolicy
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppProtectionPolicyTest {
    private val monitoredApps = setOf("com.example.youtube")

    @Test
    fun blocksSelectedAppWhenWalletIsEmptyAndProtectionIsEnabled() {
        assertTrue(
            AppProtectionPolicy.shouldBlock(
                protectionEnabled = true,
                selectedPackages = monitoredApps,
                foregroundPackage = "com.example.youtube",
                availableTimeMillis = 0L
            )
        )
    }

    @Test
    fun leavesSelectedAppAvailableWhileWalletHasTime() {
        assertFalse(
            AppProtectionPolicy.shouldBlock(
                protectionEnabled = true,
                selectedPackages = monitoredApps,
                foregroundPackage = "com.example.youtube",
                availableTimeMillis = 1L
            )
        )
    }

    @Test
    fun neverBlocksUnselectedAppsOrWhenProtectionIsDisabled() {
        assertFalse(
            AppProtectionPolicy.shouldBlock(
                protectionEnabled = true,
                selectedPackages = monitoredApps,
                foregroundPackage = "com.example.browser",
                availableTimeMillis = 0L
            )
        )
        assertFalse(
            AppProtectionPolicy.shouldBlock(
                protectionEnabled = false,
                selectedPackages = monitoredApps,
                foregroundPackage = "com.example.youtube",
                availableTimeMillis = 0L
            )
        )
    }

    @Test
    fun ignoresMissingForegroundPackage() {
        assertFalse(
            AppProtectionPolicy.shouldBlock(
                protectionEnabled = true,
                selectedPackages = monitoredApps,
                foregroundPackage = null,
                availableTimeMillis = 0L
            )
        )
    }
}
