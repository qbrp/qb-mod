package org.qbrp.main.core.game.model.components

import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer
import org.qbrp.main.core.utils.networking.messages.types.ReceiveContent

abstract class Behaviour : Component(), Loadable, Activateable {
    override var enabled: Boolean = false

    open fun onLoad() = Unit
    open fun onUnload() = Unit

    override fun load() {
        onLoad()
    }
    override fun unload() {
        disable()
        onUnload()
    }

    override fun onEnable() = Unit
    override fun onDisable() = Unit

    open fun onMessage(id: String, content: ClusterViewer) {}
}
