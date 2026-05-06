package com.circle.timer.utils.logging.impl

import co.touchlab.kermit.Logger
import com.circle.timer.common.app.info.AppEnvironment
import com.circle.timer.utils.logging.LoggerInterface

public class LoggerImpl(
    private val environment: AppEnvironment,
) : LoggerInterface {


    override fun d(message: () -> String) {
        if (environment.isDebug) {
            Logger.d { message.invoke() }
        }
    }

    override fun e(message: () -> String) {
        Logger.e { message.invoke() }
        // TODO Add logging crash log service for no debug version
    }

    override fun e(throwable: Throwable) {
        Logger.e(throwable = throwable) { "Logging error" }
        // TODO Add logging crash log service for no debug version
    }

    override fun e(throwable: Throwable, message: String) {
        Logger.e(throwable = throwable) { message }
        // TODO Add logging crash log service for no debug version
    }
}