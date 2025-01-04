package org.qbrp.system.networking
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import org.qbrp.system.utils.log.Loggers

object NetworkManager {
    val logger = Loggers.get("network", "sending")

    fun sendMessage(target: ServerPlayerEntity?, message: Message) {
        val data = message.content.writeByteBuf()
        ServerPlayNetworking.send(target, message.minecraftIdentifier, data)
        logger.log("${target?.name?.string} <-- <<${message.identifier}>> (${message.content})")
    }

    fun broadcastRadius(player: ServerPlayerEntity, message: Message, radius: Double) {
        getPlayersInRadius(player, radius).forEach {
            sendMessage(it, message)
        }
    }

    fun broadcastGlobal(world: ServerWorld, message: Message) {
        world.players.forEach { sendMessage(it, message) }
    }

    fun broadcastArea(
        world: ServerWorld,
        centerX: Double,
        centerZ: Double,
        radius: Double,
        message: Message
    ) {
        val radiusSquared = radius * radius
        world.players.filter {
            val dx = it.x - centerX
            val dz = it.z - centerZ
            dx * dx + dz * dz <= radiusSquared
        }.forEach { sendMessage(it, message) }
    }

    private fun getPlayersInRadius(player: ServerPlayerEntity, radius: Double): List<ServerPlayerEntity> {
        val radiusSquared = radius * radius
        return (player.world as ServerWorld).players.filter {
            it !== player && it.squaredDistanceTo(player) <= radiusSquared
        }
    }
}
