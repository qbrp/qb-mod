package org.qbrp.core.game.model.components

abstract class Behaviour : Component(), Loadable, Activateable {
    protected var enabled: Boolean = false

    open fun onLoad() = Unit
    open fun onUnload() = Unit

    override fun load() {
        enable()
        onLoad()
    }
    override fun unload() {
        disable()
        onUnload()
    }

    open fun onEnable() = Unit
    open fun onDisable() = Unit

    override fun enable() {
        if (!enabled) {
            enabled = true
            onEnable()
        }
    }

    override fun disable() {
        if (enabled) {
            onDisable()
            enabled = false
        }
    }
}
