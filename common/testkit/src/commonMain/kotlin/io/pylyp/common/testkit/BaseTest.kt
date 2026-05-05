package com.circle.timer.common.testkit

import com.circle.timer.common.core.di.IsolatedKoinContext
import com.circle.timer.core.threading.test.di.testDispatchersModule
import com.circle.timer.core.threading.test.testDispatcher
import org.koin.core.module.Module
import org.koin.dsl.module
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json

/**
 * Extend this from your test class and call [beforeTest] / [afterTest]
 * from `@BeforeTest` / `@AfterTest` in the concrete test.
 */
public abstract class BaseTest {

    /**
     * Return app/test modules for this test.
     * `DispatcherProvider` is pre-registered by [BaseTest] before these modules.
     */
    protected open fun koinModules(): List<Module> = listOf(
        module {
            single {
                Json {
                    ignoreUnknownKeys = true
                    /**
                     * For pretty indents
                     */
                    prettyPrint = true
                    explicitNulls = false
                }
            }
        },
        testDispatchersModule,
    )

    public open fun beforeTest() {
        Dispatchers.setMain(testDispatcher)

        IsolatedKoinContext.startKoin(
            modules = koinModules() + getDiModules(),
        )
    }

    public abstract fun getDiModules(): List<Module>

    public open fun afterTest() {
        IsolatedKoinContext.stop()
        Dispatchers.resetMain()
    }
}

