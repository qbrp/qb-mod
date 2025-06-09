package org.qbrp.main.core.game.model.components

interface Loadable {
    fun load()
    fun onLoad() {}
    fun unload()
    fun onUnload() {}
}