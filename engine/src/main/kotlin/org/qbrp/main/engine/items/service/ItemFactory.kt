package org.qbrp.main.engine.items.service

import kotlinx.serialization.encodeToString
import org.koin.core.component.KoinComponent
import org.qbrp.main.core.assets.prefabs.Prefab
import org.qbrp.main.core.components.construct.ComponentBuilderOperator.Companion.default
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.loadJson
import org.qbrp.main.core.components.extensions.toDTO
import org.qbrp.main.core.components.serialization.ContextualSerializer
import org.qbrp.main.core.components.serialization.Serialization
import org.qbrp.main.engine.items.components.meta.Meta
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.items.event.ItemLoadEvent
import org.qbrp.main.engine.items.model.ItemInstantiateContext
import org.qbrp.main.engine.items.model.ItemJsonField
import org.qbrp.main.engine.items.model.ItemStateImpl
import org.qbrp.main.engine.world.model.World

class ItemFactory(private val storage: ItemStorage<ServerItemObject>): ContextualSerializer<ServerItemObject, ItemInstantiateContext>, KoinComponent {
    fun newPrefabInstance(tag: Prefab.Tag): ServerItemObject {
        val item = ServerItemObject(ItemInstantiateContext.DEFAULT)
        item.state.parent = item
        return handleInstance(item, tag)
    }

    fun <T: ServerItemObject> handleInstance(item: T, tag: Prefab.Tag): T {
        tag.mergeAndPut(item.state)
        storage.add(item)
        item.add(Meta(tag))
        ItemLoadEvent.EVENT.invoker().onItemLoad(item)
        item.enableAll()
        return item
    }

    override fun fromJson(json: String, context: ItemInstantiateContext): ServerItemObject {
        val jsonField = Serialization.JSON.decodeFromString<ItemJsonField>(json)
        return ServerItemObject(context, ItemStateImpl(), jsonField.id).apply {
            state.parent = this
            storage.add(this)
            state.loadJson(jsonField.state, this)
            ItemLoadEvent.EVENT.invoker().onItemLoad(this)
            state.enableAll()
        }
    }

    override fun toJson(t: ServerItemObject): String {
        return Serialization.JSON.encodeToString(ItemJsonField(t.id, t.state.toDTO()))
    }
}