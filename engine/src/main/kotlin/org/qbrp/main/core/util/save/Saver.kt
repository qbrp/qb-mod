package org.qbrp.main.core.util.save

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.qbrp.main.core.components.serialization.Identifiable

fun interface Saver<T : Identifiable> {
    fun saveObject(obj: T)
}

fun <T : Identifiable> CoroutineSaver(method: suspend (T) -> Unit): Saver<T> {
    val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    return Saver { coroutineScope.launch { method(it) } }
}