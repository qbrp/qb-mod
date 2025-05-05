//package org.qbrp.core.components
//
//import net.minecraft.entity.player.PlayerEntity
//import net.minecraft.item.ItemStack
//import org.qbrp.engine.items_old.model.ItemState
//
//abstract class ItemComponent(): AbstractGameComponent() {
//    @Transient protected lateinit var cachedItemState: ItemState
//    @Transient protected lateinit var cachedItem: ItemStack
//    @Transient protected lateinit var cachedPlayer: PlayerEntity
//
//    fun cache(item: ItemStack, player: PlayerEntity, state: ItemState) {
//        cachedItem = item; cachedPlayer = player; cachedItemState = state
//    }
//
//    override fun call(callContext: String): String {
//        return try {
//            onCall(callContext, cachedItem, cachedPlayer, cachedItemState)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            CallResponse.FAIL
//        }
//    }
//
//    override fun activate() {
//        onActivate(cachedItem, cachedPlayer, cachedItemState)
//    }
//
//    override fun tick() {
//        onTick(cachedItem, cachedPlayer, cachedItemState)
//    }
//
//    protected open fun onActivate(item: ItemStack, player: PlayerEntity, state: ItemState) = Unit
//
//    protected open fun onTick(item: ItemStack, player: PlayerEntity, state: ItemState) = Unit
//
//    protected open fun onCall(context: String, item: ItemStack, player: PlayerEntity, state: ItemState): String = CallResponse.SUCCESS
//}