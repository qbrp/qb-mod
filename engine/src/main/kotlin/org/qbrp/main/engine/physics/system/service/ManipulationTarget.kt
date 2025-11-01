package org.qbrp.main.engine.physics.system.service

import org.qbrp.main.core.fabricmc.player.PlayerReference
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.world.location.WorldPos

typealias PositionProvider = () -> WorldPos
typealias DepthProvider = () -> Float

sealed class ManipulationTarget {
    class Position(val value: PositionProvider) : ManipulationTarget()
    class View(val player: ServerPlayerObject, val depth: DepthProvider): ManipulationTarget()
}