package org.qbrp.main.engine.items.model

import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.game.saving.AutomaticSaver
import org.qbrp.main.core.game.saving.Saver
import org.qbrp.main.core.game.storage.Storage
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.items.service.ItemStorage
import org.qbrp.main.engine.players.model.events.PlayerDisconnectEvent
import org.qbrp.main.engine.players.inventory.CharacterInventory

class LeavePlayerInventorySaver(val eventContext: EngineModule): AutomaticSaver<ServerItemObject> {
    override fun run(
        storage: Storage<ServerItemObject>,
        saver: Saver<ServerItemObject>
    ) {
        storage as ItemStorage<ServerItemObject>
        eventContext.apply {
            withEventContext {
                on<PlayerDisconnectEvent> {
                    it.player.filterIsInstance<CharacterInventory>()
                        .map { container -> container.getEntries() }
                        .filterIsInstance<ServerItemObject>()
                        .forEach { item -> saver.saveObject(item) }
                }
            }
        }
    }
}