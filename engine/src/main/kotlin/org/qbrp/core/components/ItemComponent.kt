package org.qbrp.core.components

import com.fasterxml.jackson.annotation.JsonIgnore
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import okhttp3.Call

abstract class ItemComponent(): AbstractGameComponent() {
    @Transient protected lateinit var cachedItem: ItemStack
    @Transient protected lateinit var cachedPlayer: PlayerEntity

    fun cache(item: ItemStack, player: PlayerEntity) {
        cachedItem = item; cachedPlayer = player
    }

    override fun call(callContext: CallContext): String {
        return try {
            onCall(callContext, cachedItem, cachedPlayer)
        } catch (e: Exception) {
            e.printStackTrace()
            CallResponse.FAIL
        }
    }

    override fun activate() {
        onActivate(cachedItem, cachedPlayer)
    }

    override fun tick() {
        onTick(cachedItem, cachedPlayer)
    }

    protected open fun onActivate(item: ItemStack, player: PlayerEntity) = Unit

    protected open fun onTick(item: ItemStack, player: PlayerEntity) = Unit

    protected open fun onCall(context: CallContext, item: ItemStack, player: PlayerEntity): String = CallResponse.SUCCESS
}