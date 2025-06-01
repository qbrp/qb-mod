package org.qbrp.main.engine.items.model

import org.qbrp.main.core.game.loop.Tick

class ItemTicker(val storage: ItemStorage): Tick<Unit> {
    override fun tick(context: Unit) {
        storage.getAll().forEach {
            it.tick(Unit)
        }
    }
}