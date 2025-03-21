package org.qbrp.engine.items.model

import com.google.gson.GsonBuilder
import org.bson.codecs.pojo.annotations.BsonId
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.core.components.ItemComponent
import org.qbrp.core.resources.ServerResources

data class ItemData(
    @BsonId val id: Int,
    val name: String,
    val type: String,
    val components: List<ItemComponent>
): KoinComponent {
    fun toJson(): String {
        return GSON.toJson(this)
    }

    companion object {
        val GSON = GsonBuilder()
            .create()
    }
}