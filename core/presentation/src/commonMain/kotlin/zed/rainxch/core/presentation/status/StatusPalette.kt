package zed.rainxch.core.presentation.status

import androidx.compose.ui.graphics.Color

fun statusColors(dark: Boolean): StatusColors = if (dark) StatusColorsDark else StatusColorsLight

private val StatusColorsLight =
    StatusColors(
        issueOpen = Color(0xFF118553),
        issueClosed = Color(0xFF8250DF),
        pullOpen = Color(0xFF118553),
        pullMerged = Color(0xFF8250DF),
        pullClosed = Color(0xFFD63A40),
        severityCritical = Color(0xFFB3261E),
        severityHigh = Color(0xFFD63A40),
        severityMedium = Color(0xFFBC4C00),
        severityLow = Color(0xFF9A6700),
        severityUnknown = Color(0xFF6E7781),
        statusReady = Color(0xFF118553),
        statusWarning = Color(0xFF9A6700),
        statusError = Color(0xFFD63A40),
        protectionSignature = Color(0xFF3F6AE8),
        protectionPrivileged = Color(0xFFBC4C00),
    )

private val StatusColorsDark =
    StatusColors(
        issueOpen = Color(0xFF6BE3B0),
        issueClosed = Color(0xFFA371F7),
        pullOpen = Color(0xFF6BE3B0),
        pullMerged = Color(0xFFA371F7),
        pullClosed = Color(0xFFFF8A8F),
        severityCritical = Color(0xFFFF6B5E),
        severityHigh = Color(0xFFFF8A8F),
        severityMedium = Color(0xFFDB6D28),
        severityLow = Color(0xFFD29922),
        severityUnknown = Color(0xFF8B949E),
        statusReady = Color(0xFF6BE3B0),
        statusWarning = Color(0xFFD29922),
        statusError = Color(0xFFFF8A8F),
        protectionSignature = Color(0xFF8FB0FF),
        protectionPrivileged = Color(0xFFDB6D28),
    )
