package org.qbrp.main.engine.props

import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.items.service.ItemStorage
import org.qbrp.main.engine.world.location.Position
import org.qbrp.main.engine.physics.dynamics.tracking.BodyTrackingPositions
import org.qbrp.main.engine.props.components.SpawnedProp

class PropTrackingPositions(val itemStorage: ItemStorage<ServerItemObject>) : BodyTrackingPositions {
    override val positions: List<Position> get() = filterProps()

    private fun filterProps(): List<Position> {
        return itemStorage.getAll().mapNotNull { it.get<SpawnedProp>()?.pos }
    }
}