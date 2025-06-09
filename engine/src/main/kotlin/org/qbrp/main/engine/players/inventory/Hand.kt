package org.qbrp.main.engine.players.inventory

import org.koin.core.component.get
import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.core.mc.player.service.PlayerBehaviour
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.inventory.InventorySynchronizer
import org.qbrp.main.engine.items.ItemsModule
import org.qbrp.main.engine.items.PrefabEntryKey

//class Hand(val side: Side) : PlayerBehaviour() {
//    override val inventoryLogic: InventorySynchronizer by lazy {
//        InventorySynchronizer(channel = player, additionalId = side.name, inventory = mutableListOf(
//            Engine.get<ItemsModule>().createItem(PrefabEntryKey("item", "test", "test2")),
//            Engine.get<ItemsModule>().createItem(PrefabEntryKey("item", "test", "test2"))))
//    }
//
//    override fun toCluster(player: ServerPlayerObject): Cluster {
//        return ClusterBuilder.concat(
//            super.toCluster(player).getBuilder(),
//            ClusterBuilder().component("side", side.name)
//        ).build()
//    }
//
//    enum class Side {
//        LEFT, RIGHT
//    }
//}