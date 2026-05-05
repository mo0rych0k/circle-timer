package com.circle.timer.common.core.di

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.koinApplication

public object IsolatedKoinContext {

    private var _koinApplication: KoinApplication? = null

    public fun koin(): Koin = _koinApplication?.koin!!

    public fun koinApplication(): KoinApplication = _koinApplication!!

    public fun startKoin(
        modules: List<Module>,
        additionalInitialization: KoinApplication.() -> Unit = {},
    ) {
        if (_koinApplication != null) return

        _koinApplication = koinApplication(createEagerInstances = false) {
            allowOverride(true)
            modules(modules)
            additionalInitialization.invoke(this)
        }

        val koin = _koinApplication!!.koin
        koin.declare(ComponentFactory(koin))

        _koinApplication!!.createEagerInstances()
    }

    public fun stop() {
        _koinApplication?.close()
        _koinApplication = null
    }

}

