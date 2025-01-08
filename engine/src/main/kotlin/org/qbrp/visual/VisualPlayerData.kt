package org.qbrp.visual

import com.google.gson.Gson
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.world.World
import java.util.UUID

class VisualPlayerData(@Transient val player: PlayerEntity) : VisualData(player.blockX, player.blockY, player.blockZ) {
    override val x: Int
        get() = player.blockX
    override val y: Int
        get() = player.blockY
    override val z: Int
        get() = player.blockZ
    private val playerUUID: UUID = player.uuid
    override val clazz: Class<VisualPlayerData> = VisualPlayerData::class.java

    override fun toString(): String {
        return "VisualPlayerData(playerUUID=$playerUUID, x=$x, y=$y, z=$z)"
    }

    override fun toJson(): String {
        return Gson().toJson(playerUUID.toString())
    }

    override fun fromJson(json: String, world: World): VisualPlayerData {
        val uuid = Gson().fromJson(json, String::class.java)
        val player = world.getPlayerByUuid(UUID.fromString(uuid)) as PlayerEntity
        return VisualPlayerData(player) // Возвращаем объект с новыми координатами
    }
}
