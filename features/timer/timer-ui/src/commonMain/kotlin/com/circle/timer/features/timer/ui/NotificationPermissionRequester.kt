package com.circle.timer.features.timer.ui

import androidx.compose.runtime.Composable

@Composable
internal expect fun NotificationPermissionRequester(
    requestPermission: Boolean,
    onPermissionResult: (Boolean) -> Unit,
)
