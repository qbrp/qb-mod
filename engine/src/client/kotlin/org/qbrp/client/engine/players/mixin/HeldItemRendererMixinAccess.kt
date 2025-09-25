package org.qbrp.client.engine.players.mixin

import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.qbrp.client.deprecated.HeldItem
import org.qbrp.client.engine.players.ClientPlayerManager
import org.qbrp.client.engine.players.ClientPlayerStorage
import org.qbrp.client.engine.players.ClientPlayersModule
import org.qbrp.client.engine.players.inventory.HandItemsComponent
import org.qbrp.client.render.item.impl.mc.ItemStackAdapterWrapper
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.mc.player.toPlayerMc

object HeldItemRendererMixinAccess: KoinComponent {
    val storage: ClientPlayerStorage by inject()
    val stackWrapper: ItemStackAdapterWrapper by inject()
    val playerManager: ClientPlayerManager by inject()

    fun getPlayerRightHandItem(player: AbstractClientPlayerEntity): ItemStack {
        val obj = storage.getByMinecraftId(player.uuid.toPlayerMc())
        val heldItem = obj?.get<HandItemsComponent>()?.getRightHandDisplay()
        val stack = heldItem?.let { stackWrapper.wrapItemStack(it) } ?: ItemStack.EMPTY
        return stack
    }

    fun getPlayerLeftHandItem(player: AbstractClientPlayerEntity): ItemStack {
        val obj = storage.getByMinecraftId(player.uuid.toPlayerMc())
        val heldItem = obj?.get<HandItemsComponent>()?.getLeftHandDisplay()
        val stack = heldItem?.let { stackWrapper.wrapItemStack(it) } ?: ItemStack.EMPTY
        return stack
    }

    fun getClientPlayerRightHandItem(): ItemStack {
        if (!playerManager.isInstantiated) return ItemStack.EMPTY
        val heldItem = playerManager.clientPlayer.get<HandItemsComponent>()?.getRightHandDisplay()
        val stack = heldItem?.let { stackWrapper.wrapItemStack(it) } ?: ItemStack.EMPTY
        return stack
    }

    fun getClientPlayerLeftHandItem(): ItemStack {
        if (!playerManager.isInstantiated) return ItemStack.EMPTY
        val heldItem = playerManager.clientPlayer.get<HandItemsComponent>()?.getLeftHandDisplay()
        val stack = heldItem?.let { stackWrapper.wrapItemStack(it) } ?: ItemStack.EMPTY
        return stack
    }
}