package org.qbrp.main.engine.props

import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.util.math.QuatIdentity
import org.qbrp.main.engine.chemistry.params.PhysicalParameters
import org.qbrp.main.engine.items.model.ItemObject
import org.qbrp.main.engine.physics.system.entity.BodyDescriptor
import org.qbrp.main.engine.physics.system.entity.Shape
import org.qbrp.main.engine.physics.system.entity.Transform
import org.qbrp.main.engine.physics.system.entity.asPhysical
import org.qbrp.main.engine.props.components.propShape
import org.qbrp.main.engine.world.location.WorldPos

fun PropDescriptor(
    item: ItemObject,
    location: WorldPos,
    physicalParameters: PhysicalParameters = item.get<PhysicalParameters>()
        ?: throw IllegalArgumentException("Требуются физические параметры для спавна пропа"),
    shape: Shape = item.propShape
        ?: throw IllegalArgumentException("Требуются параметры формы")
): BodyDescriptor {
    val pos = location

    return BodyDescriptor(
        shape,
        Transform(pos.asPhysical(), QuatIdentity().asPhysical()),
        physicalParameters.mass
    )
}