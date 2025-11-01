package org.qbrp.main.core.util.math

fun <T> Iterable<T>.sumOf(selector: (T) -> Float): Float {
    return map { selector(it) }.sum()
}

fun lerp(start: Double, end: Double, t: Double): Double {
    return start * (1 - t) + end * t
}