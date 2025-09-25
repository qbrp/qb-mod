package org.qbrp.main.engine.props.components

import org.qbrp.main.core.components.AnyComponent
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.engine.physics.dynamics.dataformats.Shape
import org.qbrp.main.engine.physics.dynamics.shapes.BoxShape
import org.qbrp.main.engine.physics.dynamics.shapes.toShape
import org.qbrp.main.engine.props.PropsModule

fun ComponentManager.fetchPropShape(): BoxShape {
    return getComponentAs<Dimensions>(PropsModule.PROP_DIMENSIONS_COMPONENT)?.toShape() ?: get<Dimensions>()?.toShape() ?: throw IllegalArgumentException("Требуются параметры формы")
}

fun ComponentManager.fetchPropShapeOrNull(): BoxShape? {
    return getComponentAs<Dimensions>(PropsModule.PROP_DIMENSIONS_COMPONENT)?.toShape() ?: get<Dimensions>()?.toShape()
}