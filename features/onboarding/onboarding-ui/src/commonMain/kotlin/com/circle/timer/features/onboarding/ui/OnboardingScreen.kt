package com.circle.timer.features.onboarding.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
public fun OnboardingScreen(
    component: OnboardingComponent,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Circle Timer",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "Infinite loop timer with visual notches and interval sounds.",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
        )
        Button(onClick = component::onContinue) {
            Text("Start")
        }
    }
}
