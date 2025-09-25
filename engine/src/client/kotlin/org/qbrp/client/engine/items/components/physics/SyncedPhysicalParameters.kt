package org.qbrp.client.engine.items.components.physics

import kotlinx.serialization.Serializable
import org.qbrp.main.engine.physics.components.Friction
import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.engine.physics.model.params.PhysicalParameters

@Serializable
class SyncedPhysicalParameters(override var mass: Double,
                               override val averageFriction: Friction
): DataComponent, PhysicalParameters