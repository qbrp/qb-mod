//package org.qbrp.main.engine.items_old.model
//
//import com.fasterxml.jackson.annotation.JsonIgnore
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties
//import com.google.gson.GsonBuilder
//import net.minecraft.entity.player.PlayerEntity
//import net.minecraft.item.ItemStack
//import org.qbrp.main.core.components.DataComponent
//import org.qbrp.main.core.components.ItemComponent
//
//@JsonIgnoreProperties(ignoreUnknown = true)
//data class ItemState(
//    val id: Int,
//    val name: String,
//    val type: String,
//    val components: List<DataComponent>
//) {
//    @JsonIgnore val itemComponents: List<ItemComponent> = components.map { it.cast() }
//    @JsonIgnore var displayName: String = name
//    @JsonIgnore var description: MutableMap<String, String> = mutableMapOf("test" to "test")
//
//    fun activate(stack: ItemStack, entity: PlayerEntity) {
//        itemComponents.forEach {
//            it.cache(stack, entity, this)
//            it.activate()
//            (stack.item as QbItem).setActivated(stack, true)
//        }
//    }
//
//    fun tick(stack: ItemStack, entity: PlayerEntity) {
//        itemComponents.forEach {
//            it.cache(stack, entity, this)
//            it.tick()
//        }
//    }
//
//    fun toJson(): String {
//        return GSON.toJson(this)
//    }
//
//    inline fun <reified T> getComponent(name: String = T::class.simpleName!!): T? {
//        return itemComponents.filterIsInstance<T>().first()
//    }
//
//    companion object {
//        val GSON = GsonBuilder()
//            .create()
//    }
//}