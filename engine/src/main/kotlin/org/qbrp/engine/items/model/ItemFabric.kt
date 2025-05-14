package org.qbrp.engine.items.model

import okhttp3.internal.parseCookie
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.core.assets.prefabs.Prefab
import org.qbrp.core.game.model.objects.BaseObject
import org.qbrp.core.game.serialization.SerializeFabric
import org.qbrp.core.mc.player.PlayerObject
import org.qbrp.engine.game.GameAPI

class ItemFabric(val gameAPI: GameAPI): SerializeFabric<ItemObject, ItemJsonField>(), KoinComponent {
    override fun toJson(t: ItemObject): ItemJsonField {
        return ItemJsonField(t.id, t.state)
    }

    override fun fromJson(json: ItemJsonField): ItemObject {
        return ItemObject(get())
    }

    fun newInstance(player: PlayerObject): BaseObject {
        return ItemObject(get(), player)
    }

    fun newInstanceFromPrefab(tag: Prefab.Tag, player: PlayerObject): BaseObject {
        return gameAPI.instantiate<ItemObject>(ItemObject(get(), player), tag)
    }
}