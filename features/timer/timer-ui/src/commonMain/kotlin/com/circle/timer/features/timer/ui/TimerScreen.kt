package com.circle.timer.features.timer.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.circle.timer.features.timer.domain.allowedIntervalsForDuration
import com.circle.timer.features.timer.domain.formatTimerCountdown
import com.circle.timer.features.timer.domain.notchStyleForSecond
import com.circle.timer.features.timer.ui.store.TimerServiceController
import com.circle.timer.features.timer.ui.store.TimerStore
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
public fun TimerScreen(
    component: TimerComponent,
    modifier: Modifier = Modifier,
) {
    val state = component.state.subscribeAsState().value
    val openNotificationSettings = rememberOpenNotificationSettings()
    var hasNotificationPermission by remember { mutableStateOf(TimerServiceController.hasNotificationPermission()) }
    ScreenResumeEffect {
        hasNotificationPermission = TimerServiceController.hasNotificationPermission()
    }
    val shouldShowNotificationStatus = TimerServiceController.isServiceBackedRuntimeEnabled() &&
        !hasNotificationPermission
    NotificationPermissionRequester(
        requestPermission = state.requestNotificationPermission,
        onPermissionResult = { granted ->
            component.onIntent(TimerStore.Intent.NotificationPermissionRequestResult(granted))
        },
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 16.dp),
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Dial(
                    progress = state.progress,
                    totalDurationSeconds = state.appliedSettings.totalDurationSeconds,
                    intervals = state.appliedSettings.enabledIntervals,
                    modifier = Modifier.size(320.dp),
                )
                Column(
                    modifier = Modifier
                        .clickable { component.onIntent(TimerStore.Intent.ToggleRun) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = displayCountdown(
                            elapsedMillis = state.elapsedMillis,
                            totalDurationSeconds = state.appliedSettings.totalDurationSeconds,
                            breakDurationSeconds = state.appliedSettings.breakDurationSeconds,
                            isRunning = state.isRunning,
                            phase = state.phase,
                            preStartCountdownSeconds = state.preStartCountdownSeconds,
                        ),
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Icon(
                        imageVector = if (state.isRunning || state.preStartCountdownSeconds != null) {
                            Icons.Default.Stop
                        } else {
                            Icons.Default.PlayArrow
                        },
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

        Button(
            onClick = { component.onIntent(TimerStore.Intent.OpenEditor) },
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
                .size(width = 92.dp, height = 64.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier.size(42.dp),
            )
        }
        if (shouldShowNotificationStatus) {
            IconButton(
                onClick = openNotificationSettings,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Notification permission required for background timer",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }

    LaunchedEffect(state.snackbarMessage, state.snackbarActionLabel) {
        if (state.snackbarMessage == null) return@LaunchedEffect
        component.onIntent(TimerStore.Intent.ConsumeSnackbar)
    }

    if (state.showEditor) {
        SettingsSheet(
            state = state,
            onDismiss = { component.onIntent(TimerStore.Intent.CloseEditor) },
            onDurationChange = { component.onIntent(TimerStore.Intent.UpdateDuration(it)) },
            onToggleInterval = { component.onIntent(TimerStore.Intent.ToggleInterval(it)) },
            onBreakDurationChange = { component.onIntent(TimerStore.Intent.UpdateBreakDuration(it)) },
            onSave = { component.onIntent(TimerStore.Intent.SaveSettings) },
        )
    }
    if (state.showNotificationPermissionSheet) {
        NotificationPermissionSheet(
            onDismiss = { component.onIntent(TimerStore.Intent.DismissNotificationPermissionSheet) },
            onRequestPermission = { component.onIntent(TimerStore.Intent.RequestNotificationPermission) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationPermissionSheet(
    onDismiss: () -> Unit,
    onRequestPermission: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Allow notifications",
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = "Circle Timer uses notifications to keep the timer visible and reliable when the app is in background.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Button(
                onClick = onRequestPermission,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Request permission")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun Dial(
    progress: Float,
    totalDurationSeconds: Int,
    intervals: Set<Int>,
    modifier: Modifier = Modifier,
) {
    val baseRingColor = MaterialTheme.colorScheme.secondaryContainer
    val progressColor = Color(0xFF00D1FF)
    val notchColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
    Canvas(modifier = modifier) {
        val stroke = 16.dp.toPx()
        val notchGap = 6.dp.toPx()
        val radius = (size.minDimension / 2f) - stroke - 30.dp.toPx()
        val center = Offset(size.width / 2f, size.height / 2f)
        val duration = totalDurationSeconds.coerceAtLeast(1)

        drawArc(
            color = baseRingColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = stroke, cap = StrokeCap.Round),
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2f, radius * 2f),
        )
        if (progress > 0f) {
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2f, radius * 2f),
            )
        }
        val notchRadius = radius + (stroke / 2f) + notchGap
        for (second in 0 until duration) {
            if (second == 0) continue
            val angle = ((second * (360.0 / duration)) - 90.0) * PI / 180.0
            val notchStyle = notchStyleForSecond(second = second)
            val color = notchColorForSecond(
                second = second,
                enabledIntervals = intervals,
                defaultColor = notchColor,
            )
            drawNotch(
                center = center,
                angleRad = angle,
                radius = notchRadius,
                style = notchStyle,
                color = color,
            )
        }
        val anchorRad = -90.0 * PI / 180.0
        val cosA = cos(anchorRad).toFloat()
        val sinA = sin(anchorRad).toFloat()
        val radiusDot = 8.dp.toPx()
        drawCircle(
            color = Color(0xFFE53935),
            radius = radiusDot,
            center = Offset(
                x = center.x + notchRadius * cosA,
                y = center.y + notchRadius * sinA - radiusDot,
            ),
        )
    }
}

private fun intervalColor(intervalSeconds: Int): Color = when (intervalSeconds) {
    1 -> Color(0xFF26A69A)
    5 -> Color(0xFF7E57C2)
    10 -> Color(0xFFFFA726)
    15 -> Color(0xFFFF7043)
    30 -> Color(0xFFEC407A)
    else -> Color(0xFF26A69A)
}

private typealias NotchStyle = com.circle.timer.features.timer.domain.DialNotchStyle

private fun notchColorForSecond(
    second: Int,
    enabledIntervals: Set<Int>,
    defaultColor: Color,
): Color {
    val interval = enabledIntervals.filter { second % it == 0 }.maxOrNull()
    return if (interval == null) defaultColor else intervalColor(interval)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawNotch(
    center: Offset,
    angleRad: Double,
    radius: Float,
    style: NotchStyle,
    color: Color,
) {
    val cosA = cos(angleRad).toFloat()
    val sinA = sin(angleRad).toFloat()
    val radialX = cosA
    val radialY = sinA
    val point = Offset(
        x = center.x + radius * cosA,
        y = center.y + radius * sinA,
    )
    when (style) {
        NotchStyle.TickSmall, NotchStyle.TickMedium, NotchStyle.TickLarge -> {
            val length = when (style) {
                NotchStyle.TickSmall -> 9.dp.toPx()
                NotchStyle.TickMedium -> 15.dp.toPx()
                NotchStyle.TickLarge -> 22.dp.toPx()
            }
            val end = Offset(
                x = center.x + (radius + length) * cosA,
                y = center.y + (radius + length) * sinA,
            )
            drawLine(
                color = color,
                start = point,
                end = end,
                strokeWidth = if (style == NotchStyle.TickSmall) 3.dp.toPx() else 6.dp.toPx(),
            )
        }

        NotchStyle.Triangle -> {
            val tip = Offset(center.x + (radius + 20.dp.toPx()) * cosA, center.y + (radius + 20.dp.toPx()) * sinA)
            val perX = -sinA
            val baseCenter = Offset(center.x + (radius) * cosA, center.y + (radius) * sinA)
            val halfBase = 6.dp.toPx()
            val left = Offset(baseCenter.x + perX * halfBase, baseCenter.y + cosA * halfBase)
            val right = Offset(baseCenter.x - perX * halfBase, baseCenter.y - cosA * halfBase)
            val path = Path().apply {
                moveTo(tip.x, tip.y)
                lineTo(left.x, left.y)
                lineTo(right.x, right.y)
                close()
            }
            drawPath(path = path, color = color)
        }

        NotchStyle.Square -> {
            val side = 11.dp.toPx()
            drawRect(
                color = color,
                topLeft = Offset(point.x - radialX - side / 2f, point.y - radialY),
                size = Size(side, side),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun SettingsSheet(
    state: TimerStore.State,
    onDismiss: () -> Unit,
    onDurationChange: (Int) -> Unit,
    onToggleInterval: (Int) -> Unit,
    onBreakDurationChange: (Int) -> Unit,
    onSave: () -> Unit,
) {
    val availableIntervals = allowedIntervalsForDuration(state.draftSettings.totalDurationSeconds)
    val scroll = rememberScrollState()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scroll)
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("Total Time", style = MaterialTheme.typography.titleMedium)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf(10, 15, 30, 60).forEach { preset ->
                        FilterChip(
                            selected = state.draftSettings.totalDurationSeconds == preset,
                            onClick = { onDurationChange(preset) },
                            label = { Text("${preset}s") },
                        )
                    }
                }
                Text("Notifications", style = MaterialTheme.typography.titleMedium)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    availableIntervals.toList().sorted().forEach { interval ->
                        FilterChip(
                            selected = state.draftSettings.enabledIntervals.contains(interval),
                            onClick = { onToggleInterval(interval) },
                            label = { Text("${interval}s") },
                        )
                    }
                }
                Text("Break Duration", style = MaterialTheme.typography.titleMedium)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf(0, 5, 10, 15, 30, 60).forEach { breakSec ->
                        FilterChip(
                            selected = state.draftSettings.breakDurationSeconds == breakSec,
                            onClick = { onBreakDurationChange(breakSec) },
                            label = { Text(if (breakSec == 0) "None" else "${breakSec}s") },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Text("Save")
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

private fun displayCountdown(
    elapsedMillis: Long,
    totalDurationSeconds: Int,
    breakDurationSeconds: Int,
    isRunning: Boolean,
    phase: TimerStore.TimerPhase,
    preStartCountdownSeconds: Int?,
): String = formatTimerCountdown(
    elapsedMillis = elapsedMillis,
    totalDurationSeconds = totalDurationSeconds,
    breakDurationSeconds = breakDurationSeconds,
    isRunning = isRunning,
    phase = phase.toDomainPhase(),
).let { default ->
    preStartCountdownSeconds?.toString() ?: default
}

private fun TimerStore.TimerPhase.toDomainPhase(): com.circle.timer.features.timer.domain.TimerPhase = when (this) {
    TimerStore.TimerPhase.Active -> com.circle.timer.features.timer.domain.TimerPhase.Active
    TimerStore.TimerPhase.Break -> com.circle.timer.features.timer.domain.TimerPhase.Break
}
