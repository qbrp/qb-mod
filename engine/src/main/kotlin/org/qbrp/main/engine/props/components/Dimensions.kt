package org.qbrp.main.engine.props.components

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.core.networking.synchronization.DtoSync

@Serializable //в метрах
open class Dimensions(override val length: Double, override val width: Double, override val height: Double) : DataComponent, DtoSync, BoxDimensions {
    companion object {
        val ZERO = Dimensions(0.0, 0.0, 0.0)
    }
}