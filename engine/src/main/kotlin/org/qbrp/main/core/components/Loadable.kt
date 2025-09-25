package org.qbrp.main.core.components

interface Loadable {
    fun load() { onLoad() }
    fun onLoad() {}
    fun unload() { onUnload()}
    fun onUnload() {}
}