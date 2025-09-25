package org.qbrp.main.core.networking

import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import org.qbrp.main.core.mc.player.model.PlayerSnapshot

class ServerNetworkContext(val server: MinecraftServer, val player: PlayerSnapshot, val handler: ServerPlayNetworkHandler): NetworkContext