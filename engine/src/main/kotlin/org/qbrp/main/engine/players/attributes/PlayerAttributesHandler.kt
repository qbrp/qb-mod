package org.qbrp.main.engine.players.attributes

import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.mc.player.setSpeedModifier
import org.qbrp.main.engine.items.components.StateTick
import org.qbrp.main.engine.players.model.components.PlayerComponent
import org.qbrp.main.engine.players.model.components.PlayerComponentInstantiateContext

class PlayerAttributesHandler(ctx: PlayerComponentInstantiateContext): PlayerComponent(ctx), StateTick {
    private var cachedSpeed: Int = getSpeed()

    private fun updateSpeed() = injectContext {
        val currentSpeed = getSpeed()
        if (cachedSpeed != currentSpeed) {
            serverPlayer.setSpeedModifier(currentSpeed.toDouble() / 10)
        }
    }

    private fun getSpeed() = require<PlayerAttributes>().getSpeed()

    override fun tickState() {
        updateSpeed()
    }
}
