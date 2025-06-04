package org.qbrp.main.engine.items.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.qbrp.main.core.assets.prefabs.Prefab
import org.qbrp.main.core.game.model.objects.BaseObject
import org.qbrp.main.core.game.serialization.Serializer
import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.game.GameAPI
import org.qbrp.main.engine.items.ItemsModule
import org.qbrp.main.engine.synchronization.`interface`.Synchronizer

class ItemFabric(val gameAPI: GameAPI, val module: ItemsModule, val sender: Synchronizer): Serializer<ServerItemObject>() {
    override fun toJson(t: ServerItemObject): String {
        return Json.encodeToString(ItemJsonField(t.id, t.state))
    }

    fun fromJson(json: String): ServerItemObject {
        return ServerItemObject(module)
    }

    fun newInstance(player: PlayerObject): BaseObject {
        return ServerItemObject(module)
    }

    fun newInstanceFromPrefab(tag: Prefab.Tag, player: PlayerObject, lifecycle: ItemLifecycle): BaseObject {
        return gameAPI.instantiate<ServerItemObject>(ServerItemObject(module), tag, lifecycle).apply {
            give(player)
        }
    }
}