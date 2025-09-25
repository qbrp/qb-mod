package org.qbrp.client.render.animation

import kotlin.math.max
import kotlin.math.min

class Animation(
    var from: Float,
    var to: Float,
    val durationMillis: Float = 300f,
    var easing: (Float) -> Float = Easing.LINEAR,
    val repeat: Int = 0,               // 0 — один проход; -1 — бесконечно
    val yoyo: Boolean = false,
    val startDelayMillis: Float = 0f,
    val lerp: (Float, Float, Float) -> Float,
    val onStart: (() -> Unit)? = null,
    var onUpdate: ((Float) -> Unit)? = null,
    val onEnd: (() -> Unit)? = null,
    val onCancel: (() -> Unit)? = null
) {
    enum class State {
        IDLE, RUNNING, PAUSED, ENDED, CANCELED
    }

    var state: State = State.IDLE
        private set

    private var delayLeftMs: Float = startDelayMillis
    private var elapsedMs: Float = 0f
    private var passIndex: Int = 0
    private var forward: Boolean = true

    var progress: Float = 0f
        private set
    var value: Float = from
        private set

    val isFinished: Boolean get() = state == State.ENDED || state == State.CANCELED
    val isRunning: Boolean get() = state == State.RUNNING
    val isPaused: Boolean get() = state == State.PAUSED

    fun start(forward: Boolean = true): Animation {
        if (state != State.IDLE && state != State.ENDED && state != State.CANCELED) return this
        this.forward = forward
        state = State.RUNNING
        delayLeftMs = startDelayMillis
        elapsedMs = 0f
        passIndex = 0
        progress = 0f
        value = from
        onStart?.invoke()
        onUpdate?.invoke(value)
        return this
    }

    fun cancel() {
        if (state == State.CANCELED || state == State.ENDED) return
        state = State.CANCELED
        onCancel?.invoke()
    }

    fun pause() {
        if (state == State.RUNNING) state = State.PAUSED
    }

    fun resume() {
        if (state == State.PAUSED) state = State.RUNNING
    }

    fun tick(dtMillis: Float) {
        if (state != State.RUNNING) return
        if (dtMillis <= 0) return

        if (delayLeftMs > 0) {
            val take = min(dtMillis, delayLeftMs)
            delayLeftMs -= take
            if (delayLeftMs > 0) return
            onUpdate?.invoke(value)
            if (dtMillis - take <= 0) return
            // остаток времени пойдёт в сам проход
            internalTick(dtMillis - take)
            return
        }

        internalTick(dtMillis)
    }

    private fun internalTick(dt: Float) {
        // накапливаем время прохода
        elapsedMs = (elapsedMs + dt).coerceAtMost(durationMillis)
        val t = if (durationMillis <= 0) 1f else (elapsedMs.toDouble() / durationMillis.toDouble()).toFloat()
        progress = t

        val eased = easing(t)
        value = if (forward) lerp(from, to, eased) else lerp(to, from, eased)
        onUpdate?.invoke(value)

        // завершение прохода
        if (elapsedMs >= durationMillis) {
            passIndex++
            val totalPasses = if (repeat < 0) Int.MAX_VALUE else (repeat + 1)
            if (yoyo) forward = !forward

            if (passIndex < totalPasses) {
                // новый проход
                elapsedMs = 0f
                progress = 0f
                // при йойо стартовое значение меняется автоматически через forward
            } else {
                // финал
                state = State.ENDED
                onEnd?.invoke()
            }
        }
    }

    fun seek(progress01: Float) {
        val clamped = progress01.coerceIn(0f, 1f)
        progress = clamped
        elapsedMs = durationMillis * clamped
        val eased = easing(clamped)
        value = if (forward) lerp(from, to, eased) else lerp(to, from, eased)
        onUpdate?.invoke(value)
    }

    fun reset(keepDelay: Boolean = true) {
        state = State.IDLE
        delayLeftMs = if (keepDelay) startDelayMillis else 0f
        elapsedMs = 0f
        passIndex = 0
        forward = true
        progress = 0f
        value = from
    }

    companion object {
        fun of(
            from: Float,
            to: Float,
            durationMillis: Float = 300f,
            easing: (Float) -> Float = Easing.LINEAR,
            repeat: Int = 0,
            yoyo: Boolean = false,
            startDelayMillis: Float = 0f,
            onUpdate: (Float) -> Unit,
            onStart: (() -> Unit)? = null,
            onEnd: (() -> Unit)? = null,
            onCancel: (() -> Unit)? = null,
        ): Animation = Animation(
            from = from,
            to = to,
            durationMillis = durationMillis,
            easing = easing,
            repeat = repeat,
            yoyo = yoyo,
            startDelayMillis = startDelayMillis,
            lerp = { a, b, t -> a + (b - a) * t },
            onStart = onStart,
            onUpdate = onUpdate,
            onEnd = onEnd,
            onCancel = onCancel
        )
    }
}