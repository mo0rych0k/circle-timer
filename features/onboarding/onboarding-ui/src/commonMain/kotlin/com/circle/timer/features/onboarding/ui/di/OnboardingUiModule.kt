package com.circle.timer.features.onboarding.ui.di

import com.arkivanov.decompose.ComponentContext
import com.circle.timer.common.core.di.ComponentFactory
import com.circle.timer.features.onboarding.ui.DefaultOnboardingComponent
import com.circle.timer.features.onboarding.ui.OnboardingComponent
import org.koin.core.module.Module
import org.koin.dsl.module

public val onboardingUiModule: Module = module { }

public fun ComponentFactory.createOnboardingComponent(
    componentContext: ComponentContext,
    onFinished: () -> Unit,
): OnboardingComponent {
    return DefaultOnboardingComponent(
        componentContext = componentContext,
        onFinished = onFinished,
    )
}
