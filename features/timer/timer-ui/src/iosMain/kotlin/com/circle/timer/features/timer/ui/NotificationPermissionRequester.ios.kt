package com.circle.timer.features.timer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
internal actual fun NotificationPermissionRequester(
    requestPermission: Boolean,
    onPermissionResult: (Boolean) -> Unit,
) {
    LaunchedEffect(requestPermission) {
        if (requestPermission) onPermissionResult(true)
    }
}
