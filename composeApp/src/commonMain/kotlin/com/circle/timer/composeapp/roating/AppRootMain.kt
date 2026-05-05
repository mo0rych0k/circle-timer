package com.circle.timer.composeapp.roating

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.circle.timer.features.onboarding.ui.OnboardingScreen
import com.circle.timer.features.timer.ui.TimerScreen

@Composable
public fun AppRootMain(rootComponent: AppRootComponent) {
    val stack = rootComponent.stack.subscribeAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Children(
            stack = stack.value,
            animation = stackAnimation(fade() + scale()),
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .weight(1f),
        ) {
            when (val instance = it.instance) {
                is AppRootComponent.Child.Onboarding -> OnboardingScreen(component = instance.component)
                is AppRootComponent.Child.Timer -> TimerScreen(component = instance.component)
            }
        }
    }
}
