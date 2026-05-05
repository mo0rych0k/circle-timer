package com.circle.timer.core.navigation

public sealed interface AppFeature {
    public data object Onboarding : AppFeature
    public data object Timer : AppFeature
}
