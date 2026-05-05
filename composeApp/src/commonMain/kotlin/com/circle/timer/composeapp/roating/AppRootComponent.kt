package com.circle.timer.composeapp.roating

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.circle.timer.common.core.di.ComponentFactory
import com.circle.timer.composeapp.roating.AppRootComponent.Child.Onboarding
import com.circle.timer.composeapp.roating.AppRootComponent.Child.Timer
import com.circle.timer.features.onboarding.ui.OnboardingComponent
import com.circle.timer.features.onboarding.ui.di.createOnboardingComponent
import com.circle.timer.features.timer.domain.usecase.CompleteOnboardingUseCase
import com.circle.timer.features.timer.domain.usecase.IsOnboardingCompletedUseCase
import com.circle.timer.features.timer.ui.TimerComponent
import com.circle.timer.features.timer.ui.di.createTimerComponent
import org.koin.core.component.get
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

public interface AppRootComponent {
    public val stack: Value<ChildStack<*, Child>>

    public sealed class Child {
        public class Onboarding(public val component: OnboardingComponent) : Child()
        public class Timer(public val component: TimerComponent) : Child()
    }
}

@OptIn(DelicateDecomposeApi::class)
public class DefaultAppRootComponent(
    componentContext: ComponentContext,
    private val componentFactory: ComponentFactory,
) : ComponentContext by componentContext, AppRootComponent {

    private val navigation = StackNavigation<AppRootConfig>()
    private val isOnboardingCompletedUseCase: IsOnboardingCompletedUseCase = componentFactory.get()
    private val completeOnboardingUseCase: CompleteOnboardingUseCase = componentFactory.get()

    override val stack: Value<ChildStack<*, AppRootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = AppRootConfig.serializer(),
            initialConfiguration = if (runBlocking { isOnboardingCompletedUseCase() }) {
                AppRootConfig.Timer
            } else {
                AppRootConfig.Onboarding
            },
            handleBackButton = true,
            childFactory = ::child,
        )

    private fun child(
        config: AppRootConfig,
        componentContext: ComponentContext,
    ): AppRootComponent.Child =
        when (config) {
            AppRootConfig.Onboarding -> Onboarding(
                component = componentFactory.createOnboardingComponent(
                    componentContext = componentContext,
                    onFinished = {
                        runBlocking { completeOnboardingUseCase() }
                        navigation.push(AppRootConfig.Timer)
                    },
                ),
            )

            AppRootConfig.Timer -> Timer(component = componentFactory.createTimerComponent(componentContext))
        }

    @Serializable
    internal sealed interface AppRootConfig {
        @Serializable
        data object Onboarding : AppRootConfig
        @Serializable
        data object Timer : AppRootConfig
    }
}
