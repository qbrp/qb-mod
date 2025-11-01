package org.qbrp.main.engine.props

import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.util.math.ZeroVec3
import org.qbrp.main.core.util.math.ZeroWorldVec3
import org.qbrp.main.core.util.math.asVec3
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.items.service.ItemStorage
import org.qbrp.main.engine.physics.tracking.MovingPoint
import org.qbrp.main.engine.physics.tracking.TrackingPositions
import org.qbrp.main.engine.props.components.SpawnedProp
import org.qbrp.main.engine.world.location.WorldPos
import org.qbrp.main.engine.world.location.asWorldVec3

class PropTrackingPositions(val itemStorage: ItemStorage<ServerItemObject>, ) : TrackingPositions {
    override val points: List<MovingPoint> get() = filterProps()

    private fun filterProps(): List<MovingPoint> {
        return itemStorage.getAll()
            .map { MovingPoint(it.get<SpawnedProp>()?.pos?.asVec3() ?: ZeroWorldVec3(), ZeroWorldVec3()) }
    }
}