package org.qbrp.main.engine.items.service

import org.qbrp.main.core.events.on
import org.qbrp.main.core.util.save.Saver
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.players.inventory.CharacterInventory
import org.qbrp.main.engine.players.model.PlayerDisconnectEvent

fun runLeavePlayerItemSaver(itemStorage: ItemStorage<ServerItemObject>, saver: Saver<ServerItemObject>) {
    on<PlayerDisconnectEvent> {
        player.filterIsInstance<CharacterInventory>()
            .map { container -> container.getEntries() }
            .filterIsInstance<ServerItemObject>()
            .forEach { item -> saver.saveObject(item) }
    }
}