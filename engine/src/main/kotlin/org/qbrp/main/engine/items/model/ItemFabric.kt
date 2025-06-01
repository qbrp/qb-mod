package org.qbrp.main.engine.items.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.main.core.assets.prefabs.Prefab
import org.qbrp.main.core.game.model.objects.BaseObject
import org.qbrp.main.core.game.serialization.Serializer
import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.game.GameAPI
import org.qbrp.main.engine.items.ItemsModule

class ItemFabric(val gameAPI: GameAPI): Serializer<ItemObject>() {
    override fun toJson(t: ItemObject): String {
        return Json.encodeToString(ItemJsonField(t.id, t.state))
    }

    fun fromJson(json: String, lifecycle: ItemLifecycle): ItemObject {
        return ItemObject(lifecycle)
    }

    fun newInstance(player: PlayerObject, lifecycle: ItemLifecycle): BaseObject {
        return ItemObject(lifecycle, player)
    }

    fun newInstanceFromPrefab(tag: Prefab.Tag, player: PlayerObject, lifecycle: ItemLifecycle): BaseObject {
        return gameAPI.instantiate<ItemObject>(ItemObject(lifecycle, player), tag)
    }
}