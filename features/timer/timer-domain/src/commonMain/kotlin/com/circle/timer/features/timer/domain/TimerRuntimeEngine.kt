package com.circle.timer.features.timer.domain

public enum class RuntimePhase {
    Active,
    Break,
}

public data class RuntimeConfig(
    val totalDurationSeconds: Int,
    val breakDurationSeconds: Int,
    val enabledIntervals: Set<Int>,
    val countdownLast5TimerEnabled: Boolean,
    val countdownLast5BreakEnabled: Boolean,
)

public data class RuntimeState(
    val isRunning: Boolean,
    val phase: RuntimePhase,
    val elapsedMillis: Long,
    val phaseStartedEpochMillis: Long,
    val cycleIndex: Long,
)

public sealed interface RuntimeCue {
    public data class IntervalTick(val intervalSeconds: Int) : RuntimeCue
    public data class CountdownTick(val phase: RuntimePhase, val secondsRemaining: Int) : RuntimeCue
    public data object PhaseComplete : RuntimeCue
}

public data class RuntimeStepResult(
    val state: RuntimeState,
    val cue: RuntimeCue?,
)

public class TimerRuntimeEngine(
    private var config: RuntimeConfig,
) {
    private var state: RuntimeState = RuntimeState(
        isRunning = false,
        phase = RuntimePhase.Active,
        elapsedMillis = 0L,
        phaseStartedEpochMillis = 0L,
        cycleIndex = 0L,
    )

    public fun updateConfig(newConfig: RuntimeConfig) {
        config = newConfig
    }

    public fun restore(runningState: RuntimeState) {
        state = runningState
    }

    public fun start(nowEpochMillis: Long): RuntimeState {
        state = RuntimeState(
            isRunning = true,
            phase = RuntimePhase.Active,
            elapsedMillis = 0L,
            phaseStartedEpochMillis = nowEpochMillis,
            cycleIndex = state.cycleIndex + 1L,
        )
        return state
    }

    public fun stop(): RuntimeState {
        state = RuntimeState(
            isRunning = false,
            phase = RuntimePhase.Active,
            elapsedMillis = 0L,
            phaseStartedEpochMillis = state.phaseStartedEpochMillis,
            cycleIndex = state.cycleIndex,
        )
        return state
    }

    public fun step(nowEpochMillis: Long): RuntimeStepResult {
        if (!state.isRunning) return RuntimeStepResult(state = state, cue = null)

        val phaseDurationSeconds = phaseDurationSeconds(state.phase)
        if (phaseDurationSeconds <= 0) {
            val nextPhase = RuntimePhase.Active
            state = state.copy(
                phase = nextPhase,
                elapsedMillis = 0L,
                phaseStartedEpochMillis = nowEpochMillis,
                cycleIndex = state.cycleIndex + 1L,
            )
            return RuntimeStepResult(state = state, cue = RuntimeCue.PhaseComplete)
        }

        val durationMillis = phaseDurationSeconds * 1000L
        val previousElapsed = state.elapsedMillis
        val nextElapsed = (nowEpochMillis - state.phaseStartedEpochMillis).coerceAtLeast(0L)

        if (nextElapsed >= durationMillis) {
            val nextPhase = if (state.phase == RuntimePhase.Active && config.breakDurationSeconds > 0) {
                RuntimePhase.Break
            } else {
                RuntimePhase.Active
            }
            state = state.copy(
                phase = nextPhase,
                elapsedMillis = 0L,
                phaseStartedEpochMillis = nowEpochMillis,
                cycleIndex = state.cycleIndex + 1L,
            )
            return RuntimeStepResult(state = state, cue = RuntimeCue.PhaseComplete)
        }

        state = state.copy(elapsedMillis = nextElapsed)
        val cue = cueForBoundaryCross(
            phase = state.phase,
            previousMillis = previousElapsed,
            elapsedMillis = nextElapsed,
            durationMillis = durationMillis,
            enabledIntervals = if (state.phase == RuntimePhase.Active) config.enabledIntervals else emptySet(),
            countdownLast5Enabled = when (state.phase) {
                RuntimePhase.Active -> config.countdownLast5TimerEnabled
                RuntimePhase.Break -> config.countdownLast5BreakEnabled
            },
        )
        return RuntimeStepResult(state = state, cue = cue)
    }

    private fun phaseDurationSeconds(phase: RuntimePhase): Int = when (phase) {
        RuntimePhase.Active -> config.totalDurationSeconds
        RuntimePhase.Break -> config.breakDurationSeconds
    }
}

private fun cueForBoundaryCross(
    phase: RuntimePhase,
    previousMillis: Long,
    elapsedMillis: Long,
    durationMillis: Long,
    enabledIntervals: Set<Int>,
    countdownLast5Enabled: Boolean,
): RuntimeCue? {
    val prevSec = previousMillis.floorSeconds()
    val nowSec = elapsedMillis.floorSeconds()
    if (nowSec <= prevSec) return null

    var selected: RuntimeCue? = null
    for (sec in (prevSec + 1)..nowSec) {
        if (sec <= 0) continue
        val boundaryMillis = sec * 1000L
        if (boundaryMillis >= durationMillis) break
        val remaining = ((durationMillis - boundaryMillis) / 1000L).toInt()
        val cue = when {
            countdownLast5Enabled && remaining in 1..5 ->
                RuntimeCue.CountdownTick(phase = phase, secondsRemaining = remaining)

            enabledIntervals.any { interval -> interval > 0 && sec % interval == 0L } -> {
                val matched = enabledIntervals.filter { it > 0 && sec % it.toLong() == 0L }
                val major = matched.filter { it >= 3 }.maxOrNull() ?: matched.maxOrNull() ?: 1
                RuntimeCue.IntervalTick(intervalSeconds = major)
            }

            else -> null
        }
        if (cue != null) {
            selected = cue
        }
    }
    return selected
}

private fun Long.floorSeconds(): Long = this / 1000L
