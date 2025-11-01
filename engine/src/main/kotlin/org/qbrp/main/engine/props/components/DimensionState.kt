package org.qbrp.main.engine.props.components

import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.getAs
import org.qbrp.main.engine.physics.system.entity.Shape
import org.qbrp.main.engine.physics.system.entity.toShape
import org.qbrp.main.engine.props.PropsModule
import kotlin.math.cos
import kotlin.math.sin

//?: throw IllegalArgumentException("Требуются параметры формы")

val ComponentManager.propShape: Shape.Box?
    get() = getAs<Dimensions>(PropsModule.PROP_DIMENSIONS_COMPONENT)?.toShape() ?: get<Dimensions>()?.toShape()