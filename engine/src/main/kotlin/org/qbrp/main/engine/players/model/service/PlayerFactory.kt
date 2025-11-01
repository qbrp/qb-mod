package org.qbrp.main.engine.players.model.service

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.component.KoinComponent
import org.qbrp.main.core.components.extensions.DtoLoadRules
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.extensions.loadJson
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.fabricmc.ActionBarMessageQueue
import org.qbrp.main.core.fabricmc.CameraOrientationComponent
import org.qbrp.main.core.fabricmc.addPlayerModelComponent
import org.qbrp.main.core.fabricmc.addPlayerPermissionsComponent
import org.qbrp.main.core.fabricmc.player.MinecraftPlayerId
import org.qbrp.main.core.fabricmc.player.Username
import org.qbrp.main.core.util.math.ZeroVec3
import org.qbrp.main.core.util.math.ZeroWorldVec3
import org.qbrp.main.core.util.math.engineWorldVec3
import org.qbrp.main.engine.controls.entity.InteractionMode
import org.qbrp.main.engine.controls.system.modes.InteractionModeComponent
import org.qbrp.main.engine.physics.player.IsSupportedComponent
import org.qbrp.main.engine.physics.player.MovementEmulation
import org.qbrp.main.engine.physics.player.MovementInputComponent
import org.qbrp.main.engine.physics.player.PlayerMovementAbilities
import org.qbrp.main.engine.physics.player.VelocityComponent
import org.qbrp.main.engine.players.DisplayName
import org.qbrp.main.network.player.ConnectionSession
import org.qbrp.main.engine.players.accounts.Account
import org.qbrp.main.engine.players.attributes.PlayerAttributes
import org.qbrp.main.engine.players.model.PlayerJsonField
import org.qbrp.main.engine.players.model.PlayerLocation
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.PlayerInitBeforeJsonLoadEvent
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.world.model.MainWorld
import org.qbrp.main.engine.world.model.World
import java.util.concurrent.ConcurrentLinkedQueue

class PlayerFactory(private val mainWorld: MainWorld) : KoinComponent {
    private fun newInstance(
        entity: ServerPlayerEntity,
        account: Account,
        session: ConnectionSession
    ): ServerPlayerObject {
        return ServerPlayerObject(
            MinecraftPlayerId(entity.uuid),
            Username(entity.name.string),
            account,
            session,
        )
            .apply {
                state.parent = this
                buildCommonComponents(entity, mainWorld)
                add(ActionBarMessageQueue(ConcurrentLinkedQueue()))
                addPlayerPermissionsComponent(entity)
                addPlayerModelComponent(entity)
                add(InteractionModeComponent(InteractionMode.DEFAULT, false))
            }
    }

    fun fromJson(
        entity: ServerPlayerEntity,
        account: Account,
        jsonField: PlayerJsonField,
        session: ConnectionSession
    ): ServerPlayerObject {
        val player = newInstance(entity, account, session)

        PlayerInitBeforeJsonLoadEvent(player).emit()
        account.updateRegisteredNicknames(player.username.value)
        player.loadJson(
            jsonField.state,
            player,
            rules = DtoLoadRules(
                mapOf("CharactersBank" to -1)
            )
        )
        return player
    }

    fun construct(entity: ServerPlayerEntity, account: Account, session: ConnectionSession): ServerPlayerObject {
        return newInstance(entity, account, session)
            .apply {
                add(DisplayName(username, null))
                add(PlayerAttributes())
                PlayerInitBeforeJsonLoadEvent(this).emit()
            }
    }
}

fun PlayerObject.buildCommonComponents(entity: PlayerEntity, world: World) {
    add(PlayerLocation(world, entity.pos.engineWorldVec3()))
    add(MovementEmulation())
    add(PlayerMovementAbilities(false))
    add(IsSupportedComponent(false))
    add(VelocityComponent(ZeroWorldVec3()))
    add(MovementInputComponent(false, false, false, false, false, false))
    add(CameraOrientationComponent(entity.yaw.toDouble(), entity.pitch.toDouble()))
}