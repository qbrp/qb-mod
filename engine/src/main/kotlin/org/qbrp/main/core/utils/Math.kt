package org.qbrp.main.core.utils

fun <T> Iterable<T>.sumOf(selector: (T) -> Float): Float {
    return map { selector(it) }.sum()
}