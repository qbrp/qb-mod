package org.qbrp.main.engine.players.model.service

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.main.core.components.extensions.loadJson
import org.qbrp.main.core.events.EventContext
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.engine.players.auth.SessionData
import org.qbrp.main.engine.players.accounts.Account
import org.qbrp.main.core.mc.player.model.impl.McPlayerStateView
import org.qbrp.main.core.mc.player.service.snapshot.MinecraftPlayerSnapshotService
import org.qbrp.main.engine.containers.v2.api.ContainerService
import org.qbrp.main.engine.physics.dynamics.manipulate.Manipulator
import org.qbrp.main.engine.players.model.mapping.PlayerJsonField
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.context.PlayerContext
import org.qbrp.main.engine.players.model.events.PlayerInitBeforeJsonLoadEvent
import org.qbrp.main.engine.world.model.World

class PlayerFactory(
    private val defaultWorld: World,
    private val snapshotService: MinecraftPlayerSnapshotService,
) : KoinComponent {
    context(event: EventContext, mc: MinecraftContext)
    fun fromJson(entity: PlayerSnapshot, account: Account, jsonField: PlayerJsonField, session: SessionData): ServerPlayerObject {
        val player = ServerPlayerObject(
            createContext(entity),
            entity.uuid,
            entity.username,
            account,
            session
        )
            .apply { state.parent = this }

        PlayerInitBeforeJsonLoadEvent(player).emit()
        account.updateRegisteredNicknames(entity.username.value)
        player.loadJson(jsonField.state, player)
        return player
    }

    context(event: EventContext, mc: MinecraftContext)
    fun construct(player: PlayerSnapshot, account: Account, session: SessionData): ServerPlayerObject {
        return ServerPlayerObject(
            createContext(player),
            player.uuid,
            player.username,
            account,
            session
        )
            .apply {
                state.parent = this
                PlayerInitBeforeJsonLoadEvent(this).emit()
            }
    }

    context(mc: MinecraftContext)
    private fun createContext(player: PlayerSnapshot) = Context(
        mc,
        McPlayerStateView(player.username, player.uuid, snapshotService),
        get(),
        get(),
        defaultWorld
    )

    class Context(
        val mc: MinecraftContext,
        val snapshotService: McPlayerStateView,
        override val containerService: ContainerService,
        override val manipulator: Manipulator,
        override val world: World
    ) : PlayerContext, MinecraftContext by mc, PlayerSnapshot by snapshotService
}