package org.qbrp.engine.items.model

import klite.NotFoundException
import net.minecraft.item.ItemStack
import org.koin.core.component.KoinComponent
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.litote.kmongo.insertOne
import org.qbrp.core.resources.ServerResources
import org.qbrp.system.database.DatabaseService

class ItemLoader: DatabaseService(ServerResources.getConfig().databases.nodeUri, ServerResources.getConfig().databases.items), KoinComponent {
    init {
        connect()
    }

    fun getItemState(id: Int): ItemData {
        val collection = db?.getCollection<ItemData>("data") ?: throw NotFoundException()
        val item = collection.findOne(ItemData::id eq id) ?: throw NotFoundException()
        return item
    }

    fun getOrCreateItem(id: Int, itemstack: ItemStack): ItemData {
        return try {
            getItemState(id)
        } catch (e: NotFoundException) {
            (itemstack.item as QbItem).data
                .also {
                    db?.getCollection("data")?.insertOne(it.toJson())
            }
        }
    }

    fun createItem(data: ItemData) {
        db?.getCollection("data")?.insertOne(data.toJson())
    }

}