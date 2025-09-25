package org.qbrp.client.render.animation

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow

object Easing {
    val LINEAR: (Float) -> Float = { t -> t }

    val EASE_IN_QUAD: (Float) -> Float = { t -> t * t }
    val EASE_OUT_QUAD: (Float) -> Float = { t -> t * (2 - t) }
    val EASE_IN_OUT_QUAD: (Float) -> Float = { t ->
        if (t < 0.5f) 2f * t * t else -1f + (4f - 2f * t) * t
    }

    val EASE_IN_CUBIC: (Float) -> Float = { t -> t * t * t }
    val EASE_OUT_CUBIC: (Float) -> Float = { t -> 1f + (t - 1f).pow(3) }
    val EASE_IN_OUT_CUBIC: (Float) -> Float = { t ->
        if (t < 0.5f) 4f * t * t * t else (t - 1f).let { 1f + 4f * it * it * it }
    }

    val EASE_IN_OUT_SINE: (Float) -> Float = { t -> (-(cos(PI.toFloat() * t) - 1f) / 2f) }

    val EASE_OUT_BACK: (Float) -> Float = { t ->
        val c1 = 1.70158f
        val c3 = c1 + 1f
        1f + c3 * (t - 1f).pow(3) + c1 * (t - 1f).pow(2)
    }

    val EASE_OUT_EXPO: (Float) -> Float = { t ->
        if (t == 1f) 1f else 1f - 2f.pow(-10f * t)
    }
}
