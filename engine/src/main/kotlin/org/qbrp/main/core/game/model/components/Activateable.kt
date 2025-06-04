package org.qbrp.main.core.game.model.components

interface Activateable {
    var enabled: Boolean
    fun onEnable()
    fun onDisable()
    fun enable() {
        if (!enabled) {
            enabled = true
            onEnable()
        }
    }

    fun disable() {
        if (enabled) {
            onDisable()
            enabled = false
        }
    }
}