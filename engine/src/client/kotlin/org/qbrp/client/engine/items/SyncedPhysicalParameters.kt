package org.qbrp.client.engine.items

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.engine.chemistry.params.PhysicalParameters

@Serializable
class SyncedPhysicalParameters(
    override var mass: Double,
) : DataComponent, PhysicalParameters