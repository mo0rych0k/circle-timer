package com.circle.timer.features.timer.domain

public interface TimerWidgetSnapshotRepository {
    public suspend fun getSnapshot(settings: TimerSettings): TimerWidgetSnapshot
    public suspend fun saveSnapshot(snapshot: TimerWidgetSnapshot)
}
