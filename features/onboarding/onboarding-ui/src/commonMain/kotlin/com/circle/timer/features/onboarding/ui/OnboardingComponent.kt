package com.circle.timer.features.onboarding.ui

import com.arkivanov.decompose.ComponentContext

public interface OnboardingComponent {
    public fun onContinue()
}

internal class DefaultOnboardingComponent(
    componentContext: ComponentContext,
    private val onFinished: () -> Unit,
) : ComponentContext by componentContext, OnboardingComponent {
    override fun onContinue() {
        onFinished()
    }
}
