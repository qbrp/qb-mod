package org.qbrp.main.core.fabricmc

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.getOrAdd
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.fabricmc.player.LuckPermsPermission
import org.qbrp.main.core.fabricmc.player.Permission
import org.qbrp.main.core.fabricmc.player.PlayerPermissions
import org.qbrp.main.core.util.math.Vec3
import org.qbrp.main.core.util.math.engineWorldVec3
import org.qbrp.main.engine.physics.system.entity.degToRad
import org.qbrp.main.engine.players.model.PlayerLocation
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.service.PlayerList
import org.qbrp.main.engine.world.location.WorldPos
import org.qbrp.main.engine.world.location.WorldVec3
import org.qbrp.main.engine.world.location.asWorldVec3
import kotlin.math.cos
import kotlin.math.sin

interface CameraOrientation {
    val yaw: Double
    val pitch: Double

    val rotationVector: WorldVec3
        get() {
            val pitchRad = degToRad(pitch)
            val yawRad = degToRad(-yaw)
            val h = cos(yawRad)
            val i = sin(yawRad)
            val j = cos(pitchRad)
            val k = sin(pitchRad)
            return Vec3(i * j, -k, h * j).asWorldVec3()
        }
}

data class CameraOrientationComponent(
    override var yaw: Double,
    override var pitch: Double
) : DataComponent, CameraOrientation

data class PlayerPermissionsComponent(
    private val lazyPermissions: () -> List<Permission>,
    private val lazyOp: () -> Boolean
) : DataComponent, PlayerPermissions {
    override val permissions: List<Permission>
        get() = lazyPermissions()
    override val isOp: Boolean
        get() = lazyOp()
}

val ServerPlayerObject.isOp get() = this.get<PlayerPermissionsComponent>()?.isOp == true

data class PlayerModel(
    private val lazyEyePos: () -> WorldVec3
) : DataComponent {
    val eyePos: WorldPos get() = lazyEyePos()
}

val ServerPlayerObject.eyePos get() = this.require<PlayerModel>().eyePos

val ServerPlayerObject.rotationVector get() = this.require<CameraOrientation>().rotationVector

class MinecraftAdapterSystem(
    server: MinecraftServer,
    private val playerList: PlayerList
) {
    private val playerManager = server.playerManager

    fun update() {
//        for (player in playerList) {
//            val entity = playerManager.getPlayer(player.minecraftPlayerId.value) ?: continue
//
//            player.require<PlayerLocation>().apply {
//                pos = entity.pos.engineWorldVec3()
//            }
//        }
    }
}


fun ServerPlayerObject.addPlayerPermissionsComponent(entity: ServerPlayerEntity) {
    add(
        PlayerPermissionsComponent(
            { LuckPermsPermission.listPermissions(entity) },
            { LuckPermsPermission.isOp(entity) }
        )
    )
}

fun ServerPlayerObject.addPlayerModelComponent(entity: ServerPlayerEntity) {
    add(
        PlayerModel(
            { entity.eyePos.engineWorldVec3() }
        )
    )
}

fun PlayerObject.getCameraOrientation(entity: PlayerEntity): CameraOrientationComponent {
    return getOrAdd { CameraOrientationComponent(entity.yaw.toDouble(), entity.pitch.toDouble()) }
}

val PlayerObject.yaw get() = this.get<CameraOrientationComponent>()?.yaw ?: 0.0

val PlayerObject.pitch get() = this.get<CameraOrientationComponent>()?.pitch ?: 0.0