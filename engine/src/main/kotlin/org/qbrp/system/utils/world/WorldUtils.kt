package org.qbrp.system.utils.world

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import kotlin.compareTo

fun List<PlayerEntity>.getPlayersInRadius(
    player: PlayerEntity,
    radius: Double,
    handleNegativeInt: Boolean = false,
    includeSource: Boolean = false
): List<PlayerEntity> {
    val radiusSquared = radius * radius
    if (handleNegativeInt && radius.toInt() == -1) {
        return player.world.players
    }
    return filter {
        (includeSource || it !== player) && it.squaredDistanceTo(player) <= radiusSquared
    }
}

fun playSoundForPlayer(player: ServerPlayerEntity, soundId: String, volume: Float = 1.0f, pitch: Float = 1.0f) {
    val pos: Vec3d = player.pos // Получаем позицию игрока
    val packet = PlaySoundS2CPacket(
        Registries.SOUND_EVENT.getEntry(Registries.SOUND_EVENT.get(Identifier.tryParse(soundId))) ?: return, // Идентификатор звука
        SoundCategory.PLAYERS, // Категория звука
        pos.x, pos.y, pos.z, // Координаты звука
        volume, pitch, System.currentTimeMillis().toLong()
    )
    player.networkHandler.sendPacket(packet) // Отправляем пакет только этому игроку
}