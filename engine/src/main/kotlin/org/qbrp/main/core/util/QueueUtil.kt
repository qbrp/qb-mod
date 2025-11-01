package org.qbrp.main.core.util

import java.util.Queue

fun <T : Any> Queue<T>.drain(let: (T) -> Unit) {
    while (isNotEmpty()) {
        let(poll())
    }
}