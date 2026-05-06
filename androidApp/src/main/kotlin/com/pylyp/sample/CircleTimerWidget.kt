package com.circle.timer.android

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.circle.timer.common.core.di.IsolatedKoinContext
import com.circle.timer.features.timer.domain.TimerPhase
import com.circle.timer.features.timer.domain.TimerSettingsRepository
import com.circle.timer.features.timer.domain.TimerWidgetSnapshot
import com.circle.timer.features.timer.domain.TimerWidgetSnapshotRepository

class CircleTimerWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val koin = IsolatedKoinContext.koin()
        val settingsRepository = koin.get<TimerSettingsRepository>()
        val snapshotRepository = koin.get<TimerWidgetSnapshotRepository>()
        val settings = settingsRepository.getTimerSettings()
        val snapshot = snapshotRepository.getSnapshot(settings)
        val visuals = snapshot.resolveVisuals(System.currentTimeMillis())

        provideContent {
            TimerWidgetContent(
                progress = visuals.progress,
                timeLabel = visuals.remainingSeconds.toString(),
                isRunning = visuals.isRunning,
            )
        }
    }
}

class CircleTimerWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CircleTimerWidget()
}

private class ToggleTimerWidgetAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val koin = IsolatedKoinContext.koin()
        val settingsRepository = koin.get<TimerSettingsRepository>()
        val snapshotRepository = koin.get<TimerWidgetSnapshotRepository>()
        val settings = settingsRepository.getTimerSettings()
        val current = snapshotRepository.getSnapshot(settings)
        val next = if (current.isRunning) {
            com.circle.timer.features.timer.domain.idleTimerWidgetSnapshot(
                totalDurationSeconds = settings.totalDurationSeconds,
                breakDurationSeconds = settings.breakDurationSeconds,
            )
        } else {
            TimerWidgetSnapshot(
                isRunning = true,
                phase = TimerPhase.Active,
                phaseStartedEpochMillis = System.currentTimeMillis(),
                totalDurationSeconds = settings.totalDurationSeconds,
                breakDurationSeconds = settings.breakDurationSeconds,
            )
        }
        snapshotRepository.saveSnapshot(next)
        CircleTimerWidget().update(context, glanceId)
    }
}

@Composable
private fun TimerWidgetContent(
    progress: Float,
    timeLabel: String,
    isRunning: Boolean,
) {
    val widgetBackground = ColorProvider(day = Color(0xFF101418), night = Color(0xFF101418))
    val primaryText = ColorProvider(day = Color.White, night = Color.White)
    val secondaryText = ColorProvider(day = Color(0xFF9E9E9E), night = Color(0xFF9E9E9E))
    val actionText = ColorProvider(day = Color(0xFFD3D3D3), night = Color(0xFFD3D3D3))
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(widgetBackground)
            .clickable(actionRunCallback<ToggleTimerWidgetAction>()),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = timeLabel,
                style = TextStyle(color = primaryText, fontWeight = FontWeight.Bold),
            )
            Text(
                text = "Progress ${(progress * 100).toInt()}%",
                style = TextStyle(color = secondaryText),
            )
            Text(
                text = if (isRunning) "Stop" else "Play",
                style = TextStyle(color = actionText),
            )
        }
    }
}
