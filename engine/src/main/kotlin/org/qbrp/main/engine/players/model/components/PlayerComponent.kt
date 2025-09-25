package org.qbrp.main.engine.players.model.components

import org.qbrp.main.core.components.AnyComponent
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.engine.players.model.context.PlayerContext
import java.util.function.Consumer

abstract class PlayerComponent(ctx: PlayerComponentInstantiateContext): PlayerComponentBehaviour, ComponentManager by ctx.componentManager {
    override var state: PlayerState = ctx.componentManager
    protected fun sendMessage(text: String) { serverPlayer.sendChatMessage(text) }

    fun withContext(post: context(PlayerContext) PlayerContext.() -> Unit) {
        with(serverPlayer.context) { post }
    }

    fun injectContext(post: context(PlayerContext) () -> Unit) {
        with(serverPlayer.context) { post }
    }

    override fun forEach(action: Consumer<in AnyComponent>?) {
        super<ComponentManager>.forEach(action)
    }
}