package com.circle.timer.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import com.circle.timer.common.core.di.ComponentFactory
import com.circle.timer.common.core.di.IsolatedKoinContext
import com.circle.timer.composeapp.App
import com.circle.timer.composeapp.di.createAppRootComponent
import org.koin.core.component.KoinComponent

class MainActivity : ComponentActivity(), KoinComponent {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val componentFactory: ComponentFactory by IsolatedKoinContext.koin().inject()

        val root =
            componentFactory.createAppRootComponent(
                componentContext = defaultComponentContext(),
            )

        setContent {
            App(root)
        }
    }
}
