package org.qbrp.main.core.util.lifecycle

interface Lifecycle<T> {
    fun onCreated(obj: T)
    fun unload(obj: T)
    fun save(obj: T)
}