package org.qbrp.main.engine.players.model.context

import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.core.mc.player.model.impl.McPlayerStateView
import org.qbrp.main.engine.containers.v2.api.ContainerService
import org.qbrp.main.engine.physics.dynamics.manipulate.Manipulator
import org.qbrp.main.engine.world.model.World

interface PlayerContext : MinecraftContext, PlayerSnapshot {
    val containerService: ContainerService
    val manipulator: Manipulator
    val world: World
}
