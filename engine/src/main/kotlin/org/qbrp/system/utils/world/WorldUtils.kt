package org.qbrp.system.utils.world

import net.minecraft.entity.player.PlayerEntity

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
