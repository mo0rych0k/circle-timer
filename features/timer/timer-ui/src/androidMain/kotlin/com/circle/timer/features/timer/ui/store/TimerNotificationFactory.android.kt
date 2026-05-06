package com.circle.timer.features.timer.ui.store

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.circle.timer.features.timer.domain.RuntimePhase
import com.circle.timer.features.timer.ui.R

internal object TimerNotificationFactory {
    const val CHANNEL_ID: String = "timer_foreground_channel"
    const val NOTIFICATION_ID: Int = 4310

    fun ensureChannel(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(CHANNEL_ID) != null) return
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                "Timer",
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = "Foreground timer updates"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setSound(null, null)
                enableVibration(false)
                vibrationPattern = longArrayOf(0L)
            },
        )
    }

    fun build(
        context: Context,
        phase: RuntimePhase,
        remainingSeconds: Int,
        phaseDurationSeconds: Int,
        isRunning: Boolean,
    ): Notification {
        val phaseLabel = if (phase == RuntimePhase.Active) "Action" else "Break"
        val safeDuration = phaseDurationSeconds.coerceAtLeast(1)
        val safeRemaining = remainingSeconds.coerceIn(0, safeDuration)
        val completed = (safeDuration - safeRemaining).coerceIn(0, safeDuration)
        val title = "Timer"
        val currentTime = "$phaseLabel ${safeRemaining.toSecondsLabel()}"
        val totalTime = "Total ${safeDuration.toSecondsLabel()}"
        val startIntent = PendingIntent.getService(
            context,
            101,
            Intent(context, TimerForegroundService::class.java).apply {
                action = TimerForegroundService.ACTION_START_FROM_NOTIFICATION
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val stopIntent = PendingIntent.getService(
            context,
            102,
            Intent(context, TimerForegroundService::class.java).apply {
                action = TimerForegroundService.ACTION_STOP_TIMER
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val contentIntent = launchIntent?.let {
            PendingIntent.getActivity(
                context,
                104,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }
        val progressValue = if (isRunning) completed else 0
        val remoteView = RemoteViews(context.packageName, R.layout.timer_notification_compact).apply {
            setTextViewText(R.id.notification_title, title)
            setTextViewText(R.id.notification_time, currentTime)
            setTextViewText(R.id.notification_time_total, totalTime)
            setProgressBar(
                R.id.notification_progress,
                safeDuration,
                progressValue,
                false,
            )
            setTextViewText(
                R.id.notification_play_stop_label,
                if (isRunning) "STOP" else "PLAY",
            )
            setImageViewResource(
                R.id.notification_play_stop_icon,
                if (isRunning) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
            )
            setOnClickPendingIntent(
                R.id.notification_play_stop_button,
                if (isRunning) stopIntent else startIntent,
            )
            setViewVisibility(R.id.notification_play_stop_button, android.view.View.VISIBLE)
            setInt(
                R.id.notification_root,
                "setBackgroundColor",
                resolveThemeNotificationColor(context),
            )
        }
        val bigRemoteView = RemoteViews(context.packageName, R.layout.timer_notification_custom).apply {
            setTextViewText(R.id.notification_title, title)
            setTextViewText(R.id.notification_time, currentTime)
            setTextViewText(R.id.notification_time_total, totalTime)
            setProgressBar(
                R.id.notification_progress,
                safeDuration,
                progressValue,
                false,
            )
            setTextViewText(
                R.id.notification_play_stop_label,
                if (isRunning) "STOP" else "PLAY",
            )
            setImageViewResource(
                R.id.notification_play_stop_icon,
                if (isRunning) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
            )
            setOnClickPendingIntent(
                R.id.notification_play_stop_button,
                if (isRunning) stopIntent else startIntent,
            )
            setViewVisibility(R.id.notification_play_stop_button, android.view.View.VISIBLE)
            setInt(
                R.id.notification_root,
                "setBackgroundColor",
                resolveThemeNotificationColor(context),
            )
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(title)
            .setContentText("$currentTime • $totalTime")
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(remoteView)
            .setCustomBigContentView(bigRemoteView)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setColor(resolveThemeNotificationColor(context))
            .setColorized(true)
            .setContentIntent(contentIntent)
            .setAutoCancel(false)
            .setCategory(if (isRunning) NotificationCompat.CATEGORY_ALARM else NotificationCompat.CATEGORY_SERVICE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            builder.setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
        }

        return builder.build().apply {
            flags = flags or Notification.FLAG_NO_CLEAR
        }
    }
}

private fun resolveThemeNotificationColor(context: Context): Int {
    val typedValue = TypedValue()
    val resolved = context.theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
    if (!resolved) return 0xFF1E88E5.toInt()
    return if (typedValue.resourceId != 0) {
        ContextCompat.getColor(context, typedValue.resourceId)
    } else {
        typedValue.data
    }
}

private fun Int.toSecondsLabel(): String {
    val safe = coerceAtLeast(0)
    return "${safe}s"
}
