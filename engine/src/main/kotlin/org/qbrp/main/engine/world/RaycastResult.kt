package org.qbrp.main.engine.world

import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.props.components.SpawnedProp

class RaycastResult(val props: List<SpawnedProp>, val player: List<ServerPlayerObject>) {
    val nearestPlayer: ServerPlayerObject?
        get() = player.getOrNull(0)
    val nearestProp: SpawnedProp?
        get() = props.getOrNull(0)
}