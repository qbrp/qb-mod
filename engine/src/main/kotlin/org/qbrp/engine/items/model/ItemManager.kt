package org.qbrp.engine.items.model

import kotlinx.coroutines.runBlocking
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.core.mc.Game
import org.qbrp.core.resources.content.ItemConfig

class ItemManager(): KoinComponent {
    private val itemStates: MutableMap<Int, ItemState> = mutableMapOf()
    private val lastId: Int by lazy { runBlocking { get<ItemLoader>().getLastId() } }
    private var autoIncrementId: Int = 0

    fun wrapItem(config: ItemConfig, tag: String, player: ServerPlayerEntity): ItemStack {
        val itemTag = Game.items.getBaseItem(config.parent)?.get()
        val stack = ItemStack(itemTag)
        val itemState = createBlankState(config.getTag(tag), config)
        initializeItemNbt(stack, itemState.id)
        stack.orCreateNbt.putBoolean("activated", false)
        stack.orCreateNbt.putBoolean("loaded", true)
        itemState.activate(stack, player)
        player.giveItemStack(stack)
        return stack
    }

    fun initializeItemNbt(stack: ItemStack, id: Int) {
        stack.orCreateNbt.putInt("id", id)
        stack.nbt!!.putBoolean("activated", false)
        stack.nbt!!.putBoolean("loaded", true)
    }

    fun createBlankState(tag: ItemConfig.Tag, config: ItemConfig): ItemState {
        val data = ItemState(getId(), tag.name, config.parent, tag.components)
        get<ItemLoader>().createItem(data)
        itemStates.put(data.id, data)
            .also { return itemStates[data.id]!! }
    }

    fun getItemState(id: Int): ItemState? {
        try {
            return itemStates[id]
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun loadFromDatabase(id: Int): ItemState? {
        return get<ItemLoader>().getItemState(id).also { if(it != null) itemStates[id] = it }
    }

    fun getId(): Int {
        return autoIncrementId++ + lastId
    }
}