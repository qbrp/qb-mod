package org.qbrp.main.engine.players.inventory

import org.qbrp.main.core.mc.player.service.PlayerBehaviour
import org.qbrp.main.core.utils.networking.messages.components.Cluster

class PlayerInventory() : PlayerBehaviour() {
    override fun onEnable() {
        requireState().apply {
            //addComponent(Hand(Side.LEFT), true)
            //addComponent(Hand(Side.RIGHT), true)
        }
    }

    fun open() {
        player.sendMessage(Cluster(), player, "inventory.open.player")
    }
}