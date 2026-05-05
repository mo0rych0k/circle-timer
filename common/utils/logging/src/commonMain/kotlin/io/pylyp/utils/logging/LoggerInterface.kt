package com.circle.timer.utils.logging

public interface LoggerInterface {

    public fun d(message: () -> String)
    public fun e(message: () -> String)
    public fun e(throwable: Throwable)
    public fun e(throwable: Throwable, message: String)

}
