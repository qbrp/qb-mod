package org.qbrp.main.core.utils

class Animation(
    private val duration: Float, // Используем Float для согласованности
    private val animationSpeed: Float = 1.0f,
    val mode: Mode = Mode.ONCE,
    private val progressMode: ProgressMode = ProgressMode.LINEAR
) {
    private var startTime: Long = -1L
    private var _direction: Int = 1
    private var onFinish: (() -> Unit)? = null
    var started: Boolean = false

    var direction: Int
        get() = _direction
        set(value) {
            require(value == 1 || value == -1) { "Direction must be 1 or -1" }
            _direction = value
        }

    val progress: Float
        get() = calculateProgress()

    val isFinished: Boolean
        get() = when (mode) {
            Mode.ONCE -> (direction == 1 && progress >= 1.0f) || (direction == -1 && progress <= 0f)
            else -> false
        }

    fun start() {
        started = true
        startTime = System.currentTimeMillis()
    }

    fun restart() {
        startTime = System.currentTimeMillis()
        started = true
    }

    fun restartForward() {
        direction = 1
        restart()
    }

    fun restartBackward() {
        direction = -1
        restart()
    }

    fun reset() {
        started = false
        startTime = -1L
    }

    fun reverse() {
        direction *= -1
        // Обновляем startTime для плавного перехода в PING_PONG
        if (mode == Mode.PING_PONG) adjustStartTimeOnReverse()
    }

    fun setForward() {
        direction = 1
    }

    fun setBackward() {
        direction = -1
    }

    fun setOnFinishListener(listener: () -> Unit) {
        onFinish = listener
    }

    private fun calculateProgress(): Float {
        if (startTime == -1L) return if (direction == 1) 0f else 1f

        val currentTime = System.currentTimeMillis()
        val elapsed = (currentTime - startTime) * animationSpeed
        var rawProgress = elapsed / duration

        when (mode) {
            Mode.ONCE -> {
                rawProgress = when (direction) {
                    1 -> rawProgress.coerceIn(0f, 1f)
                    -1 -> 1 - rawProgress.coerceIn(0f, 1f)
                    else -> 0f
                }
                // Проверяем завершение
                if ((direction == 1 && rawProgress >= 1f) || (direction == -1 && rawProgress <= 0f)) {
                    started = false
                    onFinish?.invoke()
                }
            }
            Mode.LOOP -> {
                rawProgress %= 1f
                if (rawProgress < 0f) rawProgress += 1f
                rawProgress = if (direction == -1) 1 - rawProgress else rawProgress
            }
            Mode.PING_PONG -> {
                rawProgress %= 2f
                if (rawProgress < 0f) rawProgress += 2f
                rawProgress = if (rawProgress > 1f) 2 - rawProgress else rawProgress
                // Автоматическое изменение направления
                if (currentTime - startTime >= duration / animationSpeed * 2) {
                    startTime = currentTime - (duration / animationSpeed * 2).toLong()
                }
            }
        }

        return applyProgressMode(rawProgress.coerceIn(0f, 1f))
    }

    private fun adjustStartTimeOnReverse() {
        val currentTime = System.currentTimeMillis()
        val elapsed = (currentTime - startTime) * animationSpeed
        val remaining = duration - elapsed % duration
        startTime = (currentTime - remaining / animationSpeed).toLong()
    }

    private fun applyProgressMode(rawProgress: Float): Float {
        return when (progressMode) {
            ProgressMode.LINEAR -> rawProgress
            ProgressMode.EASE_IN -> rawProgress * rawProgress
            ProgressMode.EASE_OUT -> rawProgress * (2 - rawProgress)
            ProgressMode.EASE_IN_OUT -> {
                if (rawProgress < 0.5f) {
                    2 * rawProgress * rawProgress
                } else {
                    (-2 * rawProgress * rawProgress) + (4 * rawProgress) - 1
                }
            }
        }
    }

    enum class Mode { ONCE, LOOP, PING_PONG }
    enum class ProgressMode { LINEAR, EASE_IN, EASE_OUT, EASE_IN_OUT }
}