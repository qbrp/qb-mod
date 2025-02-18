package org.qbrp.core.groups

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.view.View

object GroupSelection {
    private val selectionSessions: MutableMap<ServerPlayerEntity, Group> = mutableMapOf()
    fun handleInteraction(player: ServerPlayerEntity, interactedPlayer: ServerPlayerEntity) {
        val group: Group = selectionSessions[player] ?: return
        val interactedPlayerName = interactedPlayer.name.string
        if (interactedPlayerName in group.players) { group.players.removeIf { interactedPlayerName == it } }
        else {
            val group: Group = selectionSessions[player]!!
            group.players.add(interactedPlayerName)
        }
        View.vanillaHud.setActionBarStatus(player, group.players.map { it }.joinToString(", "))
    }

    fun getSessionGroupPlayers(player: ServerPlayerEntity): List<ServerPlayerEntity> {
        val group = selectionSessions[player] ?: return emptyList()
        return group.players.mapNotNull { player.server.playerManager.getPlayer(it) }
    }

    fun getSessionGroup(player: ServerPlayerEntity): Group {
        return selectionSessions[player]!!
    }

    fun createSession(player: ServerPlayerEntity, name: String) {
        createSession(player, Group(name))
    }

    fun createSession(player: ServerPlayerEntity, group: Group) {
        selectionSessions[player] = group
        View.vanillaHud.setActionBarStatus(player, "Выберите игроков группы, нажимая ПКМ по ним")
    }

    fun finishSession(player: ServerPlayerEntity): Group {
        View.vanillaHud.setActionBarStatus(player, "")
        return selectionSessions.remove(player)!!.also { return it }
    }
}