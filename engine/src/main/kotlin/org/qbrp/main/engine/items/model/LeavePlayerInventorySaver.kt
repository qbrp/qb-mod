package org.qbrp.main.engine.items.model

import org.qbrp.main.core.game.saving.AutomaticSaver
import org.qbrp.main.core.game.saving.Saver
import org.qbrp.main.core.game.storage.Storage
import org.qbrp.main.core.mc.player.service.PlayerDisconnectEvent
import org.qbrp.main.core.utils.InventoryUtil

class LeavePlayerInventorySaver(): AutomaticSaver<ServerItemObject> {
    override fun run(
        storage: Storage<ServerItemObject>,
        saver: Saver<ServerItemObject>
    ) {
        storage as ItemStorage<ServerItemObject>
        PlayerDisconnectEvent.EVENT.register { player, _ ->
            InventoryUtil.extractItemsStacks(player.entity.inventory)
                .mapNotNull { storage.getItemObject(it) }
                .forEach { saver.saveObject(it) }
        }
    }
}