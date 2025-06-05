package org.qbrp.main.engine.items.model

import kotlinx.serialization.encodeToString
import org.koin.core.component.get
import org.qbrp.main.core.assets.prefabs.Prefab
import org.qbrp.main.core.game.model.objects.BaseObject
import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.game.GameAPI
import org.qbrp.main.core.game.IDGenerator
import org.qbrp.main.core.game.model.State
import org.qbrp.main.core.game.serialization.ContextualFactory
import org.qbrp.main.core.game.serialization.GameMapper
import org.qbrp.main.engine.items.ItemsModule

class ItemFabric(val gameAPI: GameAPI): ContextualFactory<ServerItemObject, ItemsModule> {
    fun newInstanceFromPrefab(tag: Prefab.Tag, player: PlayerObject, context: ItemsModule): BaseObject {
        return gameAPI.instantiate<ServerItemObject>(
            ServerItemObject(IDGenerator.nextId().toString(), State(), context),
            tag, context.getLocal<ItemLifecycle>())
            .apply { give(player) }
    }

    override fun fromJson(json: String, context: ItemsModule): ServerItemObject {
        val jsonField = GameMapper.COMPONENTS_JSON.decodeFromString<ItemJsonField>(json)
        return ServerItemObject(jsonField.id, jsonField.state, context).also { context.get<ItemLifecycle>().onCreated(it) }
    }

    override fun toJson(t: ServerItemObject): String {
        return GameMapper.COMPONENTS_JSON.encodeToString(ItemJsonField(t.id, t.state))
    }
}