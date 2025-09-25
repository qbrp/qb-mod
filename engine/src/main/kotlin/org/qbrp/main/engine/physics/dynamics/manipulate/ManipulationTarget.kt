package org.qbrp.main.engine.physics.dynamics.manipulate

import org.qbrp.main.core.mc.player.model.PlayerReference
import org.qbrp.main.engine.world.location.Position

typealias PositionProvider = () -> Position
typealias DepthProvider = () -> Float

sealed class ManipulationTarget {
    class Position(val value: PositionProvider) : ManipulationTarget()
    class View(val player: PlayerReference, val depth: DepthProvider): ManipulationTarget()
}