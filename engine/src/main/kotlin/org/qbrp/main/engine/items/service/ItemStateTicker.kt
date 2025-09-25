package org.qbrp.main.engine.items.service

import net.minecraft.server.world.ServerWorld
import org.qbrp.main.core.game.loop.Tick
import org.qbrp.main.engine.items.ItemsModule
import org.qbrp.main.engine.items.model.ItemObject

class ItemStateTicker(private val repository: ItemRepository) : Tick<ServerWorld> {
    private var tickCounter = 0
    override fun tick(serverWorld: ServerWorld) {
        repository.cache.forEach { it.tickStateExact() }
        tickCounter++
        if (tickCounter % ItemsModule.TICK_RATE != 0) return

        val items = repository.cache.getAll()
        items.forEach { it.tickState() }
    }
}