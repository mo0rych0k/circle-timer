package com.circle.timer.features.timer.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
internal actual fun NotificationPermissionRequester(
    requestPermission: Boolean,
    onPermissionResult: (Boolean) -> Unit,
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = onPermissionResult,
    )
    LaunchedEffect(requestPermission) {
        if (!requestPermission) return@LaunchedEffect
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            onPermissionResult(true)
            return@LaunchedEffect
        }
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
