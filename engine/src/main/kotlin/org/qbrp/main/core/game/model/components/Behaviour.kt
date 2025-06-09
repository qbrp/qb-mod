package org.qbrp.main.core.game.model.components

abstract class Behaviour : Component(), Loadable, Activateable {
    override var enabled: Boolean = false

    override fun load() {
        onLoad()
    }
    override fun unload() {
        disable()
        onUnload()
    }

    override fun onEnable() = Unit
    override fun onDisable() = Unit
}
