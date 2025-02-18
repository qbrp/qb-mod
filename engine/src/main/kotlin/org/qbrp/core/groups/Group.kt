package org.qbrp.core.groups

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.minecraft.server.network.ServerPlayerEntity

@JsonIgnoreProperties(ignoreUnknown = true)
class Group(val name: String, val players: MutableList<String> = mutableListOf()) {
    fun contains(player: ServerPlayerEntity): Boolean = players.contains(player.name.string)
}