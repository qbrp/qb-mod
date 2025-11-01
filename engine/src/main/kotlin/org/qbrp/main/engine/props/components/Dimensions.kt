package org.qbrp.main.engine.props.components

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.DataComponent

@Serializable //в метрах
open class Dimensions(override val length: Double, override val width: Double, override val height: Double) : DataComponent, BoxDimensions {
    companion object {
        val ZERO = Dimensions(0.0, 0.0, 0.0)
    }
}