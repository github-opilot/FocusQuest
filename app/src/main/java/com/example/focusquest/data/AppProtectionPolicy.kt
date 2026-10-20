package com.example.focusquest.data

/** Pure blocking rule shared by the accessibility service and JVM tests. */
object AppProtectionPolicy {
    fun shouldBlock(
        protectionEnabled: Boolean,
        selectedPackages: Set<String>,
        foregroundPackage: String?,
        availableTimeMillis: Long
    ): Boolean = protectionEnabled &&
        foregroundPackage != null &&
        foregroundPackage in selectedPackages &&
        availableTimeMillis <= 0L
}
