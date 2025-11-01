package org.qbrp.main.engine.players.model

import org.qbrp.main.engine.containers.api.ContainerService
import org.qbrp.main.engine.physics.system.service.ManipulatorSystem
import org.qbrp.main.engine.world.model.World

interface PlayerContext {
    val containerService: ContainerService
    val manipulator: ManipulatorSystem
    val world: World
}