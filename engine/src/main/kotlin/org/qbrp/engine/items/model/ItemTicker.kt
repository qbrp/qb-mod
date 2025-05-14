package org.qbrp.engine.items.model

import net.minecraft.server.world.ServerWorld
import org.qbrp.core.game.model.tick.Tick

class ItemTicker(val storage: ItemStorage): Tick<ServerWorld> {
    override fun tick(context: ServerWorld) {
        TODO("Not yet implemented")
    }
}